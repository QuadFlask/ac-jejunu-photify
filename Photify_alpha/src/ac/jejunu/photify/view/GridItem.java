package ac.jejunu.photify.view;

import java.net.MalformedURLException;
import java.net.URL;

import ac.jejunu.photify.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GridItem extends LinearLayout {
	private View view;
	private String postId;
	private String photoUrl;
	private String profilePhoto;
	private String name;
	private String contents;
	private int defaultBackgroundColor = 0xffffffff;
	private UrlImageView ivItemImage, ivProfilepic;
	
	public GridItem(Context context, String postId, String photoUrl, int width, int height, String profilePhoto, String name, String contents, int likes, int comments,
			int defaultBackgroundColor) throws MalformedURLException {
		super(context);
		this.photoUrl = photoUrl;
		this.postId = postId;
		this.profilePhoto = profilePhoto;
		this.name = name;
		this.contents = contents;
		this.defaultBackgroundColor = defaultBackgroundColor;
		
		LayoutInflater inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.item, this);
		
		ivItemImage = (UrlImageView) view.findViewById(R.id.item_image);
		ivItemImage.setImageURL(new URL(photoUrl));
		ivItemImage.setLayoutParams(new LinearLayout.LayoutParams(width, height));
		ivItemImage.setDefaultBackgroundColor(0xff000000 | defaultBackgroundColor);
		
		ivProfilepic = (UrlImageView) view.findViewById(R.id.iv_profilepic);
		ivProfilepic.setImageURL(new URL(profilePhoto));
		ivProfilepic.setDefaultBackgroundColor(0xffffffff);
		
		TextView tvName = (TextView) view.findViewById(R.id.tv_name);
		tvName.setText(name);
		TextView tvContents = (TextView) view.findViewById(R.id.tv_contents);
		tvContents.setText(contents);
		TextView tvLikeCount = (TextView) view.findViewById(R.id.tv_like_count);
		tvLikeCount.setText("" + likes);
		TextView tvCommentCount = (TextView) view.findViewById(R.id.tv_comment_count);
		tvCommentCount.setText("" + comments);
	}
	
	@Override
	public void setVisibility(int visibility) {
		ivItemImage.setVisibility(visibility);
		ivProfilepic.setVisibility(visibility);
	}
	
	public String getPostId() {
		return postId;
	}
}