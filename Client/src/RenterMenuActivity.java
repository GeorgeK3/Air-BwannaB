package com.example.frontendds;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class RenterMenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_renter_menu);

        TextView outcomeTextView = findViewById(R.id.welcome_user);
        outcomeTextView.setText("Welcome user #"+RenterLoginActivity.renterID+" !");

        Button searchButton = (Button)findViewById(R.id.button_search);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RenterMenuActivity.this, UserSearchActivity.class));
            }
        });

        Button rateButton = (Button)findViewById(R.id.button_rate);

        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RenterMenuActivity.this, RenterRateActivity.class));
            }
        });

        Button logoutButton = (Button)findViewById(R.id.button_logout);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RenterMenuActivity.this, MainActivity.class));
            }
        });

    }

}
