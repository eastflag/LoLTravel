package com.eastflag.loltravel.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.eastflag.loltravel.LoLApplication;
import com.eastflag.loltravel.R;
import com.eastflag.loltravel.adapter.TravelHistoryAdapter;
import com.eastflag.loltravel.dialog.LocationDialogFragment;
import com.eastflag.loltravel.dto.MyLocation;
import com.eastflag.loltravel.dto.MyTravel;
import com.eastflag.loltravel.utils.PreferenceUtil;
import com.eastflag.loltravel.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MyinfoFragment extends Fragment {
	
	private View mView;
	private ListView mListView;
	private TravelHistoryAdapter mAdapter;
	private ArrayList<MyTravel> mTravelList = new ArrayList<MyTravel>();
	
	private AQuery mAq;

	public MyinfoFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = View.inflate(getActivity(), R.layout.fragment_myinfo, null);
		
		mAq = new AQuery(mView);
		
		mListView = (ListView) mView.findViewById(R.id.lvHistory);
		mAdapter = new TravelHistoryAdapter(getActivity(), mTravelList);
		mListView.setAdapter(mAdapter);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				LocationDialogFragment dialog = new LocationDialogFragment(mTravelList.get(position));
				dialog.show(getActivity().getFragmentManager(), "LocationDialog");
			}
		});
		
		getHistory();
		
		return mView;
	}
	
	private void getHistory() {	
		String url = LoLApplication.HOST + LoLApplication.API_TRAVEL_GETLIST;
		JSONObject json = new JSONObject();

		try {
			json.put("userId", PreferenceUtil.instance(getActivity()).getId());
			
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
								MyTravel travel = new MyTravel();
								travel.travelId = array.getJSONObject(i).getString("_id");
								travel.created = Utils.getDateFromMongoDate(array.getJSONObject(i).getString("created"));
								travel.distance = array.getJSONObject(i).getInt("distance");
								travel.point = array.getJSONObject(i).getInt("point");
								if(array.getJSONObject(i).has("origin") && array.getJSONObject(i).has("destination")) {
									MyLocation origin = new MyLocation();
									MyLocation destination = new MyLocation();
									origin.address = array.getJSONObject(i).getJSONObject("origin").getString("address");
									origin.lat = array.getJSONObject(i).getJSONObject("origin").getDouble("lat");
									origin.lng = array.getJSONObject(i).getJSONObject("origin").getDouble("lng");
									destination.address = array.getJSONObject(i).getJSONObject("destination").getString("address");
									destination.lat = array.getJSONObject(i).getJSONObject("destination").getDouble("lat");
									destination.lng = array.getJSONObject(i).getJSONObject("destination").getDouble("lng");
									
									travel.mOrigin = origin;
									travel.mDestination = destination;
									mTravelList.add(travel);
								}
							}
							
							mAdapter.setData(mTravelList);
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

/*url:http://www.javabrain.kr:4000/api/lol/travel/getlist
{
 "userId": "eastflag@gmail.com"
}
{
 "result": 0,
 "data": [
  {
   "_id": "556d771b52f098e80f8a2d0b",
   "userId": "eastflag@gmail.com",
   "__v": 0,
   "destination": {
    "lat": 37.5272753,
    "lng": 126.7147028,
    "address": null
   },
   "origin": {
    "lat": 37.5272751,
    "lng": 126.7147054,
    "address": "한국 인천광역시 계양구 아나지로213번길 23"
   },
   "travelInfo": {
    "flight": 1,
    "mode": 3,
    "purpose": 3
   },
   "created": "2015-06-02T09:27:55.602Z"
  },
  {
   "_id": "556d7f8a52f098e80f8a2d1d",
   "userId": "eastflag@gmail.com",
   "__v": 0,
   "travelInfo": {
    "flight": 1,
    "mode": 3,
    "purpose": 3
   },
   "created": "2015-06-02T10:03:54.664Z"
  }
 ]
}*/