package com.cauliflower.phase.vi;

public class GameInfo {
	public String key;
	public float[] xLocations;
	public float[] yLocations;
	
	public String toString(){
		String ret = "(" + xLocations[0] + "," + yLocations[0] + ")";
		for(int i = 1; i < 8; i++){
			ret += ",(" + xLocations[i] + "," + yLocations[i] + ")";
		}
		return ret;
	}

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public float[] getxLocations() {
		return xLocations;
	}
	public void setxLocations(float[] xLocations) {
		this.xLocations = xLocations;
	}
	public float[] getyLocations() {
		return yLocations;
	}
	public void setyLocations(float[] yLocations) {
		this.yLocations = yLocations;
	}
}
