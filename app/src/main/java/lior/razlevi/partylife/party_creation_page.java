package lior.razlevi.partylife;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class party_creation_page extends AppCompatActivity {

    private TextInputEditText etPartyName, etLocation, etDate, etTime, etAge, etDressCode;
    private MaterialButton btnCreate;
    private ImageView ivProfile;
    private TextView tvTitle, tvSubtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_party_creation_page);

        init();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);
    }
}
