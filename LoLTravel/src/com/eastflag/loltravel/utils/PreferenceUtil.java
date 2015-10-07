package com.eastflag.loltravel.utils;

import android.content.Context;

public class PreferenceUtil extends BasePreferenceUtil {
	private static PreferenceUtil _instance = null;

	public static synchronized PreferenceUtil instance(Context context) {
		if (_instance == null)
			_instance = new PreferenceUtil(context);
		return _instance;
	}

	private PreferenceUtil(Context context) {
		super(context);
	}
	
	public void setId(String id) {
		put("id", id);
	}
	
	public String getId() {
		return get("id");
	}
	
	public void setName(String name) {
		put("name", name);
	}
	
	public String getName() {
		return get("name");
	}
	
	public void setFacebookId(String id) {
		put("facebook_id", id);
	}
	
	public String getFacebookId() {
		return get("facebook_id");
	}

	public void setToken(String token) {
		put("token", token);
	}
	
	public String getToken() {
		return get("token");
	}
	
	public void setTravelInfo(int id) {
		put("travel", id);
	}
	
	public int getTravelInfo() {
		return get("travel", 0);
	}
	
	public void setOrigin(String date) {
		put("origin", date);
	}
	
	public String getOrigin() {
		return get("origin");
	}
	
	public void setLocationTime(int timestamp) {
		put("location_time", timestamp);
	}
	public int getLocationTime() {
		return get("location_time", 0);
	}
}
