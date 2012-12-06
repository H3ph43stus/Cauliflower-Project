package com.cauliflower.phase.vi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TwitterTestActivity extends Activity {
	private ListView tweetList;
	private ArrayList<String> values;
	private ArrayAdapter<String> adapter;
	private static final String consumerKey = "SMD41Ddj8MdupIPTV3kBfA";
	private static final String consumerSecret = "kjEZiCSaIMRIg1ixCiT7KdtIB5yRD8w1eAdzGeaik";
	private static final String accessToken = "962772151-kH1NzYU552TiOeoC4bpdpXMHDJdIT9FKz1sQr7rs";
	private static final String accessTokenSecret = "SQDW4atAYJn4tess1jI8jNVh7kyZfbRkRaQuOE";
	private Twitter twitter;
	private String message,endText;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twitter_test);

		Bundle data = getIntent().getExtras();
		//group = data.getString("group");
		//		username = data.getString("username");
		
		message = data.getString("message");
		endText = data.getString("endtext");
		((TextView)findViewById(R.id.textView1)).setText("Game Over - " + endText);
		initView();
	}

	public void initView(){
		tweetList = (ListView) findViewById(R.id.tweetListView);
		values = new ArrayList<String>();

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, values);

		// Assign adapter to ListView
		tweetList.setAdapter(adapter);

		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(consumerKey, consumerSecret);
		AccessToken a = new AccessToken(accessToken,accessTokenSecret);
		twitter.setOAuthAccessToken(a);

		new GetStatusesTask().execute(message);
		Log.d("twitter","getting twitter");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.twitter_test, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refreshTweets:
			String m = "Refreshing";
			Toast.makeText(getApplicationContext(), m, Toast.LENGTH_SHORT).show();
			new GetStatusesTask().execute("");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class GetStatusesTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected String doInBackground(String... params) {
			String message = params[0]; 

			try{			
				values.clear();
				if(message != ""){
					twitter.updateStatus(message);
					values.add(message + " at " + new Date());
				}
				Log.d("twitter","getting tweets");
				List<twitter4j.Status> statuses = twitter.getHomeTimeline();
				for (twitter4j.Status status : statuses) {
					//Log.d("Cauliflower",status.getUser().getName() + ":" + status.getText());
					values.add(status.getText() + " at " + status.getCreatedAt());
				}
			}
			catch(Exception e){
				Log.e("Cauliflower","Failed to get tweets: " + e.toString());
			}

			return "Done!";
		}

		@Override
		protected void onProgressUpdate(Integer... ints) {

		}

		@Override
		protected void onPostExecute(String result) {
			adapter.notifyDataSetChanged();
		}
	}
}
