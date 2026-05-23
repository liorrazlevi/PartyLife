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

    private TextInputEditText etPartyName, etDate, etTime, etDressCode, etPhone, etParking ,etFullAddress;
    private AutoCompleteTextView etAge, etLocation;
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
        setupTextWatchers();
       setupCity();
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

    private void setupPickers() {etDate.setOnClickListener(v -> {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(etDate, "dd/MM/yyyy");

            // הסרת השגיאה ברגע שנבחר תאריך
            etDate.setError(null);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    });

        etTime.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                updateLabel(etTime, "HH:mm");

                // הסרת השגיאה ברגע שנבחרה שעה
                etTime.setError(null);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });
    }

    private void updateLabel(TextInputEditText editText, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        editText.setText(sdf.format(calendar.getTime()));
    }

    private boolean validateInputs() {
        String partyName = etPartyName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String fullAddress = etFullAddress.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String dressCode = etDressCode.getText().toString().trim();
        String parking = etParking.getText().toString().trim();

        if (TextUtils.isEmpty(partyName)) {
            etPartyName.setError("אנא הזן שם מסיבה");
            etPartyName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(location)) {
            etLocation.setError("אנא בחר עיר");
            etLocation.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(fullAddress)) {
            etFullAddress.setError("אנא הזן כתובת מדויקת");
            etFullAddress.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(date)) {
            etDate.setError("חובה לבחור תאריך");
            Toast.makeText(this, "אנא בחר תאריך למסיבה", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(time)) {
            etTime.setError("חובה לבחור שעה");
            Toast.makeText(this, "אנא בחר שעת התחלה", Toast.LENGTH_SHORT).show();
            return false;
        }

        // --- תיקון בדיקת זמן עבר (איפוס שניות ומילי-שניות) ---
        Calendar now = Calendar.getInstance();

        // יצירת עותקים להשוואה נקייה ללא שניות
        Calendar selectedTime = (Calendar) calendar.clone();
        selectedTime.set(Calendar.SECOND, 0);
        selectedTime.set(Calendar.MILLISECOND, 0);

        Calendar currentTime = (Calendar) now.clone();
        currentTime.set(Calendar.SECOND, 0);
        currentTime.set(Calendar.MILLISECOND, 0);

        if (selectedTime.before(currentTime)) {
            Toast.makeText(this, "לא ניתן ליצור מסיבה בזמן שכבר עבר", Toast.LENGTH_SHORT).show();
            return false;
        }
        // --------------------------------------------------

        if (TextUtils.isEmpty(age)) {
           // etAge.setError("אנא בחר טווח גילים");
            etAge.requestFocus();
            Toast.makeText(this, "חובה לבחור טווח גילים", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(dressCode)) {
            etDressCode.setError("אנא ציין קוד לבוש");
            etDressCode.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(parking)) {
            etParking.setError("אנא פרט על מצב החניה");
            etParking.requestFocus();
            return false;
        }

        // בדיקת טלפון מופרדת
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("חובה להזין מספר טלפון");
            etPhone.requestFocus();
            return false;
        }
        if (!phone.startsWith("05")) {
            etPhone.setError("מספר טלפון חייב להתחיל ב-05");
            etPhone.requestFocus();
            return false;
        }
        if (phone.length() != 10) {
            etPhone.setError("מספר טלפון חייב להכיל בדיוק 10 ספרות");
            etPhone.requestFocus();
            return false;
        }

        return true;
    }

    private void setupAgeSpinner() {
        String[] ageRanges = {"18-20", "20-25", "25-30", "30+"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, ageRanges);
        etAge.setAdapter(adapter);

        // זה החלק שמוודא שהשגיאה תיעלם מיד בבחירה
        etAge.setOnItemClickListener((parent, view, position, id) -> {
            etAge.setError(null);
        });

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

    private void setupTextWatchers() {
        etPhone.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etPhone.setError(null); // הסרת השגיאה בזמן הקלדה
            }
            public void afterTextChanged(android.text.Editable s) {}
        });

        // אפשר להוסיף כאן TextWatcher דומה גם ל-etPartyName ו-etFullAddress
    }

    private  void setupCity(){

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                party_creation_page.this,
                android.R.layout.simple_list_item_1, // עיצוב שורת הרשימה (ברירת מחדל של אנדרואיד)
                MainActivity.cities
        );

        etLocation.setAdapter(adapter);

        // הגדרה: אחרי כמה תווים שהמשתמש מקליד תוצג הרשימה?
        etLocation.setThreshold(2);


    }
}










