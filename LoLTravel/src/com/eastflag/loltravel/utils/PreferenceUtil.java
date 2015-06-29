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
	
	public void setEmail(String email) {
		put("email", email);
	}
	
	public String getEmail() {
		return get("email");
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
	
	public void setTravelInfo(String id) {
		put("travel", id);
	}
	
	public String getTravelInfo() {
		return get("travel");
	}
	
	public void setOrigin(String date) {
		put("origin", date);
	}
	
	public String getOrigin() {
		return get("origin");
	}
}
