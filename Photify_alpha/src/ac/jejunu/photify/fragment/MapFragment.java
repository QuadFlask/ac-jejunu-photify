package ac.jejunu.photify.fragment;

import ac.jejunu.photify.MainActivity;
import ac.jejunu.photify.R;
import ac.jejunu.photify.activity.CustomInfoWindowAdapter;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
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
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EFragment;

@EFragment(R.layout.activity_map)
public class MapFragment extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener, OnCameraChangeListener {
	
	private GoogleMap mMap;
	boolean bFirst = true;
	private LocationClient mLocationClient;
	
	private final LatLng HAMBURG = new LatLng(33.4961664, 126.536036);
	private final LatLng HAMBURG1 = new LatLng(33.4961664, 126.535036);
	
	@AfterViews
	public void afterViews() {
		setUpMapIfNeeded();
		setUpLocationClientIfNeeded();
		mLocationClient.connect();
	}
	
	private static final LocationRequest REQUEST = LocationRequest.create().setInterval(2000) // 5seconds
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		
		try {
			Fragment fragment = (getFragmentManager().findFragmentById(R.id.map));
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.remove(fragment);
			ft.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}
	}
	
	private void setUpMapIfNeeded() {
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				mMap.setMyLocationEnabled(true); // 현재위치를 가져갈 수 있도록 설정
				setUpMap();
			}
		}
	}
	
	private void setUpLocationClientIfNeeded() {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(getActivity(), this, this);
		}
	}
	
	@Background
	void setUpMap() {
		if (mMap != null) {
			CustomInfoWindowAdapter customInfoWindowAdapter = new CustomInfoWindowAdapter(getActivity());
			customInfoWindowAdapter.initImageLoader();
			
			mMap.setInfoWindowAdapter(customInfoWindowAdapter);
			
			final Marker hamburg = mMap.addMarker(new MarkerOptions().position(HAMBURG));
			final Marker hamburg1 = mMap.addMarker(new MarkerOptions().position(HAMBURG1));
			hamburg.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
			
			customInfoWindowAdapter.addMarker(hamburg, "http://hanury.net/wp/wp-content/uploads/2008/01/hanriverhdr-small.jpg");
			customInfoWindowAdapter.addMarker(hamburg1, "http://icon.daumcdn.net/w/icon/1312/19/152729032.png");
		}
		
		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			public void onInfoWindowClick(Marker marker) {
				Intent intent = new Intent(getActivity(), MainActivity.class);
				startActivity(intent);
			}
		});
		
		mMap.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition cameraPosition) {
				LatLng abc = mMap.getCameraPosition().target;
				double lat = abc.latitude;
				double longti = abc.longitude;
				
				// TODO send latlng to server to get markers.
				
				Toast toastView = Toast.makeText(getActivity().getApplicationContext(), Double.toString(lat) + ", " + Double.toString(longti), Toast.LENGTH_LONG);
				toastView.show();
			}
		});
	}
	
	public void showMyLocation(View view) {
		if (mLocationClient != null && mLocationClient.isConnected()) {
			String msg = "Location = " + mLocationClient.getLastLocation();
			Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			if (bFirst == false) return;
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
	
}
