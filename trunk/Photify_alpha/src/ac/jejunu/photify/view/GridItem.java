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
	private String photoUrl;
	private String profilePhoto;
	private String name;
	private String contents;
	private int defaultBackgroundColor = 0xffff00ff;
	private UrlImageView ivItemImage, ivProfilepic;

	public GridItem(Context context, String photoUrl, int height, String profilePhoto, String name, String contents, int defaultBackgroundColor) throws MalformedURLException {
		super(context);
		this.photoUrl = photoUrl;
		this.profilePhoto = profilePhoto;
		this.name = name;
		this.contents = contents;
		this.defaultBackgroundColor = defaultBackgroundColor;

		LayoutInflater inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.item, this);

		ivItemImage = (UrlImageView) view.findViewById(R.id.item_image);
		ivItemImage.setImageURL(new URL(photoUrl));
		ivItemImage.setMaxWidth(220);
		ivItemImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, height));
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