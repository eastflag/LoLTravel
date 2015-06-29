package com.eastflag.loltravel;

import com.eastflag.loltravel.utils.SharedObjects;
import com.facebook.SessionDefaultAudience;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;

import android.app.Application;

public class LoLApplication extends Application {
	public static final String HOST = "http://www.javabrain.kr:4000";
	public static final String API_LOGIN_FACEBOOK = "/api/user/facebookAuth";
	public static final String API_LOGIN_LOCAL = "/api/user/localAuth";
	public static final String API_SURVEY_ADD = "/api/lol/survey/add";
	public static final String API_SURVEY_GET = "/api/lol/survey/get";
	public static final String API_TRAVEL_ADD = "/api/lol/travel/add";
	public static final String API_TRAVEL_UPDATE = "/api/lol/travel/update";
	public static final String API_TRAVEL_GET = "/api/lol/travel/get";
	public static final String API_LOCATION_ADD = "/api/lol/location/add";
	public static final String API_LOCATION_GET = "/api/lol/location/get";
	public static final String API_TRAVEL_GETLIST = "/api/lol/travel/getlist";
	public static final String API_LOCATION_GETLIST = "/api/lol/location/getlist";
	public static final String API_POINT_GETMYPOINT = "/api/lol/point/getMyPoint";
	public static final String API_POINT_GETRANKINGLIST = "/api/lol/point/getRankingList";
	
	
	private static final String APP_ID = "919687801386078";
	private static final String APP_SECRET_KEY = "35b05ca2fedf4f1d7329e7393b13025f";
	private static final String APP_NAMESPACE = "fb_loltravel";

	@Override
	public void onCreate() {
		super.onCreate();
		SharedObjects.context = this;

		// set log to true
		//Logger.DEBUG_WITH_STACKTRACE = true;

		// initialize facebook configuration
		Permission[] permissions = new Permission[] { 
				Permission.PUBLIC_PROFILE,
				Permission.PUBLISH_ACTION,
				Permission.READ_FRIENDLISTS,
				Permission.USER_FRIENDS,
				Permission.USER_STATUS
				};

		SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
			.setAppId(APP_ID)
			.setNamespace(APP_NAMESPACE)
			.setPermissions(permissions)
			.setDefaultAudience(SessionDefaultAudience.FRIENDS)
			.setAskForAllPermissionsAtOnce(false)
			.build();

		SimpleFacebook.setConfiguration(configuration);
	}
}
