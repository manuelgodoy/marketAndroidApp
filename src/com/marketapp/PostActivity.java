package com.marketapp;


import android.net.Uri;

import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
//import android.app.FragmentManager;
//import android.support.v4.app.FragmentActivity;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
//import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
//import android.text.Editable;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;


public class PostActivity extends Activity implements TaskFragment.TaskCallbacks {
	
	private static final String TAG_TASK_FRAGMENT = "task_fragment";
	private TaskFragment mTaskFragment;
	
	TextView mDisplayTextView;
	String mDisplayFolder;
	File mPhotoFile;
	Uri mPhotoFileUri;
	File mVideoFile;
	Uri mVideoFileUri;
	private static final int CAPTURE_IMAGE_REQUEST_CODE = 1;
	private static final int CAPTURE_VIDEO_REQUEST_CODE = 2;
	ArrayList<ImageView> photo = new ArrayList<ImageView>();// = (ImageView)findViewById(R.id.imageView1);;
	ImageView video;
	EditText t;
	
	ArrayList<Bitmap> photobitmap = new ArrayList<Bitmap>();
	ArrayList<File> photoFiles = new ArrayList<File>(); // to send to parseFile
	Bitmap videothumb;
	
	Activity mContext = this;
	int mWidth;
	int viewWidth;
	LayoutParams params;
	DisplayMetrics displaymetrics = new DisplayMetrics();
	
	LocationManager locationManager;
	locationReceiver lr = new locationReceiver();
	String geoURI;
	//double lat;
	//double lon;
	
	ParseGeoPoint point = new ParseGeoPoint();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);		
		
		Intent serviceIntent = new Intent(this, LocationService.class);
		startService(serviceIntent);
		//Intent intent = getIntent();
		Parse.initialize(this, "VzCaiR1xAxw1Xzs7n68DFJvNo8C8Ov80Np4DVNEV", "3Du6C0fPE8IkrLYiPS3MQrl0oSchFU2SkeKzhB1i");
		
		final ParseObject testObject = new ParseObject("TestObject");
		
		
		mContext.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		mWidth = displaymetrics.widthPixels;
		viewWidth = mWidth / 3;
		params = new LayoutParams(viewWidth, LayoutParams.WRAP_CONTENT);
		
		//locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);				
		//locationManager.requestLocationUpdates(getProvider(),10,10,locationListener);
		
		photo.add((ImageView)findViewById(R.id.imageView1));
		photo.add((ImageView)findViewById(R.id.imageView2));
		photo.add((ImageView)findViewById(R.id.imageView3));
		photo.add((ImageView)findViewById(R.id.imageView4));
		photo.get(0).setLayoutParams(params);
		photo.get(1).setLayoutParams(params);
		photo.get(2).setLayoutParams(params);
		photo.get(3).setLayoutParams(params);
		
		
	    FragmentManager fm = getFragmentManager();
	    mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);
	    if (mTaskFragment == null) {
	    	mTaskFragment = new TaskFragment();
	        fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
	        mTaskFragment.setData(photobitmap);
	    } else {
	    	photobitmap = mTaskFragment.getData();
			for (Bitmap p : photobitmap) {
				photo.get(photobitmap.indexOf(p)+1).setVisibility(0);
				photo.get(photobitmap.indexOf(p)).setImageBitmap(p);
				photo.get(photobitmap.indexOf(p)).requestLayout();
				photo.get(photobitmap.indexOf(p)).getLayoutParams().height = 500;	
			}
	    }
		
		Button b = (Button)findViewById(R.id.button1);
		
		b.setOnClickListener(new OnClickListener() {
			public void onClick (View v) {
				Intent cameraintent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				filePhotoStore(cameraintent);			
				startActivityForResult(cameraintent, CAPTURE_IMAGE_REQUEST_CODE);
				if(photobitmap.size()<4){
					photo.get(photobitmap.size()+1).setVisibility(0);
				}
			}
		});	
	/*	b2.setOnClickListener(new OnClickListener() {
			public void onClick (View v) {
				video = (ImageView)findViewById(R.id.videoView1);
				b2.setVisibility(View.GONE);
				Intent cameraintent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
				cameraintent.putExtra("android.intent.extra.durationLimit", 5);
				fileVideoStore(cameraintent);	
				startActivityForResult(cameraintent, CAPTURE_VIDEO_REQUEST_CODE);
			}
		});*/
		
		t = (EditText)findViewById(R.id.editText1);
		Button b2 = (Button)findViewById(R.id.button2);
		b2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(PostActivity.this, MainFeed.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("Post", getTitle(t));
				intent.putExtra("from", 2);
				testObject.put("Title", getTitle(t));
				testObject.put("location", point);				
				
				byte[] dataPhotos = new byte[(int) photoFiles.get(0).length()];
				try {
			        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(photoFiles.get(0)));
			        buf.read(dataPhotos, 0, dataPhotos.length);
			        buf.close();
			    } catch (FileNotFoundException e) {
			        // TODO Auto-generated catch block
			        e.printStackTrace();
			    } catch (IOException e) {
			        // TODO Auto-generated catch block
			        e.printStackTrace();
			    }
				ParseFile file = new ParseFile("image.jpg",dataPhotos);
				file.saveInBackground();
				testObject.put("photo", file);
				testObject.saveInBackground();
				startActivity(intent);				
			};			
		});	
		
		mDisplayTextView = (TextView) findViewById(R.id.textView1);
		mDisplayTextView.setOnClickListener(new OnClickListener() {
			public void onClick (View v) {
				Uri geo = Uri.parse(geoURI);
				Intent geoMap = new Intent(Intent.ACTION_VIEW, geo);
				startActivity(geoMap);
			}
		});
		 

	}	
	
	public void filePhotoStore(Intent intent) {
		
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"MarketApp");
		if (!mediaStorageDir.exists()) {
			mediaStorageDir.mkdir();
		}
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());				
		mDisplayFolder = "Pictures" + File.separator +"MarketApp" + File.separator + "IMG_" +timeStamp + ".jpg";
		mPhotoFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		photoFiles.add(mPhotoFile); // files for Parse.com
		mPhotoFileUri= Uri.fromFile(mPhotoFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoFileUri);		
		//Different for video or image			
	}
	/*
	public void fileVideoStore(Intent intent) {		
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"MarketApp");
		if (!mediaStorageDir.exists()) {
			mediaStorageDir.mkdir();
		}
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());				
		mDisplayFolder = "Pictures" + File.separator +"MarketApp" + File.separator + "VID_" +timeStamp + ".mp4";
		mVideoFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
		mVideoFileUri= Uri.fromFile(mVideoFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mVideoFileUri);
		
		//Different for video or image			
	}*/	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);		
		switch (requestCode) {
		case CAPTURE_IMAGE_REQUEST_CODE:
			if (resultCode == RESULT_OK){
				Toast.makeText(this, "Image saved to:\n" + mDisplayFolder, Toast.LENGTH_LONG).show();
				Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				mediaScanIntent.setData(mPhotoFileUri);
				this.sendBroadcast(mediaScanIntent);
				photobitmap.add(decodeSampledBitmapFromFile(mPhotoFile.getAbsolutePath(),1000,700));
				photo.get(photobitmap.size()-1).setImageBitmap(photobitmap.get(photobitmap.size()-1));
				photo.get(photobitmap.size()-1).requestLayout();
				photo.get(photobitmap.size()-1).getLayoutParams().height = 500;					
				//photo.get(p).requestLayout();
				//photo.get(p).getLayoutParams().height = 500;				
			} else if (resultCode == RESULT_CANCELED){
				
			} else {
				Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
			}		
			break;
		
		case CAPTURE_VIDEO_REQUEST_CODE:
			if (resultCode == RESULT_OK){
				Toast.makeText(this, "Video saved to:\n" + mDisplayFolder, Toast.LENGTH_LONG).show();
				Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				mediaScanIntent.setData(mVideoFileUri);
				this.sendBroadcast(mediaScanIntent);
				videothumb = ThumbnailUtils.createVideoThumbnail(mVideoFile.getAbsolutePath(),MediaStore.Images.Thumbnails.MINI_KIND);
				video.setImageBitmap(videothumb);
			} else if (resultCode == RESULT_CANCELED){
				
			} else {
				Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
			}
			break;
		default:
			break;			
		}		
	}
		
	public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {
	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(path);
	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(path);
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	
	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;
	
	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }	
	    return inSampleSize;
	}	

	public String getTitle(EditText t) {		
		return (t.getText()).toString();		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	/*
	LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			Geocoder coder = new Geocoder(getApplicationContext());
			List<Address> geocodeResults;
			
			try {
				geocodeResults = coder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
				for (Address address: geocodeResults){
					
					lat=location.getLatitude();
					lon=location.getLongitude();
					point.setLatitude(lat);
					point.setLongitude(lon);
					Log.d("Location", location.getLatitude() + "," + location.getLongitude() + ":" +address.getLocality());
					geoURI = "geo:" +location.getLatitude() +"," +location.getLongitude() + "?z=16";
					mDisplayTextView.setText(address.getLocality());
				}
			} catch (IOException e) {e.printStackTrace();}
			
		}
		@Override
		public void onProviderDisabled(String provider) {}
		
		@Override
		public void onProviderEnabled(String provider) {}
		
		@Override
		public void onStatusChanged(String provider, int status,Bundle extras) {}
				
	};
	
	public String getProvider(){
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setSpeedRequired(true);
		String providerName = locationManager.getBestProvider(criteria,true);
		if (providerName != null) {
			return providerName;
		} else
			return LocationManager.GPS_PROVIDER;
	}
	
	public Location getRecentLocation() {
		Location recentLocation = null;
		long bestTime = 0;
		List<String> matchingProviders = locationManager.getAllProviders();
		for (String provider: matchingProviders) {
			Location location = locationManager.getLastKnownLocation(provider);
			if (location!=null) {
				long time = location.getTime();
				if (time > bestTime) {
					bestTime = time;
					recentLocation = location;
				}
			}
		}
		return recentLocation;
	}*/
	
	public class locationReceiver extends BroadcastReceiver {
		
		public void onReceive (Context context, Intent intent) {
			Double lat = intent.getExtras().getDouble("Latitude");
			Double lon = intent.getExtras().getDouble("Longitude");			
			Toast.makeText(getApplicationContext(), "latitude: " +lat + ", longitude: " + lon, Toast.LENGTH_SHORT ).show();
			updateGeo(lat,lon);
		}
		
		public void updateGeo(double latitude, double longitude) {
			Geocoder coder = new Geocoder(getApplicationContext());
			List<Address> geocodeResults;
			
			try {
				geocodeResults = coder.getFromLocation(latitude,longitude,1);
				for (Address address: geocodeResults){				
					//point.setLatitude(lat);
					//point.setLongitude(lon);
					//Log.d("Location", location.getLatitude() + "," + location.getLongitude() + ":" +address.getLocality());
					geoURI = "geo:" +latitude +"," +longitude + "?z=16";
					mDisplayTextView.setText(address.getLocality());
				} 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {}

		}
	}
	
	protected void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter(LocationService.BROADCAST_ACTION);		
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
	
    @Override
    public void onDestroy() {
        super.onDestroy();
        // store the data in the fragment
        mTaskFragment.setData(photobitmap);
    }


	@Override
	public void onPreExecute() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProgressUpdate(int percent) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onCancelled() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onPostExecute() {
		// TODO Auto-generated method stub
		
	}



    


}
