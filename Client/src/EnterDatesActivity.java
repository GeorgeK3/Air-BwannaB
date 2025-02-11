package com.example.frontendds;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class EnterDatesActivity extends AppCompatActivity {
    Button mButton;
    private EditText fromDate;
    private EditText toDate;
    private Calendar fromDateCalendar;
    private Calendar toDateCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_enter_dates);

        fromDate = findViewById(R.id.fromDate);
        toDate = findViewById(R.id.toDate);

        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        EnterDatesActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                fromDateCalendar = Calendar.getInstance();
                                fromDateCalendar.set(year, monthOfYear, dayOfMonth);

                                // Check if the selected date is before today
                                Calendar today = Calendar.getInstance();
                                if (fromDateCalendar.before(today)) {
                                    Toast.makeText(EnterDatesActivity.this, "Start date cannot be before today", Toast.LENGTH_SHORT).show();
                                } else {
                                    fromDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                                    // Clear the toDate if it's before the new fromDate
                                    if (toDateCalendar != null && toDateCalendar.before(fromDateCalendar)) {
                                        toDate.setText("");
                                        toDateCalendar = null;
                                    }
                                }
                            }
                        },
                        year, month, day);

                // Set minimum date to today
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        EnterDatesActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                toDateCalendar = Calendar.getInstance();
                                toDateCalendar.set(year, monthOfYear, dayOfMonth);

                                if (fromDateCalendar != null && toDateCalendar.before(fromDateCalendar)) {
                                    Toast.makeText(EnterDatesActivity.this, "End date must be after start date", Toast.LENGTH_SHORT).show();
                                } else {
                                    toDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                }
                            }
                        },
                        year, month, day);

                // Set minimum date to the selected fromDate if available, otherwise today
                if (fromDateCalendar != null) {
                    datePickerDialog.getDatePicker().setMinDate(fromDateCalendar.getTimeInMillis());
                } else {
                    datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                }

                datePickerDialog.show();
            }
        });

        mButton = findViewById(R.id.button_send);
        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (fromDate.getText().toString().trim().isEmpty() || toDate.getText().toString().trim().isEmpty()) {
                    Toast.makeText(EnterDatesActivity.this, "Please enter both dates to continue", Toast.LENGTH_SHORT).show();
                } else {
                    String fromDateStr = fromDate.getText().toString().trim();
                    String toDateStr = toDate.getText().toString().trim();

                    Room room = getIntent().getParcelableExtra("room");
                    JSONObject object2 = null;
                    try {
                        object2 = new JSONObject(room.toJson().toString());
                        object2.put("dateFrom", fromDateStr);
                        object2.put("dateTo", toDateStr);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    Client cl;
                    try {
                        cl = new Client(object2, "update");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    cl.start();

                    startActivity(new Intent(EnterDatesActivity.this, ManagerMenuActivity.class));
                }
            }
        });
    }
}
