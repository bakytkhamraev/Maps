package com.example.maps;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {
    
    private String longitude = "longitude";
    private String latitude = "latitude";
    private static String TAG = "ololo";
    private int count;
    private GoogleMap map;
    private SupportMapFragment fragment;
    private Button drawPolygon;
    private List<LatLng> coordinates;
    private SharedPreferences preferences = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawPolygon = findViewById(R.id.button);
        preferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        coordinates = getLatLngs();

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        assert mapFragment != null;
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                CameraPosition cameraPosition = CameraPosition.builder().target(new LatLng(42.8706944, 74.5836545)).zoom(11.67f).build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                    @Override
                    public void onMapClick(LatLng latLng) {
                        map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker()));
                        coordinates.add(latLng);
                    }
                });
            }
        });

        drawPolygon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PolygonOptions polygonOptions = new PolygonOptions();
                polygonOptions.strokeWidth(5f);
                polygonOptions.strokeColor(getResources().getColor(R.color.colorAccent));
                for (LatLng latLng1 : coordinates) {
                    polygonOptions.add(latLng1);
                }
                map.addPolygon(polygonOptions);
            }
        });
    }


    public List<LatLng> getLatLngs() {
        count = preferences.getInt("count", 0);
        Log.e(TAG, "getLatLngs: count" + count);
        List<LatLng> latLngs = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            latLngs.add(new LatLng(
                    preferences.getFloat(latitude + i, 0),
                    preferences.getFloat(longitude + i, 0)
            ));
        }
        Log.e("ololo", "getLatLngs: " + latLngs.toString());
        return latLngs;
    }

    public void setLatLng(List<LatLng> latLngs) {
        for (int i = 0; i < latLngs.size(); i++) {
            preferences
                    .edit()
                    .putFloat(latitude + i, (float) latLngs.get(i).latitude)
                    .putFloat(longitude + i, (float) latLngs.get(i).longitude)
                    .apply();
            count++;
        }
        preferences.edit().putInt("count", count).apply();
        Log.e(TAG, "setLatLng: " + getLatLngs().toString());
    }

    @Override
    protected void onPause() {
        super.onPause();
        preferences.edit().clear().apply();
        setLatLng(coordinates);
    }

}