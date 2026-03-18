package lior.razlevi.partylife;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class party_details extends AppCompatActivity {

    private TextView tvPartyName, tvDate, tvTime, tvLocation, tvAgeInfo, tvParkingInfo, tvDressCode;
    private ImageView ivPartyIcon;
    private MaterialButton btnNavigate, btnYes, btnNo, btnContact;

    private String partyId;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String organizerPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_details);

        init();

        partyId = getIntent().getStringExtra("PARTY_ID");
        if (partyId == null) {
            Toast.makeText(this, "שגיאה: לא נמצא מזהה מסיבה", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // 1. טעינת פרטי המסיבה
        loadPartyDetails();

        // 2. בדיקה אם המשתמש כבר אישר הגעה
        checkUserAttendance();

        // 3. כפתור ניווט
        btnNavigate.setOnClickListener(v -> {
            String address = tvLocation.getText().toString();
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + address);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });

        // 4. אישור הגעה - כן
        btnYes.setOnClickListener(v -> updateAttendance("מגיע"));

        // 5. אישור הגעה - לא
        btnNo.setOnClickListener(v -> updateAttendance("לא מגיע"));

        // 6. יצירת קשר עם המארגן
        btnContact.setOnClickListener(v -> {
            if (organizerPhone != null) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + organizerPhone));
                startActivity(intent);
            }
        });
    }

    private void init() {
        tvPartyName = findViewById(R.id.tvPartyName);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tvLocation = findViewById(R.id.tvLocation);
        tvAgeInfo = findViewById(R.id.tvAgeInfo);
        tvParkingInfo = findViewById(R.id.tvParkingInfo);
        tvDressCode = findViewById(R.id.tvDressCode);
        ivPartyIcon = findViewById(R.id.ivPartyIcon);
        btnNavigate = findViewById(R.id.btnNavigate);
        btnYes = findViewById(R.id.btnYes);
        btnNo = findViewById(R.id.btnNo);
        btnContact = findViewById(R.id.btnContact);
    }

    private void loadPartyDetails() {
        mDatabase.child("Parties").child(partyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Party party = snapshot.getValue(Party.class);
                if (party != null) {
                    tvPartyName.setText(party.getName());
                    tvDate.setText(party.getDate());
                    tvTime.setText(party.getTime());
                    tvLocation.setText(party.getLocation());
                    tvAgeInfo.setText(party.getAge());
                    tvParkingInfo.setText(party.getParking());
                    tvDressCode.setText(party.getDressCode());
                    organizerPhone = party.getPhone();

                    if (party.getImageUrl() != null && !party.getImageUrl().isEmpty()) {
                        Glide.with(party_details.this).load(party.getImageUrl()).into(ivPartyIcon);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void checkUserAttendance() {
        if (mAuth.getCurrentUser() == null) return;
        String userId = mAuth.getCurrentUser().getUid();

        mDatabase.child("Parties").child(partyId).child("Attendance").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Guest guest = snapshot.getValue(Guest.class);
                            if (guest != null) {
                                updateButtonStyles(guest.getStatus());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void updateAttendance(String status) {
        if (mAuth.getCurrentUser() == null) return;
        String userId = mAuth.getCurrentUser().getUid();

        mDatabase.child("users").child(userId).child("fullName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName = snapshot.getValue(String.class);
                if (userName == null) userName = "אורח";

                Guest guestStatus = new Guest(userName, status);

                mDatabase.child("Parties").child(partyId).child("Attendance").child(userId)
                        .setValue(guestStatus)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(party_details.this, "סטטוס הגעה עודכן: " + status, Toast.LENGTH_SHORT).show();
                                updateButtonStyles(status);
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateButtonStyles(String status) {
        if ("מגיע".equals(status)) {
            // הבלטת כפתור "כן"
            btnYes.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B14C6E")));
            btnYes.setStrokeWidth(4);
            btnYes.setStrokeColor(ColorStateList.valueOf(Color.WHITE));
            
            // החזרת כפתור "לא" למצב רגיל
            btnNo.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#333333")));
            btnNo.setStrokeWidth(0);
        } else if ("לא מגיע".equals(status)) {
            // הבלטת כפתור "לא"
            btnNo.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF5252")));
            btnNo.setStrokeWidth(4);
            btnNo.setStrokeColor(ColorStateList.valueOf(Color.WHITE));
            
            // החזרת כפתור "כן" למצב רגיל
            btnYes.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#333333")));
            btnYes.setStrokeWidth(0);
        }
    }
}
