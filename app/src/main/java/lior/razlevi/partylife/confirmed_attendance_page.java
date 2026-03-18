package lior.razlevi.partylife;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class
confirmed_attendance_page extends AppCompatActivity {

    // רכיבי המסך
    private TextView tvComingCount, tvNotComingCount;
    private RecyclerView rvGuests;
    
    // רכיבי הנתונים
    private GuestAdapter adapter;
    private List<Guest> guestList;
    private DatabaseReference mDatabase;
    private String partyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmed_attendance_page);

        // קריאה לפעולה המאתחלת
        init();

        // הגדרת ה-RecyclerView (פעולה נוספת לסדר)
        setupRecyclerView();

        // טעינת הנתונים
        loadGuestsFromFirebase();
    }

    /**
     * פעולה המאתחלת את רכיבי המסך והחיבור ל-Firebase
     */
    private void init() {
        tvComingCount = findViewById(R.id.tvComingCount);
        tvNotComingCount = findViewById(R.id.tvNotComingCount);
        rvGuests = findViewById(R.id.rvGuests);

        // קבלת ה-ID של המסיבה
        partyId = getIntent().getStringExtra("PARTY_ID");
        
        // אתחול רשימת האורחים
        guestList = new ArrayList<>();
        
        // אתחול ההתחברות ל-Firebase (אם יש partyId)
        if (partyId != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference("Parties").child(partyId).child("Attendance");
        } else {
            Toast.makeText(this, "שגיאה: לא נמצא מזהה מסיבה", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * פעולה המגדירה את ה-RecyclerView וה-Adapter
     */
    private void setupRecyclerView() {
        rvGuests.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GuestAdapter(guestList);
        rvGuests.setAdapter(adapter);
    }

    /**
     * פעולה המושכת את הנתונים מ-Firebase
     */
    private void loadGuestsFromFirebase() {
        if (mDatabase == null) return;

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                guestList.clear();
                int countComing = 0;
                int countNotComing = 0;

                for (DataSnapshot data : snapshot.getChildren()) {
                    Guest guest = data.getValue(Guest.class);
                    if (guest != null) {
                        guestList.add(guest);

                        if ("מגיע".equals(guest.getStatus())) {
                            countComing++;
                        } else if ("לא מגיע".equals(guest.getStatus())) {
                            countNotComing++;
                        }
                    }
                }

                // עדכון ה-UI עם הפונקציה המתאימה
                updateUI(countComing, countNotComing);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(confirmed_attendance_page.this, "שגיאה בטעינת הנתונים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * פעולה המעדכנת את רכיבי הממשק (UI)
     */
    private void updateUI(int coming, int notComing) {
        tvComingCount.setText(String.valueOf(coming));
        tvNotComingCount.setText(String.valueOf(notComing));
        adapter.notifyDataSetChanged();
    }
}
