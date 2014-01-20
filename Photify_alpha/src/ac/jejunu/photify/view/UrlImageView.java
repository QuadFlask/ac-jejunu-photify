package ac.jejunu.photify.view;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * an {@link ImageView} supporting asynchronous loading from URL. Additional
 * APIs: {@link #setImageURL(URL)}, {@link #cancelLoading()}.
 *
 * @author ep@gplushub.com / Eugen Plischke
 */
public class UrlImageView extends ImageView {

	private static class UrlLoadingTask extends AsyncTask<URL, Void, Bitmap> {
		private final ImageView updateView;
		private boolean isCancelled = false;

		private InputStream urlInputStream;

		private UrlLoadingTask(ImageView updateView) {
			this.updateView = updateView;
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
						; // swallow
					} finally {
						this.urlInputStream = null;
					}
				}
			}
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (!this.isCancelled) {
				// hope that call is thread-safe
				this.updateView.setImageBitmap(result);
			}
		}

		/*
		 * just remember that we were cancelled, no synchronization necessary
		 */
		@Override
		protected void onCancelled() {
			this.isCancelled = true;
			try {
				if (this.urlInputStream != null) {
					try {
						this.urlInputStream.close();
					} catch (IOException e) {
						;// swallow
					} finally {
						this.urlInputStream = null;
					}
				}
			} finally {
				super.onCancelled();
			}
		}
	}

	/*
	   * track loading task to cancel it
	   */
	private AsyncTask<URL, Void, Bitmap> currentLoadingTask;

	/*
	   * just for sync
	   */
	private Object loadingMonitor = new Object();

	private int defaultBackgroundColor = 0xffccff00;

	private URL url;

	public UrlImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public UrlImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public UrlImageView(Context context) {
		super(context);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		cancelLoading();
		super.setImageBitmap(bm);
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		cancelLoading();
		super.setImageDrawable(drawable);
	}

	@Override
	public void setImageResource(int resId) {
		cancelLoading();
		super.setImageResource(resId);
	}

	@Override
	public void setImageURI(Uri uri) {
		cancelLoading();
		super.setImageURI(uri);
	}

	/**
	 * loads image from given url
	 *
	 * @param url
	 */
	public void setImageURL(URL url) {
		this.url = url;
		synchronized (loadingMonitor) {
			cancelLoading();
			this.currentLoadingTask = new UrlLoadingTask(this).execute(url);
		}
	}

	@Override
	public void setVisibility(int visibility) {
		if (visibility != getVisibility()) {
			if (visibility == View.VISIBLE) reloadImage();
			else delayedRecycle(5);
		}
		super.setVisibility(visibility);
	}

	public void reloadImage() {
		this.setImageURL(this.url);
	}

	private void delayedRecycle(int delayInSeconds) {
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				recycleImage();
			}
		}, delayInSeconds * 1000);
	}

	public void recycleImage() {
		try {
			cancelLoading();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Drawable drawable = getDrawable();
		if (drawable != null) {
			Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
			if (bitmap != null)
				bitmap.recycle();
		}
	}

	public void setDefaultBackgroundColor(int defaultBackgroundColor) {
		this.defaultBackgroundColor = defaultBackgroundColor;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Drawable drawable = getDrawable();
		if (drawable != null) {
			Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
			if (bitmap == null || bitmap.isRecycled()) {
				canvas.drawColor(defaultBackgroundColor);
				return;
			}
		}
		super.onDraw(canvas);
	}

	/**
	 * cancels pending image loading
	 */
	public void cancelLoading() {
		synchronized (loadingMonitor) {
			if (this.currentLoadingTask != null) {
				this.currentLoadingTask.cancel(true);
				this.currentLoadingTask = null;
			}
		}
	}
}