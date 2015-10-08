package com.eastflag.loltravel.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eastflag.loltravel.R;
import com.eastflag.loltravel.dto.MyTravel;

public class TravelHistoryAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<MyTravel> mTravelList;
	
	public TravelHistoryAdapter(Context context, ArrayList<MyTravel> travelList) {
		mContext = context;
		mTravelList = travelList;
	}
	
	public void setData(ArrayList<MyTravel> travelList) {
		mTravelList = travelList;
	}

	@Override
	public int getCount() {
		return mTravelList.size();
	}

	@Override
	public Object getItem(int position) {
		return mTravelList.get(position);
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
			convertView = View.inflate(mContext, R.layout.history_list_row, null);
			holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
			holder.tvDistance = (TextView) convertView.findViewById(R.id.tvDistance);
			holder.tvPoint = (TextView) convertView.findViewById(R.id.tvPoint);
			holder.tvOrigin = (TextView) convertView.findViewById(R.id.tvOrigin);
			holder.tvDestination = (TextView) convertView.findViewById(R.id.tvDestination);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tvDate.setText(mTravelList.get(position).created);
		holder.tvDistance.setText(String.format("%,d km", mTravelList.get(position).distance));
		holder.tvPoint.setText(mTravelList.get(position).point + " point");
		holder.tvOrigin.setText(mTravelList.get(position).mOrigin.address);
		holder.tvDestination.setText(mTravelList.get(position).mDestination.address);
		
		return convertView;
	}
	
	class ViewHolder {
		public TextView tvDate;
		public TextView tvDistance;
		public TextView tvPoint;
		public TextView tvOrigin;
		public TextView tvDestination;
	}
}
