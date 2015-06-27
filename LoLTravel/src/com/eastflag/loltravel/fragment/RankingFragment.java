package com.eastflag.loltravel.fragment;

import com.eastflag.loltravel.R;
import com.eastflag.loltravel.R.layout;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class RankingFragment extends Fragment {
	
	private View mView;

	public RankingFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = View.inflate(getActivity(), R.layout.fragment_ranking, null);
		
		return mView;
	}

}
