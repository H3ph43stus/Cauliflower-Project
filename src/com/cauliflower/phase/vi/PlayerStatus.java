package com.cauliflower.phase.vi;

public class PlayerStatus {

	public String[] Status;
	public String key;
	public String username;
	public int pages_found;
	public boolean alive;
	public String time_finished;
	
	public String toString(){
		return "Username: " + username + "\t\tPages: " + pages_found + "\t\tAlive: " + alive + "\t\tKey: " + key;
	}
	
	public String[] getStatus() {
		return Status;
	}
	public void setStatus(String[] status) {
		Status = status;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getPages_found() {
		return pages_found;
	}
	public void setPages_found(int pages_found) {
		this.pages_found = pages_found;
	}
	public boolean isAlive() {
		return alive;
	}
	public void setAlive(boolean alive) {
		this.alive = alive;
	}
	public String getTime_finished() {
		return time_finished;
	}
	public void setTime_finished(String time_finished) {
		this.time_finished = time_finished;
	}
	
	//{"Status":{"key":"test2","username":"user1","pages_found":"3","alive":true,"time_finished":null}
	
}
