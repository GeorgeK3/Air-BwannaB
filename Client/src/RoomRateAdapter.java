package com.example.frontendds;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RoomRateAdapter extends RecyclerView.Adapter<RoomRateAdapter.RoomViewHolder> {

    private List<Room> roomList;
    private OnRoomClickListener onRoomClickListener;

    public RoomRateAdapter(List<Room> roomList, OnRoomClickListener onRoomClickListener) {
        this.roomList = roomList;
        this.onRoomClickListener = onRoomClickListener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);

        holder.roomNameTextView.setText("Room Name: " + room.getRoomName());
        holder.noOfPersonsTextView.setText("No. of Persons: " + room.getNoOfPersons());
        holder.areaTextView.setText("Area: " + room.getArea());
        holder.starsTextView.setText("Stars: " + room.getStars());
        holder.noOfReviewsTextView.setText("No. of Reviews: " + room.getNoOfReviews());

        // Load image using resource name
        String imageName = room.getRoomImage().replace(".png", "").replace(".jpg", "");
        int imageResId = getResourceIdByName(imageName, holder.itemView.getContext());
        if (imageResId != 0) {
            holder.roomImageView.setImageResource(imageResId);
        } else {
            holder.roomImageView.setImageResource(R.drawable.ic_placeholder_image); // Use placeholder if image is not found
        }

        holder.itemView.setOnClickListener(v -> {
            onRoomClickListener.onRoomClick(room); // Call the interface method
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {

        ImageView roomImageView;
        TextView roomNameTextView;
        TextView noOfPersonsTextView;
        TextView areaTextView;
        TextView starsTextView;
        TextView noOfReviewsTextView;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomImageView = itemView.findViewById(R.id.roomImageView);
            roomNameTextView = itemView.findViewById(R.id.roomNameTextView);
            noOfPersonsTextView = itemView.findViewById(R.id.noOfPersonsTextView);
            areaTextView = itemView.findViewById(R.id.areaTextView);
            starsTextView = itemView.findViewById(R.id.starsTextView);
            noOfReviewsTextView = itemView.findViewById(R.id.noOfReviewsTextView);
        }
    }

    public interface OnRoomClickListener {
        void onRoomClick(Room room);
    }

    // Utility function to get resource ID by name
    private int getResourceIdByName(String resourceName, Context context) {
        return context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
    }
}
