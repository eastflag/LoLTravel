package com.eastflag.loltravel.service;

import com.eastflag.loltravel.Constants;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;

public class FetchAddressIntentService extends Service {
	protected ResultReceiver mReceiver;
	
	public FetchAddressIntentService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	 private void deliverResultToReceiver(int resultCode, String message) {
	        Bundle bundle = new Bundle();
	        bundle.putString(Constants.RESULT_DATA_KEY, message);
	        mReceiver.send(resultCode, bundle);
	    }
}
