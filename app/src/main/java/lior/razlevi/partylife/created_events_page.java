package lior.razlevi.partylife;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class created_events_page extends AppCompatActivity {

    private RecyclerView rvEvents;
    private PartyAdapter adapter;
    private List<Party> partyList;
    private TextView tvEmptyState;
    private MaterialButton btnCreateEvent;
    private ImageView ivProfile;
    
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_created_events_page);

        init();

        // 1. הגדרת הרשימה (RecyclerView)
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        partyList = new ArrayList<>();
        
        // יצירת ה-Adapter: כשלוחצים על מסיבה עוברים לדף העריכה (party_details_edit)
        adapter = new PartyAdapter(partyList, party -> {
            Intent intent = new Intent(created_events_page.this, party_details_edit.class);
            intent.putExtra("PARTY_ID", party.getPartyId()); // מעבירים את ה-ID כדי שנדע מה לערוך
            startActivity(intent);
        });
        rvEvents.setAdapter(adapter);

        // 2. כפתור למעבר לדף יצירת מסיבה חדשה
        btnCreateEvent.setOnClickListener(v -> {
            Intent intent = new Intent(created_events_page.this, party_creation_page.class);
            startActivity(intent);
        });

        // 3. לחיצה על אייקון הפרופיל מעבירה לדף הגדרות המשתמש
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(created_events_page.this, UserSettingActivity.class);
            startActivity(intent);
        });

        // 4. טעינת המסיבות מה-Firebase
        loadUserParties();
    }

    private void init() {
        rvEvents = findViewById(R.id.rvEvents);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        btnCreateEvent = findViewById(R.id.btnCreateEvent);
        ivProfile = findViewById(R.id.ivProfile);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Parties");
    }

    private void loadUserParties() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "משתמש לא מחובר", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserId = currentUser.getUid();

        // האזנה לשינויים ב-Database - שליפת כל המסיבות
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                partyList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Party party = dataSnapshot.getValue(Party.class);
                    
                    // בדיקה: האם המסיבה שייכת למשתמש שמחובר כרגע?
                    if (party != null && currentUserId.equals(party.getCreatorId())) {
                        partyList.add(party);
                    }
                }

                // עדכון המסך במידה ואין מסיבות
                if (partyList.isEmpty()) {
                    tvEmptyState.setVisibility(View.VISIBLE);
                    rvEvents.setVisibility(View.GONE);
                } else {
                    tvEmptyState.setVisibility(View.GONE);
                    rvEvents.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged(); // רענון הרשימה
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(created_events_page.this, "שגיאה בטעינת נתונים: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
