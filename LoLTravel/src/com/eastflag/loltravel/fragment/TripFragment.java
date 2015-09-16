package com.eastflag.loltravel.fragment;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
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
import com.eastflag.loltravel.dto.MyLocation;
import com.eastflag.loltravel.utils.PreferenceUtil;
import com.eastflag.loltravel.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class TripFragment extends Fragment {
	
	private View mView;
	private AQuery mAq;
	private GoogleMap mGoogleMap;
	private Location mLocation;
	private String mAddress;
	
	private int a31, a32, a33;
	
	Button btnSurvey, btnOrigin, btnDestination;
	private AlertDialog mDialog;
	private ProgressDialog mProgress;
	private MapFragment mapFragment;
	
	private GoogleApiClient mGoogleApiClient;

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
		
		setUpMapIfNeeded();
		
		mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
	        .addConnectionCallbacks(mCallbacks)
	        .addOnConnectionFailedListener(mFailedListener)
	        .addApi(LocationServices.API)
	        .build();
		mGoogleApiClient.connect();
		
//		mLocation = new Location("dummyprovider");
//		mLocation.setLatitude(((MainActivity)getActivity()).mLatitude);
//		mLocation.setLongitude(((MainActivity)getActivity()).mLongitude);
		//connect 콜백으로 이동
/*		if(mLocation != null) {
			Log.d("LDK", "loc:" + mLocation.getLatitude() + "," + mLocation.getLongitude());
			mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 13));
			
		}*/
		
		//progress창 띄우고 위치를 찾을때까지대기
		mProgress = new ProgressDialog(getActivity());
		mProgress.setTitle("Wait for finding location...");
		mProgress.setCancelable(false);
		
		return mView;
	}
	
	@Override
	public void onDestroy() {
		if(mapFragment != null) {
			getFragmentManager().beginTransaction().remove(mapFragment).commit();
		}
		super.onDestroy();
	}
	
	
	private void setUpMapIfNeeded() {
		// check if we have got the googleMap already
		if (mGoogleMap == null) {
			mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
			mGoogleMap = mapFragment.getMap();
		}
	}

	private void getTravelInfo() {
		String travelId = PreferenceUtil.instance(getActivity()).getTravelInfo();
		if(TextUtils.isEmpty(travelId)) {
			//기존 여행정보가 없다면 설문조사를 보여주고, 지도상에 origin 아이콘 표시한다.
			if(mLocation != null) {
				Marker startMarker = mGoogleMap.addMarker(new MarkerOptions()
		            .position(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()))
		            .title(getString(R.string.origin))
		            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_origin))
		            .snippet(Utils.getAddress(getActivity(),mLocation.getLatitude(), mLocation.getLongitude())));
				startMarker.showInfoWindow();
			}
			 
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
							
							//출발을 입력했다면 출발지 버튼을 비활성화 시키고, 트래킹 정보를 가져와서 지도에 뿌린다.
							if(object.getJSONObject("data").has("origin")) {
								btnOrigin.setEnabled(false);
								btnOrigin.setClickable(false);
								btnOrigin.setTextColor(Color.GRAY);
								GetLocation();
							} else {
								//출발지가 없다면 지도에 현재위치를 출발지로 설정한다.
				                Marker startMarker = mGoogleMap.addMarker(new MarkerOptions()
				                        .position(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()))
				                        .title(getString(R.string.origin))
				                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_origin))
				                        .snippet(Utils.getAddress(getActivity(), mLocation.getLatitude(), mLocation.getLongitude())));
				                startMarker.showInfoWindow();
							}
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
					Utils.showToast(getActivity(), getString(R.string.check_31));
					return;
				}
				if(a32 == 0) {
					Utils.showToast(getActivity(), getString(R.string.check_32));
					return;
				}
				if(a33 == 0) {
					Utils.showToast(getActivity(), getString(R.string.check_33));
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
		builder.setTitle(getString(R.string.information_title))
			.setView(view);
		mDialog = builder.create();
		mDialog.show();
	}
	
	//Origin 입력 다이얼로그
	private void showOrigin() {
		String msg = getString(R.string.current_location) + "\r\n" +  getMsgOfLocation() + "\r\n\r\n"
				+ getString(R.string.set_origin);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getString(R.string.origin))
			.setMessage(msg)
			.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//서버에 origin 정보 저장후 지속적인 트래킹
					postOrigin();
				}
			})
			.setNegativeButton(getString(R.string.cancel), null)
			.show();
	}

	private String getMsgOfLocation() {
		String msg = getString(R.string.latitude) + ": "+ mLocation.getLatitude() + "\r\n"
				+ getString(R.string.longitude) + ": " +mLocation.getLongitude();
		Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
		List<Address> addresses = null;
		try {
			addresses = geocoder.getFromLocation(
					mLocation.getLatitude(),
					mLocation.getLongitude(), 1);
		} catch (IOException ioException) {
			Log.d("LDK", ioException.getMessage());
		} catch (IllegalArgumentException illegalArgumentException) {
			Log.d("LDK", illegalArgumentException.getMessage());
		}
		if(addresses.size()>0) {
			mAddress = addresses.get(0).getAddressLine(0);
			msg += "\r\n" + mAddress;
		} else {
			mAddress = "";
		}
		
		return msg;
	}
	
	//Destination 입력 다이얼로그
	private void showDestination() {
		String msg = getString(R.string.current_location) + "\r\n" +  getMsgOfLocation() + "\r\n\r\n"
				+ getString(R.string.set_destination);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getString(R.string.destination))
			.setMessage(msg)
			.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//서버에 origin 정보 저장후 지속적인 트래킹
					postDestination();
				}
			})
			.setNegativeButton(getString(R.string.cancel), null)
			.show();
	}
	
	private void postTravelInfo() {
		//서버로 여행 정보 전송
		LoadingDialog.showLoading(getActivity());
		String url = LoLApplication.HOST + LoLApplication.API_TRAVEL_ADD;
		JSONObject json = new JSONObject();
		JSONObject jsonSo = new JSONObject();
		try {
			json.put("id", PreferenceUtil.instance(getActivity()).getId());
			
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
							Utils.showToast(getActivity(), getString(R.string.toast_success));
							//new trip 정보 저장
							String _id = object.getJSONObject("data").getString("_id");
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
	
	private void postOrigin() {
		//서버로 데이터 전송
		LoadingDialog.showLoading(getActivity());
		String url = LoLApplication.HOST + LoLApplication.API_TRAVEL_UPDATE;
		JSONObject json = new JSONObject();
		JSONObject jsonSo = new JSONObject();
		try {
			json.put("id", PreferenceUtil.instance(getActivity()).getTravelInfo());
			
			jsonSo.put("lat", mLocation.getLatitude());
			jsonSo.put("lng", mLocation.getLongitude());
			jsonSo.put("address", mAddress);
			json.put("origin", jsonSo);
			
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
							
							Utils.showToast(getActivity(), getString(R.string.toast_success));
							//출발지를 입력한 시간 저장
							PreferenceUtil.instance(getActivity()).setOrigin(DateFormat.getTimeInstance().format(new Date()));
							//출발지 입력 금지
							btnOrigin.setEnabled(false);
							btnOrigin.setClickable(false);
							btnOrigin.setTextColor(Color.GRAY);
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
	
	private void postDestination() {
		//서버로 데이터 전송
		LoadingDialog.showLoading(getActivity());
		String url = LoLApplication.HOST + LoLApplication.API_TRAVEL_UPDATE;
		JSONObject json = new JSONObject();
		JSONObject jsonSo = new JSONObject();
		try {
			json.put("id", PreferenceUtil.instance(getActivity()).getTravelInfo());
			
			jsonSo.put("lat", mLocation.getLatitude());
			jsonSo.put("lng", mLocation.getLongitude());
			jsonSo.put("address", mAddress);
			json.put("destination", jsonSo);
			
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
							
							Utils.showToast(getActivity(), getString(R.string.toast_success));
							//여행정보와 출발지 시간을 pref에서 제거
							PreferenceUtil.instance(getActivity()).setTravelInfo("");
							PreferenceUtil.instance(getActivity()).setOrigin("");
							
							//go to Main
							((MainActivity)getActivity()).onBackPressed();
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
	
	private void GetLocation() {
		String url = LoLApplication.HOST + LoLApplication.API_LOCATION_GET;
		JSONObject json = new JSONObject();
		try {
			String _id = PreferenceUtil.instance(getActivity()).getTravelInfo();
			json.put("travelId", _id);
			
			Log.d("LDK", "url:" + url);
			Log.d("LDK", json.toString(1));
			
			mAq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>(){
				@Override
				public void callback(String url, JSONObject object, AjaxStatus status) {
					//update or insert
					try {
						if(object.getInt("result") == 0) {
							ArrayList<MyLocation> mMyLocationList = new ArrayList<MyLocation>();
							JSONArray array = object.getJSONArray("data");
							for(int i=0; i< array.length(); ++i) {
								MyLocation loc = new MyLocation();
								JSONObject json = array.getJSONObject(i);
								loc.lat = json.getDouble("lat");
								loc.lng = json.getDouble("lng");
								loc.created = json.getString("created");
								mMyLocationList.add(loc);
							}
							
							//오름차순 정렬
							Collections.sort(mMyLocationList, new Comparator<MyLocation>() {
								@Override
								public int compare(MyLocation lhs,MyLocation rhs) {
									return lhs.created.compareTo(rhs.created);
								}

							});
							
							for(MyLocation locatoin : mMyLocationList) {
								Log.d("LDK", locatoin.created + ":" + locatoin.lat + "," + locatoin.lng);
							}
							
							//위치가 한개 이하일 경우
							if(mMyLocationList.size() <= 1) {
				                Marker startMarker = mGoogleMap.addMarker(new MarkerOptions()
				                        .position(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()))
				                        .title(getString(R.string.origin))
				                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_origin))
				                        .snippet(Utils.getAddress(getActivity(), mLocation.getLatitude(), mLocation.getLongitude())));
				                startMarker.showInfoWindow();
				            } else {
							
								//위치가 2개이상 존재할 경우
								for (int i = 0; i < mMyLocationList.size() - 1; ++i) {
									//시작점
									if(i==0) {
										Marker startMarker = mGoogleMap.addMarker(new MarkerOptions()
						                        .position(new LatLng(mMyLocationList.get(i).lat, mMyLocationList.get(i).lng))
						                        .title(getString(R.string.origin))
						                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_origin))
						                        .snippet(Utils.getAddress(getActivity(), mMyLocationList.get(i).lat, mMyLocationList.get(i).lng)));
						                startMarker.showInfoWindow();
									}
			                
									mGoogleMap.addPolyline(new PolylineOptions()
					                        .add(new LatLng(mMyLocationList.get(i).lat, mMyLocationList.get(i).lng),
					                                new LatLng(mMyLocationList.get(i+1).lat, mMyLocationList.get(i+1).lng))
					                        .width(15).color(Color.RED).geodesic(true));
									
									//끝점
									if(i==(mMyLocationList.size()-2)) {
						                Marker endMarker = mGoogleMap.addMarker(new MarkerOptions()
						                        .position(new LatLng(mMyLocationList.get(i+1).lat, mMyLocationList.get(i+1).lng))
						                        .title(getString(R.string.destination))
						                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_destination))
						                        .snippet(Utils.getAddress(getActivity(), mMyLocationList.get(i+1).lat, mMyLocationList.get(i+1).lng)));
						                endMarker.showInfoWindow();
						            }
					            }
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
				showDestination();
				break;
			}
		}
	};
	
	GoogleApiClient.ConnectionCallbacks mCallbacks = new GoogleApiClient.ConnectionCallbacks() {
		@Override
        public void onConnected(Bundle bundle) {
			mProgress.dismiss();
    		Log.d("LDK", "TripFragment: Location onConnected:");
    		mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    		
    		mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 13));
    		
    		getTravelInfo();
        }

        @Override
        public void onConnectionSuspended(int i) {

        }
    };
    
	GoogleApiClient.OnConnectionFailedListener mFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
        	mProgress.dismiss();
        	Utils.showToast(getActivity(), "Can't find current location. Please retry");
        	((MainActivity)getActivity()).onBackPressed();
        }
    };
}

//travel info insert
/*
 result:{
 "result": 0,
 "data": {
  "__v": 0,
  "userId": "eastflag@gmail.com",
  "_id": "554c1773269b3d580f9217d7",
  "travelInfo": {
   "flight": 1,
   "mode": 2,
   "purpose": 3
  },
  "created": "2015-05-08T01:54:59.479Z"
 }
} 
 */

/*
http://www.javabrain.kr:4000/api/lol/location/get
{
    "result": 0,
    "data": [{
        "_id": "554c1813269b3d580f9217d9",
        "travelId": "554c180a269b3d580f9217d8",
        "lat": 37.5275129,
        "lng": 126.7148948,
        "__v": 0,
        "created": "2015-05-08T01:57:39.283Z"
    }, {
    ...
    */
