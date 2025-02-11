package com.example.frontendds;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RenterLoginActivity extends AppCompatActivity {

    public static int renterID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_renter_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btn = (Button)findViewById(R.id.button_login);
        EditText login = (EditText)findViewById(R.id.userID);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (! (login.getText().toString().trim().length() == 0)) {
                    renterID = Integer.valueOf(login.getText().toString());
                    startActivity(new Intent(RenterLoginActivity.this, RenterMenuActivity.class));
                }else{
                    Toast.makeText(RenterLoginActivity.this, "Please enter your ID to continue", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
