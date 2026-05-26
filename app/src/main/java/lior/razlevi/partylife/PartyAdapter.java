package lior.razlevi.partylife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

/**
 *  אדפטר לניהול תצוגת רשימת המסיבות בתוך RecyclerView.
 * תומך בהצגת נתונים, לחיצה למעבר לפרטים, ואפשרות למחיקת פריט.
 */
public class PartyAdapter extends RecyclerView.Adapter<PartyHolder> {

    private final List<Party> partyList;
    private final OnPartyClickListener listener;
    private OnDeleteClickListener deleteListener;
    private boolean showDeleteButton = false;

    /**
     *  ממשק (Interface) לטיפול בלחיצה על מסיבה לצפייה בפרטים.
     */
    public interface OnPartyClickListener {
        void onPartyClick(Party party);
    }

    /**
     * ממשק לטיפול בלחיצה על כפתור המחיקה.
     */
    public interface OnDeleteClickListener {
        void onDeleteClick(Party party);
    }

    public PartyAdapter(List<Party> partyList, OnPartyClickListener listener) {
        this.partyList = partyList;
        this.listener = listener;
    }

    // פעולה להפעלת כפתור המחיקה
    public void enableDeletion(OnDeleteClickListener deleteListener) {
        this.showDeleteButton = true;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public PartyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new PartyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PartyHolder holder, int position) {
        Party party = partyList.get(position);
        holder.tvEventTitle.setText(party.getName());
        String dateTime = party.getDate() + " | " + party.getTime();
        holder.tvEventDate.setText(dateTime);
        holder.tvEventStatus.setText(party.getLocation());
        holder.tvEventStatus.setTextColor(0xFF9575CD);

        // ניהול כפתור המחיקה
        //  יוצג רק אם האופציה הופעלה
        if (showDeleteButton && holder.ivDeleteParty != null) {
            holder.ivDeleteParty.setVisibility(View.VISIBLE);
            holder.ivDeleteParty.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteClick(party);
                }
            });
        } else if (holder.ivDeleteParty != null) {
            holder.ivDeleteParty.setVisibility(View.GONE);
        }

        /**
         * טעינת תמונת המסיבה:
         *           שימוש בספריית Glide לטעינה יעילה של ה-Bitmap והצגת תמונת ברירת מחדל במקרה של שגיאה.
         */
        if (party.bringPartyImage()  != null) {
            Glide.with(holder.itemView.getContext())
                    .load(party.bringPartyImage() )
                    .placeholder(R.drawable.partyicon)
                    .error(R.drawable.partyicon)
                    .into(holder.ivEventCover);
        } else {
            holder.ivEventCover.setImageResource(R.drawable.partyicon);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPartyClick(party);
            }
        });
    }

    @Override
    public int getItemCount() {
        return partyList != null ? partyList.size() : 0;
    }
    // פונקציה שמעדכנת את הרשימה על המסך מיד אחרי המחיקה
    public void removeItem(Party party) {
        int position = partyList.indexOf(party);
        if (position != -1) {
            partyList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, partyList.size());
        }
    }
}
