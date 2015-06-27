package com.eastflag.loltravel.dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.eastflag.loltravel.LoLApplication;
import com.eastflag.loltravel.R;
import com.eastflag.loltravel.dto.MyLocation;
import com.eastflag.loltravel.dto.MyTravel;
import com.eastflag.loltravel.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class LocationDialogFragment extends DialogFragment {
	private View mView;
	//private ListView mListView;
	//private LocationAdapter mAdapter;
	private ArrayList<MyLocation> mMyLocationList = new ArrayList<MyLocation>();
	private AQuery mAq;
	
	private GoogleMap mGoogleMap;
	private MapFragment mapFragment;
	
	private MyTravel mTravel;
	
	private TextView tv_history_title;
	
	public LocationDialogFragment(MyTravel travel) {
		mTravel = travel;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mView = View.inflate(getActivity(), R.layout.fragment_location, null);
		tv_history_title = (TextView) mView.findViewById(R.id.tv_history_title);
		mAq = new AQuery(mView);
		
		String title = mTravel.created + "\r\n" 
				+ "출발:" + mTravel.mOrigin.address + "\r\n"
				+ "도착:" + mTravel.mDestination.address;
		tv_history_title.setText(title);
		
		//mListView = (ListView) mView.findViewById(R.id.lvLocation);
		//mAdapter = new LocationAdapter(getActivity(), mLocationList); 
		//mListView.setAdapter(mAdapter);
		
		setUpMapIfNeeded();
		
		if(mTravel.mDestination != null) {
			Log.d("LDK", "loc:" + mTravel.mOrigin + "," + mTravel.mOrigin.lng);
			mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mTravel.mDestination.lat, mTravel.mDestination.lng), 13));
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
	        .setTitle("Travel History")
	        .setPositiveButton("Close",
	            new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                }
	            }
	        )
	        .setView(mView);
		
		getData();

		return builder.create();
	}
	
	
	
	@Override
	public void onDestroyView() {
		if(mapFragment != null) {
			getFragmentManager().beginTransaction().remove(mapFragment).commit();
		}
		super.onDestroyView();
	}

	private void setUpMapIfNeeded() {
		// check if we have got the googleMap already
		if (mGoogleMap == null) {
			mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
			mGoogleMap = mapFragment.getMap();
		}
	}
	
	private void getData() {
		String url = LoLApplication.HOST + LoLApplication.API_LOCATION_GETLIST;
		JSONObject json = new JSONObject();

		try {
			json.put("travelId", mTravel.travelId);
			
			Log.d("LDK", "url:" + url);
			Log.d("LDK", json.toString(1));
			
			mAq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>(){
				@Override
				public void callback(String url, JSONObject object, AjaxStatus status) {
					try {
						if(object.getInt("result") == 0) {
							Log.d("LDK", object.toString(1));
							JSONArray array = object.getJSONArray("data");
							for(int i=0; i<array.length(); ++i) {
								MyLocation location = new MyLocation();
								location.created = array.getJSONObject(i).getString("created");
								location.address = array.getJSONObject(i).getString("address");
								location.lat = array.getJSONObject(i).getDouble("lat");
								location.lng = array.getJSONObject(i).getDouble("lng");
								mMyLocationList.add(location);
							}
							
							//mAdapter.setData(mLocationList);
							//mAdapter.notifyDataSetChanged();
							
							//오름차순 정렬
							Collections.sort(mMyLocationList, new Comparator<MyLocation>() {
								@Override
								public int compare(MyLocation lhs,MyLocation rhs) {
									return lhs.created.compareTo(rhs.created);
								}

							});
							
							//위치가 2개이상 존재할 경우
							for (int i = 0; i < mMyLocationList.size() - 1; ++i) {
								//시작점
								if(i==0) {
									Marker startMarker = mGoogleMap.addMarker(new MarkerOptions()
					                        .position(new LatLng(mMyLocationList.get(i).lat, mMyLocationList.get(i).lng))
					                        .title("origin")
					                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_origin))
					                        .snippet(Utils.getAddress(getActivity(), mMyLocationList.get(i).lat, mMyLocationList.get(i).lng)));
					                startMarker.showInfoWindow();
								}
		                
								mGoogleMap.addPolyline(new PolylineOptions()
				                        .add(new LatLng(mMyLocationList.get(i).lat, mMyLocationList.get(i).lng),
				                                new LatLng(mMyLocationList.get(i+1).lat, mMyLocationList.get(i+1).lng))
				                        .width(18).color(Color.RED).geodesic(true));
								
								//끝점
								if(i==(mMyLocationList.size()-2)) {
					                Marker endMarker = mGoogleMap.addMarker(new MarkerOptions()
					                        .position(new LatLng(mMyLocationList.get(i+1).lat, mMyLocationList.get(i+1).lng))
					                        .title("destination")
					                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_destination))
					                        .snippet(Utils.getAddress(getActivity(), mMyLocationList.get(i+1).lat, mMyLocationList.get(i+1).lng)));
					                endMarker.showInfoWindow();
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

}
