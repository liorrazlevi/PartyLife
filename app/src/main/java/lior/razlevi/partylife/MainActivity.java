package lior.razlevi.partylife;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private AppCompatButton loginButton;
    private AppCompatButton registerButton;
    private EditText emailInputLogin;
    private EditText passwordInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        init();
        loginButton.setOnClickListener(view -> {
            String firstName = emailInputLogin.getText().toString();
            String password = passwordInput.getText().toString();
            if (firstName.isEmpty()) {
                emailInputLogin.setError("עלייך למלא אימייל");
            }
            else  if ( password.isEmpty()) {
                passwordInput.setError("סיסמא אינה יכולה להיות ריקה");
            }
            else{
                // התחברות לfairbase
                startActivity(new Intent(this, OpenPage.class));
            }
            });
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
}