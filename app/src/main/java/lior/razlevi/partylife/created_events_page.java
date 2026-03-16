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

        // אתחול רכיבים מה-XML
        rvEvents = findViewById(R.id.rvEvents);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        btnCreateEvent = findViewById(R.id.btnCreateEvent);
        ivProfile = findViewById(R.id.ivProfile);

        // אתחול Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Parties");

        // הגדרת הרשימה (RecyclerView)
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        partyList = new ArrayList<>();
        
        // יצירת ה-Adapter עם פונקציית לחיצה שעוברת לדף פרטי המסיבה
        adapter = new PartyAdapter(partyList, party -> {
            Intent intent = new Intent(created_events_page.this, party_details.class);
            intent.putExtra("PARTY_ID", party.getPartyId()); // שולחים את ה-ID של המסיבה שנבחרה
            startActivity(intent);
        });
        rvEvents.setAdapter(adapter);

        // כפתור ליצירת מסיבה חדשה
        btnCreateEvent.setOnClickListener(v -> {
            Intent intent = new Intent(created_events_page.this, party_creation_page.class);
            startActivity(intent);
        });

        // כפתור פרופיל - מעבר ל-UserSettingActivity
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(created_events_page.this, UserSettingActivity.class);
            startActivity(intent);
        });

        // משיכת נתונים מה-Firebase
        loadUserParties();
    }

    private void loadUserParties() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        String currentUserId = currentUser.getUid();

        // מאזין לשינויים ב-Database
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                partyList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Party party = dataSnapshot.getValue(Party.class);
                    
                    // מציג רק מסיבות שהמשתמש הנוכחי יצר
                    if (party != null && currentUserId.equals(party.getCreatorId())) {
                        partyList.add(party);
                    }
                }

                // עדכון התצוגה לפי כמות המסיבות
                if (partyList.isEmpty()) {
                    tvEmptyState.setVisibility(View.VISIBLE);
                    rvEvents.setVisibility(View.GONE);
                } else {
                    tvEmptyState.setVisibility(View.GONE);
                    rvEvents.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(created_events_page.this, "שגיאה בטעינת נתונים", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
