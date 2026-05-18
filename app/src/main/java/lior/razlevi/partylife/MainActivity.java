package lior.razlevi.partylife;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private AppCompatButton loginButton;
    private AppCompatButton registerButton;
    private EditText emailInputLogin;
    private EditText passwordInput;
    private MaterialCheckBox cbRememberMe;
    private SharedPreferences sp;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        init();
        loadLastLoggedInUserData(); // טעינת פרטים שנשמרו
        SingInforUsers();

        registerButton.setOnClickListener(view -> {
            startActivity(new Intent(this, RegisterPage.class));
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void init() {
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        emailInputLogin = findViewById(R.id.emailInputLogin);
        passwordInput = findViewById(R.id.passwordInput);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        sp = getSharedPreferences("PartyLifeDetails", MODE_PRIVATE);

    }

    public void SingInforUsers() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailInputLogin.getText().toString();
                String password = passwordInput.getText().toString();

                if (email.isEmpty()) {
                    emailInputLogin.setError("עלייך למלא אימייל");
                } else if (password.isEmpty()) {
                    passwordInput.setError("סיסמא אינה יכולה להיות ריקה");
                } else {
                    Auth.signIn(MainActivity.this, email, password, task -> {
                        if (task.isSuccessful()) {

                            // אם ההתחברות הצליחה, נבדוק אם המשתמש רוצה שנזכור אותו
                            if (cbRememberMe.isChecked()) {
                                saveLogedInUserInSharedPreferences(email, password);
                            } else {
                                // אם לא מסומן, ננקה את השמירה הקודמת אם הייתה
                                clearSharedPreferences();
                            }
                            
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();


                            startActivity(new Intent(MainActivity.this, OpenPage.class));
                            finish(); // סגירת מסך הלוגין
                        } else {
                            handleLoginError(task.getException());
                        }
                    });
                }
            }
        });
    }

    // שמירת פרטי התחברות
    public void saveLogedInUserInSharedPreferences(String email, String password) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.putBoolean("remember", true);
        editor.apply();
    }

    // טעינת פרטי התחברות מזיכרון
    public void loadLastLoggedInUserData() {
        String email = sp.getString("email", "");
        String password = sp.getString("password", "");
        boolean remember = sp.getBoolean("remember", false);

        if (remember) {
            emailInputLogin.setText(email);
            passwordInput.setText(password);
            cbRememberMe.setChecked(true);
        }
    }

    // ניקוי זיכרון אם המשתמש לא סימן "זכור אותי"
    public void clearSharedPreferences() {
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    private void handleLoginError(Exception e) {
        if (e instanceof FirebaseAuthInvalidUserException || e instanceof FirebaseAuthInvalidCredentialsException) {
            Toast.makeText(MainActivity.this, "אימייל או סיסמה שגויים. ניתן להירשם אם אין לך חשבון.", Toast.LENGTH_LONG).show();
        } else if (e instanceof FirebaseNetworkException) {
            Toast.makeText(MainActivity.this, "אין חיבור לאינטרנט", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "שגיאה בהתחברות: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
