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


/**
 * דף תוצאות החיפוש.
 * שולף את כל המסיבות מה-Firebase ומציג רק את אלו שמתאימות
 *   לקריטריונים שהמשתמש הזין בדף החיפוש.
 */
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


        progressBar.setVisibility( View.VISIBLE);
        // קבלת נתוני החיפוש שנשלחו מהדף הקודם (PartySearchPage)
        searchLocation = getIntent().getStringExtra("LOCATION");
        searchDate = getIntent().getStringExtra("DATE");
        searchAge = getIntent().getStringExtra("AGE");
        searchTime = getIntent().getStringExtra("TIME");


// שורות הגנה - אם הנתון חסר, נשים טקסט ריק במקום null
        if (searchLocation == null) searchLocation = "";
        if (searchDate == null) searchDate = "";
        if (searchAge == null) searchAge = "";
        if (searchTime == null) searchTime = "";

        // הגדרת רשימת התצוגה והאדפטר
        partyList = new ArrayList<>();
        partyAdapter = new PartyAdapter(partyList, this);
        rvPartyResults.setLayoutManager(new LinearLayoutManager(this));
        rvPartyResults.setAdapter(partyAdapter);

        //  התחברות ל-Firebase וטעינת נתונים
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

    /**
     * שליפת כל המסיבות מהשרת וביצוע סינון לפי 4 פרמטרים.
     */
    private void loadFilteredParties() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                partyList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Party party = data.getValue(Party.class);

                    if (party != null) {
                        // חילוץ נתוני המסיבה מהשרת
                        String pLocation = party.getLocation() != null ? party.getLocation() : "";
                        String pDate = party.getDate() != null ? party.getDate() : "";
                        String pAge = party.getAge() != null ? party.getAge() : "";
                        String pTime = party.getTime() != null ? party.getTime() : "";

                    // בדיקת התאמה למיקום, תאריך וגיל
                        boolean matchesLocation = pLocation.contains(searchLocation);
                        boolean matchesDate = pDate.equals(searchDate);
                        boolean matchesAge = pAge.equals(searchAge);
                       // המרת שעת החיפוש ושעת המסיבה מה-Firebase לדקות
                        int searchMinutes = timeToMinutes(searchTime);
                        int partyMinutes = timeToMinutes(pTime);


                        boolean matchesTime;
                        if (searchMinutes == -1 || partyMinutes == -1) {
                            // אם יש בעיה בפורמט, נחזור להשוואה מדויקת
                            matchesTime = pTime.equals(searchTime);
                        } else {
                            // הבדיקה: האם המסיבה בטווח של השעה שביקשתי ועד שעה קדימה
                            matchesTime = (partyMinutes >= searchMinutes) && (partyMinutes <= searchMinutes + 60);
                        }

                        //  הוספה לרשימה רק אם כל התנאים מתקיימים
                        if (matchesLocation && matchesDate && matchesAge && matchesTime) {
                            partyList.add(party);
                        }
                    }
                }
                updateUI(); // עדכון הממשק בהתאם לתוצאות
              progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PartyResultsActivity.this, "שגיאה בטעינת הנתונים", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                Log.d("LIORA","error loading");
            }
        });
    }

    /**
     *  עדכון הממשק: הצגת הודעת "לא נמצאו תוצאות" או עדכון הרשימה.
     */
    private void updateUI() {
        if (partyList.isEmpty()) {
            llEmptyState.setVisibility(View.VISIBLE);
            rvPartyResults.setVisibility(View.GONE);
            tvResultsCount.setText("אופס... לא מצאנו מסיבות בדיוק בשעה הזו");
        } else {
            llEmptyState.setVisibility(View.GONE);
            rvPartyResults.setVisibility(View.VISIBLE);

            // הודעה מגניבה שמסבירה על טווח השעה
            String message = "מצאנו " + partyList.size() + " מסיבות בשבילך!\n";
            message += "💡 מוצגות תוצאות בטווח של עד שעה מהזמן שביקשת, כדי שלא תפספסו אף חגיגה.";

            tvResultsCount.setText(message);
            partyAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPartyClick(Party party) {
        // מעבר לדף פרטי המסיבה בעת לחיצה על תוצאה
        Intent intent = new Intent(this, party_details.class);
        intent.putExtra("PARTY_ID", party.getPartyId());
        startActivity(intent);
    }

    /**
     * פונקציית עזר להמרת מחרוזת זמן (HH:mm) למספר דקות כולל מתחילת היום.
     *       מאפשרת לבצע השוואות מתמטיות בין שעות.
     */
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
