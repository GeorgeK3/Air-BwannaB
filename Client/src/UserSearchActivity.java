package com.example.frontendds;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

public class UserSearchActivity extends AppCompatActivity {
    Button mButton;
    public static JSONObject filter;
    private EditText fromDate;
    private EditText toDate;
    private EditText noPeople;
    private EditText location;
    private EditText price;
    private RatingBar rating;
    private String stars;
    private Calendar fromDateCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fromDate = findViewById(R.id.fromDateInput);
        toDate = findViewById(R.id.toDateInput);

        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        UserSearchActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                fromDateCalendar = Calendar.getInstance();
                                fromDateCalendar.set(year, monthOfYear, dayOfMonth);
                                fromDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                toDate.setText(""); // Clear toDate if fromDate is changed
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
                        UserSearchActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar toDateCalendar = Calendar.getInstance();
                                toDateCalendar.set(year, monthOfYear, dayOfMonth);

                                if (fromDateCalendar != null && toDateCalendar.before(fromDateCalendar)) {
                                    toDate.setText("");
                                    Toast.makeText(UserSearchActivity.this, "End date must be after start date", Toast.LENGTH_SHORT).show();
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

        mButton = findViewById(R.id.button);
        noPeople = findViewById(R.id.peopleInput);
        location = findViewById(R.id.locationInput);
        price = findViewById(R.id.priceInput);
        rating = findViewById(R.id.ratingBar);

        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fixText(location);
                fixText(noPeople);
                fixText(price);
                if (rating.getRating() == 0.0f) {
                    stars = "-";
                } else {
                    stars = Float.toString(rating.getRating());
                }

                filter = new JSONObject();
                try {
                    filter.put("area", location.getText().toString());
                    if (!(fromDate.getText().toString().trim().length() == 0) & !(toDate.getText().toString().trim().length() == 0)) {
                        filter.put("date", fromDate.getText().toString() + " - " + toDate.getText().toString());
                    } else {
                        filter.put("date", "-");
                    }
                    filter.put("noOfPersons", noPeople.getText().toString());
                    filter.put("price", price.getText().toString());
                    filter.put("stars", stars);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                Log.d("FILTER", filter.toString());
                startActivity(new Intent(UserSearchActivity.this, ResultsActivity.class));

                location.setText("");
                noPeople.setText("");
                fromDate.setText("");
                toDate.setText("");
                price.setText("");
            }
        });
    }

    private void fixText(EditText etText) {
        if (etText.getText().toString().trim().length() == 0) {
            etText.setText("-");
        }
    }
}
