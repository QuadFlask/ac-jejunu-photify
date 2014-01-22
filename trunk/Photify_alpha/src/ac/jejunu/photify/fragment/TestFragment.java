package ac.jejunu.photify.fragment;

import ac.jejunu.photify.R;
import ac.jejunu.photify.entity.ArticleCommand;
import ac.jejunu.photify.rest.WriteArticleClient;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class TestFragment extends Fragment {
	public static final int REQUEST_CODE_FROM_GALLERY = 0x10;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		TextView textView = (TextView) rootView.findViewById(R.id.section_label);
		textView.setText("this is test fragment.");
		
		Button btn = (Button) rootView.findViewById(R.id.btn_test);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startGallery();
			}
		});
		return rootView;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// super.onActivityResult(requestCode, resultCode, data);
		if (intent == null || resultCode != Activity.RESULT_OK) return;
		
		switch (requestCode) {
			case REQUEST_CODE_FROM_GALLERY: {
				Uri selectedImage = intent.getData();
				String filePath = getFilePath(selectedImage);
				
				ArticleCommand cmd = new ArticleCommand();
				cmd.setAttachPath(filePath);
				cmd.setFbid("1076011818");
				cmd.setLat(123);
				cmd.setLng(456);
				cmd.setContent("this is content");
				
				WriteArticleClient client = new WriteArticleClient();
				client.write(cmd);
				
				break;
			}
		}
	}
	
	private String getFilePath(Uri selectedImage) {
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		
		Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
		cursor.moveToFirst();
		
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String filePath = cursor.getString(columnIndex);
		cursor.close();
		return filePath;
	}
	
	protected void startGallery() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, REQUEST_CODE_FROM_GALLERY);
	}
}
