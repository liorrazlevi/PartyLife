package lior.razlevi.partylife;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
    private ProgressBar progressBar;
    
    private DatabaseReference mDatabase;
    private String searchLocation, searchDate, searchAge, searchTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_party_results);

        init();

        Log.d("LIORA","in onCreate");
        progressBar.setVisibility( View.VISIBLE);
        // 1. קבלת נתוני החיפוש מה-Intent (כולל הזמן שנוסף)
        searchLocation = getIntent().getStringExtra("LOCATION");
        searchDate = getIntent().getStringExtra("DATE");
        searchAge = getIntent().getStringExtra("AGE");
        searchTime = getIntent().getStringExtra("TIME");


// שורות הגנה - אם הנתון חסר, נשים טקסט ריק במקום null
        if (searchLocation == null) searchLocation = "";
        if (searchDate == null) searchDate = "";
        if (searchAge == null) searchAge = "";
        if (searchTime == null) searchTime = "";

        // 2. אתחול רשימה ואדפטרא
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
        progressBar = findViewById(R.id.progressBar);
    }

    private void loadFilteredParties() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                partyList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    // 1. קבלת האובייקט מה-Snapshot
                    Party party = data.getValue(Party.class);

                    if (party != null) {
                        // 2. יצירת משתני עזר עם הגנה למקרה שב-Firebase חסר נתון
                        String pLocation = party.getLocation() != null ? party.getLocation() : "";
                        String pDate = party.getDate() != null ? party.getDate() : "";
                        String pAge = party.getAge() != null ? party.getAge() : "";
                        String pTime = party.getTime() != null ? party.getTime() : "";

                        // 3. בדיקה שהכל תואם בדיוק (כפי שביקשת)
                        // השתמשתי ב-contains למיקום כי זה יותר בטוח, וב-equals לשאר
                        boolean matchesLocation = pLocation.contains(searchLocation);
                        boolean matchesDate = pDate.equals(searchDate);
                        boolean matchesAge = pAge.equals(searchAge);
                       // המרת שעת החיפוש ושעת המסיבה מה-Firebase לדקות
                        int searchMinutes = timeToMinutes(searchTime);
                        Log.d("LIORA","searchMinutes"+searchMinutes);
                        int partyMinutes = timeToMinutes(pTime);
                        Log.d("LIORA","partyMinutes"+partyMinutes);


                        boolean matchesTime;
                        if (searchMinutes == -1 || partyMinutes == -1) {
                            // אם יש בעיה בפורמט, נחזור להשוואה מדויקת
                            Log.d("LIORA","error in time conversion"+party.getName());
                            matchesTime = pTime.equals(searchTime);
                        } else {
                            // הבדיקה: האם המסיבה בטווח של השעה שביקשתי ועד שעה קדימה
                            Log.d("LIORA","error in time1 conversion"+party.getName());
                            matchesTime = (partyMinutes >= searchMinutes) && (partyMinutes <= searchMinutes + 60);
                            Log.d("LIORA","error in time2 conversion"+ partyMinutes + " " + searchMinutes+matchesTime);
                        }

                        // 4. הוספה לרשימה רק אם כל התנאים מתקיימים
                        if (matchesLocation && matchesDate && matchesAge && matchesTime) {
                            Log.d("LIORA","found party " +party);
                            partyList.add(party);
                        }
                    }
                }
                updateUI();
              progressBar.setVisibility(View.GONE);
                Log.d("LIORA","finished loading");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PartyResultsActivity.this, "שגיאה בטעינת הנתונים", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                Log.d("LIORA","error loading");
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
        Intent intent = new Intent(this, party_details.class);
        intent.putExtra("PARTY_ID", party.getPartyId());
        startActivity(intent);
    }
    private int timeToMinutes(String timeStr) {
        try {
            if (timeStr == null || !timeStr.contains(":")) return -1;
            String[] parts = timeStr.split(":");
            int hours = Integer.parseInt(parts[0].trim());
            Log.d("LIORA","time is  h "+hours);
            int minutes = Integer.parseInt(parts[1].trim());
            Log.d("LIORA","time is  m "+minutes);
            return (hours * 60) + minutes;
        } catch (Exception e) {
            return -1;
        }
    }
}
