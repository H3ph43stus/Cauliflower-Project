package com.cauliflower.phase.vi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class GameInstance {
	private boolean hasValues = false;
	String webserviceURL = "http://cauliflowerpowershower.appspot.com/";
	String groupName; 
	String username;
	ArrayList<GameInfo> values;
	int score;
	boolean isDead;
	Date olderDate;
	long time;
	int monsterX = 0, monsterY = 0;
	int curX = 0, curY = 0;
	
	public int getCurX() {
		return curX;
	}

	public void setCurX(int curX) {
		this.curX = curX;
	}

	public int getCurY() {
		return curY;
	}

	public void setCurY(int curY) {
		this.curY = curY;
	}

	MonsterThread monster = new MonsterThread(this);
	
	public GameInstance(String groupName, String username) {
		this.groupName = groupName;
		this.username = username;
		values = new ArrayList<GameInfo>();
		score = 0;
		isDead = false;
		olderDate = new Date();
		time = 0;
		monsterX = 0;
		monsterY = 0;
	}
	
	public void startMonster(){
		monster.start();
	}
	
	public void update() {
		String url = webserviceURL + "view/" + groupName;
		new GetStatusesTask().execute(url,"true");
	}
	
	public float[] loadXValues() {
		float[] myValues = ((GameInfo) values.get(0)).getxLocations();
		return myValues;
	}
	
	public float[] loadYValues() {
		float[] myValues = ((GameInfo) values.get(0)).getyLocations();
		return myValues;
	}
	
	public void pointFound() {
		score++;
		String url = "http://plato.cs.virginia.edu/~tsc8cm/cakephp/statuses/update/"
				+groupName+"/"+username+"/"+score+"/1";
		new GetStatusesTask().execute(url,"false");
	}
	
	public void calcTime() {
		Date newerDate = new Date();
		time = (newerDate.getTime() - olderDate.getTime()) 
                / (1000);// * 60 * 60 * 24)
	}
	
	public void hasDied() {
		isDead = true;
		String url = "http://plato.cs.virginia.edu/~tsc8cm/cakephp/statuses/update/"
				+groupName+"/"+username+"/"+score+"/0";
		new GetStatusesTask().execute(url,"false");
	}
	
	public boolean isReady() {
		return hasValues;
	}
	
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
			hasValues=true;
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

	public int getMonsterX() {
		return monsterX;
	}

	public void setMonsterX(int monsterX) {
		this.monsterX = monsterX;
	}

	public int getMonsterY() {
		return monsterY;
	}

	public void setMonsterY(int monsterY) {
		this.monsterY = monsterY;
	}
	
	public int getScore(){
		return this.score;
	}
	
	private class MonsterThread extends Thread{
		GameInstance game;
		float rate = (float) 0.8;
		int delay = 10000;
		
		MonsterThread(GameInstance game){
			this.game = game;
		}
		
		public void run(){
			int mX = game.getMonsterX();
			int mY = game.getMonsterY();
			int cX = game.getCurX();
			int cY = game.getCurY();
			
			int xdif = mX - cX;
			int ydif = mY - cY;
			
			game.setMonsterX((int) (cX + rate*xdif));
			game.setMonsterY((int) (cY + rate*ydif));
			
			try {
				Thread.sleep(delay / game.getScore());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				Log.e("error",e.getMessage());
			}
		}
	}
}
