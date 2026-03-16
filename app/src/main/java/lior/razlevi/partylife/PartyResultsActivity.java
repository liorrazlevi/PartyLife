package lior.razlevi.partylife;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PartyResultsActivity extends AppCompatActivity implements PartyAdapter.OnPartyClickListener {

    private RecyclerView rvPartyResults;
    private TextView tvResultsCount;
    private LinearLayout llEmptyState;
    private PartyAdapter partyAdapter;
    private List<Party> partyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_party_results);

        init();

        // אתחול רשימה ואדפטר
        partyList = new ArrayList<>();
        partyAdapter = new PartyAdapter(partyList, this);
        rvPartyResults.setLayoutManager(new LinearLayoutManager(this));
        rvPartyResults.setAdapter(partyAdapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // כאן תוכלי לקבל נתונים שנשלחו מהחיפוש (אם שלחת ב-Intent)
        // למשל: String location = getIntent().getStringExtra("location");
    }

    private void init() {
        rvPartyResults = findViewById(R.id.rvPartyResults);
        tvResultsCount = findViewById(R.id.tvResultsCount);
        llEmptyState = findViewById(R.id.llEmptyState);
    }

    // פונקציה לעדכון התוצאות
    public void updateResults(List<Party> parties) {
        if (parties == null || parties.isEmpty()) {
            llEmptyState.setVisibility(View.VISIBLE);
            rvPartyResults.setVisibility(View.GONE);
            tvResultsCount.setText("לא נמצאו מסיבות");
        } else {
            llEmptyState.setVisibility(View.GONE);
            rvPartyResults.setVisibility(View.VISIBLE);
            tvResultsCount.setText("מצאנו " + parties.size() + " מסיבות שתואמות לחיפוש שלך");
            
            this.partyList.clear();
            this.partyList.addAll(parties);
            partyAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPartyClick(Party party) {
        // מעבר למסך פרטי מסיבה
        Intent intent = new Intent(this, party_details.class);
        intent.putExtra("PARTY_ID", party.getId());
        startActivity(intent);
    }
}
