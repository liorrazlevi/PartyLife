package lior.razlevi.partylife;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GuestHolder extends  RecyclerView.ViewHolder {
    public TextView tvGuestName, tvGuestStatus;






    public GuestHolder(@NonNull View itemView) {
        super(itemView);
        tvGuestName = itemView.findViewById(R.id.tvGuestName);
        tvGuestStatus = itemView.findViewById(R.id.tvGuestStatus);

    }
    }
