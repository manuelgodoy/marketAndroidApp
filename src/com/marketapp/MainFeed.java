package com.marketapp;


import java.util.ArrayList;
import java.util.List;


import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
//import android.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.widget.Toast;


public class MainFeed extends ActionBarActivity implements	NavigationDrawerFragment.NavigationDrawerCallbacks {
	String feedItem;
	private List<ParseObject> mPhotos = new ArrayList<ParseObject>();
	
	private CharSequence mTitle;
	private NavigationDrawerFragment mNavigationDrawerFragment;
	
	LocationManager locationManager;
	String geoURI;
	
	locationReceiver lr = new locationReceiver();
	
	ParseGeoPoint point = new ParseGeoPoint();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_layout);
		
		
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();		
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,(DrawerLayout) findViewById(R.id.drawer_layout));
		
		Parse.initialize(this, "VzCaiR1xAxw1Xzs7n68DFJvNo8C8Ov80Np4DVNEV", "3Du6C0fPE8IkrLYiPS3MQrl0oSchFU2SkeKzhB1i");
		
		//locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);				
		//locationManager.requestLocationUpdates(getProvider(),10,10,locationListener);
		
		
		Intent serviceIntent = new Intent(this, LocationService.class);
		startService(serviceIntent);
		//Intent intent = serviceIntent;
		Intent intent = getIntent();
		switch (intent.getIntExtra("from",0)) {
		case (1):
			String message = intent.getStringExtra("username");	
			break;
		case (2):
			String toast = intent.getStringExtra("Post");
			Toast.makeText(this, "Post " +toast+ " has been posted." , Toast.LENGTH_LONG).show();
		}
		
		LoadPhotos load = new LoadPhotos();
		load.execute();
		
		//String message = intent.getStringExtra("username");
		

		
		//TextView t = (TextView)findViewById(R.id.textView1);
		//t.setText("This is where the main feed would go ");
		
		//Button b = (Button)findViewById(R.id.button1);
/*		b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainFeed.this, PostActivity.class);
				startActivity(intent);
			};
		});		*/
	}
	
	public void success(List<ParseObject> l) {
		Toast.makeText(this,  l.get(0).getString("Title"), Toast.LENGTH_LONG).show();
	}
	
	private class LoadPhotos extends AsyncTask<String, String, Long> {
		@Override
		protected void onPreExecute () {
		}
		
		@Override
		protected Long doInBackground (String... params) {

			try {				
				ParseQuery<ParseObject> query = ParseQuery.getQuery("TestObject");
				mPhotos = query.find();					
				return (0l);				
			} catch (ParseException e) {
				e.printStackTrace();
				return (1l);
			}
		
		}
		
		@Override
		protected void onPostExecute (Long result) {
			if (result == 0){
				showList();
			} else {
				Toast.makeText(MainFeed.this.getApplicationContext(), "Something went wrong piece of dumb", Toast.LENGTH_SHORT).show();
			}
			//mProgressBar.setVisibility(View.GONE);
		}
	}
	
	public void showList(){
		SimpleListFragment fragmentA = new SimpleListFragment();
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.container, fragmentA);
		ft.addToBackStack("fragment a");
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();
	}
	
	public List<ParseObject> getPhotos() {
		return mPhotos;
	}	
	
	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(position + 1)).commit();
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		}
	}
	
	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		//actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.feed, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		switch (id) {
		/*case R.id.menu_post:
			Intent intent = new Intent(MainFeed.this, PostActivity.class);
			startActivity(intent);
			return true;*/
			
		case R.id.action_settings:
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainFeed) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
		}
	}
	
	
	protected void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter(LocationServicePlay.BROADCAST_ACTION);		
		registerReceiver(lr, filter);
	}
	
	@Override
	protected void onPause() {
		unregisterReceiver(lr);
		super.onPause();
	}
	
	protected void onStop(){
		super.onStop();
		//locationManager.removeUpdates(locationListener);
	}
	
	public class locationReceiver extends BroadcastReceiver {
		//private locationReceiver mLogReceiver = new locationReceiver();
		@Override
		public void onReceive (Context context, Intent intent) {
			Log.i("Received","intent received");
			Double latitude = intent.getDoubleExtra("Latitude",0);
			Double longitude = intent.getDoubleExtra("Longitude",0);
			//String address = intent.getExtras().getString("Address");
			Toast.makeText(context, " cono " + latitude + longitude , Toast.LENGTH_SHORT ).show();

		}
	}
}
	
	
	
	