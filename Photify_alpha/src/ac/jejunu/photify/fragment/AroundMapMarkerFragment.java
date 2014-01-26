package ac.jejunu.photify.fragment;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

import ac.jejunu.photify.R;
import ac.jejunu.photify.entity.ArticleCommand;
import ac.jejunu.photify.entity.FacebookArticle;
import ac.jejunu.photify.entity.PhotoMarker;
import ac.jejunu.photify.rest.ReadArticleClient;
import ac.jejunu.photify.rest.ReadFacebookArticleClient;
import ac.jejunu.photify.view.OnUrlImageLoadCompletedCallback;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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
import com.google.gson.Gson;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.rest.RestService;

@EFragment(R.layout.activity_map)
public class AroundMapMarkerFragment extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener, OnCameraChangeListener,
		ClusterManager.OnClusterClickListener<PhotoMarker>, ClusterManager.OnClusterInfoWindowClickListener<PhotoMarker>, ClusterManager.OnClusterItemClickListener<PhotoMarker>,
		ClusterManager.OnClusterItemInfoWindowClickListener<PhotoMarker> {
	
	private GoogleMap map;
	private LocationClient locationClient;
	private ClusterManager<PhotoMarker> clusterManager;
	boolean isFirst = true;
	private HashSet<String> ids = new HashSet<String>();
	private static final LocationRequest REQUEST = LocationRequest.create().setInterval(5000) // 5
																								// seconds
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	
	@RestService
	ReadArticleClient readArticleClient;
	
	@RestService
	ReadFacebookArticleClient readFacebookArticleClient;
	
	@AfterViews
	public void afterViews() {
		initMap();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		initMap();
	}
	
	void initMap() {
		if (locationClient == null) {
			locationClient = new LocationClient(getActivity(), this, this);
		}
		locationClient.connect();
		ids.clear();
		
		if (!isMapExist()) map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
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
			map.setOnCameraChangeListener(this);
			
			updateClusters();
		}
	}
	
	private boolean isMapExist() {
		return map != null;
	}
	
	@Background
	void makeItem(LatLng position, URL url) {
		final PhotoMarker photoMarker = new PhotoMarker("", position);
		photoMarker.setImageURL(url, new OnUrlImageLoadCompletedCallback() {
			public void onCompleted() {
				clusterManager.addItem(photoMarker);
				updateClusters();
			}
			
			@Override
			public void onCompleted(Bitmap bitmap) {
			}
		});
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
			mImageView.setImageBitmap(clusterItem.getBitmap());
			Bitmap icon = mIconGenerator.makeIcon();
			marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
		}
		
		@Override
		protected boolean shouldRenderAsCluster(Cluster cluster) {
			return false;
		}
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		
		try {
			map = null;
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
	
	private static Gson gson = new Gson();
	
	@Override
	public void onCameraChange(CameraPosition position) {
		// readArticleClient
		// Make a web call for the locations
		LatLng abc = map.getCameraPosition().target;
		double lat = abc.latitude;
		double lng = abc.longitude;
		
		Toast toastView = Toast.makeText(getActivity(), Double.toString(lat) + ", " + Double.toString(lng), Toast.LENGTH_LONG);
		toastView.show();
		
		fetch(lat, lng);
	}
	
	@Background
	void fetch(double lat, double lng) {
		ArticleCommand[] data = gson.fromJson(readArticleClient.readArticleList((int) (lat * 1000000), (int) (lng * 1000000)), ArticleCommand[].class);
		String id;
		for (ArticleCommand a : data) {
			Log.e("fetch photo markers", a.toString());
			try {
				if (!ids.contains(id = a.getId())) {
					ids.add(id);
					FacebookArticle fbArticle = gson.fromJson(readFacebookArticleClient.getArticle(a.getId()), FacebookArticle.class);
					makeItem(a.getPositionAsLatLng(), new URL(fbArticle.getImages()[fbArticle.getImages().length - 1].getSource()));
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onDisconnected() {
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
	}
	
	@Override
	public void onClusterItemInfoWindowClick(PhotoMarker item) {
	}
	
	@Override
	public boolean onClusterItemClick(PhotoMarker item) {
		return false;
	}
	
	@Override
	public boolean onClusterClick(Cluster<PhotoMarker> cluster) {
		return false;
	}
	
	@Override
	public void onClusterInfoWindowClick(Cluster<PhotoMarker> cluster) {
	}
	
}
