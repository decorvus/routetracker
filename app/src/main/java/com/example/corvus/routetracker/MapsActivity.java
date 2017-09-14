package com.example.corvus.routetracker;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
	private GoogleMap mMap;
	LocationManager locationManager;
	private PowerManager.WakeLock wakeLock;

	private boolean isTracking = false;
	private boolean hasAddedMarker = false;
	private PolylineOptions coordinates = new PolylineOptions();
	private Marker m = null;

	@Override
	public void onStart(){
		super.onStart();
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "No Sleep");
		wakeLock.acquire();
	}

	@Override
	public void onStop(){
		super.onStop();
		wakeLock.release();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
			.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		ToggleButton toggleButton = (ToggleButton) findViewById(R.id.trackingToggleButton);
		toggleButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				isTracking = !isTracking;
			}
		});

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			return;
		}

		/**
		* GERALD CODE
		*
		* Gi modify lang nako ang GPS part and gi remove nako ang pag kuha sa latitude and
		* longitude using the internet. So, purely GPS based gyud ang kani nga app.
		*/
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				double latitude = location.getLatitude();
				double longitude = location.getLongitude();
				LatLng latLng = new LatLng(latitude, longitude);

				if (hasAddedMarker) {
					m.setPosition(latLng);
				} else {
					MarkerOptions a = new MarkerOptions().position(latLng);
					m = mMap.addMarker(a);
					hasAddedMarker = true;
				}
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.2f));

				if (isTracking) {
					coordinates.add(latLng);
					mMap.addPolyline(coordinates);
				} else {
					coordinates = new PolylineOptions();
				}
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {}

			@Override
			public void onProviderEnabled(String provider) {}

			@Override
			public void onProviderDisabled(String provider) {}
		});
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
	}
}