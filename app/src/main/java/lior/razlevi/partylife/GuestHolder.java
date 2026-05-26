package lior.razlevi.partylife;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;


/**
 * חלקה המייצגת שורה בודדת ברשימת האורחים (RecyclerView).
 *   תפקידה להחזיק את ההפניות לרכיבי הממשק (UI) כדי לשפר את ביצועי הרשימה.
 */
public class GuestHolder extends  RecyclerView.ViewHolder {
    public TextView tvGuestName, tvGuestStatus;
    public ShapeableImageView ivGuestIcon;


    /**
     * נאי המקבל את תצוגת השורה ומקשר את המשתנים לרכיבי ה-XML.
     */
    public GuestHolder(@NonNull View itemView) {
        super(itemView);

        // קישור המשתנים לפי מזהים (ID) מקובץ ה-item_guest.xml
        tvGuestName = itemView.findViewById(R.id.tvGuestName);
        tvGuestStatus = itemView.findViewById(R.id.tvGuestStatus);
        ivGuestIcon = itemView.findViewById(R.id.ivGuestIcon);

    }
    }
