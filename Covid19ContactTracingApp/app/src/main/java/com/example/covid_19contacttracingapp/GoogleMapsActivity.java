package com.example.covid_19contacttracingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class GoogleMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap map;
    SupportMapFragment mapFragment;
    SearchView searchView;
    Button btnSubmitLocation;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);

        searchView=findViewById(R.id.searchView);
        mapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.googleMaps);
        btnSubmitLocation=findViewById(R.id.btnSubmitLocation);
        final LatLng[] latLng = new LatLng[1];
        Intent intent =getIntent();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location=searchView.getQuery().toString();
                List<Address> adressList=null;

                if (location!=null || !location.equals("")){
                    Geocoder geocoder=new Geocoder(GoogleMapsActivity.this);
                    try {
                        adressList=geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //cant send back shit
                    Address adress=adressList.get(0);
                    latLng[0] =new LatLng(adress.getLatitude(),adress.getLongitude());
                    map.addMarker(new MarkerOptions().position(latLng[0]).title(location));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng[0], 10));

                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mapFragment.getMapAsync(this);

        btnSubmitLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("LATLNG", latLng[0].toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
    }
}