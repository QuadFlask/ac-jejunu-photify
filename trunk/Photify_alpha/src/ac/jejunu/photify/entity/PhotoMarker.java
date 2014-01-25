package ac.jejunu.photify.entity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import ac.jejunu.photify.view.OnUrlImageLoadCompletedCallback;
import ac.jejunu.photify.view.UrlImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class PhotoMarker implements ClusterItem, OnUrlImageLoadCompletedCallback {
	private String address = "unknown";
	private URL url;
	private LatLng position;
	
	private Object loadingMonitor = new Object();
	private AsyncTask<URL, Void, Bitmap> currentLoadingTask;
	private Bitmap bitmap;
	private OnUrlImageLoadCompletedCallback callback;
	
	public PhotoMarker(String address, LatLng position) {
		super();
		this.address = address;
		this.position = position;
	}
	
	public void setImageURL(URL url, OnUrlImageLoadCompletedCallback callback) {
		this.url = url;
		this.callback = callback;
		synchronized (loadingMonitor) {
			cancelLoading();
			this.currentLoadingTask = new UrlLoadingTask(this).execute(url);
		}
	}
	
	public void cancelLoading() {
		synchronized (loadingMonitor) {
			if (this.currentLoadingTask != null) {
				this.currentLoadingTask.cancel(true);
				this.currentLoadingTask = null;
			}
		}
	}
	
	public void recycleImage() {
		try {
			cancelLoading();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (getBitmap() != null) getBitmap().recycle();
	}
	
	private static class UrlLoadingTask extends AsyncTask<URL, Void, Bitmap> {
		private boolean isCancelled = false;
		
		private InputStream urlInputStream;
		private OnUrlImageLoadCompletedCallback callback;
		
		private UrlLoadingTask(OnUrlImageLoadCompletedCallback callback) {
			this.callback = callback;
		}
		
		@Override
		protected Bitmap doInBackground(URL... params) {
			try {
				URLConnection con = params[0].openConnection();
				// can use some more params, i.e. caching directory etc
				con.setUseCaches(true);
				this.urlInputStream = con.getInputStream();
				return BitmapFactory.decodeStream(urlInputStream);
			} catch (IOException e) {
				Log.w(UrlImageView.class.getName(), "failed to load image from " + params[0], e);
				return null;
			} finally {
				if (this.urlInputStream != null) {
					try {
						this.urlInputStream.close();
					} catch (IOException e) {
					} finally {
						this.urlInputStream = null;
					}
				}
			}
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			if (!this.isCancelled) {
				if (callback != null) callback.onCompleted(result);
			}
		}
		
		@Override
		protected void onCancelled() {
			this.isCancelled = true;
			try {
				if (this.urlInputStream != null) {
					try {
						this.urlInputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						this.urlInputStream = null;
					}
				}
			} finally {
				super.onCancelled();
			}
		}
	}
	
	@Override
	public void onCompleted() {
	}
	
	@Override
	public void onCompleted(Bitmap bitmap) {
		this.bitmap = bitmap;
		if (callback != null) callback.onCompleted();
	}
	
	@Override
	public LatLng getPosition() {
		return position;
	}
	
	public String getAddress() {
		return address;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
}
