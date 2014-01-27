package ac.jejunu.photify;

import ac.jejunu.photify.activity.PhotoInputActivity_;
import ac.jejunu.photify.fragment.AroundMapMarkerFragment_;
import ac.jejunu.photify.fragment.MasonryGridFragment_;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
	
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	private Button btnPostPhoto;
	
	private int[] tabIcons = new int[] { R.drawable.around, R.drawable.masonry_list, R.drawable.my_list };
	private int[] tabIcons_ = new int[] { R.drawable.around_, R.drawable.masonry_list_, R.drawable.my_list_ };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});
		
		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab().setIcon(tabIcons[i]).setTabListener(this));
			// .setText(mSectionsPagerAdapter.getPageTitle(i))
		}
		inflatedLayout();
		initialize();
	}
	
	private void inflatedLayout() {
		Window win = getWindow();
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout linear = (LinearLayout) inflater.inflate(R.layout.mainbutton_overlay, null);
		
		LinearLayout.LayoutParams paramlinear = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		win.addContentView(linear, paramlinear);
	}
	
	private void initialize() {
		btnPostPhoto = (Button) findViewById(R.id.btn_post_photo);
		btnPostPhoto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, PhotoInputActivity_.class);
				startActivity(intent);
			}
		});
	}
	
	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
		tab.setIcon(tabIcons[tab.getPosition()]);
	}
	
	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		tab.setIcon(tabIcons_[tab.getPosition()]);
	}
	
	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}
	
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		private FragmentPlaceHolder holder;
		
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			holder = new FragmentPlaceHolder();
		}
		
		@Override
		public Fragment getItem(int position) {
			return holder.getFragment(position);
		}
		
		@Override
		public int getCount() {
			return holder.getCount();
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return holder.getTitle(position);
		}
	}
	
	public static class FragmentPlaceHolder {
		private Fragment[] fragments = new Fragment[] {//
		new AroundMapMarkerFragment_(), //
				new MasonryGridFragment_(), //
				new MasonryGridFragment_(), //
		};
		private String[] titles = new String[] { "TEST1", "TEST2", "TEST3", "TEST4", "TEST5", };
		
		public Fragment getFragment(int index) {
			return fragments[index];
		}
		
		public String getTitle(int index) {
			return titles[index];
		}
		
		public int getCount() {
			return fragments.length;
		}
	}
	
}