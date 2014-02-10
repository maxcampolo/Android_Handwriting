package com.maxcampolo.fingerpaint;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

public class FingerPaintMainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finger_paint_main);
		populateGallery();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.finger_paint_main, menu);
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_newDrawing:
			createNewDrawing();
			return true;
			
		default: 
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void createNewDrawing () {
		//called when the camera button is clicked
		Intent intent = new Intent(this, DrawingActivity.class);
		startActivity(intent);
	}
	
	public void populateGallery() {
		ArrayList<Bitmap> bmpArrayList = new ArrayList<Bitmap>();
		File imageDirectory = new File(Environment.getExternalStorageDirectory() + "/Pictures/Fingerpaint");
		File[] imageFiles = imageDirectory.listFiles();
		for (File file : imageFiles) {
			bmpArrayList.add(BitmapFactory.decodeFile(file.getPath()));
			
		}
		
		DrawingListAdapter adapter = new DrawingListAdapter(this, bmpArrayList);
		GridView gridView = (GridView) findViewById(R.id.image_gridview);
		gridView.setAdapter(adapter);
	}

}
