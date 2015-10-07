package com.eastflag.loltravel.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.eastflag.loltravel.LoLApplication;
import com.eastflag.loltravel.R;
import com.eastflag.loltravel.adapter.RankingAdapter;
import com.eastflag.loltravel.dto.MyLocation;
import com.eastflag.loltravel.dto.RankingVO;
import com.eastflag.loltravel.utils.PreferenceUtil;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class RankingFragment extends Fragment {
	
	private AQuery mAq;
	private View mView;
	private ImageView iv_profile;
	private TextView tv_name, tv_point;
	
	private ListView lv_ranking;
	private RankingAdapter mAdapter;
	private ArrayList<RankingVO> mRankingList = new ArrayList<RankingVO>();

	public RankingFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = View.inflate(getActivity(), R.layout.fragment_ranking, null);
		mAq = new AQuery(mView);
		
		iv_profile = (ImageView) mView.findViewById(R.id.iv_profile);
		tv_name = (TextView) mView.findViewById(R.id.tv_name);
		tv_point = (TextView) mView.findViewById(R.id.tv_point);
		mView.findViewById(R.id.tv_rank).setVisibility(View.GONE);
		
		lv_ranking = (ListView) mView.findViewById(R.id.lv_ranking);
		mAdapter = new RankingAdapter(getActivity(), mRankingList);
		lv_ranking.setAdapter(mAdapter);
		
		String facebook_id = PreferenceUtil.instance(getActivity()).getFacebookId();
		if(!TextUtils.isEmpty(facebook_id)) {
			String image_url = String.format("http://graph.facebook.com/%s/picture?type=square", facebook_id);
			mAq.id(R.id.iv_profile).image(image_url);
		}
		
		tv_name.setText(PreferenceUtil.instance(getActivity()).getName());
		
		getMyPoint();
		
		getRankingList();
		
		return mView;
	}
	
	private void getMyPoint() {
		//서버로 데이터 전송
		String url = LoLApplication.HOST + LoLApplication.API_POINT_GETMYPOINT;
		JSONObject json = new JSONObject();
		try {
			json.put("userId", PreferenceUtil.instance(getActivity()).getId());
			
			Log.d("LDK", "url:" + url);
			Log.d("LDK", json.toString(1));
			
			mAq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>(){
				@Override
				public void callback(String url, JSONObject object, AjaxStatus status) {
					//update or insert
					try {
						if(object.getInt("result") == 0) {
							Log.d("LDK", "result:" + object.toString(1));

							int count = object.getJSONObject("data").getJSONArray("count").getJSONObject(0).getInt("count");
							tv_point.setText(String.valueOf(count) + " point");
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
	
	private void getRankingList() {
		//서버로 데이터 전송
		String url = LoLApplication.HOST + LoLApplication.API_POINT_GETRANKINGLIST;

		Log.d("LDK", "url:" + url);
		
		mAq.post(url, new JSONObject(), JSONObject.class, new AjaxCallback<JSONObject>(){
			@Override
			public void callback(String url, JSONObject object, AjaxStatus status) {
				try {
					if(object.getInt("result") == 0) {
						Log.d("LDK", "result:" + object.toString(1));

						JSONArray array = object.getJSONArray("data");
						for(int i=0; i< array.length(); ++i) {
							JSONObject json = array.getJSONObject(i);
							int count = json.getInt("count");
							String id = json.getJSONObject("_id").getString("_id");
							String name = json.getJSONObject("_id").getString("name");
							
							RankingVO ranking = new RankingVO();
							ranking.email = id;
							ranking.name = name;
							ranking.point = count;
							mRankingList.add(ranking);
						}
						
						//내림차순 정렬
						Collections.sort(mRankingList, new Comparator<RankingVO>() {
							@Override
							public int compare(RankingVO lhs, RankingVO rhs) {
								return lhs.point < rhs.point ? 1 : lhs.point == rhs.point ? 0 : -1;
							}
						});
						
						mAdapter.setData(mRankingList);
						mAdapter.notifyDataSetChanged();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

	}

}
