package lior.razlevi.partylife;

import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

/**
 * דף המציג את רשימת אישורי ההגעה למסיבה ספציפית.
 * * כולל מונה של מגיעים/לא מגיעים ואפשרות לחיפוש אורחים.
 */
public class confirmed_attendance_page extends AppCompatActivity {

    private TextView tvComingCount, tvNotComingCount;
    private RecyclerView rvGuests;
    private AutoCompleteTextView etSearchGuest;
    
    private GuestAdapter adapter;
    private List<Guest> guestList;
    private DatabaseReference mDatabase;
    private String partyId;
private  List<String> answered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmed_attendance_page);

        init();
        loadGuestsFromFirebase();
        setupRecyclerView();


        // טיפול בבחירת שם מרשימת החיפוש האוטומטית
        etSearchGuest.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = parent.getItemAtPosition(position).toString();
            if (selectedName != null&& !selectedName.isEmpty()){
                filterGuests(selectedName);
            }
          else{
                setupRecyclerView();
            };

       });


        // האזנה לשינויי טקסט בשדה החיפוש לצורך סינון בזמן אמת
        etSearchGuest.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // אם השדה ריק, נחזיר את הרשימה המלאה
                if (s.toString().isEmpty()) {
                    adapter = new GuestAdapter(guestList);
                    rvGuests.setAdapter(adapter);
                } else {
                    //  סינון תוך כדי הקלדה
                    filterGuests(s.toString());
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {

            }

        });
    }

    /**
     * פונקציה לסינון רשימת האורחים לפי שם.
     */
    private void filterGuests(String name) {
        List<Guest> filteredList = new ArrayList<>();
        for (Guest guest : guestList) {
            if (guest.getName().toLowerCase().contains(name.toLowerCase())) {
                filteredList.add(guest);
            }
        }
        adapter = new GuestAdapter(filteredList);
        rvGuests.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
    private void init() {
        tvComingCount = findViewById(R.id.tvComingCount);
        tvNotComingCount = findViewById(R.id.tvNotComingCount);
        rvGuests = findViewById(R.id.rvGuests);
        etSearchGuest = findViewById(R.id.etSearchGuest);
        partyId = getIntent().getStringExtra("PARTY_ID");
        guestList = new ArrayList<>();
        
        if (partyId != null) {
            // פנייה לטבלה הנפרדת "Attendance" לפי ה-partyId
            mDatabase = FirebaseDatabase.getInstance().getReference("Attendance").child(partyId);
        } else {
            Toast.makeText(this, "שגיאה: לא נמצא מזהה מסיבה", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * הגדרת ה-RecyclerView (מנהל פריסה ואדפטר).
     */
    private void setupRecyclerView() {
        rvGuests.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GuestAdapter(guestList);
        rvGuests.setAdapter(adapter);
    }

    /**
     *שליפת נתוני האורחים מה-Firebase וחישוב כמות המגיעים והלא מגיעים.
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
                    Log.d("LIORA", "Guest: " + guest);
                    if (guest != null) {
                        guestList.add(guest);

                        if ("מגיע".equals(guest.getStatus())) {
                            countComing++;
                        } else if ("לא מגיע".equals(guest.getStatus())) {
                            countNotComing++;
                        }
                    }
                }

                setupGuest(); // עדכון רשימת ההצעות לחיפוש
                updateUI(countComing, countNotComing); // עדכון המונים בממשק
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(confirmed_attendance_page.this, "שגיאה בטעינת הנתונים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * עדכון הטקסט של מוני המגיעים ורענון הרשימה.
     */
    private void updateUI(int coming, int notComing) {
        tvComingCount.setText(String.valueOf(coming));
        tvNotComingCount.setText(String.valueOf(notComing));
        adapter.notifyDataSetChanged();
    }

    /**
     *הכנת רשימת שמות האורחים עבור שדה החיפוש האוטומטי (AutoComplete).
     */
    private  void setupGuest(){
        answered=new ArrayList<>();
        for (Guest guest : guestList) {
            answered.add(guest.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                confirmed_attendance_page.this,
                android.R.layout.simple_list_item_1,
                answered
        );

        etSearchGuest.setAdapter(adapter);
        etSearchGuest.setThreshold(2);
    }
}
