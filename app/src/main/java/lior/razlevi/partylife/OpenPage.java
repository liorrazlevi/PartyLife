package lior.razlevi.partylife;



import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OpenPage extends AppCompatActivity {
 private CardView cvCreateParty;
 private CardView cvPlanParty;
 private CardView cvMyParties;
 private ImageView ivSettings;
 private TextView tvWelcome;
    private FirebaseDatabase database;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_open_page);
        init();
        getFullName();


        cvCreateParty.setOnClickListener(view -> {
            startActivity(new Intent(this, party_creation_page.class));
        });
        cvPlanParty.setOnClickListener(view -> {
            startActivity(new Intent(this, PartySearchPage.class));
        });
        ivSettings.setOnClickListener(view -> {
            startActivity(new Intent(this, UserSettingActivity.class));
        });

        cvMyParties.setOnClickListener(view -> {
                    startActivity(new Intent(this, created_events_page.class));


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
        cvMyParties = findViewById(R.id.cvMyParties);
        ivSettings = findViewById(R.id.ivSettings);
        tvWelcome=findViewById(R.id.tvWelcome);
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("Users");



    }
    public void getFullName(){

        FirebaseUser firebaseUser = Auth.getCurrentUser();
        if (firebaseUser == null) return;
        String uid = firebaseUser.getUid();

        // פנייה לנתיב של המשתמש הספציפי ב-Database
        userRef.child(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                // שליפת השם המלא
                 String fullName = task.getResult().child("fullName").getValue(String.class);
                  tvWelcome.setText("איזה כיף שהגעת, "+ fullName);
            }
        });




    }
}


