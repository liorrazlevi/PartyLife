package lior.razlevi.partylife;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class party_details_edit extends AppCompatActivity {

    private TextInputEditText etLocation, etDate, etTime, etParking, etDressCodeEdit, etPhone;
    private AutoCompleteTextView inputAge;
    private MaterialButton btnSaveChanges, btnViewEvents;
    private TextView tvTitle;
    private ImageView ivSelectedPartyImage;
    private MaterialCardView cvPartyImage;

    private String partyId;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri imageUri;
    private String currentImageUrl;

    private final Calendar calendar = Calendar.getInstance();
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_details_edit);

        init();
        setupAgeSpinner();
        setupPickers();
        setupGalleryLauncher();

        // 1. קבלת ה-ID מה-Intent
        partyId = getIntent().getStringExtra("PARTY_ID");
        if (partyId == null) {
            Toast.makeText(this, "שגיאה: לא נמצא מזהה מסיבה", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mDatabase = FirebaseDatabase.getInstance().getReference("Parties").child(partyId);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // 2. טעינת פרטי המסיבה
        loadPartyDetails();

        // 3. בחירת תמונה חדשה
        cvPartyImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });

        // 4. שמירת השינויים
        btnSaveChanges.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadImageAndSave();
            } else {
                saveChanges(currentImageUrl);
            }
        });

        // 5. מעבר לדף אישורי הגעה
        btnViewEvents.setOnClickListener(v -> {
            Intent intent = new Intent(party_details_edit.this, confirmed_attendance_page.class);
            intent.putExtra("PARTY_ID", partyId);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.partyEdit), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void init() {
        etLocation = findViewById(R.id.etLocation);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etParking = findViewById(R.id.etParking);
        etDressCodeEdit = findViewById(R.id.etDressCodeEdit);
        etPhone = findViewById(R.id.etPhone);
        inputAge = findViewById(R.id.inputAge);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnViewEvents = findViewById(R.id.btnViewEvents);
        tvTitle = findViewById(R.id.tvTitle);
        ivSelectedPartyImage = findViewById(R.id.ivSelectedPartyImage);
        cvPartyImage = findViewById(R.id.cvPartyImage);
    }

    private void loadPartyDetails() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Party party = snapshot.getValue(Party.class);
                if (party != null) {
                    tvTitle.setText("עריכת: " + party.getName());
                    etLocation.setText(party.getLocation());
                    etDate.setText(party.getDate());
                    etTime.setText(party.getTime());
                    etDressCodeEdit.setText(party.getDressCode());
                    etPhone.setText(party.getPhone());
                    inputAge.setText(party.getAge(), false);
                    currentImageUrl = party.getImageUrl();

                    if (currentImageUrl != null && !currentImageUrl.isEmpty()) {
                        Glide.with(party_details_edit.this).load(currentImageUrl).into(ivSelectedPartyImage);
                        ivSelectedPartyImage.setAlpha(1.0f);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void setupGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        ivSelectedPartyImage.setImageURI(imageUri);
                        ivSelectedPartyImage.setAlpha(1.0f);
                    }
                }
        );
    }

    private void uploadImageAndSave() {
        String fileName = UUID.randomUUID().toString();
        StorageReference ref = storageReference.child("party_images/" + fileName);

        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveChanges(uri.toString());
                }))
                .addOnFailureListener(e -> Toast.makeText(this, "שגיאה בהעלאת תמונה", Toast.LENGTH_SHORT).show());
    }

    private void saveChanges(String imageUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("location", etLocation.getText().toString());
        updates.put("date", etDate.getText().toString());
        updates.put("time", etTime.getText().toString());
        updates.put("dressCode", etDressCodeEdit.getText().toString());
        updates.put("phone", etPhone.getText().toString());
        updates.put("age", inputAge.getText().toString());
        updates.put("imageUrl", imageUrl);

        mDatabase.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "השינויים נשמרו בהצלחה!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "שגיאה בשמירה", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupPickers() {
        etDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                etDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        etTime.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                etTime.setText(String.format("%02d:%02d", hourOfDay, minute));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });
    }

    private void setupAgeSpinner() {
        String[] ageRanges = {"18-20", "20-25", "25-30", "30+"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, ageRanges);
        inputAge.setAdapter(adapter);
        inputAge.setOnClickListener(v -> inputAge.showDropDown());
    }
}
