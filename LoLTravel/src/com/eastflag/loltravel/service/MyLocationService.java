package com.eastflag.loltravel.service;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.eastflag.loltravel.LoLApplication;
import com.eastflag.loltravel.utils.PreferenceUtil;
import com.eastflag.loltravel.utils.SharedObjects;
import com.eastflag.loltravel.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MyLocationService extends Service {
	private GoogleApiClient mGoogleApiClient;
	private Location mLastLocation;
	private LocationRequest mLocationRequest;
	private String mLastUpdateTime;
	
	private AQuery mAq;

	public MyLocationService() {
		
	}
	
    GoogleApiClient.ConnectionCallbacks mCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
/*    		Log.d("LDK", "Location onConnected:");
    		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

    		if (mLastLocation != null) {
    			postLocation();
    		}*/
            
            //연결이 되면 위치 추적
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationListener);
        }

        @Override
        public void onConnectionSuspended(int i) {

        }
    };
    
    LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
    		mLastLocation = location;
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            Log.d("LDK", mLastUpdateTime + ":" + mLastLocation.getLatitude() + mLastLocation.getLongitude());
            postLocation();
        }
    };

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
    public void onCreate() {
        mAq = new AQuery(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(mCallbacks)
                .addOnConnectionFailedListener(mFailedListener)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000 * 60 * 5); //10 minutes
        mLocationRequest.setFastestInterval(1000 * 60 * 5);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        super.onCreate();
    }

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
		super.onDestroy();
	}
	
	private void postLocation() {
		//send location to activity
		Intent intent = new Intent("com.eastflag.location");
		intent.putExtra("lat", mLastLocation.getLatitude());
		intent.putExtra("lng", mLastLocation.getLongitude());
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		
		//출발지 정보가 있으면 트래킹한다.
		String originTime = PreferenceUtil.instance(this).getOrigin();
		Log.d("LDK", "getOrigin:" + originTime);
		if(!TextUtils.isEmpty(originTime) && !TextUtils.isEmpty(PreferenceUtil.instance(this).getTravelInfo())) {
			//post to server
			//서버로 데이터 전송
			String url = LoLApplication.HOST + LoLApplication.API_LOCATION_ADD;
			JSONObject json = new JSONObject();
			try {
				String _id = PreferenceUtil.instance(this).getTravelInfo();
				json.put("travelId", _id);
				
				json.put("lat", mLastLocation.getLatitude());
				json.put("lng", mLastLocation.getLongitude());
				json.put("address", Utils.getAddress(MyLocationService.this, mLastLocation.getLatitude(), mLastLocation.getLongitude()));
				
				Log.d("LDK", "url:" + url);
				Log.d("LDK", json.toString(1));
				
				mAq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>(){
					@Override
					public void callback(String url, JSONObject object, AjaxStatus status) {
						//update or insert
						try {
							if(object.getInt("result") == 0) {
								//Log.d("LDK", "result:" + object.toString(1));
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
	}
	
	GoogleApiClient.OnConnectionFailedListener mFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

        }
    };
}
