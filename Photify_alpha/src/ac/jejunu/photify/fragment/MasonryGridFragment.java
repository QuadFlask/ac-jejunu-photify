package ac.jejunu.photify.fragment;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import ac.jejunu.photify.R;
import ac.jejunu.photify.activity.DetailedActivity_;
import ac.jejunu.photify.entity.ArticleCommand;
import ac.jejunu.photify.entity.FacebookArticle;
import ac.jejunu.photify.entity.LikeCount;
import ac.jejunu.photify.rest.ReadArticleClient;
import ac.jejunu.photify.rest.ReadFacebookArticleClient;
import ac.jejunu.photify.view.GridItem;
import ac.jejunu.photify.view.MasonryGridView;
import ac.jejunu.photify.view.OnScrollBottomListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.rest.RestService;

@EFragment(R.layout.fragment_masonry)
public class MasonryGridFragment extends Fragment implements OnScrollBottomListener, OnClickListener {
	private static Gson gson = new Gson();
	
	@ViewById(R.id.scroll_container)
	LinearLayout scrollContainer;
	
	@RestService
	ReadArticleClient readArticleClient;
	
	@RestService
	ReadFacebookArticleClient readFacebookArticleClient;
	
	private MasonryGridView masonryGridView;
	
	private int lastNo = Integer.MAX_VALUE;
	private int limit = 8, columns = 3;
	private boolean isReceived = true;
	private boolean isOver = false;
	private float imageViewWidth = 232f;
	
	@AfterViews
	void afterViews() {
		int width = getScreenSize().x;
		
		columns = Math.round(width / 240f);
		limit = Math.round(width / 50f);
		imageViewWidth = ((float) width / columns - 4f * 2f);
		
		lastNo = Integer.MAX_VALUE;
		isReceived = true;
		isOver = false;
		
		masonryGridView = new MasonryGridView(getActivity(), columns);
		masonryGridView.addOnScrollBottomListener(this);
		scrollContainer.addView(masonryGridView);
		
		getListFromServer(limit);
	}
	
	@Override
	public void onScrollBottom(int diff) {
		if (!isOver && diff <= 300) getListFromServer(limit);
	}
	
	@Background
	public void getListFromServer(int limit) {
		synchronized (this) {
			if (isReceived) {
				isReceived = false;
				try {
					ArticleCommand[] data = gson.fromJson(readArticleClient.readArticleList("recent", lastNo, limit), ArticleCommand[].class);
					
					if (data.length == 0) {
						isOver = true;
						return;
					}
					
					List<ArticleCommand> articleList = new ArrayList<ArticleCommand>();
					for (ArticleCommand a : data) {
						try {
							articleList.add(a);
							addGridItem(makeGridView(a));
							lastNo = Math.min(lastNo, a.getNo());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				isReceived = true;
			}
		}
	}
	
	@UiThread
	public void addGridItem(View item) {
		masonryGridView.addView(item);
	}
	
	// TODO 여기서 페이스북 데이터 가져오는 부분은 비동기로 처리해 줘야함.
	// 페북에서 데이터 가져오는것 때문에 느림.
	private View makeGridView(ArticleCommand c) throws MalformedURLException {
		String id = c.getId();
		FacebookArticle fbArticle = gson.fromJson(readFacebookArticleClient.getArticle(id), FacebookArticle.class);
		LikeCount likeCount = gson.fromJson(readFacebookArticleClient.getLikeCount(id), LikeCount.class);
		int commentCount = 0;
		if (fbArticle.getComments() != null && fbArticle.getComments().getData() != null) commentCount = fbArticle.getComments().getData().length;
		
		GridItem gridItem = new GridItem(getActivity(), //
				id, fbArticle.getImages()[4].getSource(), //
				Math.round(imageViewWidth), (int) (fbArticle.getImages()[4].getHeight() * imageViewWidth / fbArticle.getImages()[4].getWidth()), //
				fbArticle.getFrom().getProfileImage(), //
				fbArticle.getFrom().getName(), //
				fbArticle.getName(), //
				likeCount.getTotalCount(), //
				commentCount, c.getAvgcolor());
		gridItem.setOnClickListener(this);
		return gridItem;//
	}
	
	private Point getScreenSize() {
		Point size = new Point();
		Display d = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		size.x = d.getWidth();
		size.y = d.getHeight();
		return size;
	}
	
	@Override
	public void onClick(View v) {
		if (v instanceof GridItem) {
			GridItem gridItem = (GridItem) v;
			Intent intent = new Intent(getActivity(), DetailedActivity_.class);
			intent.putExtra("POST_ID", gridItem.getPostId());
			startActivity(intent);
		}
	}
}
