package lior.razlevi.partylife;
import android.app.Activity;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * מחלקה זו מרכזת את כל שירותי האימות של האפליקציה מול firebase
 */
public class Auth {
    // יצירת מופע יחיד (Instance) של FirebaseAuth לשימוש בכל המחלקה
    private static final FirebaseAuth auth = FirebaseAuth.getInstance();

    /**
     * פונקציה לחיבור משתמש קיים למערכת באמצעות אימייל וסיסמה.
     */
    public static void signIn(Activity activity, String email, String password, OnCompleteListener<AuthResult> onCompleteListener) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(activity, onCompleteListener);
    }
    /**
     *
     *      * מנתקת את המשתמש הנוכחי מהמערכת.
     *  הערה:כרגע לא בשימוש ישיר בממשק המשתמש.
     */
    public static void signOut() {
        auth.signOut();
    }

    /**
     *פונקציה להרשמת משתמש חדש למערכת באמצעות אימייל וסיסמה.
     */
    public static void signUp(Activity activity, String email, String password, OnCompleteListener<AuthResult> onCompleteListener) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(activity, onCompleteListener);
    }

    /**
     * פונקציה המחזירה את האובייקט של המשתמש המחובר כרגע.
     *      אם אין משתמש מחובר, יוחזר null.
     */
    public static FirebaseUser getCurrentUser() {
        if (auth.getCurrentUser() == null)
            Log.d("Eitan Debug General", "Returning a null user");
        return auth.getCurrentUser();
    }

    /**
     *פונקציה לעדכון סיסמת המשתמש המחובר.
     */
    public static void updatePassword(String newPassword, OnCompleteListener<Void> onCompleteListener){
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword).addOnCompleteListener(onCompleteListener);
        }
    }
    /**
     *
     *      * מאפשרת עדכון של כתובת האימייל של המשתמש בשרת ה-Auth.
     *       הערה: הפונקציה אינה מופעלת במסכי האפליקציה .
     */
    public static void updateEmail(String newEmail, OnCompleteListener<Void> onCompleteListener){
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.verifyBeforeUpdateEmail(newEmail).addOnCompleteListener(onCompleteListener);
        }
    }
}
