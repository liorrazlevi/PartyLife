package lior.razlevi.partylife;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


/**
 *  אדפטר האחראי על ניהול והצגת רשימת האורחים בתוך ה-RecyclerView.
 *  מקשר בין אובייקט ה-Guest לבין קובץ העיצוב item_guest.
 */
public class GuestAdapter extends RecyclerView.Adapter<GuestHolder> {
    private List<Guest> guestList;


    public GuestAdapter(List<Guest> guestList) {
        this.guestList = guestList;
    }


    /**
     * יצירת (ViewHolder) חדש עבור שורה ברשימה.
     */
    @NonNull
    @Override
    public GuestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_guest, parent, false);
        return new GuestHolder(view);
    }


    /**
     *הצמדת הנתונים של אורח ספציפי לרכיבי הממשק בשורה המתאימה.
     */
    @Override
    public void onBindViewHolder(@NonNull GuestHolder holder, int position) {
        Guest guest = guestList.get(position);
        holder.tvGuestName.setText(guest.getName());

        // המרת מחרוזת התמונה מ-Base64 ל-Bitmap והצגתה
        holder.ivGuestIcon.setImageBitmap(convertStringToBitmap(guest.getPicture()));

         // עדכון סטטוס ההגעה (צבע וטקסט) לפי בחירת האורח
        if (guest.getStatus() != null && guest.getStatus().equals("מגיע")) {
            holder.tvGuestStatus.setText("כן");
            holder.tvGuestStatus.setBackgroundResource(R.drawable.bg_status_green);
        } else {
            holder.tvGuestStatus.setText("לא");
            holder.tvGuestStatus.setBackgroundResource(R.drawable.bg_status_red);
        }
    }

    /**
     * מחזיר את מספר הפריטים הכולל ברשימה.
     */
    @Override
    public int getItemCount() {
        return guestList.size();
    }

    /**
     * פונקציית עזר להמרת מחרוזת Base64 חזרה לאובייקט Bitmap להצגה ב-ImageView.
     */
    public Bitmap convertStringToBitmap(String imageString) {
        if (imageString != null && !imageString.isEmpty()) {
            try {
                // המרה מ-Base64 למערך בתים
                byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
                // יצירת Bitmap מהבתים
                return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

}
