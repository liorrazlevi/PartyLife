package lior.razlevi.partylife;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * מחלקה המייצגת שורה בודדת ברשימת המסיבות (RecyclerView).
 *   משמשת לקישור בין רכיבי ה-XML (עיצוב השורה) לבין הקוד של האדפטר.
 */
public class PartyHolder extends RecyclerView.ViewHolder {
    public ImageView ivEventCover;
    public TextView tvEventTitle, tvEventDate, tvEventStatus;
    public ImageView ivDeleteParty; //כפתור מחיקת מסיבה.
     // יוצג רק בדף "המסיבות שלי" כדי לאפשר למארגן למחוק אירוע.


    public PartyHolder(@NonNull View itemView) {
        super(itemView);
        // קישור המשתנים לרכיבים המתאימים בקובץ ה-XML (item_event.xml)
        ivEventCover = itemView.findViewById(R.id.ivEventCover);
        tvEventTitle = itemView.findViewById(R.id.tvEventTitle);
        tvEventDate = itemView.findViewById(R.id.tvEventDate);
        tvEventStatus = itemView.findViewById(R.id.tvEventStatus);
        // קישור כפתור המחיקה
        ivDeleteParty = itemView.findViewById(R.id.ivDeleteParty);
    }
}
