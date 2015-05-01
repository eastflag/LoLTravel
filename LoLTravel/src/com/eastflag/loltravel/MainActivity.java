package com.eastflag.loltravel;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.eastflag.loltravel.fragment.MyinfoFragment;
import com.eastflag.loltravel.fragment.SetupFragment;
import com.eastflag.loltravel.fragment.TripFragment;
import com.eastflag.loltravel.utils.PreferenceUtil;
import com.facebook.Request;
import com.facebook.Request.GraphUserListCallback;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.sromku.simple.fb.Permission.Type;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;
import com.sromku.simple.fb.listeners.OnProfileListener;

public class MainActivity extends Activity {
	
	private SimpleFacebook mSimpleFacebook;
	
	private TextView btnLogout, btnName;
	private ProgressDialog mProgressDialog;
	
	private String mId, mName, mEmail;
	
	private FragmentManager mFm;
	private Fragment mFragment;
	
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
		
		btnLogout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("LoLTravel")
					.setMessage("로그아웃하시겠습니까?")
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int arg1) {
							dialog.dismiss();
							mSimpleFacebook.logout(new OnLogoutListener() {
								@Override
								public void onFail(String reason) {

								}
								@Override
								public void onException(Throwable throwable) {

								}
								@Override
								public void onThinking() {

								}
								@Override
								public void onLogout() {
									refreshLogoutUI();
								}
							});
						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					})
					.show();
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mSimpleFacebook = SimpleFacebook.getInstance(this);
		
		/*findViewById(R.id.mainLayout).setVisibility(View.GONE);
		mFragment = new SetupFragment();
		mFm.beginTransaction().replace(R.id.container, mFragment).commit();*/
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
	}
	
	@Override
	public void onBackPressed() {
		if(mFragment == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("LoLTravel")
				.setMessage("종료하시겠습니까?")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.dismiss();
						finish();
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.show();
		} else {
			mFm.beginTransaction().remove(mFragment).commit();
			mFragment = null;
			hideSubmenu();
		}
	}
	
	private void init() {
		mAq = new AQuery(this);
		mFm = getFragmentManager();
		
		findViewById(R.id.btnSurvey).setOnClickListener(mMenuClick);
		findViewById(R.id.btnMyInfo).setOnClickListener(mMenuClick);
		findViewById(R.id.btnTrip).setOnClickListener(mMenuClick);
		
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
				
				//이메일 저장
				PreferenceUtil.instance(MainActivity.this).setEmail(mEmail);
				
				Log.d("LDK", String.format("name:%s, id:%s, email:%s", mName, mId, mEmail));
				refreshLoggedUI();
				
				checkSurveyExist();
			}
		});
	}
	
	private void checkSurveyExist() {
		String url = LoLApplication.HOST + LoLApplication.API_SURVEY_GET;
		JSONObject json = new JSONObject();

		try {
			json.put("id", PreferenceUtil.instance(this).getEmail());
			
			Log.d("LDK", "url:" + url);
			Log.d("LDK", json.toString(1));
			
			mAq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>(){
				@Override
				public void callback(String url, JSONObject object, AjaxStatus status) {
					try {
						//데이터 존재하지 않음
						if(object.getInt("result") == 100) {
							AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
							builder.setTitle("LoLTravel")
								.setMessage("Socioeconomic and Residential environment do not exist. Please create a survey")
								.setPositiveButton("Go to survey", new DialogInterface.OnClickListener() {
								    @Override
								    public void onClick(DialogInterface dialog, int which) {
								    	mFragment = new SetupFragment();
								    	showSubmenu();
								    	
								    	dialog.dismiss();
								    	
								    }
								})
								.setCancelable(false)
								.show();
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
	
	private void refreshLoggedUI() {
		findViewById(R.id.beforeLogin).setVisibility(View.GONE);
		findViewById(R.id.afterLogin).setVisibility(View.VISIBLE);
		
		btnLogout.setVisibility(View.VISIBLE);
		btnName.setVisibility(View.VISIBLE);
		btnName.setText(mName);
		
		//토큰 인증은 향후에...
		/*String url = LoLApplication.HOST + LoLApplication.API_LOGIN_FACEBOOK;
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
		}*/
	}
	
	private void refreshLogoutUI() {
		findViewById(R.id.beforeLogin).setVisibility(View.VISIBLE);
		findViewById(R.id.afterLogin).setVisibility(View.GONE);
		
		btnLogout.setVisibility(View.GONE);
		btnName.setVisibility(View.GONE);
	}
	
	private void showSubmenu() {
		findViewById(R.id.mainLayout).setVisibility(View.GONE);
		
		mFm.beginTransaction().replace(R.id.container, mFragment).commit();
	}
	
	private void hideSubmenu() {
		findViewById(R.id.mainLayout).setVisibility(View.VISIBLE);
	}
	
	View.OnClickListener mMenuClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.btnSurvey:
				mFragment = new SetupFragment();
				break;
				
			case R.id.btnMyInfo:
				mFragment = new MyinfoFragment();
				break;
				
			case R.id.btnTrip:
				mFragment = new TripFragment();
				return;
			}
			
			showSubmenu();
		}
	};

	//페이스북 친구리스트 정책 변경됨
	//graph API 1.x에서는 페이스북 모든 친구리스트를 가져왔으나, 보안문제로 2015.4.30일부터 막히고,
	//2.x부터 해당 앱을 사용하는 친구목록만 가져옴.
}
