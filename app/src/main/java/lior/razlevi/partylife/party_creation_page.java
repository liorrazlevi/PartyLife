package lior.razlevi.partylife;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    private StorageReference storageReference;

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
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

    private void uploadImageAndCreateParty() {
        if (imageUri != null) {
            String fileName = UUID.randomUUID().toString();
            StorageReference ref = storageReference.child("party_images/" + fileName);

            ref.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        savePartyToDatabase(uri.toString());
                    }))
                    .addOnFailureListener(e -> Toast.makeText(this, "שגיאה בהעלאת תמונה: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            savePartyToDatabase(""); 
        }
    }

    private void savePartyToDatabase(String imageUrl) {
        if (mAuth.getCurrentUser() == null) return;
        String currentUserId = mAuth.getCurrentUser().getUid();

        // יצירת מזהה ייחודי ב-Firebase
        DatabaseReference newPartyRef = mDatabase.push();
        String partyId = newPartyRef.getKey(); // זה ה-ID הייחודי של המסיבה

        // יצירת אובייקט מסיבה (השתמשתי ב-Map כדי לוודא שכל השדות נשמרים)
        HashMap<String, Object> partyMap = new HashMap<>();
        partyMap.put("partyId", partyId); // שמירה של ה-ID בתוך האובייקט
        partyMap.put("name", etPartyName.getText().toString().trim());
        partyMap.put("location", etLocation.getText().toString().trim());
        partyMap.put("date", etDate.getText().toString().trim());
        partyMap.put("time", etTime.getText().toString().trim());
        partyMap.put("age", etAge.getText().toString().trim());
        partyMap.put("dressCode", etDressCode.getText().toString().trim());
        partyMap.put("phone", etPhone.getText().toString().trim());
        partyMap.put("parking", etParking.getText().toString().trim());
        partyMap.put("imageUrl", imageUrl);
        partyMap.put("creatorId", currentUserId);

        if (partyId != null) {
            newPartyRef.setValue(partyMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "המסיבה נוצרה בהצלחה!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "שגיאה: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
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
        if (TextUtils.isEmpty(etDate.getText())) {
            etDate.setError("אנא בחר תאריך");
            return false;
        }
        if (TextUtils.isEmpty(etTime.getText())) {
            etTime.setError("אנא בחר שעה");
            return false;
        }
        if (TextUtils.isEmpty(etAge.getText())) {
            Toast.makeText(this, "אנא בחר טווח גילאים", Toast.LENGTH_SHORT).show();
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
        mDatabase = FirebaseDatabase.getInstance().getReference("Parties");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        
        if (mAuth.getCurrentUser() != null) {
            fetchUserName(mAuth.getCurrentUser().getUid());
        }
    }
}
