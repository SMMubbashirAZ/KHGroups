package com.blazeminds.pos;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class RoutePlanMapActivity extends Activity implements OnMapReadyCallback {
    
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private MapView mapView;
    private GoogleMap gmap;
    private TextView titleTV;
    private ImageView backBtn;
    private double latitude = 0, longitude = 0;
    private String lati, longi;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_plan_map);
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            lati = getIntent().getStringExtra("latitude");
            longi = getIntent().getStringExtra("longitude");
            if (!lati.equalsIgnoreCase("")) {
                latitude = Double.parseDouble(lati);
            }
            if (!longi.equalsIgnoreCase("")) {
                longitude = Double.parseDouble(longi);
            }
        }
        
        titleTV = findViewById(R.id.titleTxt);
        backBtn = findViewById(R.id.backBtn);
        
        titleTV.setText(getString(R.string.app_name));
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                finish();
            }
        });
        
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
        
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }
        
        mapView.onSaveInstanceState(mapViewBundle);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(12);
        gmap.setMaxZoomPreference(20);
        LatLng ny = new LatLng(latitude, longitude);
        
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(ny).title(latitude + "," + longitude);
        gmap.addMarker(markerOptions);
        
        gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));
    }
}
