package com.example.frontendds;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class AddJsonRoomActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_json_room);

        Button confirmButton = findViewById(R.id.button_confirm);
        EditText jsonFile = findViewById(R.id.jsonFile);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String jsonFileName = jsonFile.getText().toString().trim();
                if (!jsonFileName.isEmpty()) {
                    String data = null;
                    try {
                        data = loadJSONFromAsset(jsonFileName);
                        if (data == null) {
                            throw new IOException("File not found");
                        }
                    } catch (IOException e) {
                        Toast.makeText(AddJsonRoomActivity.this, "The file you entered does not exist, please try again!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JSONObject newObj = null;
                    try {
                        newObj = new JSONObject(data);
                        newObj.put("managerID", ManagerLoginActivity.managerID);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    Client cl = null;
                    try {
                        cl = new Client(newObj, "room", ManagerLoginActivity.managerID);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    cl.start();
                    Toast.makeText(AddJsonRoomActivity.this, "Room Added Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddJsonRoomActivity.this, ManagerMenuActivity.class));
                } else {
                    Toast.makeText(AddJsonRoomActivity.this, "Please enter the name of the file", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String loadJSONFromAsset(String fileName) {
        String json = null;
        try {
            InputStream is = getAssets().open(fileName + ".json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
