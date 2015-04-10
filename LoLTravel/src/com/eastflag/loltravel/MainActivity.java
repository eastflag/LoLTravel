package com.eastflag.loltravel;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.sromku.simple.fb.Permission.Type;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;

public class MainActivity extends Activity {
	
	private SimpleFacebook mSimpleFacebook;
	
	private TextView btnLogout, btnName;
	private ProgressDialog mProgressDialog;
	
	private String mId, mName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSimpleFacebook = SimpleFacebook.getInstance(this);
		setContentView(R.layout.activity_main);
		
		init();
		
		//커스텀 액션바----------------------------------------------------------------------------------
		ActionBar mActionBar = getActionBar();
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		View mCustomView = View.inflate(this, R.layout.actionbar, null);
		btnLogout = (TextView) mCustomView.findViewById(R.id.btnLogout);
		btnName = (TextView) mCustomView.findViewById(R.id.btnName);
		btnLogout.setVisibility(View.INVISIBLE);
		btnName.setVisibility(View.INVISIBLE);
		mActionBar.setCustomView(mCustomView);
		mActionBar.setDisplayShowCustomEnabled(true);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mSimpleFacebook = SimpleFacebook.getInstance(this);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
	}
	
	private void init() {
		findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mSimpleFacebook.login(new OnLoginListener() {
					@Override
					public void onFail(String reason) {
						Log.d("LDK", "onFail:" + reason);
					}
					
					@Override
					public void onException(Throwable throwable) {
						Log.d("LDK", "onException:" + throwable.getMessage());
					}
					
					@Override
					public void onThinking() {
						
					}
					
					@Override
					public void onNotAcceptingPermissions(Type type) {
						Log.d("LDK", "onNotAcceptingPermissions:" + type.name());
					}
					
					@Override
					public void onLogin() {
						Log.d("LDK", "onLogin:");
						getProfile();
					}
				});
			}
		});
	}
	
	private void getProfile() {
		mSimpleFacebook.getProfile(new OnProfileListener() {
			@Override
			public void onThinking() {
				mProgressDialog = new ProgressDialog(MainActivity.this);
				mProgressDialog.setTitle("Wait");
				mProgressDialog.setMessage("Getting the facebook info...");
				mProgressDialog.show();
			}

			@Override
			public void onException(Throwable throwable) {
				mProgressDialog.hide();
			}

			@Override
			public void onFail(String reason) {
				mProgressDialog.hide();
			}

			@Override
			public void onComplete(Profile response) {
				mProgressDialog.hide();
				mName = response.getName();
				mId = response.getId();
				refreshLoggedUI();
			}
		});
	}
	
	private void refreshLoggedUI() {
		findViewById(R.id.mainLayout).setVisibility(View.GONE);
		
		btnLogout.setVisibility(View.VISIBLE);
		btnName.setVisibility(View.VISIBLE);
		btnName.setText(mName);
	}

}
