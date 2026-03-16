package lior.razlevi.partylife;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
    private AutoCompleteTextView inputLocation;
    private EditText inputDate;
    private EditText inputTime;
    private AutoCompleteTextView inputAge;
    private com.google.android.material.button.MaterialButton btnSearch;
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

        btnSearch.setOnClickListener(v -> {
            performSearch();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.partySearch), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupLocationSpinner() {
        String[] regions = {"דרום", "צפון", "מרכז"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, regions);
        inputLocation.setAdapter(adapter);
        inputLocation.setOnClickListener(v -> inputLocation.showDropDown());
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
        // איסוף הנתונים מהשדות
        String location = inputLocation.getText().toString();
        String date = inputDate.getText().toString();
        String age = inputAge.getText().toString();

        // מעבר ל-Activity החדש של התוצאות
        Intent intent = new Intent(this, PartyResultsActivity.class);
        intent.putExtra("LOCATION", location);
        intent.putExtra("DATE", date);
        intent.putExtra("AGE", age);
        startActivity(intent);
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
