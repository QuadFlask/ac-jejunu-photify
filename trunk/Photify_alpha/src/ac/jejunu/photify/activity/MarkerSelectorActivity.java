package ac.jejunu.photify.activity;

import ac.jejunu.photify.R;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_marker_selector)
public class MarkerSelectorActivity extends FragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener, OnCameraChangeListener, LocationListener {
	
	@ViewById(R.id.btn_select)
	Button btnSelect;
	
	private GoogleMap map;
	private LocationClient mLocationClient;
	private LatLng currentlyMarkedPosition;
	
	@AfterViews
	void afterViews() {
		btnSelect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = getIntent();
				intent.putExtra("lat", currentlyMarkedPosition.latitude);
				intent.putExtra("lng", currentlyMarkedPosition.longitude);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
		setUpLocationClientIfNeeded();
		mLocationClient.connect();
		mark();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (mLocationClient != null) {
			mLocationClient.disconnect();
			mark();
		}
	}
	
	public synchronized void goCurrentLocation() {
		Location lcCurrent = map.getMyLocation();
		if (lcCurrent != null) {
			map.getProjection().toScreenLocation(new LatLng(lcCurrent.getLatitude(), lcCurrent.getLongitude()));
		}
	}
	
	private void setUpMapIfNeeded() {
		if (map == null) {
			map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			if (map != null) map.setMyLocationEnabled(true);
		}
	}
	
	private void mark() {
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng latLng) {
				MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.position(latLng);
				
				map.clear();
				map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
				map.addMarker(markerOptions);
				
				currentlyMarkedPosition = latLng;
			}
		});
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	}
	
	private void setUpLocationClientIfNeeded() {
		if (mLocationClient == null) mLocationClient = new LocationClient(getApplicationContext(), this, this);
	}
	
	@Override
	public void onLocationChanged(Location location) {
	}
	
	@Override
	public void onConnected(Bundle connectionHint) {
	}
	
	@Override
	public void onDisconnected() {
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
	}
	
	@Override
	public void onCameraChange(CameraPosition position) {
	}
	
	@Override
	public void onProviderDisabled(String provider) {
	}
	
	@Override
	public void onProviderEnabled(String provider) {
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
}
