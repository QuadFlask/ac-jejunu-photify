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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
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

	@ViewById(R.id.scroll_container)
	LinearLayout scrollContainer;

	@RestService
	ReadArticleClient readArticleClient;

	@RestService
	ReadFacebookArticleClient readFacebookArticleClient;

	private MasonryGridView masonryGridView;

	private static Gson gson;

	private int lastNo = Integer.MAX_VALUE;
	private boolean isReceived = true;
	private boolean isOver = false;
	private int limit = 6;

	@AfterViews
	void afterViews() {
		masonryGridView = new MasonryGridView(getActivity(), 2);
		scrollContainer.addView(masonryGridView);
		isOver = false;

		masonryGridView.addOnScrollBottomListener(this);
		gson = new Gson();

		getListFromServer();
	}

	@Override
	public void onScrollBottom(int diff) {
		if (!isOver && diff <= 200)
			getListFromServer();
	}

	@Background
	public void getListFromServer() {
		synchronized (this){
			if(isReceived){
				isReceived = false;
				try {
					ArticleCommand[] data = gson.fromJson(readArticleClient.readArticleList("recent", lastNo, limit), ArticleCommand[].class);
					List<ArticleCommand> articleList = new ArrayList<ArticleCommand>();
					
					if(data.length == 0) isOver= true;
					
					for (ArticleCommand a : data) {
						articleList.add(a);
						lastNo = Math.min(lastNo, a.getNo());
					}
		
					for (ArticleCommand a : articleList) {
						try {
							View gridView = makeGridView(a);
							addGridItem(gridView);
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
		
		Log.e("canvas", ""+c.getAvgcolor());

		return new GridItem(
				getActivity(), 
				fbArticle.getImages()[4].getSource(), 
				(int) (fbArticle.getImages()[4].getHeight() * 232f / fbArticle.getImages()[4].getWidth()),
				fbArticle.getFrom().getProfileImage(),
				fbArticle.getFrom().getName(), 
				fbArticle.getName(), 
				c.getAvgcolor());
	}
}
