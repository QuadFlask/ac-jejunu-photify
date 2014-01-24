package ac.jejunu.photify.activity;

import java.util.Random;

import ac.jejunu.photify.R;
import ac.jejunu.photify.entity.Person;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class CustomMarkerClusteringActivity extends CustomMarkerClusterBaseActivity implements ClusterManager.OnClusterClickListener<Person>,
		ClusterManager.OnClusterInfoWindowClickListener<Person>, ClusterManager.OnClusterItemClickListener<Person>,
		ClusterManager.OnClusterItemInfoWindowClickListener<Person> {
	private ClusterManager<Person> mClusterManager;
	private Random mRandom = new Random(1984);

	/**
	 * Draws profile photos inside markers (using IconGenerator). When there are
	 * multiple people in the cluster, draw multiple photos (using
	 * MultiDrawable).
	 */
	private class PersonRenderer extends DefaultClusterRenderer<Person> {
		private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
		private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
		private final ImageView mImageView;
		private final ImageView mClusterImageView;
		private final int mDimension;

		public PersonRenderer() {
			super(getApplicationContext(), getMap(), mClusterManager);

			View multiProfile = getLayoutInflater().inflate(R.layout.multi_profile, null);
			mClusterIconGenerator.setContentView(multiProfile);
			mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);

			mImageView = new ImageView(getApplicationContext());
			mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
			mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
			int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
			mImageView.setPadding(padding, padding, padding, padding);
			mIconGenerator.setContentView(mImageView);
		}

		// 우리는 각각 마커로 그리기 때문에
		// 클러스터로 그리지 않기에 onBeforeClusterRendered()는 필요가 없음.
		// 바로 이 onBeforeClusterItemRendered() 이 함수만 호출됨.
		@Override
		protected void onBeforeClusterItemRendered(Person person, MarkerOptions markerOptions) {
			// Draw a single person.
			// Set the info window to show their name.
			mImageView.setImageResource(person.profilePhoto);
			Bitmap icon = mIconGenerator.makeIcon(); // 그냥 여기에  Drawable 을 셋팅해 주면 될거 같음.
			markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(person.name);
		}

		@Override
		protected boolean shouldRenderAsCluster(Cluster cluster) {
			return false; // cluster.getSize() > 1;
		}
	}

	@Override
	public boolean onClusterClick(Cluster<Person> cluster) {
		return false;
	}

	@Override
	public void onClusterInfoWindowClick(Cluster<Person> cluster) {
	}

	@Override
	public boolean onClusterItemClick(Person item) {
		// Does nothing, but you could go into the user's profile page, for
		// example.
		Log.e("onClusterItemClick", "onClusterItemClick");
		mClusterManager.addItem(new Person(position(), "Teach", R.drawable.daumlogo));
		mClusterManager.cluster();

//		Intent intent = new Intent(CustomMarkerClusteringDemoActivity.this, AnotherActivity.class);
//		startActivity(intent);
		return true;
	}

	@Override
	public void onClusterItemInfoWindowClick(Person item) {
	}

	@Override
	public void startDemo() {
		mClusterManager = new ClusterManager<Person>(this, getMap());
		mClusterManager.setRenderer(new PersonRenderer());
		getMap().setOnCameraChangeListener(mClusterManager);
		getMap().setOnMarkerClickListener(mClusterManager);
		getMap().setOnInfoWindowClickListener(mClusterManager);
		mClusterManager.setOnClusterClickListener(this);
		mClusterManager.setOnClusterInfoWindowClickListener(this);
		mClusterManager.setOnClusterItemClickListener(this);
		mClusterManager.setOnClusterItemInfoWindowClickListener(this);

		addItems();
		mClusterManager.cluster();
	}

	private void addItems() {
		// TODO 동적으로 아래 에드아이템 해놓고
		// mClusterManager.cluster();를 호출해주면 알아서 그려줌. 이게  notify()같음.
		mClusterManager.addItem(new Person(position(), "Walter", R.drawable.walter));
		mClusterManager.addItem(new Person(position(), "Gran", R.drawable.gran));
		mClusterManager.addItem(new Person(position(), "Ruth", R.drawable.ruth));
		mClusterManager.addItem(new Person(position(), "Stefan", R.drawable.stefan));
		mClusterManager.addItem(new Person(position(), "Mechanic", R.drawable.mechanic));
		mClusterManager.addItem(new Person(position(), "Yeats", R.drawable.yeats));
		mClusterManager.addItem(new Person(position(), "John", R.drawable.john));
		mClusterManager.addItem(new Person(position(), "Trevor the Turtle", R.drawable.turtle));
		mClusterManager.addItem(new Person(position(), "Teach", R.drawable.teacher));
		mClusterManager.addItem(new Person(position(), "Teach", R.drawable.daumlogo));
	}

	private LatLng position() {
		return new LatLng(random(51.6723432, 51.38494009999999), random(0.148271, -0.3514683));
	}

	private double random(double min, double max) {
		return mRandom.nextDouble() * (max - min) + min;
	}

	@Override
	public void onClick(View v) {
	}
}
