package lior.razlevi.partylife;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class UserSettingActivity extends AppCompatActivity {

    private TextInputEditText etEFullName;
    private TextInputEditText etEEmail;
    private TextInputEditText etEPhone;
    private TextInputEditText etEPassword;
    private TextInputEditText etEConfirmPassword;
    private MaterialButton btEnRegister;
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private ShapeableImageView ivProfileImageE;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private String encodedImage = "";
    private FloatingActionButton fabAddPhotoE;
    private static final String TAG = "UserSet" + "tingActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_user_setting);
        init();
        setupGalleryLauncher();

        fabAddPhotoE.setOnClickListener(view -> {

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });


        FirebaseUser firebaseUser = Auth.getCurrentUser();
        if (firebaseUser == null) return;
        String uid = firebaseUser.getUid();
        userRef.child(uid).get().addOnSuccessListener(dataSnapshot -> { //שליפה
            if (dataSnapshot.exists()) {
                UserProperties userProperties = dataSnapshot.getValue(UserProperties.class);
                etEPhone.setText(String.valueOf(userProperties.getUserPhone()));
                etEFullName.setText(userProperties.getFullName());
                etEEmail.setText(firebaseUser.getEmail());
                ivProfileImageE.setImageBitmap(convertStringToBitmap(userProperties.getProfileImage()));
            }

        });
        btEnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullName = etEFullName.getText().toString().trim();
                String email = etEEmail.getText().toString().trim();
                String phone = etEPhone.getText().toString().trim();
                String password = etEPassword.getText().toString();
                String confirmPassword = etEConfirmPassword.getText().toString();

                if (fullName.isEmpty()) {
                    etEFullName.setError("נא למלא שם מלא");
                    return;
                }
                if (email.isEmpty()) {
                    etEEmail.setError("נא למלא אימייל");
                    return;
                }
                if (phone.isEmpty()) {
                    etEPhone.setError("נא למלא מספר טלפון");
                    return;
                }



                if (!password.isEmpty()) {
                    if (!password.equals(confirmPassword)) {
                        etEConfirmPassword.setError("הסיסמאות אינן תואמות");
                        return;
                    }
                    updatePassword(password);
                }
                updateUserInfo(fullName, email, phone);
                Intent intent = new Intent(UserSettingActivity.this, OpenPage.class);
                startActivity(intent);

            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.usersetting), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void init() {

        etEConfirmPassword = findViewById(R.id.etEConfirmPassword);
        etEPassword = findViewById(R.id.etEPassword);
        etEPhone = findViewById(R.id.etEPhone);
        etEEmail = findViewById(R.id.etEEmail);
        etEFullName = findViewById(R.id.etEFullName);
        btEnRegister = findViewById(R.id.btEnRegister);
        ivProfileImageE = findViewById(R.id.ivProfileImageE);
        fabAddPhotoE = findViewById(R.id.fabAddPhotoE);
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("Users");
    }

    private void updateUserInfo(String fullName, String email, String phone) {
        FirebaseUser firebaseUser = Auth.getCurrentUser();
        if (firebaseUser == null) return;

        // Update email in Firebase Auth if it has changed
        if (!email.equals(firebaseUser.getEmail())) {
            Auth.updateEmail(email, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(UserSettingActivity.this, "נשלח אימייל לאימות כתובת המייל החדשה.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(UserSettingActivity.this, "שגיאה בעדכון כתובת המייל.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to update email", task.getException());
                    }
                }
            });
        }


        // Update user properties in Realtime Database
        String uid = firebaseUser.getUid();
        UserProperties userProperties = new UserProperties(phone, uid, fullName,encodedImage);

        userRef.child(uid).setValue(userProperties)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User properties updated successfully.");
                    Toast.makeText(UserSettingActivity.this, "הפרטים עודכנו בהצלחה", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update user properties", e);
                    Toast.makeText(UserSettingActivity.this, "שגיאה בעדכון הפרטים", Toast.LENGTH_SHORT).show();
                });
    }

    private void updatePassword(String newPassword) {
        Auth.updatePassword(newPassword, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(UserSettingActivity.this, "הסיסמה עודכנה בהצלחה", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserSettingActivity.this, "שגיאה בעדכון הסיסמה. ייתכן שתצטרך להתחבר מחדש.", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Failed to update password", task.getException());
                }
            }
        });
    }

    private void setupGalleryLauncher() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                            // המרה של הביטמאפ לסטרינג (Base64)ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); // דחיסה ל-50% כדי לחסוך מקום ב-Database
                            byte[] b = baos.toByteArray();
                            encodedImage = android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT);
                            // הצגת התמונה הנבחרת
                            ivProfileImageE.setImageBitmap(bitmap);
                            ivProfileImageE.setAlpha(1.0f);
                        } catch (Exception e) {
                            Toast.makeText(this, "שגיאה בטעינת התמונה", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    public Bitmap convertStringToBitmap(String imageString) {
        if (imageString != null && !imageString.isEmpty()) {
            try {
                // המרה מ-Base64 למערך בתים
                byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
                // יצירת Bitmap מהבתים
                return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}
