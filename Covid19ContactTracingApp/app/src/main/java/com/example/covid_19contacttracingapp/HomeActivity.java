package com.example.covid_19contacttracingapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {
    private final String TAG="HomeActivity";
    private final int REQ_HOME=2;

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String location=data.getStringExtra("LATLNG");

        //just here to view data
        TextView tvUsername=findViewById(R.id.tvUsername);
        tvUsername.setText(location);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView tvUsername=findViewById(R.id.tvUsername);
        EditText etTime=findViewById(R.id.etTime);
        EditText etDate=findViewById(R.id.etDate);
        Button btnLogout=findViewById(R.id.btnLogout);
        Button btnConfirmCovid=findViewById(R.id.btnConfirmCovid);
        Button btnSubmitData=findViewById(R.id.btnSubmitData);
        Button btnGetLocation=findViewById(R.id.btnGetLocation);


        Intent intent =getIntent();
        tvUsername.setText(intent.getStringExtra("USERNAME"));

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),GoogleMapsActivity.class);
                startActivityForResult(intent, REQ_HOME);
            }
        });
    }
}