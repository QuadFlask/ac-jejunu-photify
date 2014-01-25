package ac.jejunu.photify.fragment;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import ac.jejunu.photify.R;
import ac.jejunu.photify.entity.PhotoMarker;
import ac.jejunu.photify.view.OnUrlImageLoadCompletedCallback;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.UiThread;

@EFragment(R.layout.activity_map)
public class AroundMapMarkerFragment2 extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener, OnCameraChangeListener,
		ClusterManager.OnClusterClickListener<PhotoMarker>, ClusterManager.OnClusterInfoWindowClickListener<PhotoMarker>, ClusterManager.OnClusterItemClickListener<PhotoMarker>,
		ClusterManager.OnClusterItemInfoWindowClickListener<PhotoMarker> {
	
	private GoogleMap map;
	private LocationClient locationClient;
	private ClusterManager<PhotoMarker> clusterManager;
	boolean isFirst = true;
	private static final LocationRequest REQUEST = LocationRequest.create().setInterval(5000) // 5
			// seconds
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	
	@AfterViews
	public void afterViews() {
		initMap();
	}
	
	void initMap() {
		if (locationClient == null) {
			locationClient = new LocationClient(getActivity(), this, this);
		}
		locationClient.connect();
		
		if (!isMapExist()) {
			map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		}
		if (isMapExist()) {
			clusterManager = new ClusterManager<PhotoMarker>(getActivity(), map);
			clusterManager.setRenderer(new PhotoMarkerRenderer(map));
			clusterManager.setOnClusterClickListener(this);
			clusterManager.setOnClusterInfoWindowClickListener(this);
			clusterManager.setOnClusterItemClickListener(this);
			clusterManager.setOnClusterItemInfoWindowClickListener(this);
			
			map.setMyLocationEnabled(true); // 현재위치를 가져갈 수 있도록 설정
			map.setOnCameraChangeListener(clusterManager);
			map.setOnMarkerClickListener(clusterManager);
			map.setOnInfoWindowClickListener(clusterManager);
			map.setOnCameraChangeListener(new OnCameraChangeListener() {
				@Override
				public void onCameraChange(CameraPosition cameraPosition) {
					// Make a web call for the locations
					LatLng abc = map.getCameraPosition().target;
					double lat = abc.latitude;
					double longti = abc.longitude;
					
					Toast toastView = Toast.makeText(getActivity(), Double.toString(lat) + ", " + Double.toString(longti), Toast.LENGTH_LONG);
					toastView.show();
					makeItem();
				}
			});
			
			updateClusters();
		}
	}
	
	private boolean isMapExist() {
		return map != null;
	}
	
	@Background
	void makeItem() {
		String[] urls = new String[] { 
				"http://icon.daumcdn.net/w/icon/1312/19/152729032.png",//
				"http://img.naver.net/static/www/u/2013/0731/nmms_224940510.gif",//
				"http://img.naver.net/static/newsstand/up/2013/0718/nsd101142358.gif",//
				"http://img.naver.net/static/newsstand/up/2013/0319/nsd124110953.gif",//
				"https://cdn3.iconfinder.com/data/icons/faticons/32/refresh-01-64.png"
				};
		try {
			final PhotoMarker photoMarker = new PhotoMarker("", position());
			photoMarker.setImageURL(new URL(urls[(int) (Math.random() * 4)]), new OnUrlImageLoadCompletedCallback() {
				public void onCompleted() {
					clusterManager.addItem(photoMarker);
					updateClusters();
				}
				
				@Override
				public void onCompleted(Bitmap bitmap) {
				}
			});
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	@UiThread
	void updateClusters() {
		clusterManager.cluster();
	}
	
	private class PhotoMarkerRenderer extends DefaultClusterRenderer<PhotoMarker> {
		private final IconGenerator mIconGenerator = new IconGenerator(getActivity());
		private final IconGenerator mClusterIconGenerator = new IconGenerator(getActivity());
		private final ImageView mImageView;
		private final ImageView mClusterImageView;
		private final int mDimension;
		
		public PhotoMarkerRenderer(GoogleMap mMap) {
			super(getActivity(), mMap, clusterManager);
			
			View multiProfile = getActivity().getLayoutInflater().inflate(R.layout.multi_profile, null);
			mClusterIconGenerator.setContentView(multiProfile);
			mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);
			
			// mImageView = new ImageView(getActivity());
			mImageView = new ImageView(getActivity());
			mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
			mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
			int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
			mImageView.setPadding(padding, padding, padding, padding);
			mIconGenerator.setContentView(mImageView);
		}
		
		@Override
		protected void onClusterItemRendered(PhotoMarker clusterItem, Marker marker) {
			super.onClusterItemRendered(clusterItem, marker);
			// 여기서 그냥
			// clusterItem으로
			// 부터 리소스 받아다가
			// 아이콘 그리도록
			// 하면돨듯!
			
			mImageView.setImageBitmap(clusterItem.getBitmap());
			// 당연히 드로워블로 던져줘야함.
			Bitmap icon = mIconGenerator.makeIcon();
			marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
		}
		
		@Override
		protected boolean shouldRenderAsCluster(Cluster cluster) {
			return false; // cluster.getSize() > 1;
		}
	}
	
	// private void addItems() {
	// // TODO 동적으로 아래 에드아이템 해놓고
	// // mClusterManager.cluster();를 호출해주면 알아서 그려줌. 이게 notify()같음.
	// clusterManager.addItem(new PhotoMarker(position(), "Walter",
	// R.drawable.walter));
	// }
	
	private LatLng position() {
		return new LatLng(random(-180, 180) / 4, random(-90, 90) / 4);
	}
	
	private Random mRandom = new Random();
	
	private double random(double min, double max) {
		return mRandom.nextDouble() * (max - min) + min;
	}
	
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
	public void onLocationChanged(Location location) {
		if (location != null) {
			if (isFirst == false) return;
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 16));
			isFirst = false;
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (locationClient != null) {
			locationClient.disconnect();
		}
	}
	
	@Override
	public void onConnected(Bundle connectionHint) {
		locationClient.requestLocationUpdates(REQUEST, this); // LocationListener
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
	public void onClusterItemInfoWindowClick(PhotoMarker item) {
	}
	
	@Override
	public boolean onClusterItemClick(PhotoMarker item) {
		return false;
	}
	
	@Override
	public void onClusterInfoWindowClick(Cluster<PhotoMarker> cluster) {
	}
	
	@Override
	public boolean onClusterClick(Cluster<PhotoMarker> cluster) {
		return false;
	}
	
}
