package com.eastflag.loltravel.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eastflag.loltravel.R;
import com.eastflag.loltravel.dto.MyLocation;

public class LocationAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<MyLocation> mLocationList;
	
	public LocationAdapter(Context context, ArrayList<MyLocation> locationList) {
		mContext = context;
		mLocationList = locationList;
	}
	
	public void setData(ArrayList<MyLocation> locationList) {
		mLocationList = locationList;
	}

	@Override
	public int getCount() {
		return mLocationList.size();
	}

	@Override
	public Object getItem(int position) {
		return mLocationList.get(position);
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
			convertView = View.inflate(mContext, R.layout.location_list_row, null);
			holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
			holder.tvOrigin = (TextView) convertView.findViewById(R.id.tvOrigin);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tvDate.setText(mLocationList.get(position).created);
		holder.tvOrigin.setText(mLocationList.get(position).address);
		
		return convertView;
	}
	
	class ViewHolder {
		public TextView tvDate;
		public TextView tvOrigin;
	}

}
