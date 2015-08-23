package com.eastflag.loltravel.utils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.eastflag.loltravel.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.telephony.TelephonyManager;
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
	
	public static String getDateFromMongoDate(String mongo_date) {
		//mongo_date = "2015-06-27T06:48:54.578Z";
		try{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		    
		    Date mongoDate = dateFormat.parse(mongo_date.replace("T", " ").replace("Z", ""));
			long current_date = mongoDate.getTime() + 1000*60*60*9; //+9시간
			
		    Date date = new Date(current_date);
		    
		    return dateFormat.format(date).toString();
		}catch(ParseException e){//this generic but you can control another types of exception
			 e.printStackTrace();
			 return "";
		}
	}
	
	public static String getAddress(Context context, double lat, double lng) {
		List<Address> addresses = null;
		try {
			Geocoder geocoder = new Geocoder(context, Locale.getDefault());
			addresses = geocoder.getFromLocation(lat, lng, 1);
		} catch (IOException ioException) {

		} catch (IllegalArgumentException illegalArgumentException) {

		} catch (NullPointerException e) {

		}

		if(addresses != null && addresses.size() > 0) {
			String address = addresses.get(0).getAddressLine(0);

			return address;
		} else {
			return "";
		}
	}
	
/*	public static String getMdn(Context context) {
		TelephonyManager tMgr =(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		//2015-06-29 전화번호가 +82로 시작하는 경우 보정
		String number = tMgr.getLine1Number();
		if(number.startsWith("+82")) {
			number = number.replace("+82", "0");
		}
	    return number;
	}*/
}
