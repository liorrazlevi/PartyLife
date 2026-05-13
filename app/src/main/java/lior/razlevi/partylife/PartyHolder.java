package lior.razlevi.partylife;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PartyHolder extends RecyclerView.ViewHolder {
      public   ImageView ivEventCover;
     public    TextView tvEventTitle, tvEventDate, tvEventStatus;

        public PartyHolder(@NonNull View itemView) {
            super(itemView);
            ivEventCover = itemView.findViewById(R.id.ivEventCover);
            tvEventTitle = itemView.findViewById(R.id.tvEventTitle);
            tvEventDate = itemView.findViewById(R.id.tvEventDate);
            tvEventStatus = itemView.findViewById(R.id.tvEventStatus);
        }
    }

