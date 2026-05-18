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
import android.view.View;
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class party_creation_page extends AppCompatActivity {

    private TextInputEditText etPartyName, etLocation, etDate, etTime, etDressCode, etPhone, etParking ,etFullAddress;
    private AutoCompleteTextView etAge;
    private MaterialButton btnCreate;
    private ImageView ivProfile, ivSelectedPartyImage;
    private MaterialCardView cvPartyImage;
    private TextView tvSubtitle;

    private Uri imageUri;
    private ActivityResultLauncher<Intent> galleryLauncher;
    
    private FirebaseAuth mAuth;
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
                createParty();
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
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            
                            // הצגת התמונה הנבחרת
                            ivSelectedPartyImage.setImageBitmap(bitmap);
                            ivSelectedPartyImage.setAlpha(1.0f);
                        } catch (Exception e) {
                            Toast.makeText(this, "שגיאה בטעינת התמונה", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private String encodeImage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // דחיסה משמעותית כדי שהסטרינג לא יהיה ארוך מדי עבור ה-Database
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            return "";
        }
    }

    private void createParty() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "עליך להיות מחובר כדי ליצור מסיבה", Toast.LENGTH_SHORT).show();
            return;
        }

        String imageString = "";
        if (imageUri != null) {
            imageString = encodeImage(imageUri);
        }

        String partyId = partyRef.push().getKey();
        if (partyId == null) return;
Log.d("PartyLior", "PartyId: " + partyId);
        Party party = new Party(
                partyId,
                etPartyName.getText().toString().trim(),
                etLocation.getText().toString().trim(),
                etDate.getText().toString().trim(),
                etTime.getText().toString().trim(),
                etAge.getText().toString().trim(),
                etDressCode.getText().toString().trim(),
                etPhone.getText().toString().trim(),
                imageString,
                currentUser.getUid(),
                etParking.getText().toString().trim()
                ,etFullAddress.getText().toString().trim()
        );
Log.d("PartyLior", "Party before: " + party);
        partyRef.child(partyId).setValue(party)
                .addOnSuccessListener(aVoid -> {
                    Log.d("PartyLior", "Party created: " + party);
                    Toast.makeText(this, "המסיבה נוצרה בהצלחה!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("PartyLior", "Error creating party", e);
                    Toast.makeText(this, "שגיאה בשמירה: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        if (TextUtils.isEmpty(etPartyName.getText())) {
            etPartyName.setError("אנא הזן שם מסיבה");
            return false;
        }
        if (TextUtils.isEmpty(etLocation.getText())) {
            etLocation.setError("אנא הזן מיקום");
            return false;
        }
        if(TextUtils.isEmpty(etFullAddress.getText())) {
            etFullAddress.setError("אנא הזן כתובת מלאה");
            return false;
        }
        if (TextUtils.isEmpty(etDate.getText())) {
            etDate.setError("אנא בחר תאריך");
            return false;
        }
        if (TextUtils.isEmpty(etTime.getText())) {
            etTime.setError("אנא בחר שעה");
            return false;
        }
        if (TextUtils.isEmpty(etAge.getText())) {
            Toast.makeText(this, "אנא בחר גיל", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setupAgeSpinner() {
        String[] ageRanges = {"18-20", "20-25", "25-30", "30+"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, ageRanges);
        etAge.setAdapter(adapter);
        etAge.setOnClickListener(v -> etAge.showDropDown());
    }

    private void fetchUserName(String userId) {
        FirebaseDatabase.getInstance().getReference("Users").child(userId)
                .get().addOnSuccessListener(dataSnapshot -> {
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
        etFullAddress = findViewById(R.id.etFullAddress);
        btnCreate = findViewById(R.id.btnCreate);
        ivProfile = findViewById(R.id.ivProfile);
        cvPartyImage = findViewById(R.id.cvPartyImage);
        ivSelectedPartyImage = findViewById(R.id.ivSelectedPartyImage);
        tvSubtitle = findViewById(R.id.tvSubtitle);

        mAuth = FirebaseAuth.getInstance();
        partyRef = FirebaseDatabase.getInstance().getReference("Parties");
        
        if (mAuth.getCurrentUser() != null) {
            fetchUserName(mAuth.getCurrentUser().getUid());
        }

    }
}
