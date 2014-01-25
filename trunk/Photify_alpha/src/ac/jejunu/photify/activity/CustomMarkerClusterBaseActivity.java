package ac.jejunu.photify.activity;

import ac.jejunu.photify.R;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public abstract class CustomMarkerClusterBaseActivity extends FragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener,
		OnCameraChangeListener, OnClickListener {
	private GoogleMap mMap;
	boolean bFirst = true;
	private LocationClient mLocationClient;

	private static final LocationRequest REQUEST = LocationRequest.create().setInterval(5000) // 5seconds
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	protected int getLayoutId() {
		return R.layout.activity_map;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
		setUpLocationClientIfNeeded();
		mLocationClient.connect();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}
	}

	public synchronized void goCurrentLocation() {
	}

	private void setUpMapIfNeeded() {
		if (mMap != null) {
			return;
		}
		mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		if (mMap != null) {
			mMap.setMyLocationEnabled(true); // 현재위치를 가져갈 수 있도록 설정
			startDemo();
			setUpMap();
		}
	}

	private void setUpLocationClientIfNeeded() {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(getApplicationContext(), this, // ConnectionCallbacks
					this); // OnConnectionFailedListener
		}
	}

	private void setUpMap() {
		if (mMap != null) {
			mMap.setOnCameraChangeListener(new OnCameraChangeListener() {
				@Override
				public void onCameraChange(CameraPosition cameraPosition) {
					// Make a web call for the locations
					LatLng abc = mMap.getCameraPosition().target;
					double lat = abc.latitude;
					double longti = abc.longitude;

					Toast toastView = Toast.makeText(getApplicationContext(), Double.toString(lat) + ", " + Double.toString(longti), Toast.LENGTH_LONG);
					toastView.show();
				}
			});
		}
	}

	public void showMyLocation(View view) {
		if (mLocationClient != null && mLocationClient.isConnected()) {
			String msg = "Location = " + mLocationClient.getLastLocation();
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			if (bFirst == false)
				return;
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 16));
			bFirst = false;
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mLocationClient.requestLocationUpdates(REQUEST, this); // LocationListener
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

	protected abstract void startDemo();

	protected GoogleMap getMap() {
		setUpMapIfNeeded();
		return mMap;
	}
}
