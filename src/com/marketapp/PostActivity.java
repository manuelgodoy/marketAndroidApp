package com.marketapp;


import android.net.Uri;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
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
import android.location.Location;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
//import android.app.FragmentManager;
//import android.support.v4.app.FragmentActivity;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
//import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.DialogFragment;
//import android.text.Editable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;


public class PostActivity extends Activity implements TaskFragment.TaskCallbacks, LocationListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
	
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
	
    // A request to connect to Location Services
    private LocationRequest mLocationRequest;

    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;

    // Handles to UI widgets
    //private ProgressBar mActivityIndicator;

    // Handle to SharedPreferences for this app
    SharedPreferences mPrefs;
    // Handle to a SharedPreferences editor
    SharedPreferences.Editor mEditor;
    /*
     * Note if updates have been turned on. Starts out as "false"; is set to "true" in the
     * method handleRequestSuccess of LocationUpdateReceiver.
     *
     */
    boolean mUpdatesRequested = false;	
	LocationManager locationManager;
	//locationReceiver lr = new locationReceiver();
	String geoURI;
	//double lat;
	//double lon;
	
	ParseGeoPoint point = new ParseGeoPoint();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);		
		
		//Intent serviceIntent = new Intent(this, LocationService.class);
		//startService(serviceIntent);
		//Intent intent = getIntent();
		Parse.initialize(this, "VzCaiR1xAxw1Xzs7n68DFJvNo8C8Ov80Np4DVNEV", "3Du6C0fPE8IkrLYiPS3MQrl0oSchFU2SkeKzhB1i");
		
		final ParseObject testObject = new ParseObject("TestObject");
		
		
		mContext.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		mWidth = displaymetrics.widthPixels;
		viewWidth = mWidth / 3;
		params = new LayoutParams(viewWidth, LayoutParams.WRAP_CONTENT);
		
		
		
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
				
				
				byte[] dataPhotos;
				if (!photoFiles.isEmpty()){
					dataPhotos = new byte[(int) photoFiles.get(0).length()];
				}
				else {
					dataPhotos = new byte[0];
				}
				
				
				try {
					if (dataPhotos.length>0){
						BufferedInputStream buf = new BufferedInputStream(new FileInputStream(photoFiles.get(0)));
						buf.read(dataPhotos, 0, dataPhotos.length);
						buf.close();
					}
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
		
		 // Create a new global location parameters object
        mLocationRequest = LocationRequest.create();
        /*
         * Set the update interval
         */
        mLocationRequest.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);
        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the interval ceiling to one minute
        mLocationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);
        // Note that location updates are off until the user turns them on
        mUpdatesRequested = false;
        // Open Shared Preferences
        mPrefs = getSharedPreferences(LocationUtils.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        // Get an editor
        mEditor = mPrefs.edit();
         /* Create a new location client, using the enclosing class to
         * handle callbacks. */
        mLocationClient = new LocationClient(this, this, this);		

       

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
			
			// If the request code matches the code sent in onConnectionFailed
		case LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :

			switch (resultCode) {
			// If Google Play services resolved the problem
			case Activity.RESULT_OK:

				// Log the result
				Log.d(LocationUtils.APPTAG, getString(R.string.resolved));

				// Display the result
				break;

				// If any other result was returned by Google Play services
			default:
				// Log the result
				Log.d(LocationUtils.APPTAG, getString(R.string.no_resolution));

				// Display the result

				break;
            }
		default:
			Log.d(LocationUtils.APPTAG, getString(R.string.unknown_activity_request_code, requestCode));
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
	/*
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
	}*/
	
	/*protected void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter(LocationService.BROADCAST_ACTION);		
		registerReceiver(lr, filter);
	}*/
	
    @Override
    public void onResume() {
        super.onResume();

        // If the app already has a setting for getting location updates, get it
        if (mPrefs.contains(LocationUtils.KEY_UPDATES_REQUESTED)) {
            mUpdatesRequested = mPrefs.getBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);

        // Otherwise, turn off location updates until requested
        } else {
            mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);
            mEditor.commit();
        }
        /*if (mLocationClient.isConnected()) {
        	Log.i("****","Connected");
        	getAddress();
        }*/
       	

    }
	
	@Override
	public void onPause() {
		//unregisterReceiver(lr);
        mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, mUpdatesRequested);
        mEditor.commit();
		super.onPause();
	}
	
	@Override
	public void onStop(){
		super.onStop();

        // If the client is connected
        if (mLocationClient.isConnected()) {
            stopPeriodicUpdates();
        }

        // After disconnect() is called, the client is considered "dead".
        mLocationClient.disconnect();
	}
	
	@Override
    public void onStart() {

        super.onStart();

        /*
         * Connect the client. Don't re-start any requests here;
         * instead, wait for onResume()
         */
        mLocationClient.connect();

    }
	
	
    @Override
    public void onDestroy() {
        super.onDestroy();
        // store the data in the fragment
        mTaskFragment.setData(photobitmap);
    }
    
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode =  GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d(LocationUtils.APPTAG, getString(R.string.play_services_available));

            // Continue
            return true;
        // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                //errorFragment.show(getSupportFragmentManager(), LocationUtils.APPTAG);
            }
            return false;
        }
    }

    /**
     * Invoked by the "Get Location" button.
     *
     * Calls getLastLocation() to get the current location
     *
     * @param v The view object associated with this method, in this case a Button.
     */
    public void getLocation() {

        // If Google Play Services is available
        if (servicesConnected()) {
        	// Get the current location
            Location currentLocation = mLocationClient.getLastLocation();
            point.setLatitude(currentLocation.getLatitude());
            point.setLongitude(currentLocation.getLongitude());
            geoURI = "geo:" +currentLocation.getLatitude() +"," + currentLocation.getLongitude() + "?z=16";
            
        }
    }

    /**
     * Invoked by the "Get Address" button.
     * Get the address of the current location, using reverse geocoding. This only works if
     * a geocoding service is available.
     *
     * @param v The view object associated with this method, in this case a Button.
     */
    // For Eclipse with ADT, suppress warnings about Geocoder.isPresent()
    @SuppressLint("NewApi")
    public void getAddress() {

        if (servicesConnected()) {
            // Get the current location
            Location currentLocation = mLocationClient.getLastLocation();
            // Turn the indefinite activity indicator on
            //mActivityIndicator.setVisibility(View.VISIBLE);
            // Start the background task
            (new PostActivity.GetAddressTask(this)).execute(currentLocation);
        }
    }

    /**
     * Invoked by the "Start Updates" button
     * Sends a request to start location updates
     *
     * @param v The view object associated with this method, in this case a Button.
     */
    public void startUpdates() {
        mUpdatesRequested = true;

        if (servicesConnected()) {
            startPeriodicUpdates();
        }
    }

    /**
     * Invoked by the "Stop Updates" button
     * Sends a request to remove location updates
     * request them.
     *
     * @param v The view object associated with this method, in this case a Button.
     */
    public void stopUpdates() {
        mUpdatesRequested = false;

        if (servicesConnected()) {
            stopPeriodicUpdates();
        }
    }

    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
    	Log.w("**********", "onConnected");
    	if (!mLocationClient.isConnecting()) {
    		Log.i("****","Connected thru onConnected() method");
    		getAddress();
    		getLocation();
    	}
        if (mUpdatesRequested) {
            startPeriodicUpdates();
        }
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {

                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

                /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */

            } catch (IntentSender.SendIntentException e) {

                // Log the error
                e.printStackTrace();
            }
        } else {

            // If no resolution is available, display a dialog to the user with the error.
            showErrorDialog(connectionResult.getErrorCode());
        }
    }

    /* Report location updates to the UI.
     *
     * @param location The updated location.
     */
    @Override
    public void onLocationChanged(Location location) {

      
    }

    /**
     * In response to a request to start updates, send a request
     * to Location Services
     */
    private void startPeriodicUpdates() {

        mLocationClient.requestLocationUpdates(mLocationRequest, this);
    }

    /**
     * In response to a request to stop updates, send a request to
     * Location Services
     */
    private void stopPeriodicUpdates() {
        mLocationClient.removeLocationUpdates(this);
    }

    /**
     * An AsyncTask that calls getFromLocation() in the background.
     * The class uses the following generic types:
     * Location - A {@link android.location.Location} object containing the current location,
     *            passed as the input parameter to doInBackground()
     * Void     - indicates that progress units are not used by this subclass
     * String   - An address passed to onPostExecute()
     */
    protected class GetAddressTask extends AsyncTask<Location, Void, String> {

        // Store the context passed to the AsyncTask when the system instantiates it.
        Context localContext;

        // Constructor called by the system to instantiate the task
        public GetAddressTask(Context context) {

            // Required by the semantics of AsyncTask
            super();

            // Set a Context for the background task
            localContext = context;
        }

        /**
         * Get a geocoding service instance, pass latitude and longitude to it, format the returned
         * address, and return the address to the UI thread.
         */
        @Override
        protected String doInBackground(Location... params) {
            /*
             * Get a new geocoding service instance, set for localized addresses. This example uses
             * android.location.Geocoder, but other geocoders that conform to address standards
             * can also be used.
             */
            Geocoder geocoder = new Geocoder(localContext, Locale.getDefault());

            // Get the current location from the input parameter list
            Location location = params[0];

            // Create a list to contain the result address
            List <Address> addresses = null;

            // Try to get an address for the current location. Catch IO or network problems.
            try {

                /*
                 * Call the synchronous getFromLocation() method with the latitude and
                 * longitude of the current location. Return at most 1 address.
                 */
                addresses = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1
                );

                // Catch network or other I/O problems.
                } catch (IOException exception1) {

                    // Log an error and return an error message
                    Log.e(LocationUtils.APPTAG, getString(R.string.IO_Exception_getFromLocation));

                    // print the stack trace
                    exception1.printStackTrace();

                    // Return an error message
                    return (getString(R.string.IO_Exception_getFromLocation));

                // Catch incorrect latitude or longitude values
                } catch (IllegalArgumentException exception2) {

                    // Construct a message containing the invalid arguments
                    String errorString = getString(
                            R.string.illegal_argument_exception,
                            location.getLatitude(),
                            location.getLongitude()
                    );
                    // Log the error and print the stack trace
                    Log.e(LocationUtils.APPTAG, errorString);
                    exception2.printStackTrace();

                    //
                    return errorString;
                }
                // If the reverse geocode returned an address
                if (addresses != null && addresses.size() > 0) {

                    // Get the first address
                    Address address = addresses.get(0);

                    // Format the first line of address
                    String addressText = getString(R.string.address_output_string,

                            // If there's a street address, add it
                            address.getMaxAddressLineIndex() > 0 ?
                                    address.getAddressLine(0) : "",

                            // Locality is usually a city
                            address.getLocality(),

                            // The country of the address
                            address.getCountryName()
                    );

                    // Return the text
                    return addressText;

                // If there aren't any addresses, post a message
                } else {
                  return getString(R.string.no_address_found);
                }
        }

        /**
         * A method that's called once doInBackground() completes. Set the text of the
         * UI element that displays the address. This method runs on the UI thread.
         */
        @Override
        protected void onPostExecute(String address) {

        	// Turn off the progress bar
        	// mActivityIndicator.setVisibility(View.GONE);
        	
        	mDisplayTextView.setText(address);

        }
    }

    /**
     * Show a dialog returned by Google Play services for the
     * connection error code
     *
     * @param errorCode An error code returned from onConnectionFailed
     */
    private void showErrorDialog(int errorCode) {

        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
            errorCode,
            this,
            LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {

            // Create a new DialogFragment in which to show the error dialog
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);

            // Show the error dialog in the DialogFragment
         //   errorFragment.show(getSupportFragmentManager(), LocationUtils.APPTAG);
        }
    }

    /**
     * Define a DialogFragment to display the error dialog generated in
     * showErrorDialog.
     */
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /**
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
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
