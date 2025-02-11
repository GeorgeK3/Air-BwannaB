package com.example.frontendds;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ManagerMenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manager_menu);

        TextView outcomeTextView = findViewById(R.id.welcome_manager);
        outcomeTextView.setText("Welcome manager #"+ManagerLoginActivity.managerID+" !");

        Button addDatesButton = (Button)findViewById(R.id.button_add_dates);

        addDatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManagerMenuActivity.this, AddDatesActivity.class));
            }
        });

        Button jsonRoomButton = (Button)findViewById(R.id.button_jsonroom);

        jsonRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManagerMenuActivity.this, AddJsonRoomActivity.class));
            }
        });

        Button bookedButton = (Button)findViewById(R.id.button_booked);

        bookedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManagerMenuActivity.this, ShowBookedRoomsActivity.class));
            }
        });

        Button areasButton = (Button)findViewById(R.id.button_areas);

        areasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManagerMenuActivity.this, TotalBookedRoomsActivity.class));
            }
        });

        Button logoutButton = (Button)findViewById(R.id.button_logout);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManagerMenuActivity.this, MainActivity.class));
            }
        });

    }

}
