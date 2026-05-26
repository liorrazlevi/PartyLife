package lior.razlevi.partylife;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *  מחלקה האחראית על שליפת רשימת הערים והיישובים בישראל ממאגר הנתונים הממשלתי (API).
 */
public class CityApiManager {

    // הכתובת של ה-API הממשלתי (רשימת יישובים)
    private static final String API_URL = "https://data.gov.il/api/3/action/datastore_search?resource_id=e9701dcb-9f1c-43bb-bd44-eb380ade542f&limit=1500";

    /**
     * ממשק (Interface) המשמש להחזרת רשימת הערים לאקטיביטי לאחר סיום השליפה.
     */
    public interface CityCallback {
        void onCitiesLoaded(List<String> cities);
        void onError(String error);
    }

    /**
     * פונקציה המבצעת קריאת רשת לקבלת נתוני הערים.
     */
    public void fetchCities(CityCallback callback) {
        // יצירת Thread נפרד לעבודה ברקע
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                //  יצירת חיבור HTTP
                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                Log.d("LIORA", "connection");
                connection.setRequestMethod("GET");


                //  קריאת הנתונים מהשרת
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Log.d("LIORA", "reader");
                StringBuilder response = new StringBuilder();
                Log.d("LIORA", "response");
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();


                // פענוח ה-JSON שהתקבל מהשרת
                List<String> cityNames = new ArrayList<>();
                JSONObject jsonObject = new JSONObject(response.toString());
                JSONArray records = jsonObject.getJSONObject("result").getJSONArray("records");


                for (int i = 0; i < records.length(); i++) {
                    JSONObject record = records.getJSONObject(i);
                    // "שם_ישוב" הוא המפתח ב-JSON של הממשלה
                    String cityName = record.getString("name_in_hebrew").trim();
                    cityNames.add(cityName);
                }

                //  חזרה ל-Main Thread כדי לעדכן את הממשק
                handler.post(() -> callback.onCitiesLoaded(cityNames));

            } catch (Exception e) {
                // במקרה של שגיאה (למשל חוסר באינטרנט), שליחת הודעת השגיאה לממשק
                handler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }
}
