package ac.jejunu.photify.fragment;

import java.util.Random;

import ac.jejunu.photify.R;
import ac.jejunu.photify.entity.Person;
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
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.UiThread;

@EFragment(R.layout.activity_map)
public class AroundMapMarkerFragment extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener, OnCameraChangeListener,
		ClusterManager.OnClusterClickListener<Person>, ClusterManager.OnClusterInfoWindowClickListener<Person>, ClusterManager.OnClusterItemClickListener<Person>,
		ClusterManager.OnClusterItemInfoWindowClickListener<Person> {
	
	private GoogleMap map;
	private LocationClient locationClient;
	private ClusterManager<Person> clusterManager;
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
			clusterManager = new ClusterManager<Person>(getActivity(), map);
			clusterManager.setRenderer(new PersonRenderer(map));
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
					
					clusterManager.addItem(new Person(position(), "Teach", R.drawable.daumlogo));
					updateClusters();
				}
			});
			
			addItems();
			updateClusters();
		}
	}
	
	private boolean isMapExist() {
		return map != null;
	}
	
	@UiThread
	void updateClusters() {
		clusterManager.cluster();
	}
	
	private class PersonRenderer extends DefaultClusterRenderer<Person> {
		private final IconGenerator mIconGenerator = new IconGenerator(getActivity());
		private final IconGenerator mClusterIconGenerator = new IconGenerator(getActivity());
		private final ImageView mImageView;
		private final ImageView mClusterImageView;
		private final int mDimension;
		
		public PersonRenderer(GoogleMap mMap) {
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
		
		// 우리는 각각 마커로 그리기 때문에
		// 클러스터로 그리지 않기에 onBeforeClusterRendered()는 필요가 없음.
		// 바로 이 onBeforeClusterItemRendered() 이 함수만 호출됨.
		// @Override
		// protected void onBeforeClusterItemRendered(Person person,
		// MarkerOptions markerOptions) {
		// Draw a single person.
		// Set the info window to show their name.
		// mImageView.setImageResource(person.profilePhoto); // 그냥 여기에
		// Drawable
		// 을 셋팅해 주면 될거
		// 같음.
		// Bitmap icon = mIconGenerator.makeIcon();
		// markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(person.name);
		// }
		
		@Override
		protected void onClusterItemRendered(Person clusterItem, Marker marker) {
			super.onClusterItemRendered(clusterItem, marker);
			mImageView.setImageResource(R.drawable.daumlogo);// 여기서 그냥 clusterItem으로 부터 리소스 받아다가 아이콘 그리도록 하면돨듯!
			// 당연히 드로워블로 던져줘야함.
			Bitmap icon = mIconGenerator.makeIcon();
			marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
		}
		
//		@Override
//		protected void onClusterRendered(Cluster<Person> cluster, Marker marker) {
//			super.onClusterRendered(cluster, marker);
//			Log.e("onClusterRendered", "size : " + cluster.getSize());
//			
//			for (Person p : cluster.getItems()) {
//				mImageView.setImageResource(p.profilePhoto);
//				Bitmap icon = mIconGenerator.makeIcon();
//				BitmapDescriptor fromBitmap = BitmapDescriptorFactory.fromBitmap(icon);
//				marker.setIcon(fromBitmap);
//				
//				Log.e("onClusterItemRendered", p.toString());
//			}
//			updateClusters();
//		}
		
		@Override
		protected boolean shouldRenderAsCluster(Cluster cluster) {
			return false; // cluster.getSize() > 1;
		}
	}
	
	private void addItems() {
		// TODO 동적으로 아래 에드아이템 해놓고
		// mClusterManager.cluster();를 호출해주면 알아서 그려줌. 이게 notify()같음.
		clusterManager.addItem(new Person(position(), "Walter", R.drawable.walter));
		clusterManager.addItem(new Person(position(), "Gran", R.drawable.gran));
		clusterManager.addItem(new Person(position(), "Ruth", R.drawable.ruth));
		clusterManager.addItem(new Person(position(), "Stefan", R.drawable.stefan));
		clusterManager.addItem(new Person(position(), "Mechanic", R.drawable.mechanic));
		clusterManager.addItem(new Person(position(), "Yeats", R.drawable.yeats));
		clusterManager.addItem(new Person(position(), "John", R.drawable.john));
		clusterManager.addItem(new Person(position(), "Trevor the Turtle", R.drawable.turtle));
		clusterManager.addItem(new Person(position(), "Teach", R.drawable.teacher));
		clusterManager.addItem(new Person(position(), "Teach", R.drawable.daumlogo));
	}
	
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
	public void onClusterItemInfoWindowClick(Person item) {
	}
	
	@Override
	public boolean onClusterItemClick(Person item) {
		return false;
	}
	
	@Override
	public void onClusterInfoWindowClick(Cluster<Person> cluster) {
	}
	
	@Override
	public boolean onClusterClick(Cluster<Person> cluster) {
		return false;
	}
}
