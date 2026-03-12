package lior.razlevi.partylife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PartyAdapter extends RecyclerView.Adapter<PartyAdapter.PartyViewHolder> {

    private List<Party> partyList;
    private OnPartyClickListener listener;

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
        holder.tvEventDate.setText(party.getDate() + ", " + party.getTime());
        holder.tvEventStatus.setText(party.getStatus());
        
        // כאן בהמשך אפשר להשתמש ב-Glide או Picasso לטעינת התמונה מה-URL
        // holder.ivEventCover.setImageResource(R.drawable.partyicon);

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
