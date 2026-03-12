package lior.razlevi.partylife;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.button.MaterialButton;

public class party_details extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView ivPartyIcon;
    private TextView tvPartyName, tvDate, tvTime, tvLocation, tvAgeInfo, tvParkingInfo, tvDressCode;
    private MaterialButton btnNavigate, btnNo, btnYes, btnContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_party_details);

        init();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbar).getRootView(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        ivPartyIcon = findViewById(R.id.ivPartyIcon);
        tvPartyName = findViewById(R.id.tvPartyName);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tvLocation = findViewById(R.id.tvLocation);
        tvAgeInfo = findViewById(R.id.tvAgeInfo);
        tvParkingInfo = findViewById(R.id.tvParkingInfo);
        tvDressCode = findViewById(R.id.tvDressCode);
        btnNavigate = findViewById(R.id.btnNavigate);
        btnNo = findViewById(R.id.btnNo);
        btnYes = findViewById(R.id.btnYes);
        btnContact = findViewById(R.id.btnContact);
    }
}
