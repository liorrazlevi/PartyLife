package lior.razlevi.partylife;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;

/**
 *  דף ההרשמה למערכת.
 *   מנהל את יצירת החשבון החדש מול Firebase Auth ושמירת פרטי המשתמש הנוספים ב-Database.
 */
public class RegisterPage extends AppCompatActivity {
    private MaterialButton btnRegister;
    private TextInputEditText etFullName;
    private TextInputEditText etEmail;
    private TextInputEditText etPhone;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private TextView tvLoginLink;
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private ShapeableImageView ivProfileImage;
    private FloatingActionButton fabAddPhoto;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private String encodedImage = ""; // יוצב כאן הסטרינג של התמונה


    /**
     *  מבצעת את תהליך ההרשמה מול Firebase Authentication.
     *  1. שולפת את האימייל והסיסמה מהשדות.
     *  2. קוראת לפעולת הרישום (signUp) ומאזינה לתוצאה.
     *  3. במקרה של הצלחה: שומרת את פרטי המשתמש ב-Database ועוברת לדף הבית.
     *   4. במקרה של כישלון: מנתחת את סוג השגיאה ומציגה חיווי מתאים למשתמש (אימייל קיים, סיסמה חלשה וכו').
     */
    public void registerFB() {
        String fullName = etFullName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        Auth.signUp(RegisterPage.this, email, password, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(RegisterPage.this, "Signup Successful", Toast.LENGTH_SHORT).show();

                SaveUserInDBS();

                startActivity(new Intent(RegisterPage.this, OpenPage.class));

            } else {
                try {
                    throw task.getException();
                } catch (FirebaseAuthWeakPasswordException e) {
                    etPassword.setError("הסיסמה חלשה מדי");
                    etPassword.requestFocus();
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    etEmail.setError("כתובת האימייל לא תקינה");
                    etEmail.requestFocus();
                } catch (FirebaseAuthUserCollisionException e) {

                    Toast.makeText(RegisterPage.this, "האימייל הזה כבר רשום במערכת", Toast.LENGTH_LONG).show();
                } catch (FirebaseNetworkException e) {
                    Toast.makeText(RegisterPage.this, "אין חיבור לאינטרנט", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(RegisterPage.this, "שגיאה בהרשמה: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_page);
        init();
        setupGalleryLauncher();

       // בחירת תמונה מהגלריה
        fabAddPhoto.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
                });

        // לחיצה על כפתור ההרשמה
        btnRegister.setOnClickListener(view -> {
            String fullName = etFullName.getText().toString();
            String email = etEmail.getText().toString();
            String phone = etPhone.getText().toString();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();
            if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(RegisterPage.this, "נא למלא את כל הפרטים המתבקשים", Toast.LENGTH_LONG).show();
            } else if (!password.equals(confirmPassword)) {
                etConfirmPassword.setError("נא להזין סיסמא תואמת");
                Toast.makeText(RegisterPage.this, "ווידוא הסיסמא לא תואם את הסיסמא שהזנת", Toast.LENGTH_LONG).show();
            } else {
                registerFB();
            }

        });


        // מעבר חזרה לדף הכניסה
        tvLoginLink.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registerpage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void init() {
        btnRegister = findViewById(R.id.btnRegister);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        tvLoginLink = findViewById(R.id.tvLoginLink);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        fabAddPhoto = findViewById(R.id.fabAddPhoto);
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("Users");

    }


    /**
     *  שמירת פרטי המשתמש המורחבים ב-Realtime Database תחת ענף "Users".
     */
    public void SaveUserInDBS() {
        FirebaseUser firebaseUser = Auth.getCurrentUser();
        if (firebaseUser == null) return;
        String uid = firebaseUser.getUid();
      UserProperties userProperties = new UserProperties(etPhone.getText().toString(), uid, etFullName.getText().toString(), encodedImage);
        // שמירת ההמשתמש  במסד הנתונים
        userRef.child(uid).setValue(userProperties)
                .addOnSuccessListener(aVoid -> {
                })
                //כשלון בשמירה
                .addOnFailureListener(e -> {
                });

    }


    /**
     *  הגדרת ה-Launcher לטיפול בתמונה שנבחרה:
     *      המרת התמונה ל-Bitmap, דחיסה והמרה למחרוזת Base64.
     */
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

                            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); // דחיסה ל-50% כדי לחסוך מקום ב-Database
                            byte[] b = baos.toByteArray();
                            encodedImage = android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT);
                            // הצגת התמונה הנבחרת
                            ivProfileImage.setImageBitmap(bitmap);
                            ivProfileImage.setAlpha(1.0f);
                        } catch (Exception e) {
                            Toast.makeText(this, "שגיאה בטעינת התמונה", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }
}