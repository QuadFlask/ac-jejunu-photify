package ac.jejunu.photify.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
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

		// int[] backgroundColor = new int[] { 0xffff0000, 0xffff8800,
		// 0xffff00ff, 0xff00ff00, 0xff00ffff, 0xff0000ff };
		for (int i = 0; i < Math.max(1, column); i++) {
			StackLinearLayout stackLinearLayout = new StackLinearLayout(context, new LinearLayout.LayoutParams(240, LinearLayout.LayoutParams.WRAP_CONTENT, 1f),
					LinearLayout.VERTICAL);
			// stackLinearLayout.setBackgroundColor(backgroundColor[i]);
			columns.add(stackLinearLayout);
		}
		reloadColumn();
	}

	@Override
	public void addView(View child) {
		StackLinearLayout layout = getMinimumHeightLayout();
		children.add(child);
		layout.addView(child);
	}

	private void reloadColumn() {
		columnContainer.removeAllViews();
		for (StackLinearLayout col : columns)
			columnContainer.addView(col.getLayout());
	}

	private StackLinearLayout getMinimumHeightLayout() {
		StackLinearLayout min = columns.get(0);
		for (StackLinearLayout col : columns)
			if (min.getHeight() > col.getHeight())
				min = col;
		return min;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		Rect scrollBounds = new Rect();
		for (View view : children) {
			if (!view.getLocalVisibleRect(scrollBounds))
				view.setVisibility(View.INVISIBLE);
			else
				view.setVisibility(View.VISIBLE);
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

class StackLinearLayout {
	private int height;
	private LinearLayout layout;

	public StackLinearLayout(Context context, LinearLayout.LayoutParams params, int orientation) {
		layout = new LinearLayout(context);
		layout.setOrientation(orientation);
		layout.setLayoutParams(params);
	}

	public int getHeight() {
		return height;
	}

	public void setBackgroundColor(int color) {
		layout.setBackgroundColor(color);
	}

	public void addView(View view) {
		layout.addView(view);
		increaseHeight(view);
	}

	public LinearLayout getLayout() {
		return layout;
	}

	private void increaseHeight(View view) {
		view.measure(ViewGroup.MeasureSpec.UNSPECIFIED, ViewGroup.MeasureSpec.UNSPECIFIED);
		height += view.getMeasuredHeight();
	}
}