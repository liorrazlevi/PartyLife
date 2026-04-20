package lior.razlevi.partylife;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class party_creation_page extends AppCompatActivity {

    private TextInputEditText etPartyName, etLocation, etDate, etTime, etDressCode, etPhone, etParking;
    private AutoCompleteTextView etAge;
    private MaterialButton btnCreate;
    private ImageView ivProfile, ivSelectedPartyImage;
    private MaterialCardView cvPartyImage;
    private TextView tvTitle, tvSubtitle;

    private Uri imageUri;
    private ActivityResultLauncher<Intent> galleryLauncher;
    
    private FirebaseAuth mAuth;

    private FirebaseDatabase database;
    private DatabaseReference partyRef;

    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_party_creation_page);

        init();
        setupAgeSpinner();
        setupPickers();
        setupGalleryLauncher();

        ivProfile.setOnClickListener(view -> {
            startActivity(new Intent(this, UserSettingActivity.class));
        });

        btnCreate.setOnClickListener(v -> {
            if (validateInputs()) {
                uploadImageAndCreateParty();
            }
        });


        cvPartyImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.partycreation), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        ivSelectedPartyImage.setImageURI(imageUri);
                        ivSelectedPartyImage.setAlpha(1.0f);
                    }
                }
        );
    }

    // פונקציה שמקבלת URI ומחזירה מחרוזת Base64
    public String encodeImage(Uri imageUri) {
        try {
            // הפיכת ה-URI ל-InputStream ואז ל-Bitmap
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // הכנת הזרם שבו נדחוס את התמונה
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // דחיסה לפורמט JPEG (ניתן לשנות ל-PNG אם השקיפות חשובה)
            // איכות 100 היא המקסימלית, ניתן להוריד כדי לחסוך מקום
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byte[] imageBytes = baos.toByteArray();

            // המרה סופית למחרוזת
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void uploadImageAndCreateParty() {
        Log.d("uploadImageAndCreateParty", "Uploading image and creating party");
        if (imageUri != null) {
            Log.d("uploadImageAndCreateParty", "Image URI is not null");

            String picture=encodeImage(imageUri);
          //  String fileName = UUID.randomUUID().toString();
          //  StorageReference ref = storageReference.child("party_images/" + fileName);

         //   ref.putFile(imageUri)
                //    .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    //    savePartyToDatabase(uri.toString());
                  //  }))
                  //  .addOnFailureListener(e -> Toast.makeText(this, "שגיאה בהעלאת תמונה: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        savePartyToDatabase(picture);
        } else {
            Log.d("uploadImageAndCreateParty", "Image URI is null");
            savePartyToDatabase(""); 
        }
    }

    private void savePartyToDatabase(String image) {
        FirebaseUser firebaseUser = Auth.getCurrentUser();
        if (firebaseUser == null) return;
        String uid = firebaseUser.getUid();


        // יצירת מזהה ייחודי ב-Firebase// /// // DatabaseReference newPartyRef = database.push();
        DatabaseReference newPartyRef = partyRef.push();
        String partyId = newPartyRef.getKey(); // זה ה-ID הייחודי של המסיבה

        Party  party = new Party(partyId, etPartyName.getText().toString(), etLocation.getText().toString(),
                etDate.getText().toString(), etTime.getText().toString(), etAge.getText().toString(),
                etDressCode.getText().toString(), etPhone.getText().toString(), image, uid, etParking.getText().toString());



      newPartyRef.setValue(party)
                .addOnSuccessListener(aVoid -> {
                    Log.d("MARIELA", "User saved successfully");
Toast.makeText(this,"party save successfully",Toast.LENGTH_SHORT).show();
                   Intent intent=new Intent(party_creation_page.this,created_events_page.class);
                   startActivity(intent);

                    // כאן אפשר לעבור מסך, אבל אנחנו עושים את זה למטה ב-OnClickListener
                })
                //כשלון בשמירה
                .addOnFailureListener(e -> {
                    Log.e("MARIELA", "Failed to save party", e);
                    Toast.makeText(this,"failed to save party",Toast.LENGTH_SHORT).show();
                });



    }

    private void setupPickers() {
        etDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(etDate, "dd/MM/yyyy");
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        etTime.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                updateLabel(etTime, "HH:mm");
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });
    }

    private void updateLabel(TextInputEditText editText, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        editText.setText(sdf.format(calendar.getTime()));
    }

    private boolean validateInputs() {
        if (etPartyName.getText().toString().isEmpty()) {
            etPartyName.setError("אנא הזן שם מסיבה");
            return false;
        }
        if (etLocation.getText().toString().isEmpty()) {
            etLocation.setError("אנא הזן מיקום");
            return false;
        }
        if (etDate.getText().toString().isEmpty()) {
            etDate.setError("אנא בחר תאריך");
            return false;

        }
        // לבדןק שהתאריך והזמן לא פגו.

        if (etTime.getText().toString().isEmpty()) {
            etTime.setError("אנא בחר שעה");
            return false;
        }
        if (etAge.getText().toString().isEmpty()) {
            Toast.makeText(this, "אנא בחר טווח גילאים", Toast.LENGTH_SHORT).show();
            return false;
        }
        Log.d("validateInputs", "All fields are valid");
        return true;

    }

    private void setupAgeSpinner() {
        String[] ageRanges = {"18-20", "20-25", "25-30", "30+"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, ageRanges);
        etAge.setAdapter(adapter);
        etAge.setOnClickListener(v -> etAge.showDropDown());
    }

    private void fetchUserName(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        userRef.get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                String name = dataSnapshot.child("fullName").getValue(String.class);
                if (name != null) tvSubtitle.setText("היי " + name + ", מלא את הפרטים ליצירת המסיבה שלך");
            }
        });
    }

    private void init() {
        etPartyName = findViewById(R.id.etPartyName);
        etLocation = findViewById(R.id.etLocation);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etAge = findViewById(R.id.etAge);
        etDressCode = findViewById(R.id.etDressCode);
        etPhone = findViewById(R.id.etPhone);
        etParking = findViewById(R.id.etParking);
        btnCreate = findViewById(R.id.btnCreate);
        ivProfile = findViewById(R.id.ivProfile);
        cvPartyImage = findViewById(R.id.cvPartyImage);
        ivSelectedPartyImage = findViewById(R.id.ivSelectedPartyImage);
        tvSubtitle = findViewById(R.id.tvSubtitle);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    partyRef = database.getReference("Parties");
        if (mAuth.getCurrentUser() != null) {
            fetchUserName(mAuth.getCurrentUser().getUid());
        }
    }
}
