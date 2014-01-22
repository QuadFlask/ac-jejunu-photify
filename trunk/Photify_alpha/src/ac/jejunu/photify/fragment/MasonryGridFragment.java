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

	@AfterViews
	void afterViews() {
		masonryGridView = new MasonryGridView(getActivity(), 2);
		scrollContainer.addView(masonryGridView);
		
		masonryGridView.addOnScrollBottomListener(this);
		gson = new Gson();
		
		getListFromServer();
	}

	@Override
	public void onScrollBottom(int diff) {
		if (diff <= 200) 
			getListFromServer();
	}

	@Background
	public void getListFromServer() {
		// TODO 가져올때 이미 가져온것은 가져 오지 않도록 해야함. 
		// 또 파라미터 둬서 현재 몇번 인덱스까지 클라가 가지고 있으니 그 다음 부분을 보내줘.. 이런식으로 해줘야함,..,
		ArticleCommand[] data = gson.fromJson(readArticleClient.readArticleList("recent"), ArticleCommand[].class);
		List<ArticleCommand> articleList = new ArrayList<ArticleCommand>();
		
		for (ArticleCommand a : data)
			articleList.add(a);

		for (ArticleCommand a : articleList) {
			try {
				View gridView = makeGridView(a);
				addGridItem(gridView);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@UiThread
	public void addGridItem(View item) {
		masonryGridView.addView(item);
	}

	private View makeGridView(ArticleCommand c) throws MalformedURLException{
		String id = c.getId();
		FacebookArticle fbArticle = gson.fromJson(readFacebookArticleClient.getArticle(id), FacebookArticle.class);
		
		return new GridItem(getActivity(), 
				fbArticle.getImages()[3].getSource(), 
				fbArticle.getImages()[3].getHeight(),
				fbArticle.getFrom().getProfileImage(),
				fbArticle.getFrom().getName(), 
				fbArticle.getName(),
				c.getAvgColor());
	}
}
