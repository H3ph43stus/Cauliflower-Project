package com.cauliflower.phase.vi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.cauliflower.phase.vi.R;
import com.example.overlaymanager.ManagedOverlay;
import com.example.overlaymanager.ManagedOverlayGestureDetector;
import com.example.overlaymanager.ManagedOverlayItem;
import com.example.overlaymanager.OverlayManager;
import com.example.overlaymanager.ZoomEvent;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class CreateGame extends MapActivity {
	MyLocationOverlay myLocation;
	OverlayManager overlayManager;
	String webserviceURL = "http://cauliflowerpowershower.appspot.com/";
	ArrayList<GameInfo> values;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.setSatellite(true);
        
        overlayManager = new OverlayManager(this, mapView);
        
        Drawable drawable = this.getResources().getDrawable(R.drawable.ic_action_locate);
        ManagedOverlay.boundToCenter(drawable);
        ManagedOverlay managedOverlay = overlayManager.createOverlay(drawable);
        
        managedOverlay.setOnOverlayGestureListener(new ManagedOverlayGestureDetector.OnOverlayGestureListener() {
			
			public boolean onZoom(ZoomEvent arg0, ManagedOverlay arg1) {
				return false;
			}
			
			public boolean onSingleTap(MotionEvent arg0, ManagedOverlay arg1,
					GeoPoint arg2, ManagedOverlayItem arg3) {
				arg1.createItem(arg2);
				return true;
			}
			
			public boolean onScrolled(MotionEvent arg0, MotionEvent arg1, float arg2,
					float arg3, ManagedOverlay arg4) {
				return false;
			}
			
			public void onLongPressFinished(MotionEvent arg0, ManagedOverlay arg1,
					GeoPoint arg2, ManagedOverlayItem arg3) {
				if(arg3 != null){
					arg1.remove(arg3);
				}
			}
			
			public void onLongPress(MotionEvent arg0, ManagedOverlay arg1) {
				
			}
			
			public boolean onDoubleTap(MotionEvent arg0, ManagedOverlay arg1,
					GeoPoint arg2, ManagedOverlayItem arg3) {
				return false;
			}
		});
        
        overlayManager.populate();
        
        myLocation = new MyLocationOverlay(this, mapView);
        mapView.getOverlays().add(myLocation);
        
    }
    
    public void createGame(View view){
    	EditText groupText = (EditText) findViewById(R.id.newGroupText);
    	EditText leaderText = (EditText) findViewById(R.id.leaderText);
    	String group = groupText.getText().toString();
    	String username = leaderText.getText().toString();

		//Log.e("length1", ""+username.length());
		
    	if((group.length() > 0) && (username.length() > 0)){
    		ManagedOverlay overlay = overlayManager.getOverlay(0);
    		ArrayList<Integer> xCords = new ArrayList<Integer>();
    		ArrayList<Integer> yCords = new ArrayList<Integer>();

    		for(int i=0; i<overlay.size(); i++) {
    			GeoPoint point1 = overlay.getItem(i).getPoint();
    			xCords.add(point1.getLatitudeE6());
    			yCords.add(point1.getLongitudeE6());
    		}
    		/*intent.putExtra("xCords", xCords);
    		intent.putExtra("yCords", yCords);
    		startActivity(intent);

			Bundle data = getIntent().getExtras();
			String group = data.getString("groupName");*/
    		values = new ArrayList<GameInfo>();
    		String url = "";
    		for(int i=0; i<xCords.size(); i++) {
    			url = webserviceURL + "add2/" + group + "/" + yCords.get(i) + "/" + xCords.get(i);
    			new GetStatusesTask().execute(url,"false");
    		}

        	Intent intent = new Intent(this, GameActivity.class);
    		intent.putExtra("groupName", group);
    		intent.putExtra("username", username);
    		
    		startActivity(intent);
    	}
    	else{
    		String m = "Please enter a username and group";
    		Toast.makeText(getApplicationContext(), m, Toast.LENGTH_SHORT).show();
    	}
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	// when our activity resumes, we want to register for location updates
    	myLocation.enableMyLocation();
    }

    @Override
    protected void onPause() {
    	super.onPause();
    	// when our activity pauses, we want to remove listening for location updates
    	myLocation.disableMyLocation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_create_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	// The definition of our task class
		private class GetStatusesTask extends AsyncTask<String, Integer, String> {
			@Override
			protected void onPreExecute() {
			}

			@Override
			protected String doInBackground(String... params) {
				String url = params[0];
				String update = params[1];
				ArrayList<GameInfo> lcs = new ArrayList<GameInfo>();

				try {

					Log.d("URL",url);
					String webJSON = getJSONfromURL(url);
					Log.d("JSON", webJSON);
					if(update.equals("true")){
						Gson gson = new Gson();

						JsonParser parser = new JsonParser();
						JsonArray Jarray = parser.parse(webJSON).getAsJsonArray();

						for (JsonElement obj : Jarray) {
							GameInfo st = gson.fromJson(obj, GameInfo.class);
							lcs.add(st);
						}
					}

				} catch (Exception e) {
					Log.e("Cauliflower", "JSONPARSE:" + e.toString());
				}

				values.clear();
				values.addAll(lcs);

				return "Done!";
			}

			@Override
			protected void onProgressUpdate(Integer... ints) {

			}

			@Override
			protected void onPostExecute(String result) {
				//updatePoints();
			}
		}

		/*@Override
		protected boolean isRouteDisplayed() {
			return false;
		}*/

		public static String getJSONfromURL(String url) {

			// initialize
			InputStream is = null;
			String result = "";

			// http post
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpGet httpget = new HttpGet(url);
				HttpResponse response = httpclient.execute(httpget);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();

			} catch (Exception e) {
				Log.e("Cauliflower", "Error in http connection " + e.toString());
			}

			// convert response to string
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				result = sb.toString();
			} catch (Exception e) {
				Log.e("Cauliflower", "Error converting result " + e.toString());
			}


			return result;
		}
}

