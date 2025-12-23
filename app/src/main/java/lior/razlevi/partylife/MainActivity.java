package lior.razlevi.partylife;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class MainActivity extends AppCompatActivity {

    private AppCompatButton loginButton;
    private AppCompatButton registerButton;
    private EditText emailInputLogin;
    private EditText passwordInput;

    public void SingInforUsers(){
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=emailInputLogin.getText().toString();
                String password=passwordInput.getText().toString();

                if (email.isEmpty()) {
                    emailInputLogin.setError("עלייך למלא אימייל");
                }
                else  if (password.isEmpty()) {
                    passwordInput.setError("סיסמא אינה יכולה להיות ריקה");
                }
                else {
                    Auth.signIn(MainActivity.this, email, password, task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(MainActivity.this, OpenPage.class));
                                } else {
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthInvalidUserException | FirebaseAuthInvalidCredentialsException e) {
                                        Toast.makeText(MainActivity.this, "אימייל או סיסמה שגויים. ניתן להירשם אם אין לך חשבון.", Toast.LENGTH_LONG).show();
                                    } catch (FirebaseNetworkException e) {
                                        Toast.makeText(MainActivity.this, "אין חיבור לאינטרנט", Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        // שגיאה כללית אחרת
                                        Toast.makeText(MainActivity.this, "שגיאה בהתחברות: " + e.getMessage(), Toast.LENGTH_LONG).show();


                                    }//catch
                                }//else
                            }//task
                    );//signIn
                }
            }//onclick;
        });//button

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        init();
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
    public  void  init(){
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        emailInputLogin = findViewById(R.id.emailInputLogin);
        passwordInput = findViewById(R.id.passwordInput);


    }

        }//class
