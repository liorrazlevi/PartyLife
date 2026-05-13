package lior.razlevi.partylife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class PartyAdapter extends RecyclerView.Adapter<PartyHolder> {

    private final List<Party> partyList;
    private final OnPartyClickListener listener;

    public interface OnPartyClickListener {
        void onPartyClick(Party party);
    }

    public PartyAdapter(List<Party> partyList, OnPartyClickListener listener) {
        this.partyList = partyList;
        this.listener = listener;
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

        // תיקון: שימוש ב-getImageUrl() במקום getImage()
        if (party.bringPartyImage() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(party.bringPartyImage())
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


}
