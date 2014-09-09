package com.marketapp;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.app.ListFragment;

import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;


public class SimpleListFragment extends ListFragment    {
	String[] mTitles;
	String address;
	double distance;
	ParseFile photo;
	
	List<ParseObject> titles = new ArrayList<ParseObject>();
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		MainFeed currentActivity = (MainFeed) this.getActivity();
		titles = currentActivity.getPhotos();
		//mTitles = new String[photos.size()];
		mTitles = new String[titles.size()];
		for (ParseObject t : titles) {			
			address = getAddress(t.getParseGeoPoint("location"));	
			photo = t.getParseFile("photo");			
			/*try {
				//byte[] photoData = photo.getData();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			if (address !=null ){
				mTitles[titles.indexOf(t)] = t.getString("Title")+" posted in "+address;
			} else {
				mTitles[titles.indexOf(t)] = t.getString("Title");
			}
				
		}
		setListAdapter(new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_list_item_1, mTitles));
	}
	
	@Override 
	public void onListItemClick(ListView l, View v, int position, long id) {
		Toast.makeText(this.getActivity(), mTitles[position], Toast.LENGTH_SHORT).show();
	}	
	
	public String getAddress(ParseGeoPoint point) { // Too slow must be moved to server or background!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		Geocoder coder = new Geocoder(getActivity().getApplicationContext());
		double lat = point.getLatitude();
		double lon = point.getLongitude(); 
		List<Address> geocodeResults;
		try {
			geocodeResults = coder.getFromLocation(lat,lon,1);
			for (Address a : geocodeResults){				
				return (a.getLocality());
			}
		} catch (IOException e) {e.printStackTrace();}
		return null;		
	}
	
	public double getDistance(ParseGeoPoint here, ParseGeoPoint point) {
		distance = here.distanceInMilesTo(point);
		return distance;
	}
	
} 