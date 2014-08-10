package com.marketapp;


import java.util.ArrayList;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;

public class TaskFragment extends Fragment {
	
	private ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
	
	
	  static interface TaskCallbacks {
		    void onPreExecute();
		    void onProgressUpdate(int percent);
		    void onCancelled();
		    void onPostExecute();
		  }
	
		
	
		  /**
	   * Hold a reference to the parent Activity so we can report the
	   * task's current progress and results. The Android framework 
	   * will pass us a reference to the newly created Activity after 
	   * each configuration change.
	   */
	
	
	  /**
	   * This method will only be called once when the retained
	   * Fragment is first created.
	   */
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
	
	    // Retain this fragment across configuration changes.
		  setRetainInstance(true);
	
	
	  }
	    public void setData(ArrayList<Bitmap> data) {
	        this.bitmaps = data;
	    }

	    public ArrayList<Bitmap> getData() {
	        return bitmaps;
	    }

}