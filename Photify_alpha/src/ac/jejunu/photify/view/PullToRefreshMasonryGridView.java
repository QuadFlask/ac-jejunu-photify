package ac.jejunu.photify.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

public class PullToRefreshMasonryGridView extends PullToRefreshScrollView {
	private LinearLayout columnContainer;
	private List<StackLinearLayout> columns;
	private List<View> children;
	private List<OnScrollBottomListener> onScrollBottomListeners;
	
	public PullToRefreshMasonryGridView(Context context, int column) {
		super(context);
		columnContainer = new LinearLayout(context);
		columns = new ArrayList<StackLinearLayout>();
		children = new ArrayList<View>();
		onScrollBottomListeners = new ArrayList<OnScrollBottomListener>();
		getRefreshableView().addView(columnContainer);
		
		setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		columnContainer.setOrientation(LinearLayout.HORIZONTAL);
		columnContainer.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		for (int i = 0; i < Math.max(1, column); i++) {
			StackLinearLayout stackLinearLayout = new StackLinearLayout(context, new LinearLayout.LayoutParams(240, LinearLayout.LayoutParams.WRAP_CONTENT, 1f),
					LinearLayout.VERTICAL);
			columns.add(stackLinearLayout);
		}
		
		setOnRefreshListener(new OnRefreshListener<ScrollView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				new GetDataTask(refreshView).execute();
			}
		});
		
		reloadColumn();
	}
	
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {
		PullToRefreshBase mPullRefreshScrollView;
		
		public GetDataTask(PullToRefreshBase mPullRefreshScrollView) {
			this.mPullRefreshScrollView = mPullRefreshScrollView;
		}
		
		@Override
		protected String[] doInBackground(Void... params) {
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String[] result) {
			mPullRefreshScrollView.onRefreshComplete();
			super.onPostExecute(result);
		}
	}
	
	@Override
	public void addView(View child) {
		StackLinearLayout column = getMinimumHeightColumn();
		children.add(child);
		column.addView(child);
		onRefreshComplete();
	}
	
	private void reloadColumn() {
		columnContainer.removeAllViews();
		for (StackLinearLayout col : columns)
			columnContainer.addView(col.getLayout());
		onRefreshComplete();
	}
	
	private StackLinearLayout getMinimumHeightColumn() {
		StackLinearLayout min = columns.get(0);
		for (StackLinearLayout col : columns)
			if (min.getHeight() > col.getHeight()) min = col;
		return min;
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		Rect scrollBounds = new Rect();
		getRefreshableView().getHitRect(scrollBounds);
		Log.e("PullToRefreshMasonryGridView", "onScrollChanged" + scrollBounds.toString());
		
		for (View view : children) {
			if (!view.getLocalVisibleRect(scrollBounds)) view.setVisibility(View.INVISIBLE);
			else view.setVisibility(View.VISIBLE);
		}
		fireOnScrollBottom();
	}
	
	
	
	private void fireOnScrollBottom() {
		int diff = (columns.get(0).getHeight() - (getRefreshableView().getHeight() + getRefreshableView().getScrollY()));
		Log.e("PullToRefreshMasonryGridView", "diff : " + diff);
		for (OnScrollBottomListener listener : onScrollBottomListeners)
			listener.onScrollBottom(diff);
	}
	
	public void addOnScrollBottomListener(OnScrollBottomListener onScrollBottomListener) {
		onScrollBottomListeners.add(onScrollBottomListener);
	}
	
	public void removeOnScrollBottomListener(OnScrollBottomListener onScrollBottomListener) {
		onScrollBottomListeners.remove(onScrollBottomListener);
	}
	
	public void removeAllOnScrollBottomListener() {
		onScrollBottomListeners.clear();
	}
}