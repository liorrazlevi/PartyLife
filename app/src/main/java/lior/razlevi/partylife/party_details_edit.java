package lior.razlevi.partylife;

import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class party_details_edit extends AppCompatActivity {

    private TextInputEditText etLocation, etDate, etTime, etParking, etDressCodeEdit, etPhone;
    private AutoCompleteTextView inputAge;
    private MaterialButton btnSaveChanges, btnViewEvents;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_party_details_edit);

        init();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.partyEdit), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void init() {
        etLocation = findViewById(R.id.etLocation);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etParking = findViewById(R.id.etParking);
        etDressCodeEdit = findViewById(R.id.etDressCodeEdit);
        etPhone = findViewById(R.id.etPhone);
        inputAge = findViewById(R.id.inputAge);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnViewEvents = findViewById(R.id.btnViewEvents);
        tvTitle = findViewById(R.id.tvTitle);
    }
}
