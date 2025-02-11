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

public class BookedRoomsAdapter extends RecyclerView.Adapter<BookedRoomsAdapter.RoomViewHolder> {

    private List<BookedRooms> roomList;
    private OnRoomClickListener onRoomClickListener;

    public BookedRoomsAdapter(List<BookedRooms> roomList, OnRoomClickListener onRoomClickListener) {
        this.roomList = roomList;
        this.onRoomClickListener = onRoomClickListener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booked_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        BookedRooms room = roomList.get(position);

        holder.roomNameTextView.setText("Room Name: " + room.getRoomName());
        holder.noOfPersonsTextView.setText("No. of Persons: " + room.getNoOfPersons());
        holder.areaTextView.setText("Area: " + room.getArea());
        holder.starsTextView.setText("Stars: " + room.getStars());
        holder.noOfReviewsTextView.setText("No. of Reviews: " + room.getNoOfReviews());
        holder.priceTextView.setText("Price: " + room.getPrice());

        // Format rents data
        String rents = room.getRents();
        String formattedRents = formatRents(rents);
        holder.rentsTextView.setText(formattedRents);

        // Load image using resource name
        String imageName = room.getRoomImage().replace(".png", "").replace(".jpg", "");
        int imageResId = getResourceIdByName(imageName, holder.itemView.getContext());
        if (imageResId != 0) {
            holder.roomImageView.setImageResource(imageResId);
        } else {
            // Set a placeholder image if resource not found
            holder.roomImageView.setImageResource(R.drawable.ic_placeholder_image);
        }

        holder.itemView.setOnClickListener(v -> onRoomClickListener.onRoomClick(room));
    }

    private String formatRents(String rents) {
        try {
            StringBuilder formattedRents = new StringBuilder();
            String[] rentPeriods = rents.split(";");
            for (String rentPeriod : rentPeriods) {
                rentPeriod = rentPeriod.trim().replace("[", "").replace("]", ""); // Remove the brackets
                String[] parts = rentPeriod.split(",");
                if (parts.length == 2) {
                    String userId = parts[0].trim();
                    String dateRange = parts[1].trim();
                    formattedRents.append("UserID: ").append(userId).append("\nDates: ").append(dateRange).append("\n\n");
                }
            }
            return formattedRents.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Rents: Invalid format";
    }

    private int getResourceIdByName(String name, Context context) {
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView roomNameTextView;
        TextView noOfPersonsTextView;
        TextView areaTextView;
        TextView starsTextView;
        TextView noOfReviewsTextView;
        TextView priceTextView;
        TextView rentsTextView; // New TextView for rents
        ImageView roomImageView;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);

            roomNameTextView = itemView.findViewById(R.id.roomNameTextView);
            noOfPersonsTextView = itemView.findViewById(R.id.noOfPersonsTextView);
            areaTextView = itemView.findViewById(R.id.areaTextView);
            starsTextView = itemView.findViewById(R.id.starsTextView);
            noOfReviewsTextView = itemView.findViewById(R.id.noOfReviewsTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            rentsTextView = itemView.findViewById(R.id.rentsTextView); // Initialize new TextView
            roomImageView = itemView.findViewById(R.id.roomImageView);
        }
    }

    public interface OnRoomClickListener {
        void onRoomClick(BookedRooms room);
    }
}
