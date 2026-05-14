package com.example.lostandfoundapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int MAP_PERMISSION_CODE = 200;

    GoogleMap googleMap;
    DatabaseHelper databaseHelper;
    FusedLocationProviderClient fusedLocationClient;
    float radiusKm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        databaseHelper = new DatabaseHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        radiusKm = getIntent().getFloatExtra("radiusKm", 10);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        checkPermissionAndShowMap();
    }

    private void checkPermissionAndShowMap() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MAP_PERMISSION_CODE
            );

            return;
        }

        googleMap.setMyLocationEnabled(true);
        showNearbyAdverts();
    }

    private void showNearbyAdverts() {

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(myLocation -> {

                    if (myLocation == null) {
                        Toast.makeText(this, "Turn on device location and try again", Toast.LENGTH_LONG).show();
                        return;
                    }

                    LatLng myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

                    googleMap.addMarker(new MarkerOptions()
                            .position(myLatLng)
                            .title("You are here")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 12));

                    ArrayList<Advert> adverts = databaseHelper.getAllAdverts();

                    int shownItems = 0;

                    for (Advert advert : adverts) {

                        if (advert.getLatitude() == 0.0 && advert.getLongitude() == 0.0) {
                            continue;
                        }

                        float[] distanceResult = new float[1];

                        Location.distanceBetween(
                                myLocation.getLatitude(),
                                myLocation.getLongitude(),
                                advert.getLatitude(),
                                advert.getLongitude(),
                                distanceResult
                        );

                        float distanceKm = distanceResult[0] / 1000;

                        if (distanceKm <= radiusKm) {

                            LatLng advertLatLng = new LatLng(advert.getLatitude(), advert.getLongitude());

                            float markerColour;

                            if (advert.getPostType().equals("Lost")) {
                                markerColour = BitmapDescriptorFactory.HUE_RED;
                            } else {
                                markerColour = BitmapDescriptorFactory.HUE_GREEN;
                            }

                            String distanceText = String.format(Locale.getDefault(), "%.2f km away", distanceKm);

                            googleMap.addMarker(new MarkerOptions()
                                    .position(advertLatLng)
                                    .title(advert.getPostType() + ": " + advert.getName())
                                    .snippet(advert.getCategory() + " - " + distanceText)
                                    .icon(BitmapDescriptorFactory.defaultMarker(markerColour)));

                            shownItems++;
                        }
                    }

                    Toast.makeText(
                            this,
                            "Showing " + shownItems + " items within " + radiusKm + " km",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MAP_PERMISSION_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            checkPermissionAndShowMap();

        } else {
            Toast.makeText(this, "Location permission is needed for the map", Toast.LENGTH_SHORT).show();
        }
    }
}