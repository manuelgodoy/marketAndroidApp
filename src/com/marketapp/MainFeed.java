package com.marketapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

import android.widget.TextView;


public class MainFeed extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_layout);
		Intent intent = getIntent();
		String message = intent.getStringExtra("username");				
		TextView t = (TextView)findViewById(R.id.textView1);
		t.setText("Hello: " +message);
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
