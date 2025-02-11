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

public class AddDatesActivity extends AppCompatActivity implements RoomRateAdapter.OnRoomClickListener {
    private RecyclerView recyclerView;
    private RoomRateAdapter roomAdapter;
    private List<Room> roomList;

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
                cl = new Client(new JSONObject(), "get", ManagerLoginActivity.managerID);
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
                    Toast.makeText(AddDatesActivity.this, "You have not added a room yet!", Toast.LENGTH_SHORT).show();
                });
                startActivity(new Intent(AddDatesActivity.this, ManagerMenuActivity.class));
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
                        boolean availability = object1.getBoolean("availability");
                        int price = object1.getInt("price");
                        String dates = object1.getString("dates");

                        roomList.add(new Room(roomName, noOfPersons, area, stars, noOfReviews, roomImage, availability, price, dates));
                    }
                    runOnUiThread(() -> {
                        roomAdapter = new RoomRateAdapter(roomList, AddDatesActivity.this);
                        recyclerView.setAdapter(roomAdapter);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onRoomClick(Room room) {
        Intent intent = new Intent(AddDatesActivity.this, EnterDatesActivity.class);
        intent.putExtra("room", room); // Pass the Room object
        startActivity(intent);
    }
}
