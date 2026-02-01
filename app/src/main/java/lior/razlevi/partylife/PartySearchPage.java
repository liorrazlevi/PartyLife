package lior.razlevi.partylife;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PartySearchPage extends AppCompatActivity {
    private AutoCompleteTextView inputLocation,inputAge;
    private EditText inputDate;
    private EditText inputTime;

    private AppCompatButton btnSearch;
    private int selectedHour;
    private int selectedMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_party_search_page);
        init();
        inputTime.setOnClickListener(v -> {
            OpenTimePicker(v);


        });
        String[] regions = {"דרום", "צפון", "מרכז"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, regions);
        inputLocation.setAdapter(adapter);
        inputLocation.setOnClickListener(v -> {
            String selection = (String) inputLocation.getText().toString();
            inputLocation.showDropDown();
        });
        // 1. הרשימה שלך
        String[] ageRange = {"18-25", "25-35", "35-45"};

// 2. מציאת הרכיב (שימי לב לסוג: AutoCompleteTextView)
        AutoCompleteTextView inputAge = findViewById(R.id.inputAge);

// 3. יצירת האדפטר (המקשר בין הרשימה למראה שלה)
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, ageRange);

        inputAge.setAdapter(adapter2);

// 4. מה קורה כשלוחצים? הרשימה נפתחת מתחת לשדה
        inputAge.setOnClickListener(v -> {
            inputAge.showDropDown();
        });

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String date = sdf.format(new Date());
        inputDate.setText(date);
        inputDate.setOnClickListener(v -> {
            OpenDatePicker(v);
        });
        btnSearch.setOnClickListener(v -> {


        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.partySearch), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void init() {
        inputAge = findViewById(R.id.inputAge);
        inputTime = findViewById(R.id.inputTime);
        inputLocation = findViewById(R.id.inputLocation);
        inputDate = findViewById(R.id.inputDate);
        btnSearch = findViewById(R.id.btnSearch);
    }

    public void OpenDatePicker(View v) {
        final Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        int currentMonth = c.get(Calendar.MONTH);
        int currentDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(PartySearchPage.this, (view, year, month, dayOfMonth) -> {
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
        selectedHour = currentHour;
        selectedMinute = currentMinute;

        TimePickerDialog timePickerDialog = new TimePickerDialog(PartySearchPage.this, (view, hourOfDay, minute) -> {
            this.selectedHour = hourOfDay;
            this.selectedMinute = minute;
            inputTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
        }, selectedHour, selectedMinute, true);
        timePickerDialog.show();
    }
}
