package lior.razlevi.partylife;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PartyResultsActivity extends AppCompatActivity implements PartyAdapter.OnPartyClickListener {

    private RecyclerView rvPartyResults;
    private TextView tvResultsCount;
    private LinearLayout llEmptyState;
    private PartyAdapter partyAdapter;
    private List<Party> partyList;
    
    private DatabaseReference mDatabase;
    private String searchLocation, searchDate, searchAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_party_results);

        init();

        // 1. קבלת נתוני החיפוש מה-Intent
        searchLocation = getIntent().getStringExtra("LOCATION");
        searchDate = getIntent().getStringExtra("DATE");
        searchAge = getIntent().getStringExtra("AGE");

        // 2. אתחול רשימה ואדפטר
        partyList = new ArrayList<>();
        partyAdapter = new PartyAdapter(partyList, this);
        rvPartyResults.setLayoutManager(new LinearLayoutManager(this));
        rvPartyResults.setAdapter(partyAdapter);

        // 3. התחברות ל-Firebase וטעינת נתונים
        mDatabase = FirebaseDatabase.getInstance().getReference("Parties");
        loadFilteredParties();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void init() {
        rvPartyResults = findViewById(R.id.rvPartyResults);
        tvResultsCount = findViewById(R.id.tvResultsCount);
        llEmptyState = findViewById(R.id.llEmptyState);
    }

    private void loadFilteredParties() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                partyList.clear();
                
                for (DataSnapshot data : snapshot.getChildren()) {
                    Party party = data.getValue(Party.class);
                    
                    if (party != null) {
                        // לוגיקת הסינון: בודקים אם המסיבה מתאימה לכל הקריטריונים
                        boolean matchesLocation = party.getLocation() != null && party.getLocation().contains(searchLocation);
                        boolean matchesDate = searchDate.equals(party.getDate());
                        boolean matchesAge = searchAge.equals(party.getAge());

                        if (matchesLocation && matchesDate && matchesAge) {
                            partyList.add(party);
                        }
                    }
                }
                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PartyResultsActivity.this, "שגיאה בטעינת הנתונים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        if (partyList.isEmpty()) {
            llEmptyState.setVisibility(View.VISIBLE);
            rvPartyResults.setVisibility(View.GONE);
            tvResultsCount.setText("לא נמצאו מסיבות");
        } else {
            llEmptyState.setVisibility(View.GONE);
            rvPartyResults.setVisibility(View.VISIBLE);
            tvResultsCount.setText("מצאנו " + partyList.size() + " מסיבות שתואמות לחיפוש שלך");
            partyAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPartyClick(Party party) {
        // מעבר למסך פרטי מסיבה עם ה-ID של המסיבה שנבחרה
        Intent intent = new Intent(this, party_details.class);
        intent.putExtra("PARTY_ID", party.getPartyId());
        startActivity(intent);
    }
}
