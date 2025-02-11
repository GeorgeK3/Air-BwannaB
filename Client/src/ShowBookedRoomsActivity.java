package com.example.frontendds;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShowBookedRoomsActivity extends AppCompatActivity implements BookedRoomsAdapter.OnRoomClickListener {
    private RecyclerView recyclerView;
    private BookedRoomsAdapter roomAdapter;
    private List<BookedRooms> roomList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Log.d("HELLO THERE", "INSIDE ADD DATES ACTIVITY");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        roomList = new ArrayList<>();

        loadRooms();
    }

    private void loadRooms() {
        new Thread(() -> {
            Client cl;
            try {
                cl = new Client(new JSONObject(), "getRents", ManagerLoginActivity.managerID);
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
            JSONArray jsonArray = cl.getJsonArray();
            if (jsonArray == null || jsonArray.length() == 0) {
                runOnUiThread(() -> {
                    Toast.makeText(ShowBookedRoomsActivity.this, "You don't have any booked rooms yet!", Toast.LENGTH_SHORT).show();
                });
                startActivity(new Intent(ShowBookedRoomsActivity.this, ManagerMenuActivity.class));
            } else {
                try {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object1 = jsonArray.getJSONObject(i);

                        String roomName = object1.getString("roomName");
                        int noOfPersons = object1.getInt("noOfPersons");
                        String area = object1.getString("area");
                        double stars = object1.getDouble("stars");
                        int noOfReviews = object1.getInt("noOfReviews");
                        String roomImage = object1.getString("roomImage");
                        int price = object1.getInt("price");
                        String dates = object1.getString("dates");
                        String rents = object1.getString("rents");

                        roomList.add(new BookedRooms(roomName, noOfPersons, area, stars, noOfReviews, roomImage, price, dates, rents));
                    }
                    runOnUiThread(() -> {
                        roomAdapter = new BookedRoomsAdapter(roomList, ShowBookedRoomsActivity.this);
                        recyclerView.setAdapter(roomAdapter);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onRoomClick(BookedRooms room) {
        // Handle room click
    }
}
