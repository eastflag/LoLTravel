package com.eastflag.loltravel;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

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

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.eastflag.loltravel.utils.PreferenceUtil;
import com.facebook.Request;
import com.facebook.Request.GraphUserListCallback;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.sromku.simple.fb.Permission.Type;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;

public class MainActivity extends Activity {
	
	private SimpleFacebook mSimpleFacebook;
	
	private TextView btnLogout, btnName;
	private ProgressDialog mProgressDialog;
	
	private String mId, mName, mEmail;
	
	private AQuery mAq;

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
		mAq = new AQuery(this);
		
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
				mEmail = response.getEmail();
				Log.d("LDK", String.format("name:%s, id:%s, email:%s", mName, mId, mEmail));
				refreshLoggedUI();
			}
		});
	}
	
	private void refreshLoggedUI() {
		findViewById(R.id.mainLayout).setVisibility(View.GONE);
		
		btnLogout.setVisibility(View.VISIBLE);
		btnName.setVisibility(View.VISIBLE);
		btnName.setText(mName);
		
		String url = LoLApplication.HOST + LoLApplication.API_LOGIN_FACEBOOK;
		JSONObject json = new JSONObject();
		try {
			json.put("_id", mEmail);
			json.put("facebook_id", mId);
			json.put("facebook_name", mName);
			Log.d("LDK", "input url:" + url);
			Log.d("LDK", "parameter:" + json.toString(1));
			
			mAq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>(){
				@Override
				public void callback(String url, JSONObject object, AjaxStatus status) {
					try {
						Log.d("LDK", "result:" + object.toString());
						if(status.getCode() != 200) {
							return;
						}
						if("0".equals(object.getString("result"))) {
							Log.d("LDK", "token:" + object.getString("token"));
							PreferenceUtil.instance(MainActivity.this).setToken(object.getString("token"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}		
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	//페이스북 친구리스트 정책 변경됨
	//graph API 1.x에서는 페이스북 모든 친구리스트를 가져왔으나, 보안문제로 2015.4.30일부터 막히고,
	//2.x부터 해당 앱을 사용하는 친구목록만 가져옴.
}
