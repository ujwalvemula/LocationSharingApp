package com.vemula.locationsharer;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class MapActivity extends FragmentActivity {

	private GoogleMap gmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_layout);
		Bundle i = getIntent().getExtras();
		String latitude = i.getString("latitude");
		String longitude = i.getString("longitude");

		gmap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();

		gmap.setMyLocationEnabled(true);
		gmap.getUiSettings().setMyLocationButtonEnabled(true);

		MarkerOptions marker = new MarkerOptions().position(
				new LatLng(Double.parseDouble(latitude), Double
						.parseDouble(longitude))).title("hello");

		gmap.addMarker(marker);

		CameraPosition cp = new CameraPosition.Builder()
				.target(new LatLng(Double.parseDouble(latitude), Double
						.parseDouble(longitude))).zoom(12).build();

		gmap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));

	}

}
