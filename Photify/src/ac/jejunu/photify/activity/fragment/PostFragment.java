package ac.jejunu.photify.activity.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.restfb.BinaryAttachment;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import ac.jejunu.photify.R;

public class PostFragment extends Fragment {
	final static String MY_APP_ID = "1433071126925017";
	final static String MY_APP_SECRET = "1d35095948180a07dc68893f0492017d";
	final static String MY_APP_TOKEN = "ffff16834ad4c0c38fc2a495aa7b026f";

	final static String PAGE_ID = "638274506208023";
	final static String ACCESS_TOKEN = "1433071126925017|_h5IC9ElrV4teyYwii8VaWxQ06g";

	static FacebookClient facebookClient = new DefaultFacebookClient(ACCESS_TOKEN);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		TextView textView = (TextView) rootView.findViewById(R.id.section_label);
		textView.setText("this is test PostFragment.");

		new Thread() {
			@Override
			public void run() {
				try {
					publishPhoto(facebookClient, PAGE_ID);
					publishPhoto(facebookClient, "me");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}.start();

		return rootView;
	}

	public void publishPhoto(FacebookClient client, String id) throws FileNotFoundException {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("value", "SELF");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String appStorage = Environment.getExternalStorageDirectory().getPath();
		String path = appStorage + "/PolarClock_background/background.png";

		FacebookType publishPhotoResponse = facebookClient.publish(id + "/photos", FacebookType.class,
				BinaryAttachment.with("cat.png", new FileInputStream(path)),
				Parameter.with("message", "Test cat"));

		System.out.println("Published photo ID: " + publishPhotoResponse.getId());
	}
}
//		FacebookType publishPhotoResponse = client.publish(id + "/photos", FacebookType.class,
//				BinaryAttachment.with("cat.png", new FileInputStream(path)),
////				Parameter.with("privacy", jsonObject.toJSONString()),
//				Parameter.with("message", "photo test3 hidden"));