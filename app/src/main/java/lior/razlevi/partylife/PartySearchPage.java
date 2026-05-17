package lior.razlevi.partylife;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PartySearchPage extends AppCompatActivity {
    private AutoCompleteTextView inputLocation;
    private EditText inputDate;
    private EditText inputTime;
    private AutoCompleteTextView inputAge;
    private com.google.android.material.button.MaterialButton btnSearch;
    private CardView cvProfile;
    private int selectedHour;
    private int selectedMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_party_search_page);
        init();

        setupLocationSpinner();
        setupAgeSpinner();
        setupDateTimePickers();

        cvProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, UserSettingActivity.class));
        });

        btnSearch.setOnClickListener(v -> {
            if (validateFields()) {
                performSearch();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.partySearch), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private boolean validateFields() {
        if (inputLocation.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "נא להזין עיר לחיפוש", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (inputDate.getText().toString().isEmpty()) {
            Toast.makeText(this, "נא לבחור תאריך", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (inputTime.getText().toString().isEmpty()) {
            Toast.makeText(this, "נא לבחור שעה", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (inputAge.getText().toString().isEmpty()) {
            Toast.makeText(this, "נא לבחור טווח גילאים", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setupLocationSpinner() {
        //1. רשימת ערים נפוצות בישראל (תוכלי להוסיף עוד כרצונך)
        String[] cities = {
                "תל אביב", "ירושלים", "חיפה", "ראשון לציון", "פתח תקווה",
                "אשדוד", "נתניה", "באר שבע", "בני ברק", "חולון",
                "רמת גן", "רחובות", "אשקלון", "בת ים", "בית שמש",
                "כפר סבא", "הרצליה", "חדרה", "מודיעין", "רעננה",
                "יבנה", "רמלה", "נהריה", "מודיעין עילית"
        };

        // 2. יצירת האדאפטר
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, cities);

        inputLocation.setAdapter(adapter);

        // 3. חשוב: הסרנו את ה-showDropDown בלחיצה כדי לאפשר למשתמש להקליד בחופשיות
        // האפשרויות יקפצו לבד כשהוא יתחיל להקליד אותיות.
        inputLocation.setOnClickListener(null);
        // מאפשר לפתוח את הרשימה המלאה גם בלחיצה, לא רק בהקלדה
        // inputLocation.setOnClickListener(v -> inputLocation.showDropDown());
    }

    private void setupAgeSpinner() {
        String[] ageRanges = {"18-20", "20-25", "25-30", "30+"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, ageRanges);
        inputAge.setAdapter(adapter);
        inputAge.setOnClickListener(v -> inputAge.showDropDown());
    }

    private void setupDateTimePickers() {
        inputTime.setOnClickListener(v -> OpenTimePicker(v));
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        inputDate.setText(sdf.format(new Date()));
        inputDate.setOnClickListener(v -> OpenDatePicker(v));
    }

    private void performSearch() {
        String location = inputLocation.getText().toString().trim();
        String date = inputDate.getText().toString();
        String time = inputTime.getText().toString();
        String age = inputAge.getText().toString();

        Intent intent = new Intent(this, PartyResultsActivity.class);
        intent.putExtra("LOCATION", location);
        intent.putExtra("DATE", date);
        intent.putExtra("TIME", time); // הוספנו את הזמן
        intent.putExtra("AGE", age);
        startActivity(intent);
    }

    public void init() {
        inputAge = findViewById(R.id.inputAge);
        inputTime = findViewById(R.id.inputTime);
        inputLocation = findViewById(R.id.inputLocation);
        inputDate = findViewById(R.id.inputDate);
        btnSearch = findViewById(R.id.btnSearch);
        cvProfile = findViewById(R.id.cvProfile);
    }

    public void OpenDatePicker(View v) {
        final Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        int currentMonth = c.get(Calendar.MONTH);
        int currentDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            inputDate.setText(sdf.format(selectedDate.getTime()));
        }, currentYear, currentMonth, currentDay);
        datePickerDialog.show();
    }

    public void OpenTimePicker(View v) {
        final Calendar c = Calendar.getInstance();
        int currentHour = c.get(Calendar.HOUR_OF_DAY);
        int currentMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            this.selectedHour = hourOfDay;
            this.selectedMinute = minute;
            inputTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
        }, currentHour, currentMinute, true);
        timePickerDialog.show();
    }
}
