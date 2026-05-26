package lior.razlevi.partylife;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


/**
 * דף עריכת פרטי מסיבה (צד המארגן).
 *   מאפשר עדכון נתונים קיימים, שינוי תמונת האירוע ומעבר לרשימת האורחים.
 */
public class party_details_edit extends AppCompatActivity {

    private TextInputEditText etLocation, etDate, etTime, etParking, etDressCodeEdit, etPhone, etFullAddress;
    private AutoCompleteTextView inputAge;
    private MaterialButton btnSaveChanges, btnViewEvents;
    private TextView tvTitle;
    private ImageView ivSelectedPartyImage;
    private MaterialCardView cvPartyImage;
    private String partyId;
    private DatabaseReference mDatabase;
    private Uri imageUri;
    private String currentImageString;
    private Bitmap currentImageBitMap;
    private final Calendar calendar = Calendar.getInstance();
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_details_edit);

        init();
        setupAgeSpinner();
        setupPickers();
        setupGalleryLauncher();

        // קבלת מזהה המסיבה לעריכה
        partyId = getIntent().getStringExtra("PARTY_ID");
        if (partyId == null) {
            Toast.makeText(this, "שגיאה: לא נמצא מזהה מסיבה", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mDatabase = FirebaseDatabase.getInstance().getReference("Parties").child(partyId);

        loadPartyDetails();

        // שינוי תמונה
        cvPartyImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });

        // שמירת שינויים שנעשו
        btnSaveChanges.setOnClickListener(v -> {
            if (validateInputs()) { // רק אם הכל תקין נמשיך לשמירה
                if (imageUri != null) {
                    convertUriAndSave();
                } else {
                    saveChanges(currentImageString);
                }
            }
        });

        setupTextWatchers();

        // מעבר לדף רשימת האורחים (אישורי הגעה)
        btnViewEvents.setOnClickListener(v -> {
            Intent intent = new Intent(party_details_edit.this, confirmed_attendance_page.class);
            intent.putExtra("PARTY_ID", partyId);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.partyEdit), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void init() {
        etLocation = findViewById(R.id.etLocation);
        etFullAddress = findViewById(R.id.etFullAddress); // השדה החדש
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etParking = findViewById(R.id.etParking);
        etDressCodeEdit = findViewById(R.id.etDressCodeEdit);
        etPhone = findViewById(R.id.etPhone);
        inputAge = findViewById(R.id.inputAge);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnViewEvents = findViewById(R.id.btnViewEvents);
        tvTitle = findViewById(R.id.tvTitle);
        ivSelectedPartyImage = findViewById(R.id.ivSelectedPartyImageE);
        cvPartyImage = findViewById(R.id.cvPartyImage);
    }

    /**
     * טעינת פרטי המסיבה מה-Database ומילוי השדות בממשק.
     */
    private void loadPartyDetails() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Party party = snapshot.getValue(Party.class);
                if (party != null) {
                    tvTitle.setText("עריכת: " + party.getName());
                    etLocation.setText(party.getLocation());
                    etFullAddress.setText(party.getFullAddress());
                    etDate.setText(party.getDate());
                    etTime.setText(party.getTime());
                    etParking.setText(party.getParking());
                    etDressCodeEdit.setText(party.getDressCode());
                    etPhone.setText(party.getPhone());
                    inputAge.setText(party.getAge(), false);

                    currentImageString = party.getImageString();
                    currentImageBitMap = party.bringPartyImage();

                    //  עדכון ה-calendar בזמן הטעינה
                    try {
                        String[] dateParts = party.getDate().split("/");
                        String[] timeParts = party.getTime().split(":");
                        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateParts[0]));
                        calendar.set(Calendar.MONTH, Integer.parseInt(dateParts[1]) - 1);
                        calendar.set(Calendar.YEAR, Integer.parseInt(dateParts[2]));
                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
                        calendar.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // הצגת תמונה
                    ivSelectedPartyImage.setAlpha(1.0f);
                    ivSelectedPartyImage.setVisibility(android.view.View.VISIBLE);
                 //   ivSelectedPartyImage.bringToFront();
                    if (currentImageBitMap != null) {
                        ivSelectedPartyImage.setImageBitmap(currentImageBitMap);
                    } else if (currentImageString != null && !currentImageString.isEmpty()) {
                        Glide.with(party_details_edit.this).load(currentImageString).into(ivSelectedPartyImage);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
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
                            ivSelectedPartyImage.setImageBitmap(bitmap);
                            ivSelectedPartyImage.setAlpha(1.0f);
                         //   ivSelectedPartyImage.bringToFront();
                        } catch (Exception e) {
                            Toast.makeText(this, "שגיאה בטעינת התמונה", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    /**
     *  הופכת את התמונה שנבחרה מהגלריה למחרוזת טקסט (Base64)
     *   כדי שניתן יהיה לשמור אותה ב-Realtime Database, ולאחר מכן מבצעת את השמירה.
     */
    private void convertUriAndSave() {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] imageBytes = baos.toByteArray();
            
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            saveChanges(encodedImage);
        } catch (Exception e) {
            Toast.makeText(this, "שגיאה בעיבוד התמונה", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * שמירת השינויים ב-Firebase Database באמצעות Map של עדכונים.
     */
    private void saveChanges(String imageString) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("location", etLocation.getText().toString().trim());
        updates.put("fullAddress", etFullAddress.getText().toString().trim()); // <--- הוספת השורה הזו
        updates.put("date", etDate.getText().toString().trim());
        updates.put("time", etTime.getText().toString().trim());
        updates.put("parking", etParking.getText().toString().trim());
        updates.put("dressCode", etDressCodeEdit.getText().toString().trim());
        updates.put("phone", etPhone.getText().toString().trim());
        updates.put("age", inputAge.getText().toString().trim());
        updates.put("imageString", imageString);

        mDatabase.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "השינויים נשמרו בהצלחה!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "שגיאה בשמירה: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setupPickers() {
        etDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                // עדכון האובייקט calendar
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                etDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                etDate.setError(null);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            // חסימת תאריכים שעברו
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

            datePickerDialog.show();
        });

        etTime.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                // עדכון האובייקט calendar
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                etTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                etTime.setError(null);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });
    }

    private void setupAgeSpinner() {
        String[] ageRanges = {"18-20", "20-25", "25-30", "30+"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, ageRanges);
        inputAge.setAdapter(adapter);
        inputAge.setOnClickListener(v -> inputAge.showDropDown());
    }

    private boolean validateInputs() {
        String location = etLocation.getText().toString().trim();
        String fullAddress = etFullAddress.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String age = inputAge.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String dressCode = etDressCodeEdit.getText().toString().trim();
        String parking = etParking.getText().toString().trim();

        if (location.isEmpty()) { etLocation.setError("אנא בחר עיר"); etLocation.requestFocus(); return false; }
        if (fullAddress.isEmpty()) { etFullAddress.setError("אנא הזן כתובת מדויקת"); etFullAddress.requestFocus(); return false; }
        if (date.isEmpty()) { etDate.setError("חובה לבחור תאריך"); return false; }
        if (time.isEmpty()) { etTime.setError("חובה לבחור שעה"); return false; }

        // בדיקת זמן עבר (עם איפוס שניות למניעת שגיאות של דקה שחלפה)
        Calendar now = Calendar.getInstance();
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);

        // יצירת עותק לבדיקה כדי לא להרוס את התאריך שנבחר
        Calendar selectedTime = (Calendar) calendar.clone();
        selectedTime.set(Calendar.SECOND, 0);
        selectedTime.set(Calendar.MILLISECOND, 0);

        if (selectedTime.before(now)) {
            Toast.makeText(this, "לא ניתן לקבוע מסיבה לזמן שכבר עבר", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (age.isEmpty()) {
            inputAge.setError("אנא בחר טווח גילים");
            inputAge.requestFocus();
            return false;
        }

        if (dressCode.isEmpty()) { etDressCodeEdit.setError("אנא ציין קוד לבוש"); etDressCodeEdit.requestFocus(); return false; }
        if (parking.isEmpty()) { etParking.setError("אנא פרט על מצב החניה"); etParking.requestFocus(); return false; }

        // בדיקת טלפון (10 ספרות, מתחיל ב-05)
        if (phone.isEmpty()) { etPhone.setError("חובה להזין מספר טלפון"); etPhone.requestFocus(); return false; }
        if (!phone.startsWith("05") || phone.length() != 10) {
            etPhone.setError("מספר טלפון חייב להתחיל ב-05 ולהכיל 10 ספרות");
            etPhone.requestFocus();
            return false;
        }

        return true;
    }

    private void setupTextWatchers() {
        TextInputEditText[] fields = {etLocation, etFullAddress, etPhone, etDressCodeEdit, etParking};
        for (TextInputEditText field : fields) {
            field.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    field.setError(null); // מסיר את השגיאה כשהמשתמש מתחיל לתקן
                }

                @Override
                public void afterTextChanged(android.text.Editable s) {}
            });
        }
    }

}
