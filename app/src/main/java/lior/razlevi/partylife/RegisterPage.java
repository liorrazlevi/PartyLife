package lior.razlevi.partylife;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


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


public void registerFB(){

    String fullName=etFullName.getText().toString();
            String email=etEmail.getText().toString();
            String password=etPassword.getText().toString();
            Auth.signUp(RegisterPage.this, email, password, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterPage.this, "Signup Successful", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(RegisterPage.this, OpenPage.class));

                }
                else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        etPassword.setError("הסיסמה חלשה מדי");
                        etPassword.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        etEmail.setError("כתובת האימייל לא תקינה");
                        etEmail.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e) {
                        // שגיאה קריטית: המשתמש כבר קיים!
                        Toast.makeText(RegisterPage.this, "האימייל הזה כבר רשום במערכת", Toast.LENGTH_LONG).show();
                    } catch (FirebaseNetworkException e) {
                        Toast.makeText(RegisterPage.this, "אין חיבור לאינטרנט", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(RegisterPage.this, "שגיאה בהרשמה: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        });
          //
}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_page);
        init();
        btnRegister.setOnClickListener(view -> {
            String fullName = etFullName.getText().toString();
            String email = etEmail.getText().toString();
            String phone = etPhone.getText().toString();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();
            if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(RegisterPage.this, "נא למלא את כל הפרטים המתבקשים", Toast.LENGTH_LONG).show();
            } else if (!password.equals(confirmPassword)) {

            } else {
             registerFB();
            }

        });
        tvLoginLink.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registerpage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void init(){
        btnRegister = findViewById(R.id.btnRegister);
        etFullName = findViewById(R.id.etFullName);
       etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword= findViewById(R.id.etConfirmPassword);
        tvLoginLink = findViewById(R.id.tvLoginLink);
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("Users");

    }
}