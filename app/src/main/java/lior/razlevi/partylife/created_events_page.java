package lior.razlevi.partylife;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class created_events_page extends AppCompatActivity implements PartyAdapter.OnPartyClickListener {

    private View profileGlow;
    private ImageView ivProfile;
    private MaterialButton btnCreateEvent;
    private TextView tvHeader;
    private RecyclerView rvEvents;
    private TextView tvEmptyState;
    private PartyAdapter partyAdapter;
    private List<Party> partyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_created_events_page);

        init();

        // אתחול רשימה ואדפטר
        partyList = new ArrayList<>();
        partyAdapter = new PartyAdapter(partyList, this);
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        rvEvents.setAdapter(partyAdapter);

        btnCreateEvent.setOnClickListener(v -> {
            startActivity(new Intent(this, party_creation_page.class));
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rvEvents).getRootView(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void init() {
        profileGlow = findViewById(R.id.profileGlow);
        ivProfile = findViewById(R.id.ivProfile);
        btnCreateEvent = findViewById(R.id.btnCreateEvent);
        tvHeader = findViewById(R.id.tvHeader);
        rvEvents = findViewById(R.id.rvEvents);
        tvEmptyState = findViewById(R.id.tvEmptyState);
    }

    @Override
    public void onPartyClick(Party party) {
        // כאן נעבור למסך פרטי המסיבה ונשלח את ה-ID שלה
        Intent intent = new Intent(this, party_details.class);
        intent.putExtra("PARTY_ID", party.getId());
        startActivity(intent);
    }
}
