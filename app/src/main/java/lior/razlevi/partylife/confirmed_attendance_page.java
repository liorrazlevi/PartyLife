package lior.razlevi.partylife;

import android.os.Bundle;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class confirmed_attendance_page extends AppCompatActivity {

    private TextView tvPartyTitle, tvNotComingCount, tvComingCount;
    private TextInputEditText etSearchGuest;
    private RecyclerView rvGuests;
    private GuestAdapter guestAdapter;
    private List<Guest> guestList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirmed_attendance_page);

        init();

        // אתחול הרשימה והאדפטר
        guestList = new ArrayList<>();
        guestAdapter = new GuestAdapter(guestList);
        rvGuests.setLayoutManager(new LinearLayoutManager(this));
        rvGuests.setAdapter(guestAdapter);

        // כאן בהמשך תוכלי להוסיף קוד שימשוך את האורחים מ-Firebase
        // ויעדכן את guestList

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tvPartyTitle).getRootView(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void init() {
        tvPartyTitle = findViewById(R.id.tvPartyTitle);
        tvNotComingCount = findViewById(R.id.tvNotComingCount);
        tvComingCount = findViewById(R.id.tvComingCount);
        etSearchGuest = findViewById(R.id.etSearchGuest);
        rvGuests = findViewById(R.id.rvGuests);
    }
}
