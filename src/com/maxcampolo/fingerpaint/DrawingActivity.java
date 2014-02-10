package com.maxcampolo.fingerpaint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class DrawingActivity extends Activity implements OnClickListener {
	
	private DrawingView drawingView;
	private ImageButton currentPaintColor;
	private ImageButton saveAndCheck;
	private ImageButton saveButton;
	private ImageButton newDrawingButton;
	private String key = "11773edfd643f813c18d82f56a8104ed";
	//private String q;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawing);
		setupActionBar();
		
		drawingView = (DrawingView) findViewById(R.id.drawing);
		LinearLayout colorsLayout = (LinearLayout) findViewById(R.id.colors);
		currentPaintColor = (ImageButton) colorsLayout.getChildAt(0);
		currentPaintColor.setImageDrawable(getResources().getDrawable(R.drawable.color_border_pressed));
		
		saveAndCheck = (ImageButton) findViewById(R.id.button_check);
		newDrawingButton = (ImageButton) findViewById(R.id.button_newdrawing);
		saveButton = (ImageButton) findViewById (R.id.button_save);
		
		newDrawingButton.setOnClickListener((android.view.View.OnClickListener) this);
		saveButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.drawing, menu);
		return true;
	}
	
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void colorSelected(View view) {
		//Choose color
		if (view != currentPaintColor) {
			ImageButton imageButton = (ImageButton) view;
			String color = view.getTag().toString();
			
			drawingView.setColor(color);
			
			imageButton.setImageDrawable(getResources().getDrawable(R.drawable.color_border_pressed));
			currentPaintColor.setImageDrawable(getResources().getDrawable(R.drawable.color_border));
			currentPaintColor = (ImageButton) view;
		}
	}
	
	public void saveAndCheck(View view) {
		ArrayList<Float> pathPoints = drawingView.getPathPoints();
		String q = new String("[");
		pathPoints.add((float)255);              //end letter sketch
		pathPoints.add((float)255);
		
		//scale to (0, 255)
		float largest = 0;
		float scale = 0;
		for (int i = 0; i < pathPoints.size(); i++) {
			if (pathPoints.get(i) > largest) {
				largest = (float)pathPoints.get(i);
			}
		}
		scale = (float)255/largest;
		
		//make array of points from arraylist
		int[] pathPointsArray = new int[pathPoints.size()];
		int listIndex = 0;
		for (float f : pathPoints) {
			if (f != (float)255 && f != (float)0) {
				f = f*scale;
				int m = (int)f;
				pathPointsArray[listIndex++] = m;
			} else {
				int m = (int)f;
				pathPointsArray[listIndex++] = m;
			}
		}
		
		//Make string q
		for (int i = 0; i < pathPointsArray.length; i++) {
			if (i != pathPointsArray.length - 1) {
				q = q + pathPointsArray[i] + ", ";
			} else {
				q = q + pathPointsArray[i];
			}
		}
		q = q + "]";
		System.out.println(q); 
		
		new GetServerResponse().execute(q);
		
	}
	
	private class GetServerResponse extends AsyncTask<String, Void, String>{
		
		@Override
		protected String doInBackground(String...params) {
			String q = (String)params[0];
			try
			{
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost("http://cwritepad.appspot.com/reco/usen");
	
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("key", key));
				nameValuePairs.add(new BasicNameValuePair("q", q));
				try {
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
				} catch (UnsupportedEncodingException e) {
					Log.e("encoding exception", e.getMessage() + "");
				}
				HttpResponse response = httpClient.execute(httpPost);
				String serverResponse = EntityUtils.toString(response.getEntity());
	
				return serverResponse;
			}
			catch(IOException e)
			{
				Log.e("deScribe", e.getMessage() + "");
			}
			return null;
		}
		
		protected void onPostExecute (String response) {
			if (response.equals(null)) {
				System.out.println("There was no match");
			} else {
				System.out.println(response);
				showResultAlert(response);
			}
			
		}
	}
	
	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.button_newdrawing) {
			AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
			newDialog.setTitle("new drawing");
			newDialog.setMessage("current drawing will be lost! start new drawing?");
			newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which){
			        drawingView.startNew();	
			        dialog.dismiss();
			    }
			    
			});
			newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which){
			        dialog.cancel();
			    }
			});
			newDialog.show();
		} else if (view.getId() == R.id.button_save) {
			AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
			saveDialog.setTitle("Save Drawing");
			saveDialog.setMessage("Save drawing?");
			saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which){
			    	writeImage();
			    }
			    
			});
			saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int which){
			        dialog.cancel();
			    }
			});
			saveDialog.show();
		}
	} 
	
	public void showResultAlert(String response) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("your letter matches: ");
		alert.setMessage(response);
		alert.setNeutralButton("cool!", null);
		alert.show();
	}
	
	public void writeImage() {
		
		drawingView.setDrawingCacheEnabled(true);
		
		File mediaStorageDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Fingerpaint");
		
		if(!mediaStorageDirectory.exists()) {
			mediaStorageDirectory.mkdirs();
		}
		
		System.out.println(mediaStorageDirectory.getAbsolutePath());
		String mediaFileName = UUID.randomUUID().toString() + ".png";
		File mediaFile = new File(mediaStorageDirectory.getPath() + File.separator + mediaFileName);
		//String imageToSave = MediaStore.Images.Media.insertImage(getContentResolver(), drawingView.getDrawingCache(), UUID.randomUUID().toString() + ".png", "fingerpaint");
		if (mediaFile != null) {
			try {
				FileOutputStream fos = new FileOutputStream(mediaFile);
				Bitmap bmp = drawingView.getDrawingCache();
				bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.close();
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
			} catch (Exception e) {
				Log.e("error", e.getMessage());
			}
			Toast saveSuccessful = Toast.makeText(getApplicationContext(), "Saved to gallery!", Toast.LENGTH_SHORT);
			saveSuccessful.show();
		} else {
			Toast saveUnsuccessful = Toast.makeText(getApplicationContext(), "Code Red! Image could not be saved!", Toast.LENGTH_SHORT);	
			saveUnsuccessful.show();
		}
		
		drawingView.destroyDrawingCache();
	}
}
