package lior.razlevi.partylife;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
    private  String fullAddress;

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

        loadPartyDetails();
        checkUserAttendance();

        btnNavigate.setOnClickListener(v -> {
            try {
                // 1. יצירת פורמט לקריאת התאריך והשעה (ודאי שהפורמט תואם למה ששמור ב-Firebase)
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());

                // חיבור מחרוזת התאריך והשעה של המסיבה
                String partyDateTimeStr = tvDate.getText().toString() + " " + tvTime.getText().toString();
                java.util.Date partyDate = sdf.parse(partyDateTimeStr);

                if (partyDate != null) {
                    long currentTime = System.currentTimeMillis();
                    long partyTimeMillis = partyDate.getTime();

                    // 24 שעות במילישניות
                    long twentyFourHoursInMillis = 24 * 60 * 60 * 1000;

                    // בדיקה: האם המסיבה רחוקה יותר מ-24 שעות מעכשיו?
                    if (partyTimeMillis - currentTime > twentyFourHoursInMillis) {
                        // הצגת דיאלוג התראה
                        new AlertDialog.Builder(party_details.this)
                                .setTitle("מיקום מדויק טרם נחשף")
                                .setMessage("למען פרטיות המסיבה, המיקום המדויק והניווט יהיו זמינים רק 24 שעות לפני תחילת האירוע.")
                                .setPositiveButton("הבנתי", null)
                                .show();
                    } else {
                        // ניווט (הקוד המקורי שלך)
                        String address = tvLocation.getText().toString() + " " + (fullAddress != null ? fullAddress : "");
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + address);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");

                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(mapIntent);
                        } else {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=" + address)));
                        }
                    }
                }
            } catch (Exception e) {
                // במקרה של שגיאה בפורמט התאריך, נאפשר ניווט ליתר ביטחון
                Toast.makeText(this, "לא ניתן לחשב את זמן המסיבה, מנווט כרגיל", Toast.LENGTH_SHORT).show();
                String address = tvLocation.getText().toString();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + address)));
            }
        });

        btnYes.setOnClickListener(v -> updateAttendance("מגיע"));
        btnNo.setOnClickListener(v -> updateAttendance("לא מגיע"));

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
                    // עדכון פרטי הטקסט
                    tvPartyName.setText(party.getName());
                    tvDate.setText(party.getDate());
                    tvTime.setText(party.getTime());
                    tvLocation.setText(party.getLocation());
                    tvAgeInfo.setText(party.getAge());
                    tvParkingInfo.setText(party.getParking());
                    tvDressCode.setText(party.getDressCode());

                    organizerPhone = party.getPhone();
                    fullAddress = (party.getFullAddress() != null) ? party.getFullAddress() : "";

                    // טיפול בתמונה - המרה ל-Bitmap והצגה
                    Bitmap partyBitmap = party.bringPartyImage();
                    if (partyBitmap != null) {
                        // אם קיימת תמונה - מציגים אותה
                        ivPartyIcon.setImageBitmap(partyBitmap);

                        // הגדרות לחיתוך מושלם בתוך העיגול
                        ivPartyIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        ivPartyIcon.setClipToOutline(true);

                    } else {
                        // אם אין תמונה - מציגים את אייקון ברירת המחדל
                        ivPartyIcon.setImageResource(R.drawable.partyicon);
                        ivPartyIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        ivPartyIcon.setClipToOutline(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PartyDetails", "Error loading party details: " + error.getMessage());
            }
        });
    }

    private void checkUserAttendance() {
        if (mAuth.getCurrentUser() == null) return;
        String userId = mAuth.getCurrentUser().getUid();

        mDatabase.child("Attendance").child(partyId).child(userId)
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
Log.d("LIORA", "User ID: " + userId);
        mDatabase.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName = snapshot.child("fullName").getValue(String.class);
                String picture = snapshot.child("profileImage").getValue(String.class);

                Log.d("LIORA", "User Name: " + userName + " Picture URL: " + picture.substring(0, Math.min(picture.length(), 10)));
                if (userName == null) userName = "אורח";

                Guest guestStatus = new Guest(userName, status,picture);
Log.d("LIORA", "Guest Status: " + guestStatus);
                mDatabase.child("Attendance").child(partyId).child(userId)
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
            btnYes.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B14C6E")));
            btnYes.setStrokeWidth(4);
            btnYes.setStrokeColor(ColorStateList.valueOf(Color.WHITE));
            btnNo.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#333333")));
            btnNo.setStrokeWidth(0);
        } else if ("לא מגיע".equals(status)) {
            btnNo.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF5252")));
            btnNo.setStrokeWidth(4);
            btnNo.setStrokeColor(ColorStateList.valueOf(Color.WHITE));
            btnYes.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#333333")));
            btnYes.setStrokeWidth(0);
        }
    }
}
