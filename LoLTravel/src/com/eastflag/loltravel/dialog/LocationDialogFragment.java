package com.eastflag.loltravel.dialog;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.eastflag.loltravel.LoLApplication;
import com.eastflag.loltravel.R;
import com.eastflag.loltravel.adapter.LocationAdapter;
import com.eastflag.loltravel.dto.MyLocation;
import com.eastflag.loltravel.dto.MyTravel;
import com.eastflag.loltravel.utils.PreferenceUtil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class LocationDialogFragment extends DialogFragment {
	private View mView;
	private ListView mListView;
	private LocationAdapter mAdapter;
	private ArrayList<MyLocation> mLocationList = new ArrayList<MyLocation>();
	private AQuery mAq;
	
	private MyTravel mTravel;
	
	public LocationDialogFragment(MyTravel travel) {
		mTravel = travel;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mView = View.inflate(getActivity(), R.layout.fragment_location, null);
		mAq = new AQuery(mView);
		
		mListView = (ListView) mView.findViewById(R.id.lvLocation);
		mAdapter = new LocationAdapter(getActivity(), mLocationList); 
		mListView.setAdapter(mAdapter);
		
		getData();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
	        .setTitle("Location History")
	        .setPositiveButton("Close",
	            new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                }
	            }
	        )
	        .setView(mView);

		return builder.create();
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
								mLocationList.add(location);
							}
							
							mAdapter.setData(mLocationList);
							mAdapter.notifyDataSetChanged();
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

}
