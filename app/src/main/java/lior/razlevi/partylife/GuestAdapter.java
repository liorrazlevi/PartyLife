package lior.razlevi.partylife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class GuestAdapter extends RecyclerView.Adapter<GuestAdapter.GuestViewHolder> {

    private List<Guest> guestList;

    public GuestAdapter(List<Guest> guestList) {
        this.guestList = guestList;
    }

    @NonNull
    @Override
    public GuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_guest, parent, false);
        return new GuestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GuestViewHolder holder, int position) {
        Guest guest = guestList.get(position);
        holder.tvGuestName.setText(guest.getName());
        holder.tvGuestStatus.setText(guest.getStatus());
    }

    @Override
    public int getItemCount() {
        return guestList != null ? guestList.size() : 0;
    }

    public static class GuestViewHolder extends RecyclerView.ViewHolder {
        TextView tvGuestName, tvGuestStatus;

        public GuestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGuestName = itemView.findViewById(R.id.tvGuestName);
            tvGuestStatus = itemView.findViewById(R.id.tvGuestStatus);
        }
    }
}
