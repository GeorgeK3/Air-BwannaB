package com.example.frontendds;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RentOutcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_outcome);

        TextView outcomeTextView = findViewById(R.id.outcomeTextView);
        boolean isSuccess = getIntent().getBooleanExtra("isSuccess", false);

        if (isSuccess) {
            outcomeTextView.setText("Rent completed successfully!");
        } else {
            outcomeTextView.setText("Rent was not successful. Please try again.");
        }

        Button mButton = (Button)findViewById(R.id.button_menu);
        mButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(RentOutcomeActivity.this, RenterMenuActivity.class));
                    }
                });
    }


}
