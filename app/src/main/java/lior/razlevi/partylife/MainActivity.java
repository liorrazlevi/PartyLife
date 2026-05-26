package lior.razlevi.partylife;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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

import java.util.ArrayList;
import java.util.List;

/**
 *  מסך הכניסה הראשי של האפליקציה.
 *   מנהל את תהליך ההתחברות, שמירת פרטי משתמש ("זכור אותי") וטעינת נתונים ראשוניים.
 */
public class MainActivity extends AppCompatActivity {
    private AppCompatButton loginButton;
    private AppCompatButton registerButton;
    private EditText emailInputLogin;
    private EditText passwordInput;
    private MaterialCheckBox cbRememberMe;
    private SharedPreferences sp;

public static List<String> cities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        getCities();
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

    /**
     *  ניהול תהליך ההתחברות מול Firebase Auth ובדיקת תקינות קלט.
     */
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

                            //  נבדוק אם המשתמש רוצה שנזכור אותו
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

    // טעינה אוטומטית של פרטי המשתמש אם בחר באפשרות "זכור אותי" בעבר.
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

    /**
     * טיפול בשגיאות התחברות נפוצות מול Firebase.
     */
    private void handleLoginError(Exception e) {
        if (e instanceof FirebaseAuthInvalidUserException ) {
            Toast.makeText(MainActivity.this, "אימייל שגויי. ניתן להירשם אם אין לך חשבון.", Toast.LENGTH_LONG).show();
        }
         else if(e instanceof FirebaseAuthInvalidCredentialsException){
            Toast.makeText(MainActivity.this, " סיסמה שגוייה. ניתן להירשם אם אין לך חשבון.", Toast.LENGTH_LONG).show();
        }
        else if (e instanceof FirebaseNetworkException) {
            Toast.makeText(MainActivity.this, "אין חיבור לאינטרנט", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "שגיאה בהתחברות: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     *  הפעלת מנהל ה-API לשליפת רשימת הערים המעודכנת.
     */
    public  void getCities(){
        cities=new ArrayList<>();

        CityApiManager apiManager;
        apiManager = new CityApiManager();

        // קריאה למחלקה כדי להביא את הנתונים
        apiManager.fetchCities(new CityApiManager.CityCallback() {
            @Override
            public void onCitiesLoaded(List<String> gotcities) {
                cities = gotcities;

            }

            @Override
            public void onError(String error) {
                // רשימת גיבוי במקרה ששירות ה-API לא זמין
                cities.add("תל אביב");
                cities.add("חדרה");
                cities.add("ראשון לציון");
                cities.add("הרצליה");
                cities.add("נתניה");

            }
    });

}
}


