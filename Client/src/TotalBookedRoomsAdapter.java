package com.example.frontendds;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TotalBookedRoomsAdapter extends RecyclerView.Adapter<TotalBookedRoomsAdapter.ViewHolder> {

    private final List<ShowTotalBookedRoomsActivity.TotalBookedRoom> totalBookedRoomList;

    public TotalBookedRoomsAdapter(List<ShowTotalBookedRoomsActivity.TotalBookedRoom> totalBookedRoomList) {
        this.totalBookedRoomList = totalBookedRoomList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_total_booked_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShowTotalBookedRoomsActivity.TotalBookedRoom room = totalBookedRoomList.get(position);
        holder.areaTextView.setText(room.getArea());
        holder.rentCountTextView.setText(String.valueOf(room.getRentCount()));
    }

    @Override
    public int getItemCount() {
        return totalBookedRoomList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView areaTextView;
        TextView rentCountTextView;

        ViewHolder(View itemView) {
            super(itemView);
            areaTextView = itemView.findViewById(R.id.areaTextView);
            rentCountTextView = itemView.findViewById(R.id.rentCountTextView);
        }
    }
}
