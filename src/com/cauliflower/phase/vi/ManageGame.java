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
import com.example.overlaymanager.OverlayManager;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class ManageGame extends MapActivity {
	String webserviceURL = "http://cauliflowerpowershower.appspot.com/";
	ArrayList<GameInfo> values;
	OverlayManager overlayManager;
	MyLocationOverlay myLocation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_game);

		initView();
	}

	public void initView() {
		values = new ArrayList<GameInfo>();
		Bundle data = getIntent().getExtras();
		String group = data.getString("groupName");
		String boundary = data.getString("boundary");

		String url = webserviceURL + "add/" + group + "/" + boundary;

		new GetStatusesTask().execute(url,"true");
		
		MapView mapView = (MapView) findViewById(R.id.mapview2);
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(true);

		overlayManager = new OverlayManager(this, mapView);

		Drawable drawable = this.getResources().getDrawable(R.drawable.ic_action_locate);
		ManagedOverlay.boundToCenter(drawable);
				
		overlayManager.createOverlay(drawable);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_manage_game, menu);
		return true;
	}

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

	public void updatePoints(){
		ManagedOverlay overlay = overlayManager.getOverlay(0);
		if(values.size() > 0){
			for(int i = 0; i < values.get(0).xLocations.length; i++){
				GeoPoint pt = new GeoPoint((int)values.get(0).yLocations[i],(int)values.get(0).xLocations[i]);
				Log.d("update",pt.toString());
				overlay.createItem(pt);
			}
			overlayManager.populate();
		}
		else{
			Log.d("update","no values");
		}
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
			updatePoints();
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
