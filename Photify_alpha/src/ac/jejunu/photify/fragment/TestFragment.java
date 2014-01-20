package ac.jejunu.photify.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ac.jejunu.photify.R;

public class TestFragment extends Fragment {

	private String title = "Test fragment!";

	public TestFragment(String title) {
		this.title = title;
	}

	public TestFragment(){}

	public String getTitle() {
		return title;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		TextView textView = (TextView) rootView.findViewById(R.id.section_label);
		textView.setText("this is test fragment.");
		return rootView;
	}
}