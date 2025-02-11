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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShowTotalBookedRoomsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TotalBookedRoomsAdapter adapter;
    private List<TotalBookedRoom> totalBookedRoomList;
    private String fromDateStr;
    private String toDateStr;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_total_booked_rooms);

        fromDateStr = getIntent().getStringExtra("FROM_DATE");
        toDateStr = getIntent().getStringExtra("TO_DATE");

        dateFormat = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());

        recyclerView = findViewById(R.id.recyclerViewTotalBookedRooms);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        totalBookedRoomList = new ArrayList<>();

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
                    Toast.makeText(ShowTotalBookedRoomsActivity.this, "You don't have any booked rooms yet!", Toast.LENGTH_SHORT).show();
                });
                startActivity(new Intent(ShowTotalBookedRoomsActivity.this, ManagerMenuActivity.class));
            } else {
                try {
                    Date fromDate = dateFormat.parse(fromDateStr);
                    Date toDate = dateFormat.parse(toDateStr);

                    Map<String, Integer> areaRentCount = new HashMap<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object1 = jsonArray.getJSONObject(i);
                        String area = object1.getString("area");
                        String rents = object1.getString("rents");

                        String[] rentsArray = rents.split(" ; ");
                        for (String rent : rentsArray) {
                            String[] rentDetails = rent.replace("[", "").replace("]", "").split(",");
                            String rentDateRange = rentDetails[1].trim();
                            String[] dates = rentDateRange.split(" - ");
                            Date startDate = dateFormat.parse(dates[0].trim());
                            Date endDate = dateFormat.parse(dates[1].trim());

                            if ((startDate.compareTo(fromDate) >= 0 && startDate.compareTo(toDate) <= 0) ||
                                    (endDate.compareTo(fromDate) >= 0 && endDate.compareTo(toDate) <= 0) ||
                                    (startDate.compareTo(fromDate) <= 0 && endDate.compareTo(toDate) >= 0)) {
                                areaRentCount.put(area, areaRentCount.getOrDefault(area, 0) + 1);
                            }
                        }
                    }

                    for (Map.Entry<String, Integer> entry : areaRentCount.entrySet()) {
                        totalBookedRoomList.add(new TotalBookedRoom(entry.getKey(), entry.getValue()));
                    }

                    runOnUiThread(() -> {
                        adapter = new TotalBookedRoomsAdapter(totalBookedRoomList);
                        recyclerView.setAdapter(adapter);
                    });

                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    static class TotalBookedRoom {
        String area;
        int rentCount;

        TotalBookedRoom(String area, int rentCount) {
            this.area = area;
            this.rentCount = rentCount;
        }

        public String getArea() {
            return area;
        }

        public int getRentCount() {
            return rentCount;
        }
    }
}
