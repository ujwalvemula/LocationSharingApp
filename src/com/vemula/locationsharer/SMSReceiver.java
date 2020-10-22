package com.vemula.locationsharer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {
	String pno;
	String latitude, longitude, time;

	@Override
	public void onReceive(Context context, Intent intent) {
		// ---get the SMS message passed in---
		Bundle bundle = intent.getExtras();
		SmsMessage[] msgs = null;
		String str = "";

		if (bundle != null) {

			Object[] pdus = (Object[]) bundle.get("pdus");
			msgs = new SmsMessage[pdus.length];
			for (int i = 0; i < msgs.length; i++) {
				msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				pno = msgs[i].getOriginatingAddress();
				str = msgs[i].getMessageBody().toString();

			}

			// Toast.makeText(context, str, Toast.LENGTH_SHORT).show();

			if (str.substring(0, 7) == "LATITUDE") {
				latitude = str.substring(8, 17);
				longitude = str.substring(19, 28);
				time = str.substring(30);
			}

			/*
			 * Intent broadcastIntent=new Intent();
			 * broadcastIntent.setAction("SMS_RECEIVED_ACTION");
			 * broadcastIntent.putExtra("sms",str);
			 * context.sendBroadcast(broadcastIntent);
			 */
		}

	}

}
