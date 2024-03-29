package ac.jejunu.photify.activity.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.rest.RestService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import ac.jejunu.photify.R;
import ac.jejunu.photify.command.ArticleCommand;
import ac.jejunu.photify.entity.Article;
import ac.jejunu.photify.rest.ArticleCommandRestClient;
import ac.jejunu.photify.view.MasonryGridView;
import ac.jejunu.photify.view.OnScrollBottomListener;
import ac.jejunu.photify.view.UrlImageView;

@EFragment(R.layout.fragment_masonry)
public class MasonryGridFragment extends Fragment implements OnScrollBottomListener {

	@ViewById(R.id.scroll_container)
	LinearLayout scrollContainer;

	@RestService
	ArticleCommandRestClient articleCommandRestClient;

	private MasonryGridView masonryGridView;

	@AfterViews
	void afterViews() {
		masonryGridView = new MasonryGridView(getActivity(), 2);
		scrollContainer.addView(masonryGridView);
		addSampleImages(32);
		masonryGridView.addOnScrollBottomListener(this);
	}

	// TODO 현재 사용하는 pull to refresh가 더이상 관리 되지 않음.
	// ㄷ따라서.... 새로운걸로 갈아타아껬당
	// https://github.com/nhnopensource/android-pull-to-refresh
	// 여기는 관리도 꽤 잘되고 좀더 다양한 옵션(탄성계수라던지) 조절을 할 수 있다고 한다!

	@Override
	public void onScrollBottom(int diff) {
		if (diff <= 200) {
			Log.e("MasonryGridFragment", "scroll is bottom!");
			addSampleImages(8);
		}
	}

	private void addSampleImages(int count) {
		try {
			for (int i = 0; i < count; i++) {
				int height = (int) (80 + Math.random() * 250);
				masonryGridView.addView(getSampleImageView(height));
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private View getSampleImageView(int height) throws MalformedURLException {
		return new GridItem(
				getActivity(),
				"https://cdn1.iconfinder.com/data/icons/Map-Markers-Icons-Demo-PNG/256/Map-Marker-Marker-Outside-Pink.png",
				height,
				"https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash2/c5.5.65.65/s56x56/374637_189230964497111_247316888_t.jpg",
				"Flask" + height,
				"contents..."
		);
	}
}

class GridItem extends LinearLayout {
	private View view;
	private String photoUrl;
	private String profilePhoto;
	private String name;
	private String contents;
	private int defaultBackgroundColor = 0xffff00ff;
	UrlImageView ivItemImage, ivProfilepic;

	public GridItem(Context context, String photoUrl, int height, String profilePhoto, String name, String contents) throws MalformedURLException {
		super(context);
		this.photoUrl = photoUrl;
		this.profilePhoto = profilePhoto;
		this.name = name;
		this.contents = contents;

		LayoutInflater inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.item, this);

		ivItemImage = (UrlImageView) view.findViewById(R.id.item_image);
		ivItemImage.setImageURL(new URL(photoUrl));
		ivItemImage.setMaxWidth(220);
		ivItemImage.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, height));
		ivItemImage.setDefaultBackgroundColor(defaultBackgroundColor);

		ivProfilepic = (UrlImageView) view.findViewById(R.id.iv_profilepic);
		ivProfilepic.setImageURL(new URL(profilePhoto));
		ivProfilepic.setDefaultBackgroundColor(0xffff00cc);

		TextView tvName = (TextView) view.findViewById(R.id.tv_name);
		tvName.setText(name);
		TextView tvContents = (TextView) view.findViewById(R.id.tv_contents);
		tvContents.setText(contents);
	}

	@Override
	public void setVisibility(int visibility) {
		ivItemImage.setVisibility(visibility);
		ivProfilepic.setVisibility(visibility);
	}
}