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
    //FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
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
            //just here to view data
            /*TextView tvUsername = findViewById(R.id.tvUsername);
            tvUsername.setText(String.valueOf(longitude));*/
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
        /*EditText etTime = findViewById(R.id.etTime);
        EditText etDate = findViewById(R.id.etDate);*/
        Button btnLogout = findViewById(R.id.btnLogout);
        /*Button btnConfirmCovid = findViewById(R.id.btnConfirmCovid);
        Button btnSubmitData = findViewById(R.id.btnSubmitData);*/
        Button btnSetLocation = findViewById(R.id.btnSetLocation);
        Intent intent = getIntent();
        String username = intent.getStringExtra("USERNAME");
        tvUsername.setText(username);
        //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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
                /*SetLocationDialogFragment aSetLocationDialogFragment = new SetLocationDialogFragment();
                aSetLocationDialogFragment.show(getSupportFragmentManager(), "location");*/
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
//                HealthData avgHealthData = dataAccessObject.getAVGHealthData();
                boolean success = dataAccessObject.addUserToCovidInfoTable(user);
                if (success) {
                    Integer covidContacts = getCovidContacts(dataAccessObject, user);
                    if (covidContacts > 0) {
                        showAlertDialog(covidContacts);
                    }
                }
                /*if (avgHealthData != null) {
                    Toast.makeText(MainActivity.this, user.compare(avgHealthData),
                            Toast.LENGTH_LONG).show();
                }*/
            }
        }
    }

    /*@Override
    public void onSetLocationDialogPositiveClick(DialogFragment dialog) {
        getLastLocation();
        showDatePickerDialog();
    }*/

    /*@SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            mLatitude = location.getLatitude();
                            mLongitude = location.getLongitude();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }*/

    /*@SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }*/

    /*private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            mLatitude = mLastLocation.getLatitude();
            mLongitude = mLastLocation.getLongitude();
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        //return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }*/

    /*@Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }*/
}