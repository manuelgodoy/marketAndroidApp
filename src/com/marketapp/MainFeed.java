package com.marketapp;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainFeed extends Activity {
	String feedItem;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_layout);
		Parse.initialize(this, "VzCaiR1xAxw1Xzs7n68DFJvNo8C8Ov80Np4DVNEV", "3Du6C0fPE8IkrLYiPS3MQrl0oSchFU2SkeKzhB1i");
		
		
		Intent intent = getIntent();
		switch (intent.getIntExtra("from",0)) {
		case (1):
			String message = intent.getStringExtra("username");	
			break;
		case (2):
			String toast = intent.getStringExtra("Post");
			Toast.makeText(this, "Post " +toast+ " has been posted." , Toast.LENGTH_LONG).show();
		}
		
		//String message = intent.getStringExtra("username");
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("TestObject");
		query.findInBackground(new FindCallback<ParseObject>() {
		    public void done(List<ParseObject> titleList, ParseException e) {
		        if (e == null) {
		            Log.d("Title", "Title is " + titleList.get(0) );
		            success(titleList);
		            
		        } else {
		            Log.d("Title", "Error: " + e.getMessage());
		        }
		    }

	
		});
		
		TextView t = (TextView)findViewById(R.id.textView1);
		t.setText("This is where the main feed would go ");
		
		Button b = (Button)findViewById(R.id.button1);
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainFeed.this, PostActivity.class);
				startActivity(intent);
			};
		});		
	}
	
	public void success(List<ParseObject> l) {
		Toast.makeText(this,  l.get(0).getString("Title"), Toast.LENGTH_LONG).show();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
