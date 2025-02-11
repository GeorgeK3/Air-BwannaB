package com.example.frontendds;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;

public class RoomDetailActivity extends AppCompatActivity {
    private ImageView roomImageView;
    private TextView roomNameTextView, noOfPersonsTextView, areaTextView, starsTextView, noOfReviewsTextView, availabilityTextView, priceTextView;
    private LinearLayout datesLayout;
    private JSONObject roomJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        roomImageView = findViewById(R.id.roomImageView);
        roomNameTextView = findViewById(R.id.roomNameTextView);
        noOfPersonsTextView = findViewById(R.id.noOfPersonsTextView);
        areaTextView = findViewById(R.id.areaTextView);
        starsTextView = findViewById(R.id.starsTextView);
        noOfReviewsTextView = findViewById(R.id.noOfReviewsTextView);
        availabilityTextView = findViewById(R.id.availabilityTextView);
        priceTextView = findViewById(R.id.priceTextView);
        datesLayout = findViewById(R.id.datesLayout);

        Room room = getIntent().getParcelableExtra("room");
        if (room != null) {
            // Populate the views with room data
            String imageName = room.getRoomImage().replace(".png", "").replace(".jpg", "");
            int imageResId = getResourceIdByName(imageName, this);
            if (imageResId != 0) {
                roomImageView.setImageResource(imageResId);
            } else {
                roomImageView.setImageResource(R.drawable.ic_placeholder_image); // Use placeholder if image is not found
            }
            roomNameTextView.setText(room.getRoomName());
            noOfPersonsTextView.setText("No. of Persons: " + room.getNoOfPersons());
            areaTextView.setText("Area: " + room.getArea());
            starsTextView.setText("Stars: " + room.getStars());
            noOfReviewsTextView.setText("No. of Reviews: " + room.getNoOfReviews());
            availabilityTextView.setText("Availability: " + (room.isAvailability() ? "Available" : "Not Available"));
            priceTextView.setText("Price: $" + room.getPrice() + " per night");

            String[] dates = room.getDates().split(",");
            for (String date : dates) {
                LinearLayout dateLayout = new LinearLayout(this);
                dateLayout.setOrientation(LinearLayout.HORIZONTAL);

                TextView dateTextView = new TextView(this);
                dateTextView.setText(date.trim());
                dateTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                Button rentButton = new Button(this);
                rentButton.setText("Rent");
                rentButton.setTag(date.trim()); // Set the date range as the tag
                rentButton.setOnClickListener(v -> {
                    String dateRange = (String) v.getTag(); // Retrieve the date range
                    try {
                        handleRentButtonClick(dateRange); // Handle the button click
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

                dateLayout.addView(dateTextView);
                dateLayout.addView(rentButton);

                datesLayout.addView(dateLayout);
            }

            // Convert the Room object to a JSON object
            roomJson = room.toJson();
        }
    }

    private int getResourceIdByName(String resourceName, Context context) {
        return context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
    }

    private void handleRentButtonClick(String dateRange) throws JSONException {
        // Handle the rent button click
        Toast.makeText(this, "Rent button clicked for date range: " + dateRange, Toast.LENGTH_SHORT).show();

        roomJson.put("userID", RenterLoginActivity.renterID);

        new Thread(() -> {
            Client cl = null;
            try {
                cl = new Client(roomJson, "rent", dateRange);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            cl.start();
            while (true) {
                try {
                    if (cl.isAnswerRecieved()) {
                        break;
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            boolean isSuccess = false;
            try {
                isSuccess = cl.getJsonObj().getBoolean("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            boolean finalIsSuccess = isSuccess;
            runOnUiThread(() -> {
                Log.d("RentOutcomeActivity", "Success: " + finalIsSuccess);
                Intent intent = new Intent(RoomDetailActivity.this, RentOutcomeActivity.class);
                intent.putExtra("isSuccess", finalIsSuccess);
                startActivity(intent);
            });
        }).start();
    }
}
