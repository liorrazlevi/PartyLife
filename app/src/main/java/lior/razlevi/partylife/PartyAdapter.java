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

public class PartyAdapter extends RecyclerView.Adapter<PartyAdapter.PartyViewHolder> {

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
    public PartyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new PartyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PartyViewHolder holder, int position) {
        Party party = partyList.get(position);
        holder.tvEventTitle.setText(party.getName());
        
        String dateTime = party.getDate() + " | " + party.getTime();
        holder.tvEventDate.setText(dateTime);
        
        holder.tvEventStatus.setText(party.getLocation());
        holder.tvEventStatus.setTextColor(0xFF9575CD);

        if (party.getImageUrl() != null && !party.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(party.getImageUrl())
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

    public static class PartyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivEventCover;
        TextView tvEventTitle, tvEventDate, tvEventStatus;

        public PartyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivEventCover = itemView.findViewById(R.id.ivEventCover);
            tvEventTitle = itemView.findViewById(R.id.tvEventTitle);
            tvEventDate = itemView.findViewById(R.id.tvEventDate);
            tvEventStatus = itemView.findViewById(R.id.tvEventStatus);
        }
    }
}
