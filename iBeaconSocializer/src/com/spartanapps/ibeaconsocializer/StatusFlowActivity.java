package com.spartanapps.ibeaconsocializer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class StatusFlowActivity extends Activity {

	StatusListArrayAdapter myStatusListAdapter;
	ListView StatusList;
	ArrayList<StatusFlowItem> myStatusList = new ArrayList<StatusFlowItem>();

	private String MY_ID;

	SharedPreferences sharedpreferences;
	private BeaconManager beaconManager;
	private NotificationManager notificationManager;

	private static final String MyPREFERENCES = "HACKATHON_IBEACON_APP";
	private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
	private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId",
			ESTIMOTE_PROXIMITY_UUID, null, null);
	private ParseQuery<ParseObject> query;
	private FindCallback<ParseObject> findQuery = new FindCallback<ParseObject>() {
		public void done(List<ParseObject> objects, ParseException e) {
			if (e == null) {
				int x;
				myStatusList.clear();
				myStatusListAdapter.notifyDataSetChanged();

				for (x = 0; x < objects.size(); x++) {
					StatusFlowItem myCurrentItem = new StatusFlowItem(objects
							.get(x).getString("BeaconID"), objects.get(x)
							.getString("Gender"), objects.get(x).getString(
							"Age"), objects.get(x).getString("Status"));
					myStatusList.add(myCurrentItem);
					myStatusListAdapter.notifyDataSetChanged();
				}

			} else {
				// Toast.makeText(getApplicationContext(),
				// "Error retrievindg status list...",
				// Toast.LENGTH_SHORT).show();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status_flow);

		StatusList = (ListView) findViewById(R.id.lvStatus);

		// PARSE INIT
		Parse.initialize(this, "O5hlONu2LBd04JvEdl8dEAwdVgDNj2lrBvawFXQS",
				"EJTqizil9I3PcjpIuTs2jPzYLOpR2vDrFwx7EoIi");

		// BEACON/SHARED PREF INIT
		beaconManager = new BeaconManager(getApplicationContext());
		sharedpreferences = getSharedPreferences(MyPREFERENCES,
				Context.MODE_PRIVATE);

		// GET YOUR SAVED ID
		if (sharedpreferences.contains("BeaconID")) {
			MY_ID = sharedpreferences.getString("BeaconID", "");

		}

		// LIST ADAPTER
		myStatusListAdapter = new StatusListArrayAdapter(
				getApplicationContext(), R.layout.list_status_flow_item,
				myStatusList);
		StatusList.setAdapter(myStatusListAdapter);

		beaconManager.setRangingListener(new BeaconManager.RangingListener() {

			@Override
			public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {

				// LOOP THROUGH BEACONS LIST
				int x;

				for (x = 0; x < beacons.size(); x++) {

					final String CurrID = beacons.get(x).getProximityUUID()
							+ ":" + beacons.get(x).getMajor() + ":"
							+ beacons.get(x).getMinor();
					// Toast.makeText(getApplicationContext(), CurrID,
					// Toast.LENGTH_SHORT).show();

				}

			}

		});

		// PARSE QUERY
		query = ParseQuery.getQuery("StatusFlow");
		// query.whereEqualTo("BeaconID", CurrID);
		query.findInBackground(findQuery);

		// START LIST CREATION

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		myStatusList.clear();
		myStatusListAdapter.notifyDataSetChanged();
		query.findInBackground(findQuery);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		beaconManager.disconnect();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
			@Override
			public void onServiceReady() {
				try {
					beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
				} catch (RemoteException e) {

				}
			}
		});
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		try {
			beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
		} catch (RemoteException e) {

		}

	}

	public void menuUpdateClicked(View target) {
		Intent myIntent = new Intent(StatusFlowActivity.this,
				UpdateActivity.class);
		startActivity(myIntent);
	}

	public void menuRefresh(View target) {
		myStatusList.clear();
		myStatusListAdapter.notifyDataSetChanged();
		query.findInBackground(findQuery);
	}

}
