package ac.jejunu.photify.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class MasonryGridView extends ScrollView {
	private LinearLayout columnContainer;
	private List<StackLinearLayout> columns;
	private List<View> children;
	private List<OnScrollBottomListener> onScrollBottomListeners;
	
	public MasonryGridView(Context context, int column) {
		super(context);
		columnContainer = new LinearLayout(context);
		columns = new ArrayList<StackLinearLayout>();
		children = new ArrayList<View>();
		onScrollBottomListeners = new ArrayList<OnScrollBottomListener>();
		super.addView(columnContainer);
		
		columnContainer.setOrientation(LinearLayout.HORIZONTAL);
		columnContainer.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		for (int i = 0; i < Math.max(1, column); i++) {
			StackLinearLayout stackLinearLayout = new StackLinearLayout(context, new LinearLayout.LayoutParams(240, LinearLayout.LayoutParams.WRAP_CONTENT, 1f),
					LinearLayout.VERTICAL);
			columns.add(stackLinearLayout);
		}
		reloadColumn();
	}
	
	@Override
	public void addView(View child) {
		StackLinearLayout layout = getMinimumHeightColumn();
		children.add(child);
		layout.addView(child);
	}
	
	private void reloadColumn() {
		columnContainer.removeAllViews();
		for (StackLinearLayout col : columns)
			columnContainer.addView(col.getLayout());
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
		getHitRect(scrollBounds);
		for (View view : children) {
			if (!view.getLocalVisibleRect(scrollBounds)) view.setVisibility(View.INVISIBLE);
			else view.setVisibility(View.VISIBLE);
		}
		fireOnScrollBottom();
	}
	
	private void fireOnScrollBottom() {
		int diff = (columns.get(0).getHeight() - (getHeight() + getScrollY()));
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
