package com.example.covid_19contacttracingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, Covid19DialogFragment.Covid19DialogListener {
    Calendar mCalender = Calendar.getInstance();
    Double mLatitude, mLongitude;
    Boolean mCovid19Positive;
    private final int LAUNCH_GOOGLE_MAPS = 1;

    @Override
    public String toString() {
        return "HomeActivity{}";
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent locationIntent) {
        super.onActivityResult(requestCode, resultCode, locationIntent);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            mLatitude = locationIntent.getDoubleExtra("latitude", 0);
            mLongitude = locationIntent.getDoubleExtra("longitude", 0);
            showDatePickerDialog();
        }
    }

    public void showCovid19InfoDialog() {
        DialogFragment covid19DialogFragment = new Covid19DialogFragment();
        covid19DialogFragment.show(getSupportFragmentManager(), "covid19Info");
    }

    public void showDatePickerDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment aDatePickerFragment = new DatePickerFragment();
        aDatePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment aTimePickerFragment = new TimePickerFragment();
        aTimePickerFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mCalender.set(Calendar.YEAR, year);
        mCalender.set(Calendar.MONTH, month);
        mCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        showTimePickerDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView tvUsername = findViewById(R.id.tvUsername);
        Button btnLogout = findViewById(R.id.btnLogout);
        Button btnSetLocation = findViewById(R.id.btnSetLocation);
        Intent intent = getIntent();
        String username = intent.getStringExtra("USERNAME");
        tvUsername.setText(username);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        btnSetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),GoogleMapsActivity.class);
                startActivityForResult(intent, LAUNCH_GOOGLE_MAPS);
            }
        });

        try (DataAccessObject dataAccessObject = new DataAccessObject(HomeActivity.this)) {
            List<User> userTuplesList = dataAccessObject.getUserTuples(username);
            Integer covidContacts = 0;
            for (User user : userTuplesList) {
                covidContacts += getCovidContacts(dataAccessObject, user);
            }
            if (covidContacts > 0) {
                showAlertDialog(covidContacts);
            }
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mCalender.set(Calendar.MINUTE, minute);
        showCovid19InfoDialog();
    }

    @Override
    public void onCovid19DialogPositiveClick(DialogFragment dialog) {
        mCovid19Positive = true;
        addUserInfoToCovidTable();
    }

    @Override
    public void onCovid19DialogNegativeClick(DialogFragment dialog) {
        mCovid19Positive = false;
        addUserInfoToCovidTable();
    }

    private Integer getCovidContacts(DataAccessObject dataAccessObject, User user) {
        List<User> userSubList = dataAccessObject.getCovid19InfoSubList(user.getUsername());
        Integer covidContacts = dataAccessObject.checkForCovidContacts(user, userSubList);
        return covidContacts;
    }

    public void showAlertDialog(Integer covidContacts) {
        new AlertDialog.Builder(HomeActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Stay home!")
                .setMessage("You've been near covid infected people " + covidContacts.toString() + " time(s)!")
                .setNegativeButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                .show();
    }

    public void addUserInfoToCovidTable() {
        try (DataAccessObject dataAccessObject = new DataAccessObject(HomeActivity.this)) {
            User user = null;
            TextView tvUsername = findViewById(R.id.tvUsername);
            String username = tvUsername.getText().toString();
            try {
                user = new User(-1, username, mLatitude, mLongitude, mCalender.getTimeInMillis(), mCovid19Positive);
            } catch (Exception e) {
                Toast.makeText(HomeActivity.this, "Infos wurden nicht gespeichert!",
                        Toast.LENGTH_LONG).show();
            }
            if (user != null) {
                boolean success = dataAccessObject.addUserToCovidInfoTable(user);
                if (success) {
                    Integer covidContacts = getCovidContacts(dataAccessObject, user);
                    if (covidContacts > 0) {
                        showAlertDialog(covidContacts);
                    }
                }
            }
        }
    }
}