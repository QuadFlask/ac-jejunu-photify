package ac.jejunu.photify.activity;

import java.util.Hashtable;

import ac.jejunu.photify.R;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class CustomInfoWindowAdapter implements InfoWindowAdapter {
	
	private Activity activity;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	
	private Hashtable<String, String> markers;
	
	public CustomInfoWindowAdapter(Activity activity) {
		this.activity = activity;
		
		imageLoader = initImageLoader();
		markers = new Hashtable<String, String>();
	}
	
	public void addMarker(Marker marker, String url) {
		markers.put(marker.getId(), url);
	}
	
	@Override
	public View getInfoWindow(final Marker marker) {
		String url = markers.get(marker.getId());
		return getMarkerImageViewWithUrl(marker, url);
	}
	
	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}
	
	private View getMarkerImageViewWithUrl(final Marker marker, String url) {
		View view = activity.getLayoutInflater().inflate(R.layout.custom_info_window, null);
		
		final ImageView image = ((ImageView) view.findViewById(R.id.badge));
		
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.ic_launcher)
		// Display Stub Image
				.showImageForEmptyUri(R.drawable.ic_launcher)
				// If Empty image found
				.cacheInMemory().cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();
		
		if (url != null && !url.equalsIgnoreCase("null") && !url.equalsIgnoreCase("")) {
			imageLoader.displayImage(url, image, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					super.onLoadingComplete(imageUri, view, loadedImage);
					getInfoContents(marker);
				}
			});
		} else {
			image.setImageResource(R.drawable.ic_launcher);
		}
		
		return view;
	}
	
	public ImageLoader initImageLoader() {
		int memoryCacheSize;
		
		int memClass = ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		memoryCacheSize = (memClass / 8) * 1024 * 1024;
		
		final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(activity).threadPoolSize(5).threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCacheSize(memoryCacheSize).memoryCache(new FIFOLimitedMemoryCache(memoryCacheSize - 1000000)).denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO).enableLogging().build();
		
		ImageLoader instance = ImageLoader.getInstance();
		instance.init(config);
		
		return instance;
	}
	
}
