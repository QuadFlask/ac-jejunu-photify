package ac.jejunu.photify.fragment;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import ac.jejunu.photify.R;
import ac.jejunu.photify.entity.ArticleCommand;
import ac.jejunu.photify.entity.FacebookArticle;
import ac.jejunu.photify.rest.ReadArticleClient;
import ac.jejunu.photify.rest.ReadFacebookArticleClient;
import ac.jejunu.photify.view.GridItem;
import ac.jejunu.photify.view.MasonryGridView;
import ac.jejunu.photify.view.OnScrollBottomListener;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.View;
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
public class MasonryGridFragment extends Fragment implements OnScrollBottomListener {
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
		Display d = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int width = d.getWidth();
		
		columns = Math.round(width / 240f);
		limit = Math.round(width / 50f);
		imageViewWidth = (width - 4f * columns) / columns;
		
		lastNo = Integer.MAX_VALUE;
		isReceived = true;
		isOver = false;
		
		masonryGridView = new MasonryGridView(getActivity(), columns);
		scrollContainer.addView(masonryGridView);
		
		masonryGridView.addOnScrollBottomListener(this);
		
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
					List<ArticleCommand> articleList = new ArrayList<ArticleCommand>();
					
					if (data.length == 0) {
						isOver = true;
						return;
					}
					
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
	
	private View makeGridView(ArticleCommand c) throws MalformedURLException {
		String id = c.getId();
		FacebookArticle fbArticle = gson.fromJson(readFacebookArticleClient.getArticle(id), FacebookArticle.class);
		
		return new GridItem(getActivity(), //
				fbArticle.getImages()[4].getSource(), //
				(int) (fbArticle.getImages()[4].getHeight() * imageViewWidth / fbArticle.getImages()[4].getWidth()), //
				fbArticle.getFrom().getProfileImage(), //
				fbArticle.getFrom().getName(), //
				fbArticle.getName(), //
				c.getAvgcolor());//
	}
}
