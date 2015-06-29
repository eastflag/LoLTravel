package com.eastflag.loltravel.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.eastflag.loltravel.R;
import com.eastflag.loltravel.dto.RankingVO;

public class RankingAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<RankingVO> mRankingList;
	
	public RankingAdapter(Context context, ArrayList<RankingVO> rankingList) {
		mContext = context;
		this.mRankingList = rankingList;
	}
	
	public void setData(ArrayList<RankingVO> rankingList) {
		mRankingList = rankingList;
	}

	@Override
	public int getCount() {
		return mRankingList.size();
	}

	@Override
	public Object getItem(int position) {
		return mRankingList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.ranking_list_row, null);
			holder.tvRank = (TextView) convertView.findViewById(R.id.tv_rank);
			holder.ivProfile = (ImageView) convertView.findViewById(R.id.iv_profile);
			holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tvPoint = (TextView) convertView.findViewById(R.id.tv_point);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Log.d("LDK", "count:" + mRankingList.get(position).point);
		Log.d("LDK", "name:" + mRankingList.get(position).name);
		Log.d("LDK", "facebook_id:" + mRankingList.get(position).facebook_id);
		
		holder.tvRank.setText(String.valueOf(position+1)); //순위
		holder.tvName.setText(mRankingList.get(position).name);
		holder.tvPoint.setText(String.valueOf((mRankingList.get(position).point)) + " point");
		AQuery aq = new AQuery(convertView);
		String image_url = String.format("http://graph.facebook.com/%s/picture?type=square", mRankingList.get(position).facebook_id);
		aq.id(R.id.iv_profile).image(image_url);
		
		return convertView;
	}
	
	class ViewHolder {
		public TextView tvRank;
		public ImageView ivProfile;
		public TextView tvName;
		public TextView tvPoint;
	}

}
