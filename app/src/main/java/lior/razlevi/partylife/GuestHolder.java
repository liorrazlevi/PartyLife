package lior.razlevi.partylife;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

public class GuestHolder extends  RecyclerView.ViewHolder {
    public TextView tvGuestName, tvGuestStatus;
    public ShapeableImageView ivGuestIcon;





    public GuestHolder(@NonNull View itemView) {
        super(itemView);
        tvGuestName = itemView.findViewById(R.id.tvGuestName);
        tvGuestStatus = itemView.findViewById(R.id.tvGuestStatus);
        ivGuestIcon = itemView.findViewById(R.id.ivGuestIcon);

    }
    }
