package com.eastflag.loltravel;

import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.eastflag.loltravel.dialog.LoadingDialog;
import com.eastflag.loltravel.dialog.LoginDialog;
import com.eastflag.loltravel.dto.MyInfoVO;
import com.eastflag.loltravel.fragment.MyinfoFragment;
import com.eastflag.loltravel.fragment.RankingFragment;
import com.eastflag.loltravel.fragment.SetupFragment;
import com.eastflag.loltravel.fragment.TripFragment;
import com.eastflag.loltravel.listener.LoginListener;
import com.eastflag.loltravel.service.MyLocationService;
import com.eastflag.loltravel.utils.PreferenceUtil;
import com.eastflag.loltravel.utils.SharedObjects;
import com.eastflag.loltravel.utils.Utils;
import com.sromku.simple.fb.Permission.Type;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;
import com.sromku.simple.fb.listeners.OnProfileListener;

public class MainActivity extends Activity {
	
	private SimpleFacebook mSimpleFacebook;
	
	private TextView btnLogout, btnName;
	private ImageView iv_pin;
	private Button btnSurvey, btnMyInfo, btnTrip, btnRanking;
	private ProgressDialog mProgressDialog;
	
	private String mId, mName, mEmail;
	
	private LoginDialog mLoginDialog;
	
	private FragmentManager mFm;
	private Fragment mFragment;
	
	private AQuery mAq;
	
	public double mLatitude, mLongitude;
	
/*	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	mLatitude = intent.getDoubleExtra("lat", 0);
	    	mLongitude = intent.getDoubleExtra("lng", 0);
	    	Log.d("LDK", "MainActivity BR:" + mLatitude + "," + mLongitude);
	    }
	};*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSimpleFacebook = SimpleFacebook.getInstance(this);
		setContentView(R.layout.activity_main);
		
		init();
		
		SharedObjects.context = getApplicationContext();
		
		//앱 실행시 위치 조회 시작. 앱종료시에 만일 new trip 정보가 있다면 서비스를 종료하지 않는다.
		startService(new Intent(this, MyLocationService.class));
		
		//서비스에서 보내오는 위치정보를 수신
		//LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("com.eastflag.location"));
		
		//커스텀 액션바----------------------------------------------------------------------------------
		ActionBar mActionBar = getActionBar();
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		View mCustomView = View.inflate(this, R.layout.actionbar, null);
		btnLogout = (TextView) mCustomView.findViewById(R.id.btnLogout);
		btnName = (TextView) mCustomView.findViewById(R.id.btnName);
		//btnLogout.setVisibility(View.INVISIBLE);
		//btnName.setVisibility(View.INVISIBLE);
		mActionBar.setCustomView(mCustomView);
		mActionBar.setDisplayShowCustomEnabled(true);
		
		/*btnLogout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle(getString(R.string.main_title))
					.setMessage(getString(R.string.logout))
					.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
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
					.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					})
					.show();
			}
		});*/
		
		checkLogin();
		
		//main animaition
		//Animation aniBounce = AnimationUtils.loadAnimation(this, R.anim.translate_bounce);
        //iv_pin.startAnimation(aniBounce);
        //iv_pin.startAnimation(aniBounce);
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
	protected void onDestroy() {
		/*if(TextUtils.isEmpty(PreferenceUtil.instance(this).getOrigin())) {
			stopService(new Intent(this, MyLocationService.class));
		}*/
		super.onDestroy();
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
			builder.setTitle(getString(R.string.main_title))
				.setMessage(getString(R.string.logout))
				.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.dismiss();
						finish();
					}
				})
				.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
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
		
		iv_pin = (ImageView) findViewById(R.id.iv_pin);
		btnSurvey = (Button) findViewById(R.id.btnSurvey);
		btnMyInfo = (Button) findViewById(R.id.btnMyInfo);
		btnTrip = (Button) findViewById(R.id.btnTrip);
		btnRanking = (Button) findViewById(R.id.btnRanking);
		
		btnSurvey.setOnClickListener(mMenuClick);
		btnMyInfo.setOnClickListener(mMenuClick);
		btnTrip.setOnClickListener(mMenuClick);
		btnRanking.setOnClickListener(mMenuClick);
		
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
	
	private void checkLogin() {
		if(!Utils.isNetworkConnected(this)) {
			Utils.showToast(this, "network connection is not available");
			showLoginDialog();
			return;
		}
		
		String id = PreferenceUtil.instance(this).getId();
		
		if(TextUtils.isEmpty(id)) {
			showLoginDialog();
		} else {
			checkSurveyExist();
		}
	}
	
	private void showLoginDialog(){
		if(mLoginDialog == null){
			mLoginDialog = new LoginDialog(this, mLoginListener);
		}
		
		if(mLoginDialog.isShowing() == false){
			mLoginDialog.show();
		}
	}
	
	private void getProfile() {
		mSimpleFacebook.getProfile(new OnProfileListener() {
			@Override
			public void onThinking() {
				mProgressDialog = new ProgressDialog(MainActivity.this);
				mProgressDialog.setTitle(getString(R.string.facebook_title));
				mProgressDialog.setMessage(getString(R.string.facebook_message));
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
				
				//이메일, 이름, 아이디 저장
				PreferenceUtil.instance(MainActivity.this).setId(mEmail);
				PreferenceUtil.instance(MainActivity.this).setName(mName);
				PreferenceUtil.instance(MainActivity.this).setFacebookId(mId);
				
				Log.d("LDK", String.format("name:%s, id:%s, email:%s", mName, mId, mEmail));
				refreshLoggedUI();
				
				checkSurveyExist();
			}
		});
	}
	
	private void checkSurveyExist() {
		btnName.setText(PreferenceUtil.instance(MainActivity.this).getName());
		
		String url = LoLApplication.HOST + LoLApplication.API_SURVEY_GET;
		JSONObject json = new JSONObject();

		try {
			json.put("id", PreferenceUtil.instance(MainActivity.this).getId());
			
			Log.d("LDK", "url:" + url);
			Log.d("LDK", json.toString(1));
			
			mAq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>(){
				@Override
				public void callback(String url, JSONObject object, AjaxStatus status) {
					try {	
						//데이터 존재하지 않음
						if(object.getInt("result") == 100) {
							AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
							builder.setTitle(getString(R.string.main_title))
								.setMessage(getString(R.string.title_not_exist_survey))
								.setPositiveButton(getString(R.string.ok_not_exist_survey), new DialogInterface.OnClickListener() {
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
	
	private void checkLastTravel() {
		String url = LoLApplication.HOST + LoLApplication.API_TRAVEL_LAST;
		JSONObject json = new JSONObject();

		try {
			json.put("userId", PreferenceUtil.instance(MainActivity.this).getId());
			
			Log.d("LDK", "url:" + url);
			Log.d("LDK", json.toString(1));
			
			mAq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>(){
				@Override
				public void callback(String url, JSONObject object, AjaxStatus status) {
					try {	
						//작성중인 통행정보를 가져온다.
						Log.d("LDK", object.toString(1));
						
						if(object.getInt("result") == 0) {
							if(object.getJSONArray("data").length() > 0) {
								JSONObject json = object.getJSONArray("data").getJSONObject(0);
								PreferenceUtil.instance(MainActivity.this).setTravelInfo(json.getInt("_id"));
								if(json.has("origin")) {
									PreferenceUtil.instance(MainActivity.this).setOrigin(json.getJSONObject("origin").getString("created"));
								}
							} else {
								PreferenceUtil.instance(MainActivity.this).setTravelInfo(0);
								PreferenceUtil.instance(MainActivity.this).setOrigin("");
							}
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
		
		Animation aniRightToLeft = AnimationUtils.loadAnimation(this, R.anim.right_to_left);
		btnSurvey.startAnimation(aniRightToLeft);
		
		Animation aniRightToLeft2 = AnimationUtils.loadAnimation(this, R.anim.right_to_left_off_250);
		btnMyInfo.startAnimation(aniRightToLeft2);
		
		Animation aniRightToLeft3 = AnimationUtils.loadAnimation(this, R.anim.right_to_left_off_500);
		btnTrip.startAnimation(aniRightToLeft3);
		
		Animation aniRightToLeft4 = AnimationUtils.loadAnimation(this, R.anim.right_to_left_off_750);
		btnRanking.startAnimation(aniRightToLeft4);
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
				
			case R.id.btnRanking:
				mFragment = new RankingFragment();
				break;
				
			case R.id.btnTrip:
				//해당 메뉴는 GPS를 켜야만 이용가능하게 한다.
				LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setTitle(getString(R.string.gps_title))
						.setMessage(getString(R.string.gps_message))
						.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(intent);
							}
						})
						.setNegativeButton(getString(R.string.cancel), null)
						.show();
					return;
				} else {
					mFragment = new TripFragment();
				}
				break;
				
			}
			
			showSubmenu();
		}
	};
	
	LoginListener mLoginListener = new LoginListener() {
		@Override
		public void onLogin(final MyInfoVO myInfo) {
			LoadingDialog.showLoading(MainActivity.this);
			String url = LoLApplication.HOST + LoLApplication.API_USER_ADD;
			JSONObject json = new JSONObject();

			try {
				json.put("id", myInfo.email); //email이 키
				//json.put("email", PreferenceUtil.instance(MainActivity.this).getEmail());
				json.put("name", myInfo.name);
				//국적 정보
				json.put("locale", Locale.getDefault().getCountry());
				
				Log.d("LDK", "url:" + url);
				Log.d("LDK", json.toString(1));
				
				mAq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>(){
					@Override
					public void callback(String url, JSONObject object, AjaxStatus status) {
						LoadingDialog.hideLoading();
						try {
							if(object.getInt("result") == 0) {
								//로그인 성공시 id(email), name 저장
								PreferenceUtil.instance(MainActivity.this).setId(myInfo.email);
								PreferenceUtil.instance(MainActivity.this).setName(myInfo.name);
								
								mLoginDialog.dismiss();
								checkSurveyExist();
								checkLastTravel();
							} else {
								Utils.showToast(MainActivity.this, "네트워크 오류입니다");
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
	};

	//페이스북 친구리스트 정책 변경됨
	//graph API 1.x에서는 페이스북 모든 친구리스트를 가져왔으나, 보안문제로 2015.4.30일부터 막히고,
	//2.x부터 해당 앱을 사용하는 친구목록만 가져옴.
}
