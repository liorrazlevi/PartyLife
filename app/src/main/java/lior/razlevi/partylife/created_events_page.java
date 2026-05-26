package lior.razlevi.partylife;

import android.app.AlertDialog;
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

/**
 * דף המציג את כל המסיבות שהמשתמש הנוכחי יצר.
 *  מאפשר צפייה ברשימה, מעבר לעריכה, יצירת מסיבה חדשה או מחיקת מסיבה קיימת.
 */
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

        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        partyList = new ArrayList<>();

        // הגדרת האדפטר עם פונקציית לחיצה למעבר לעריכת מסיבה
        adapter = new PartyAdapter(partyList, party -> {
            Intent intent = new Intent(created_events_page.this, party_details_edit.class);
            intent.putExtra("PARTY_ID", party.getPartyId());
            startActivity(intent);
        });

        // הפעלת אפשרות המחיקה בדף זה
        adapter.enableDeletion(party -> {
            showDeleteDialog(party);
        });

        rvEvents.setAdapter(adapter);

        // מעבר לדף יצירת מסיבה חדשה
        btnCreateEvent.setOnClickListener(v -> {
            Intent intent = new Intent(created_events_page.this, party_creation_page.class);
            startActivity(intent);
        });

        // מעבר לדף עריכת פרופיל המשתמש
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(created_events_page.this, UserSettingActivity.class);
            startActivity(intent);
        });

        loadUserParties();
    }

    /**
     * הצגת דיאלוג אישור לפני מחיקת מסיבה.
     */
    private void showDeleteDialog(Party party) {
        new AlertDialog.Builder(this)
                .setTitle("מחיקת מסיבה")
                .setMessage("האם את בטוחה שברצונך למחוק את \"" + party.getName() + "\"?")
                .setPositiveButton("מחק", (dialog, which) -> {
                    deleteParty(party);
                })
                .setNegativeButton("ביטול", null)
                .show();
    }

    /**
     * מחיקת מסיבה מה-Firebase Database וניקוי נתוני אישורי ההגעה שלה.
     */
    private void deleteParty(Party party) {
        // מחיקת המסיבה מהענף Parties
        mDatabase.child(party.getPartyId()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // מחיקת אישורי הגעה קשורים כדי לא להשאיר מידע מיותר
                FirebaseDatabase.getInstance().getReference("Attendance").child(party.getPartyId()).removeValue();
                Toast.makeText(this, "המסיבה נמחקה", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "שגיאה במחיקה", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        rvEvents = findViewById(R.id.rvEvents);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        btnCreateEvent = findViewById(R.id.btnCreateEvent);
        ivProfile = findViewById(R.id.ivProfile);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Parties");
    }

    /**
     * שליפת כל המסיבות מהדאטה-בייס וסינון אלו שנוצרו על ידי המשתמש המחובר בלבד.
     */
    private void loadUserParties() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        String currentUserId = currentUser.getUid();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                partyList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Party party = dataSnapshot.getValue(Party.class);
                    if (party != null && currentUserId.equals(party.getCreatorId())) {
                        partyList.add(party);
                    }
                }

                if (partyList.isEmpty()) {
                    tvEmptyState.setVisibility(View.VISIBLE);
                    rvEvents.setVisibility(View.GONE);
                } else {
                    tvEmptyState.setVisibility(View.GONE);
                    rvEvents.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(created_events_page.this, "שגיאה בטעינה", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
