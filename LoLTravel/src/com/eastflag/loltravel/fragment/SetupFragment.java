package com.eastflag.loltravel.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.eastflag.loltravel.R;
import com.eastflag.loltravel.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class SetupFragment extends Fragment {
	private View mView;
	
	private EditText q12_input, q13_input, q14_input; 
	
	//answer
	private int a11, a15, a21, a22, a23, a24, a25;
	private String a12, a13, a14;

	public SetupFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = View.inflate(getActivity(), R.layout.fragment_setup, null);
		
		//사회경제 조사------------------------------------------------------------------
		RadioGroup q11 = (RadioGroup) mView.findViewById(R.id.q11);
		q11.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId == R.id.q11_1) {
					a11 = 1;
				} else {
					a11 = 2;
				}
			}
		});
		
		q12_input = (EditText) mView.findViewById(R.id.q12_input);
		q13_input = (EditText) mView.findViewById(R.id.q13_input);
		q14_input = (EditText) mView.findViewById(R.id.q14_input);
		
		RadioGroup q15 = (RadioGroup) mView.findViewById(R.id.q15);
		q15.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId == R.id.q15_1) {
					a15 = 1;
				} else {
					a15 = 2;
				}
			}
		});
		
		//거주환경 조사------------------------------------------------------------------
		RadioGroup q21 = (RadioGroup) mView.findViewById(R.id.q21);
		q21.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId) {
				case R.id.q21_1:
					a21 = 1;
					break;
				case R.id.q21_2:
					a21 = 2;
					break;
				case R.id.q21_3:
					a21 = 3;
					break;
				case R.id.q21_4:
					a21 = 4;
					break;
				case R.id.q21_5:
					a21 = 5;
					break;
				}
			}
		});
		
		RadioGroup q22 = (RadioGroup) mView.findViewById(R.id.q22);
		q22.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId) {
				case R.id.q22_1:
					a22 = 1;
					break;
				case R.id.q22_2:
					a22 = 2;
					break;
				case R.id.q22_3:
					a22 = 3;
					break;
				case R.id.q22_4:
					a22 = 4;
					break;
				case R.id.q22_5:
					a22 = 5;
					break;
				}
			}
		});
		
		RadioGroup q23 = (RadioGroup) mView.findViewById(R.id.q23);
		q23.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId) {
				case R.id.q23_1:
					a23 = 1;
					break;
				case R.id.q23_2:
					a23 = 2;
					break;
				case R.id.q23_3:
					a23 = 3;
					break;
				case R.id.q23_4:
					a23 = 4;
					break;
				case R.id.q23_5:
					a23 = 5;
					break;
				}
			}
		});
		
		RadioGroup q24 = (RadioGroup) mView.findViewById(R.id.q24);
		q24.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId) {
				case R.id.q24_1:
					a24 = 1;
					break;
				case R.id.q24_2:
					a24 = 2;
					break;
				case R.id.q24_3:
					a24 = 3;
					break;
				case R.id.q24_4:
					a24 = 4;
					break;
				case R.id.q24_5:
					a24 = 5;
					break;
				}
			}
		});
		
		RadioGroup q25 = (RadioGroup) mView.findViewById(R.id.q25);
		q25.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId) {
				case R.id.q25_1:
					a25 = 1;
					break;
				case R.id.q25_2:
					a25 = 2;
					break;
				case R.id.q25_3:
					a25 = 3;
					break;
				case R.id.q25_4:
					a25 = 4;
					break;
				case R.id.q25_5:
					a25 = 5;
					break;
				}
			}
		});
		
		mView.findViewById(R.id.submit).setOnClickListener(mSubmit);
		
		return mView;
	}

	View.OnClickListener mSubmit = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			//validataion check
			if(a11 == 0) {
				Utils.showToast(getActivity(), "Check your sex");
				return;
			}
			if(TextUtils.isEmpty(q12_input.getText().toString())) {
				Utils.showToast(getActivity(), "Check your age");
				return;
			}
			if(TextUtils.isEmpty(q13_input.getText().toString())) {
				Utils.showToast(getActivity(), "Check your average income");
				return;
			}
			if(TextUtils.isEmpty(q14_input.getText().toString())) {
				Utils.showToast(getActivity(), "Check your address");
				return;
			}
			if(a15 == 0) {
				Utils.showToast(getActivity(), "Check a drive license");
				return;
			}
			
			if(a21 == 0) {
				Utils.showToast(getActivity(), "Check: " + getResources().getString(R.string.q21));
				return;
			}
			if(a22 == 0) {
				Utils.showToast(getActivity(), "Check: " + getResources().getString(R.string.q22));
				return;
			}
			if(a23 == 0) {
				Utils.showToast(getActivity(), "Check: " + getResources().getString(R.string.q23));
				return;
			}
			if(a24 == 0) {
				Utils.showToast(getActivity(), "Check: " + getResources().getString(R.string.q24));
				return;
			}
			if(a25 == 0) {
				Utils.showToast(getActivity(), "Check: " + getResources().getString(R.string.q25));
				return;
			}
			
			//서버로 데이터 전송
			JSONObject json = new JSONObject();
			JSONObject jsonSo = new JSONObject();
			JSONObject jsonRe = new JSONObject();
			try {
				jsonSo.put("sex", a11);
				jsonSo.put("age", q12_input.getText().toString());
				jsonSo.put("income", q13_input.getText().toString());
				jsonSo.put("address", q14_input.getText().toString());
				jsonSo.put("drive_license", a15);
				json.put("socioeconomic", jsonSo);
				
				jsonRe.put("public_transport", a21);
				jsonRe.put("commercial_facility", a22);
				jsonRe.put("leisure_facility", a23);
				jsonRe.put("green_space", a24);
				jsonRe.put("floor_space", a25);
				json.put("residential", jsonRe);
				
				Log.d("LDK", json.toString(1));
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};
}

//입력 로그
/*04-30 22:12:12.680: D/LDK(3827): { "socioeconomic": {
04-30 22:12:12.680: D/LDK(3827):   "sex": 2,
04-30 22:12:12.680: D/LDK(3827):   "age": "22",
04-30 22:12:12.680: D/LDK(3827):   "income": "12345",
04-30 22:12:12.680: D/LDK(3827):   "address": "경주",
04-30 22:12:12.680: D/LDK(3827):   "drive_license": 2
04-30 22:12:12.680: D/LDK(3827):  },
04-30 22:12:12.680: D/LDK(3827):  "residential": {
04-30 22:12:12.680: D/LDK(3827):   "public_transport": 1,
04-30 22:12:12.680: D/LDK(3827):   "commercial_facility": 2,
04-30 22:12:12.680: D/LDK(3827):   "leisure_facility": 3,
04-30 22:12:12.680: D/LDK(3827):   "green_space": 4,
04-30 22:12:12.680: D/LDK(3827):   "floor_space": 5
04-30 22:12:12.680: D/LDK(3827):  }
04-30 22:12:12.680: D/LDK(3827): }*/

