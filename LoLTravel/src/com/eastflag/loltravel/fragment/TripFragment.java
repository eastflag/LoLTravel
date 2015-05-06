package com.eastflag.loltravel.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.eastflag.loltravel.LoLApplication;
import com.eastflag.loltravel.MainActivity;
import com.eastflag.loltravel.R;
import com.eastflag.loltravel.dialog.LoadingDialog;
import com.eastflag.loltravel.utils.PreferenceUtil;
import com.eastflag.loltravel.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class TripFragment extends Fragment {
	
	private View mView;
	private AQuery mAq;
	private GoogleMap mGoogleMap;
	Location mLocation;
	
	private int a31, a32, a33;
	
	Button btnSurvey, btnOrigin, btnDestination;
	private AlertDialog mDialog;
	private MapFragment mapFragment;

	public TripFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = View.inflate(getActivity(), R.layout.fragment_trip, null);
		mAq = new AQuery(mView);
		
		btnSurvey = (Button) mView.findViewById(R.id.button1);
		btnOrigin = (Button) mView.findViewById(R.id.button2);
		btnDestination = (Button) mView.findViewById(R.id.button3);
		btnSurvey.setOnClickListener(mClick);
		btnOrigin.setOnClickListener(mClick);
		btnDestination.setOnClickListener(mClick);
		
		mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		mGoogleMap = mapFragment.getMap();
		
		mLocation.setLatitude(((MainActivity)getActivity()).mLatitude);
		mLocation.setLongitude(((MainActivity)getActivity()).mLongitude);
		if(mLocation != null) {
			Log.d("LDK", "loc:" + mLocation.getLatitude() + "," + mLocation.getLongitude());
			mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 16));
		}
		
		getTravelInfo();
		
		return mView;
	}
	
	@Override
	public void onDestroy() {
		if(mapFragment != null) {
			getFragmentManager().beginTransaction().remove(mapFragment).commit();
		}
		super.onDestroy();
	}

	private void getTravelInfo() {
		String travelId = PreferenceUtil.instance(getActivity()).getTravelInfo();
		if(TextUtils.isEmpty(travelId)) {
			showSurvery();
			return;
		}
				
		String url = LoLApplication.HOST + LoLApplication.API_TRAVEL_GET;
		JSONObject json = new JSONObject();

		try {
			json.put("travelId", PreferenceUtil.instance(getActivity()).getTravelInfo());
			
			Log.d("LDK", "url:" + url);
			Log.d("LDK", json.toString(1));
			
			mAq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>(){
				@Override
				public void callback(String url, JSONObject object, AjaxStatus status) {
					try {
						if(object.getInt("result") == 0) {
							Log.d("LDK", object.toString(1));
							JSONObject json = object.getJSONObject("data").getJSONObject("travelInfo");
							a31 = json.getInt("flight");
							a32 = json.getInt("mode");
							a33 = json.getInt("purpose");
						} else {
							
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
	
	private void showSurvery() {
		View view = View.inflate(getActivity(), R.layout.dialog_trip_survery, null);
		//통행정보 조사------------------------------------------------------------------
		RadioGroup q31 = (RadioGroup) view.findViewById(R.id.q31);
		q31.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId) {
				case R.id.q31_1:
					a31 = 1;
					break;
				case R.id.q31_2:
					a31 = 2;
					break;
				}
			}
		});
		
		RadioGroup q32 = (RadioGroup) view.findViewById(R.id.q32);
		q32.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId) {
				case R.id.q32_1:
					a32 = 1;
					break;
				case R.id.q32_2:
					a32 = 2;
					break;
				case R.id.q32_3:
					a32 = 3;
					break;
				case R.id.q32_4:
					a32 = 4;
					break;
				case R.id.q32_5:
					a32 = 5;
					break;
				}
			}
		});
		
		RadioGroup q33 = (RadioGroup) view.findViewById(R.id.q33);
		q33.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId) {
				case R.id.q33_1:
					a33 = 1;
					break;
				case R.id.q33_2:
					a33 = 2;
					break;
				case R.id.q33_3:
					a33 = 3;
					break;
				case R.id.q33_4:
					a33 = 4;
					break;
				case R.id.q33_5:
					a33 = 5;
					break;
				case R.id.q33_6:
					a33 = 6;
					break;
				case R.id.q33_7:
					a33 = 7;
					break;
				case R.id.q33_8:
					a33 = 8;
					break;
				}
			}
		});
		
		Button submit = (Button) view.findViewById(R.id.submit);
		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(a31 == 0) {
					Utils.showToast(getActivity(), "Check if you use flight for this trip");
					return;
				}
				if(a32 == 0) {
					Utils.showToast(getActivity(), "Check what is your travel mode for this trip");
					return;
				}
				if(a33 == 0) {
					Utils.showToast(getActivity(), "Check what is your purpose for this trip");
					return;
				}
				
				postTravelInfo();
			}
		});
		
		//이미 통행정보를 저장하였다면 저장된 데이터를 보여준다
		if(a31>0) {
			switch(a31) {
			case 1: q31.check(R.id.q31_1); break;
			case 2: q31.check(R.id.q31_2); break;
			}
		}
		if(a32>0) {
			switch(a32) {
			case 1: q32.check(R.id.q32_1); break;
			case 2: q32.check(R.id.q32_2); break;
			case 3: q32.check(R.id.q32_3); break;
			case 4: q32.check(R.id.q32_4); break;
			case 5: q32.check(R.id.q32_5); break;
			}
		}
		if(a33>0) {
			switch(a33) {
			case 1: q33.check(R.id.q33_1); break;
			case 2: q33.check(R.id.q33_2); break;
			case 3: q33.check(R.id.q33_3); break;
			case 4: q33.check(R.id.q33_4); break;
			case 5: q33.check(R.id.q33_5); break;
			case 6: q33.check(R.id.q33_6); break;
			case 7: q33.check(R.id.q33_7); break;
			case 8: q33.check(R.id.q33_8); break;
			}
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Travel Infomation")
			.setView(view);
		mDialog = builder.create();
		mDialog.show();
	}
	
	private void showOrigin() {
		String msg = "Latitude: "+ mLocation.getLatitude() + "\r\n"
				+ "Longitude: " +mLocation.getLongitude();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Origin")
			.setMessage(msg)
			.setPositiveButton("ok", null)
			.show();
	}
	
	private void showDestination() {
		String msg = "Latitude: "+ mLocation.getLatitude() + "\r\n"
				+ "Longitude: " +mLocation.getLongitude();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Destination")
			.setMessage(msg)
			.setPositiveButton("ok", null)
			.show();
	}
	
	private void postTravelInfo() {
		//서버로 데이터 전송
		LoadingDialog.showLoading(getActivity());
		String url = LoLApplication.HOST + LoLApplication.API_TRAVEL_ADD;
		JSONObject json = new JSONObject();
		JSONObject jsonSo = new JSONObject();
		try {
			json.put("id", PreferenceUtil.instance(getActivity()).getEmail());
			
			jsonSo.put("flight", a31);
			jsonSo.put("mode", a32);
			jsonSo.put("purpose", a33);
			json.put("travelInfo", jsonSo);
			
			Log.d("LDK", "url:" + url);
			Log.d("LDK", json.toString(1));
			
			mAq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>(){
				@Override
				public void callback(String url, JSONObject object, AjaxStatus status) {
					LoadingDialog.hideLoading();
					//update or insert
					try {
						if(object.getInt("result") == 0) {
							Log.d("LDK", "result:" + object.toString(1));
							
							mDialog.dismiss();
							Utils.showToast(getActivity(), "저장하였습니다");
							//new trip 정보 저장
							String _id = object.getJSONObject("data").getJSONArray("upserted").getJSONObject(0).getString("_id");
							PreferenceUtil.instance(getActivity()).setTravelInfo(_id);
							//show origin
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

	View.OnClickListener mClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.button1:
				showSurvery();
				break;
				
			case R.id.button2:
				showOrigin();
				break;
				
			case R.id.button3:
				
				break;
			}
		}
	};
}

//travel info insert
/*05-06 02:56:38.260: D/LDK(6453): result:{
05-06 02:56:38.260: D/LDK(6453):  "result": 0,
05-06 02:56:38.260: D/LDK(6453):  "data": {
05-06 02:56:38.260: D/LDK(6453):   "ok": 1,
05-06 02:56:38.260: D/LDK(6453):   "nModified": 0,
05-06 02:56:38.260: D/LDK(6453):   "n": 1,
05-06 02:56:38.260: D/LDK(6453):   "upserted": [
05-06 02:56:38.260: D/LDK(6453):    {
05-06 02:56:38.260: D/LDK(6453):     "index": 0,
05-06 02:56:38.260: D/LDK(6453):     "_id": "5549045b3775758199511f18"
05-06 02:56:38.260: D/LDK(6453):    }
05-06 02:56:38.260: D/LDK(6453):   ]
05-06 02:56:38.260: D/LDK(6453):  }
05-06 02:56:38.260: D/LDK(6453): }*/

/*05-07 00:36:21.932: D/LDK(20041): {
05-07 00:36:21.932: D/LDK(20041):  "result": 0,
05-07 00:36:21.932: D/LDK(20041):  "data": {
05-07 00:36:21.932: D/LDK(20041):   "_id": "554a33b23775758199511f1a",
05-07 00:36:21.932: D/LDK(20041):   "userId": "eastflag@gmail.com",
05-07 00:36:21.932: D/LDK(20041):   "travelInfo": {
05-07 00:36:21.932: D/LDK(20041):    "flight": 1,
05-07 00:36:21.932: D/LDK(20041):    "mode": 2,
05-07 00:36:21.932: D/LDK(20041):    "purpose": 3
05-07 00:36:21.932: D/LDK(20041):   },
05-07 00:36:21.932: D/LDK(20041):   "created": "2015-05-06T15:36:31.313Z"
05-07 00:36:21.932: D/LDK(20041):  }
05-07 00:36:21.932: D/LDK(20041): }*/
