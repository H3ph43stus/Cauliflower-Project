package com.cauliflower.phase.vi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.cauliflower.phase.vi.R;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class MainActivity extends Activity {

	ListView userList;
	String webserviceURL = "http://plato.cs.virginia.edu/~tsc8cm/cakephp/statuses/";
	ArrayList<PlayerStatus> values;
	ArrayAdapter<PlayerStatus> adapter;
	String group;
	String username;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initView();
	}

	public void initView() {
		userList = (ListView) findViewById(R.id.playerView);
		values = new ArrayList<PlayerStatus>();

		// First parameter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the TextView to which the data is written
		// Forth - the Array of data
		adapter = new ArrayAdapter<PlayerStatus>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, values);

		// Assign adapter to ListView
		userList.setAdapter(adapter);
	}

	public void refreshGroup(){
		if(group == null)
			this.group = ((EditText) findViewById(R.id.groupText)).getText().toString();
		new GetStatusesTask().execute(webserviceURL + "view/" + this.group, "true");
	}

	public void joinGroup(View view){
		EditText groupText = (EditText) findViewById(R.id.groupText);
		EditText usernameText = (EditText) findViewById(R.id.userText);
		this.group = groupText.getText().toString();
		this.username = usernameText.getText().toString();
		String url = webserviceURL + "add/" + groupText.getText() + "/" + usernameText.getText();
		//((TextView) findViewById(R.id.debug)).setText(url);
		new GetStatusesTask().execute(url,"false");
		new GetStatusesTask().execute(webserviceURL + "view/" + this.group, "true");
	}

	public void startGame(View view){
		Intent intent = new Intent(this, TwitterTestActivity.class);
		EditText groupText = (EditText) findViewById(R.id.groupText);
		EditText userText = (EditText) findViewById(R.id.userText);
		String group = groupText.getText().toString();
		String username = userText.getText().toString();
		if((group.length() > 0) && (username.length() > 0)){
			intent.putExtra("group", group);
			intent.putExtra("username", username);
			startActivity(intent);
		}
		else{
			String m = "Please enter a username and group";
			Toast.makeText(getApplicationContext(), m, Toast.LENGTH_SHORT).show();
		}
	}

	public static String getJSONfromURL(String url) {

		// initialize
		InputStream is = null;
		String result = "";

		// http post
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			HttpResponse response = httpclient.execute(httppost);
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

	// The definition of our task class
	private class GetStatusesTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {
			String url = params[0];
			String update = params[1];
			ArrayList<PlayerStatus> lcs = new ArrayList<PlayerStatus>();

			try {

				String webJSON = getJSONfromURL(url);
				if(update.equals("true")){
					Gson gson = new Gson();

					JsonParser parser = new JsonParser();
					JsonArray Jarray = parser.parse(webJSON).getAsJsonArray();

					for (JsonElement obj : Jarray) {
						PlayerStatus st = gson.fromJson(obj, PlayerStatus.class);
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
			// tells the adapter that the underlying data has changed and it
			// needs to update the view
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
		case R.id.createButton:
			Intent intent = new Intent(this, CreateGame.class);
			startActivity(intent);
			return true;
		case R.id.refreshButton:
			refreshGroup();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
