package com.vemula.locationsharer;


import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	TextView lat, longi;
	Button refresh, send, ib, mb;
	GPSTracker gps;
	EditText pno;
	ImageButton contact;
	double latitude;
	double longitude;
	protected ContextWrapper context;
	String message;
	private static final int CONTACT_PICKER_RESULT = 1001; 
	String phone1;
	String phone = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);
		gps = new GPSTracker(this);
		pno = (EditText) findViewById(R.id.editText1);
		send = (Button) findViewById(R.id.send);
		ib = (Button) findViewById(R.id.inbox);
		contact = (ImageButton) findViewById(R.id.imageButton1);

		send.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				phone = pno.getText().toString();

				if (gps.canGetLocation()) {

					latitude = gps.getLatitude();
					longitude = gps.getLongitude();
					String time = removeChar(java.text.DateFormat.getDateTimeInstance()
							.format(Calendar.getInstance().getTime()),',');

					message = "LATITUDE," + String.valueOf(latitude) + ","
							+ String.valueOf(longitude) + "," + time;

					if (phone.length() > 0) {
						DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case DialogInterface.BUTTON_POSITIVE:
									sendSMS(phone, message);
									break;

								case DialogInterface.BUTTON_NEGATIVE:
									dialog.dismiss();
									break;
								}
							}
						};

						AlertDialog.Builder builder = new AlertDialog.Builder(
								MainActivity.this);

						builder.setMessage("Send location to " + phone + "?")
								.setPositiveButton("Yes", dialogClickListener)
								.setNegativeButton("No", dialogClickListener)
								.show();

					} else
						Toast.makeText(getApplicationContext(),
								"Enter Phone Number", Toast.LENGTH_LONG).show();

					// }
				} else {
					gps.showSettingsAlert();
				}

			}
		});

		ib.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getApplicationContext(), Inbox.class);
				Log.d("tag", "after starting intent");
				startActivity(intent);
				Log.d("tag", "after starting intent");

			}
		});

		contact.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
						Contacts.CONTENT_URI);
				startActivityForResult(contactPickerIntent,
						CONTACT_PICKER_RESULT);

			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CONTACT_PICKER_RESULT:
				Cursor cursor = null;
				try {
					Uri result = data.getData();
					String id = result.getLastPathSegment();
					cursor = getContentResolver().query(Phone.CONTENT_URI,
							null, Phone.CONTACT_ID + "=?", new String[] { id },
							null);
					int phoneId = cursor.getColumnIndex(Phone.DATA);
					if (cursor.moveToFirst()) {
						phone1 = cursor.getString(phoneId);
						Log.d("tag", "phone no.");
					} else {
						Log.d("tag", "else");
					}
				} catch (Exception e) {
					Log.d("tag", "Failed to get email data", e);
				} finally {
					if (cursor != null) {
						cursor.close();
					}
					pno.setText(phone1);
				}

				break;
			}

		} else {
			Log.d("tag", "error");
		}
	}

	void sendSMS(String Phno, String msg) {
		String SENT = "SMS_SENT";
		String DELIVERED = "SMS_DELIVERED";

		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
				SENT), 0);

		PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
				new Intent(DELIVERED), 0);

		// ---when the SMS has been sent---
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "SMS sent",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(getBaseContext(), "Generic failure",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(getBaseContext(), "No service",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(getBaseContext(), "Null PDU",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(getBaseContext(), "Radio off",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(SENT));

		// ---when the SMS has been delivered---
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "SMS delivered",
							Toast.LENGTH_SHORT).show();
					break;
				case Activity.RESULT_CANCELED:
					Toast.makeText(getBaseContext(), "SMS not delivered",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(DELIVERED));

		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(Phno, null, msg, sentPI, deliveredPI);
	}

	String removeChar(String s, char c) {
        StringBuffer buf = new StringBuffer(s.length());
        buf.setLength(s.length());
        int current = 0;
        for (int i=0; i<s.length(); i++){
            char cur = s.charAt(i);
            if(cur != c) buf.setCharAt(current++, cur);
        }
        return buf.toString();
    }
}
