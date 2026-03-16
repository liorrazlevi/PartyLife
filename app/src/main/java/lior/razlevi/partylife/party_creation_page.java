package lior.razlevi.partylife;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class party_creation_page extends AppCompatActivity {

    private TextInputEditText etPartyName, etLocation, etDate, etTime, etDressCode;
    private AutoCompleteTextView etAge;
    private MaterialButton btnCreate;
    private ImageView ivProfile, ivSelectedPartyImage;
    private MaterialCardView cvPartyImage;
    private TextView tvTitle, tvSubtitle;

    // משתני Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_party_creation_page);

        init();
        setupAgeSpinner();
        setupPickers();

        btnCreate.setOnClickListener(v -> {
            if (validateInputs()) {
                createParty();
            }
        });

        cvPartyImage.setOnClickListener(v -> {
            // TODO: פתיחת גלריה לבחירת תמונה
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupPickers() {
        // בחירת תאריך
        etDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(etDate, "dd/MM/yyyy");
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // בחירת שעה
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
            etPartyName.setError("אנא הזן שם למסיבה");
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

    private void createParty() {
        // 1. איסוף הנתונים מהטפסים
        String name = etPartyName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String dressCode = etDressCode.getText().toString().trim();

        // בדיקה אם המשתמש מחובר (כדי לקבל את ה-UID שלו)
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "שגיאה: משתמש לא מחובר", Toast.LENGTH_SHORT).show();
            return;
        }
        String currentUserId = mAuth.getCurrentUser().getUid();

        // 2. יצירת מזהה ייחודי למסיבה בתוך ענף Parties
        String partyId = mDatabase.push().getKey();

        // 3. יצירת HashMap לשמירת הנתונים (השיטה שבה השתמשת ברישום)
        HashMap<String, Object> partyMap = new HashMap<>();
        partyMap.put("partyId", partyId);
        partyMap.put("name", name);
        partyMap.put("location", location);
        partyMap.put("date", date);
        partyMap.put("time", time);
        partyMap.put("age", age);
        partyMap.put("dressCode", dressCode);
        partyMap.put("creatorId", currentUserId); // מקשר את המסיבה ליוצר שלה

        // 4. שמירה ב-Firebase Database
        if (partyId != null) {
            mDatabase.child(partyId).setValue(partyMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(party_creation_page.this, "המסיבה נוצרה ונשמרה בהצלחה!", Toast.LENGTH_SHORT).show();
                    finish(); // סגירת הדף וחזרה אחורה
                } else {
                    Toast.makeText(party_creation_page.this, "שגיאה בשמירה: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupAgeSpinner() {
        String[] ageRanges = {"18-20", "20-25", "25-30", "30+"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, ageRanges);
        etAge.setAdapter(adapter);
        etAge.setOnClickListener(v -> etAge.showDropDown());
    }

    private void init() {
        // חיבור רכיבי ה-UI
        etPartyName = findViewById(R.id.etPartyName);
        etLocation = findViewById(R.id.etLocation);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etAge = findViewById(R.id.etAge);
        etDressCode = findViewById(R.id.etDressCode);
        btnCreate = findViewById(R.id.btnCreate);
        ivProfile = findViewById(R.id.ivProfile);
        cvPartyImage = findViewById(R.id.cvPartyImage);
        ivSelectedPartyImage = findViewById(R.id.ivSelectedPartyImage);
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);

        // אתחול Firebase
        mAuth = FirebaseAuth.getInstance();
        // יצירת קישור לענף Parties ב-Database
        mDatabase = FirebaseDatabase.getInstance().getReference("Parties");
    }
}