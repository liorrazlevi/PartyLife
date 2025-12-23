package lior.razlevi.partylife;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OpenPage extends AppCompatActivity {
 private CardView cvCreateParty;
 private CardView cvPlanParty;
 private ImageView ivSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_open_page);
        init();
        cvCreateParty.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
        });
        cvPlanParty.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
        });
        ivSettings.setOnClickListener(view -> {
            startActivity(new Intent(this, UserSettingActivity.class));
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.openpage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public  void  init(){
        cvCreateParty = findViewById(R.id.cvCreateParty);
        cvPlanParty = findViewById(R.id.cvPlanParty);
        ivSettings = findViewById(R.id.ivSettings);
    }
}


