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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class party_creation_page extends AppCompatActivity {

    private TextInputEditText etPartyName, etLocation, etDate, etTime, etDressCode;
    private AutoCompleteTextView etAge;
    private MaterialButton btnCreate;
    private ImageView ivProfile, ivSelectedPartyImage;
    private MaterialCardView cvPartyImage;
    private TextView tvTitle, tvSubtitle;

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
        // תאריך
        etDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(etDate, "dd/MM/yyyy");
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // שעה
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
        // כאן בהמשך נוסיף את הקוד ששומר ל-Firebase
        Toast.makeText(this, "המסיבה נוצרה בהצלחה!", Toast.LENGTH_SHORT).show();
        finish(); // סגירת הדף וחזרה אחורה
    }

    private void setupAgeSpinner() {
        String[] ageRanges = {"18-20", "20-25", "25-30", "30+"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, ageRanges);
        etAge.setAdapter(adapter);
        etAge.setOnClickListener(v -> etAge.showDropDown());
    }

    private void init() {
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
    }
}
