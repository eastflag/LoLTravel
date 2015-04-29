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

	public void setToken(String token) {
		put("token", token);
	}
	
	public String getToken() {
		return("token");
	}
}
