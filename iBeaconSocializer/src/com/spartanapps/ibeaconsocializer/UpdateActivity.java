package com.spartanapps.ibeaconsocializer;

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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class UpdateActivity extends Activity {

	private int MY_MAJOR;
	private int MY_MINOR;
	private String MY_UUID;
	private String MY_ID;
	// 513:30919

	private static final String MyPREFERENCES = "HACKATHON_IBEACON_APP";
	private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
	private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId",
			ESTIMOTE_PROXIMITY_UUID, null, null);

	// VIEWS VARS
	EditText etAge;
	Spinner etGender;
	EditText etStatus;

	private FindCallback<ParseObject> findQuery = new FindCallback<ParseObject>() {
		public void done(List<ParseObject> objects, ParseException e) {
			if (e == null) {

				for (int x = 0; x < objects.size(); x++) {
					objects.get(x).deleteInBackground();
				}

			}
		}
	};

	// Functions
	public void AddToStatusFlow(String Status, String Age, String Gender,
			String MYID) {
		// DOESNT EXIST
		ParseObject beaconObject = new ParseObject("StatusFlow");
		beaconObject.put("BeaconID", MYID);
		beaconObject.put("Age", Age);
		beaconObject.put("Gender", Gender);
		beaconObject.put("Status", Status);

		beaconObject.saveInBackground();
	}

	SharedPreferences sharedpreferences;
	private BeaconManager beaconManager;
	private NotificationManager notificationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);

		// VIEWS VARS
		etAge = (EditText) findViewById(R.id.etAge);
		etGender = (Spinner) findViewById(R.id.etGender);
		etStatus = (EditText) findViewById(R.id.etStatus);

		// PARSE INIT
		Parse.initialize(this, "O5hlONu2LBd04JvEdl8dEAwdVgDNj2lrBvawFXQS",
				"EJTqizil9I3PcjpIuTs2jPzYLOpR2vDrFwx7EoIi");

		// INITIALIZATION OF VARS
		beaconManager = new BeaconManager(getApplicationContext());
		notificationManager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
		sharedpreferences = getSharedPreferences(MyPREFERENCES,
				Context.MODE_PRIVATE);

		// GET YOUR SAVED ID
		if (sharedpreferences.contains("BeaconID")) {
			MY_ID = sharedpreferences.getString("BeaconID", "");

		}

		// ---by default you scan 5s and then wait 25s
		// for this demo, you will scan more
		// frequently---
		beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 0);

		// INITIALIZE LISTENER
		beaconManager.setRangingListener(new BeaconManager.RangingListener() {

			@Override
			public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {

				// LOOP THROUGH BEACONS LIST
				int x;
				for (x = 0; x < beacons.size(); x++) {

					// Toast.makeText(getApplicationContext(),
					// Double.toString(Utils.computeAccuracy(beacons.get(0))),
					// Toast.LENGTH_SHORT).show();

					// Toast.makeText(
					// getApplicationContext(),
					// beacons.get(x).getMajor() + ":"
					// + beacons.get(x).getMinor(),
					// Toast.LENGTH_SHORT).show();

					// Toast.makeText(getApplicationContext(),
					// "FOUND MY BEACON",
					// Toast.LENGTH_SHORT).show();

					if (Utils.computeAccuracy(beacons.get(x)) < 0.2) {
						Toast.makeText(getApplicationContext(),
								"FOUND MY BEACON", Toast.LENGTH_SHORT).show();
						MY_MAJOR = beacons.get(x).getMajor();
						MY_MINOR = beacons.get(x).getMinor();
						MY_UUID = beacons.get(x).getProximityUUID();

						try {
							beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
						} catch (RemoteException e) {

						}

					}

				}
				if (MY_UUID != null)
					if (x == beacons.size()) {

						MY_ID = MY_UUID + ":" + MY_MAJOR + ":" + MY_MINOR;
						Editor editor = sharedpreferences.edit();
						editor.putString("BeaconID", MY_ID);
						editor.commit();

						Toast.makeText(getApplicationContext(), MY_ID,
								Toast.LENGTH_SHORT).show();

					}

			}
		});

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

	public void btnConfigureClicked(View target) {

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

	public void btnUpdateClicked(View target) {
		boolean b = false;
		try {
			Integer age = Integer.parseInt(etAge.getText().toString());
		} catch (NumberFormatException e) {
			b = true;
		}
		if (b == true || etStatus.getText().equals("")) {
			Toast.makeText(getApplicationContext(), "Wrong input given",
					Toast.LENGTH_SHORT).show();
			return;
		}

		if (MY_ID == null || MY_ID.equals("")) {
			Toast.makeText(getApplicationContext(),
					"Unable to use service - Please try to re-configure",
					Toast.LENGTH_SHORT).show();
			return;
		}

		ParseQuery<ParseObject> query = ParseQuery.getQuery("StatusFlow");
		query.whereEqualTo("BeaconID", MY_ID);
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					if (objects.size() > 0) {
						// EXISTS
						objects.get(0).deleteInBackground();

						AddToStatusFlow(etStatus.getText().toString(), etAge
								.getText().toString(), etGender
								.getSelectedItem().toString(), MY_ID);

						ParseQuery<ParseObject> query = ParseQuery
								.getQuery("Chat");
						query.whereEqualTo("User1", MY_ID);
						query.findInBackground(findQuery);

					} else {
						// DOESNT EXIST
						AddToStatusFlow(etStatus.getText().toString(), etAge
								.getText().toString(), etGender
								.getSelectedItem().toString(), MY_ID);
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"Error querying database..", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		// QUERY

		finish();

	}

	public void terminate(View target) {
		finish();
	}

	public void menuUpdateClicked(View target) {

		if (MY_ID != null) {
			Intent myIntent = new Intent(UpdateActivity.this,
					MessagesActivity.class);

			myIntent.putExtra("PRIMARY_KEY", MY_ID);

			startActivity(myIntent);
			finish();
		} else {
			Toast.makeText(getApplicationContext(),
					"You have to configure your beacon first!",
					Toast.LENGTH_SHORT).show();
		}

	}

}
