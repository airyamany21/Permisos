package com.example.googlemaps;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import static com.example.googlemaps.R.*;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final int ACEPTAR_PERMISOS = 120;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng lastLong;
    private GoogleMap mMap;
    private MediaPlayer mediaPlayer;

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void comenzarLocalizacion() {

        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        //getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            Location loc = locationManager.getLastKnownLocation(
                    LocationManager.NETWORK_PROVIDER);
        }
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(),
                        location.getLongitude());
                if (lastLong == null) {
                    lastLong = latLng;

                }
                mMap.addMarker(new MarkerOptions().
                        position(latLng).title("My potition")
                        .snippet("Soy el SNIPPET!!"));
                Polyline Line = mMap.addPolyline(new PolylineOptions()
                        .add(lastLong, latLng).width(10).color(Color.RED));
                Circle circle = mMap.addCircle(new CircleOptions()
                        .center(lastLong).radius(3)
                        .strokeColor(Color.GREEN)
                        .fillColor(Color.BLUE));
//                mMap.addMarker(new MarkerOptions().position(latLng).title("My position").snippet("Soy el SNIPPET!!"));
                lastLong = latLng;
                mMap.moveCamera(CameraUpdateFactory.
                        newLatLngZoom(latLng, 21));
                Vibrator v = (Vibrator) getSystemService(
                        Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(
                            500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(500);
                }
                managerOfSong();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                System.err.println("Provider Status:" + status);
            }
            @Override
            public void onProviderEnabled(String provider) {
                System.err.println("Provider ON");
            }
            @Override
            public void onProviderDisabled(String provider) {
                System.err.println("Provider OFF");
            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                9000, 1, locationListener);
        // }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == ACEPTAR_PERMISOS) {

            if (permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }

        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.setOnMyLocationClickListener(
                    new GoogleMap.OnMyLocationClickListener() {
                        @Override
                        public void onMyLocationClick(@NonNull Location location) {

                            Toast.makeText(MapsActivity.this,
                                    ("Lat:[" +
                                            location.getLatitude()
                                            + "] Longitud:[" + location.getLongitude() + "]"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
            mMap.setOnMyLocationButtonClickListener(
                    new GoogleMap.OnMyLocationButtonClickListener() {
                        @Override
                        public boolean onMyLocationButtonClick() {
                            return false;
                        }
                    });
            Toast.makeText(this, "Tiene permisos", Toast.LENGTH_SHORT).show();

        } else {
            String[] permi = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permi, ACEPTAR_PERMISOS);
            Toast.makeText(this, "No tiene permisos", Toast.LENGTH_SHORT).show();
        }
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(32.62781, -115.45446);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Puro chicali hommie"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        comenzarLocalizacion();
    }
    protected void managerOfSong() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, raw.pain);
        mediaPlayer.start();
    }
}