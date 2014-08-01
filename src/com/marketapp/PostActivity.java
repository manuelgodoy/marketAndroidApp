package com.marketapp;

import java.io.File;
import java.text.SimpleDateFormat;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import android.widget.TextView;


public class PostActivity extends Activity {
	private static final int CAPTURE_IMAGE_REQUEST_CODE = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_layout);
		Intent intent = getIntent();
		String message = intent.getStringExtra("username");				
		TextView t = (TextView)findViewById(R.id.textView1);
		t.setText("Hello: " +message);
		Button takePhoto = (Button)findViewById(R.id.button1);
		takePhoto.setOnClickListener(new OnClickListener() {
			public void onClick (View v) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"MarketApp");
				if (!mediaStorageDir.exists()) {
					mediaStorageDir.mkdir();
				}
				String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(newDate());				
				mDisplayFolder = "Pictures" + File.separator +"MarketApp" + File.separator + "IMG_" +timeStamp + ".jpg";
				mPhotoFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
				mPhotoFileUri= Uri.fromFile(File(mPhotoFile));
				intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoFileUri);
				startActivityForResult(intent, CAPTURE_IMAGE_REQUEST_CODE);
			}
		});
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
