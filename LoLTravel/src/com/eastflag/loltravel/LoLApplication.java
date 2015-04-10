package com.eastflag.loltravel;

import com.eastflag.loltravel.utils.SharedObjects;
import com.facebook.SessionDefaultAudience;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;

import android.app.Application;

public class LoLApplication extends Application {
	private static final String APP_ID = "919687801386078";
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
