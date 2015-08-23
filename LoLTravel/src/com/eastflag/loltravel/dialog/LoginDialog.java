package com.eastflag.loltravel.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.eastflag.loltravel.R;
import com.eastflag.loltravel.dto.MyInfoVO;
import com.eastflag.loltravel.listener.LoginListener;
import com.eastflag.loltravel.utils.PreferenceUtil;
import com.eastflag.loltravel.utils.Utils;

public class LoginDialog extends Dialog {
	private LoginListener mListener;
	private Button bt_login;
	private EditText et_email, et_name;
	private Context mContext;
	
	public LoginDialog(Context context, LoginListener listener) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar);
		mContext = context;
		mListener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
		lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		lpWindow.dimAmount = 0.7f;
		getWindow().setAttributes(lpWindow);
		
		setContentView(R.layout.login_dialog);
		
		et_email = (EditText)findViewById(R.id.et_email);
		et_name = (EditText)findViewById(R.id.et_name);
		bt_login = (Button)findViewById(R.id.bt_login);
		
		bt_login.setOnClickListener(mClick);
	}
	
	@Override
	public void onBackPressed() {
		this.dismiss();
		((Activity)mContext).finish();
		return;
	}
	
	View.OnClickListener mClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			MyInfoVO myInfo = new MyInfoVO();
			myInfo.mdn = Utils.getMdn(mContext);;
			myInfo.email = et_email.getText().toString();
			myInfo.name = et_name.getText().toString();
			
			if(TextUtils.isEmpty(myInfo.email) || TextUtils.isEmpty(myInfo.name)){
				Utils.showToast(mContext, "입력되지 않은 항목이 있습니다.");
				return;
			}
			
			if(!android.util.Patterns.EMAIL_ADDRESS.matcher(myInfo.email).matches()) {
				Utils.showToast(mContext, "올바른 이메일 형식을 입력하세요.");
				return;
			}
			
			//저장 : mdn은 등록 성공후 저장한다.
			//PreferenceUtil.instance(mContext).setMdn(myInfo.mdn);
			PreferenceUtil.instance(mContext).setEmail(myInfo.email);
			PreferenceUtil.instance(mContext).setName(myInfo.name);
			
			//로그인 처리
			mListener.onLogin(myInfo);
		}
	};

}