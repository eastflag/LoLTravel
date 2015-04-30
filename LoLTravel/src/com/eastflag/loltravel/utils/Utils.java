package com.eastflag.loltravel.utils;

import com.eastflag.loltravel.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class Utils {

    private static Toast m_toast = null;
    
	public static void showToast(Context context, String text) {
		if (m_toast == null) {
			m_toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		}
		m_toast.setText(text);
		m_toast.setDuration(Toast.LENGTH_SHORT);
		m_toast.show();
	}
	
	public static void showAlertDialog(Context context, String strErrorMsg){
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    dialog.dismiss();
		    }
		});
		alert.setMessage(strErrorMsg);
		alert.show();
	}
}
