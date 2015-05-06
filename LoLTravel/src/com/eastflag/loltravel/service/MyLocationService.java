package com.eastflag.loltravel.service;

import java.text.DateFormat;
import java.util.Date;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.eastflag.loltravel.utils.PreferenceUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MyLocationService extends Service implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
	private GoogleApiClient mGoogleApiClient;
	private Location mLastLocation;
	private LocationRequest mLocationRequest;
	private String mLastUpdateTime;

	public MyLocationService() {
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("LDK", "onStartCommand");
		
		mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API)
        .build();
		
		mGoogleApiClient.connect();
		
		mLocationRequest = new LocationRequest();
	    mLocationRequest.setInterval(1000 * 60); //1분
	    mLocationRequest.setFastestInterval(10000); 
	    mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
	    
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
		super.onDestroy();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.d("LDK", "Location onConnected:");
		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
        	Log.d("LDK", "Location :" + mLastLocation.getLatitude() + mLastLocation.getLongitude());
            //mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        	postLocation();
        }
        
        //연결이 되면 위치 추적
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(Location location) {
		mLastLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        Log.d("LDK", mLastUpdateTime + ":" + mLastLocation.getLatitude() + mLastLocation.getLongitude());
        postLocation();
	}
	
	private void postLocation() {
		//send location to activity
		Intent intent = new Intent("com.eastflag.location");
		intent.putExtra("lat", mLastLocation.getLatitude());
		intent.putExtra("lng", mLastLocation.getLongitude());
		sendBroadcast(intent);
		
		//if _id is not null, post to server
		String _id = PreferenceUtil.instance(this).getTravelInfo();
		if(!TextUtils.isEmpty(_id)) {
			//post to server
		}
	}
}
