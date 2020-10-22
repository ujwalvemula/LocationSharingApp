package com.vemula.locationsharer;

import java.util.StringTokenizer;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Inbox extends ListActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		int i = 0;
		String[] phone = new String[100];
		String[] time = new String[100];
		String[] lat = new String[100];
		String[] longi = new String[100];

		super.onCreate(savedInstanceState);

		Cursor cursor = getContentResolver().query(
				Uri.parse("content://sms/inbox"), null,
				//"date >=1406194853062 "
				null, null, null);

		cursor.moveToFirst();

		do {

			if (cursor.getString(12).startsWith("LATITUDE,")
					&& cursor.getString(12).length() > 9) {

				phone[i] = cursor.getString(2);
				StringTokenizer st = new StringTokenizer(cursor.getString(12),
						",");
				st.nextToken();
				lat[i] = st.nextToken();
				longi[i] = st.nextToken();
				String ti=st.nextToken();
				while(st.hasMoreTokens())
					ti=ti+st.nextToken();
				time[i] =ti;//st.nextToken(); //+ "," + st.nextToken();			//cursor.getString(5); 
				i++;

			}

		} while (cursor.moveToNext());

		String phone2[] = new String[i];
		final String lat2[] = new String[i];
		final String longi2[] = new String[i];

		for (int j = 0; j < i; j++) {
			phone2[j] = phone[j] + "\n" + time[j];
			lat2[j] = lat[j];
			longi2[j] = longi[j];
		}

		this.setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item,
				R.id.textView1, phone2));

		ListView lv = getListView();

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent map = new Intent(getApplicationContext(),
						MapActivity.class);
				map.putExtra("latitude", lat2[position]);
				map.putExtra("longitude", longi2[position]);
				startActivity(map);

			}
		});

	}

	String contacts_read(String Pno) {
		String name = "";
		Uri uri = Uri.withAppendedPath(
				ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(Pno));
		ContentResolver resolver = getContentResolver();
		Cursor c1 = resolver.query(uri,
				new String[] { PhoneLookup.DISPLAY_NAME }, null, null, null);
		Log.d("ccc", c1.getColumnName(0));
		String ss = c1.getColumnName(0);
		if (c1 != null) {
			name = c1.getString(c1.getColumnIndex(ss) + 1);
		}

		if (name != null)
			return name;
		else
			return Pno;
	}

}
