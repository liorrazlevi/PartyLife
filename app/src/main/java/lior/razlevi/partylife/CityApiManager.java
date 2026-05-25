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

public class CityApiManager {

    // הכתובת של ה-API הממשלתי (רשימת יישובים)
    private static final String API_URL = "https://data.gov.il/api/3/action/datastore_search?resource_id=e9701dcb-9f1c-43bb-bd44-eb380ade542f&limit=1500";

    // ממשק (Interface) כדי להחזיר את התוצאה לאקטיביטי
    public interface CityCallback {
        void onCitiesLoaded(List<String> cities);
        void onError(String error);
    }

    public void fetchCities(CityCallback callback) {

        Log.d("LIORA", "fetchCities");
        // יצירת Thread נפרד לעבודה ברקע
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                // 1. יצירת חיבור HTTP
                Log.d("LIORA", "try");
                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                Log.d("LIORA", "connection");
                connection.setRequestMethod("GET");
Log.d("LIORA", "connection2");

                // 2. קריאת הנתונים מהשרת
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Log.d("LIORA", "reader");
                StringBuilder response = new StringBuilder();
                Log.d("LIORA", "response");
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                Log.d("LIORA", "response: " + response.toString());
                //  פענוח ה-JSON (Parsing)
                List<String> cityNames = new ArrayList<>();
                JSONObject jsonObject = new JSONObject(response.toString());
                JSONArray records = jsonObject.getJSONObject("result").getJSONArray("records");

                Log.d("LIORA", "records: " + records.length());
                for (int i = 0; i < records.length(); i++) {
                    JSONObject record = records.getJSONObject(i);
                    // "שם_ישוב" הוא המפתח ב-JSON של הממשלה
                    String cityName = record.getString("name_in_hebrew").trim();
                    cityNames.add(cityName);
                }
     Log.d("LIORA", "cityNames: " + cityNames);
                //  חזרה ל-Main Thread כדי לעדכן את הממשק
                handler.post(() -> callback.onCitiesLoaded(cityNames));

            } catch (Exception e) {
                handler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }
}
