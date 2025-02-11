package com.example.frontendds;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;

public class RoomDetailRateActivity extends AppCompatActivity {
    private ImageView roomImageView;
    private TextView roomNameTextView, noOfPersonsTextView, areaTextView, starsTextView, noOfReviewsTextView, priceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail_rate);

        Log.d("HELLO THERE","INSIDE ROOM DETAIL RATE ACTIVITY");

        roomImageView = findViewById(R.id.roomImageView);
        roomNameTextView = findViewById(R.id.roomNameTextView);
        noOfPersonsTextView = findViewById(R.id.noOfPersonsTextView);
        areaTextView = findViewById(R.id.areaTextView);
        starsTextView = findViewById(R.id.starsTextView);
        noOfReviewsTextView = findViewById(R.id.noOfReviewsTextView);

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

        }


        Button commitButton = (Button)findViewById(R.id.button_commit);
        RatingBar rating = (RatingBar)findViewById(R.id.ratingBar);

        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject rForStar = new JSONObject();
                try {
                    rForStar.put("roomName", room.getRoomName());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                try {
                    rForStar.put("review", rating.getRating());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                Client cl = null;
                try {
                    cl = new Client(rForStar,"updateReview");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                cl.start();

                Toast.makeText(RoomDetailRateActivity.this, "Thank you for the review!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RoomDetailRateActivity.this, RenterMenuActivity.class));
            }
        });
    }
    private int getResourceIdByName(String resourceName, Context context) {
        return context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
    }
}


