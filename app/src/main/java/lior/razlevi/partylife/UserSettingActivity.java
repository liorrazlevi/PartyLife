package lior.razlevi.partylife;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserSettingActivity extends AppCompatActivity {

    private TextInputEditText etEFullName;
    private TextInputEditText etEEmail;
    private TextInputEditText etEPhone;
    private TextInputEditText etEPassword;
    private TextInputEditText etEConfirmPassword;
    private MaterialButton btEnRegister;
    private FirebaseDatabase database;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_setting);
        init();
        FirebaseUser firebaseUser = Auth.getCurrentUser();
        if (firebaseUser == null) return;
        String uid = firebaseUser.getUid();
        userRef.child(uid).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                UserProperties userProperties = dataSnapshot.getValue(UserProperties.class);
                etEPhone.setText(String.valueOf(userProperties.getUserPhone()));
                etEFullName.setText(userProperties.getFullName());
                etEEmail.setText(firebaseUser.getEmail());
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.usersetting), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void init(){

       etEConfirmPassword= findViewById(R.id.etEConfirmPassword);
       etEPassword= findViewById(R.id.etEPassword);
       etEPhone= findViewById(R.id.etEPhone);
       etEEmail= findViewById(R.id.etEEmail);
       etEFullName= findViewById(R.id.etEFullName);
       btEnRegister= findViewById(R.id.btEnRegister);
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("Users");
    }
}
