package com.spartanapps.ibeaconsocializer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@SuppressLint("ValidFragment")
public class StatusFlowActivity extends Activity {

	StatusListArrayAdapter myStatusListAdapter;
	ListView StatusList;
	ArrayList<StatusFlowItem> myStatusList = new ArrayList<StatusFlowItem>();

	private String MY_ID;

	Object lock = new Object();

	SharedPreferences sharedpreferences;
	private BeaconManager beaconManager;
	private NotificationManager notificationManager;

	ArrayList<String> IDSList = new ArrayList<String>();

	private static final String MyPREFERENCES = "HACKATHON_IBEACON_APP";
	private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";

	private ParseQuery<ParseObject> query;
	private FindCallback<ParseObject> findQuery = new FindCallback<ParseObject>() {
		public void done(List<ParseObject> objects, ParseException e) {
			if (e == null) {

				synchronized (lock) {
					int x;

					for (x = 0; x < objects.size(); x++) {
						StatusFlowItem myCurrentItem = new StatusFlowItem(
								objects.get(x).getString("BeaconID"), objects
										.get(x).getString("Gender"), objects
										.get(x).getString("Age"), objects
										.get(x).getString("Status"));
						
						myStatusList.add(myCurrentItem);
						myStatusListAdapter.notifyDataSetChanged();

					}
				}

			} else {
				// Toast.makeText(getApplicationContext(),
				// "Error retrievindg status list...",
				// Toast.LENGTH_SHORT).show();
			}
		}
	};

	private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId",
			ESTIMOTE_PROXIMITY_UUID, null, null);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status_flow);

		StatusList = (ListView) findViewById(R.id.lvStatus);

		// Check for WiFi or 3g and pop up network settings if not available
		// +Bluetooth

		// Boolean
		// isBluetooth=manager.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH).isAvailable();
		// Enables Bluetooth
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		mBluetoothAdapter.enable();

		// PARSE INIT
		Parse.initialize(this, "O5hlONu2LBd04JvEdl8dEAwdVgDNj2lrBvawFXQS",
				"EJTqizil9I3PcjpIuTs2jPzYLOpR2vDrFwx7EoIi");

		// BEACON/SHARED PREF INIT

		beaconManager = new BeaconManager(getApplicationContext());
		sharedpreferences = getSharedPreferences(MyPREFERENCES,
				Context.MODE_PRIVATE);

		beaconManager.setForegroundScanPeriod(TimeUnit.SECONDS.toMillis(5), 0);

		// GET YOUR SAVED ID
		if (sharedpreferences.contains("BeaconID")) {
			MY_ID = sharedpreferences.getString("BeaconID", "");

		}

		// LIST ADAPTER
		myStatusListAdapter = new StatusListArrayAdapter(this,
				R.layout.list_status_flow_item, myStatusList);
		StatusList.setAdapter(myStatusListAdapter);

		beaconManager.setRangingListener(new BeaconManager.RangingListener() {

			@Override
			public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {

				IDSList.clear();
				// LOOP THROUGH BEACONS LIST
				int x;

				for (x = 0; x < beacons.size(); x++) {

					final String CurrID = beacons.get(x).getProximityUUID()
							+ ":" + beacons.get(x).getMajor() + ":"
							+ beacons.get(x).getMinor();
					// Toast.makeText(getApplicationContext(), CurrID,
					// Toast.LENGTH_SHORT).show();
					IDSList.add(CurrID);
				}

			}

		});

		// PARSE QUERY
		getServer();

	}

	@Override
	protected void onRestart() {
		super.onRestart();

	}

	@Override
	protected void onResume() {
		super.onResume();
		getServer();
	}

	public void getServer() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(StatusFlowActivity.CONNECTIVITY_SERVICE);

		Boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.isConnectedOrConnecting();
		Boolean iswifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.isConnectedOrConnecting();
		if (!is3g && !iswifi) {
			DialogFragment alert = new NoConnectionDialog();
			alert.show(getFragmentManager(), "alert");
		} else {
			myStatusList.clear();
			for (String str : IDSList) {
				Log.e ("Main",str + "+" + MY_ID);
				
				if (!str.equals(MY_ID)) {
					ParseQuery<ParseObject> query = ParseQuery
							.getQuery("StatusFlow");
					query.whereEqualTo("BeaconID", str);
					query.findInBackground(findQuery);
				}

			}
			myStatusListAdapter.notifyDataSetChanged();
		}
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

	public void callChat(View target) {
		Intent myIntent = new Intent(StatusFlowActivity.this,
				MessagesActivity.class);

		myIntent.putExtra("PRIMARY_KEY", MY_ID);
		startActivity(myIntent);
	}

	public void menuRefresh(View target) {

		getServer();

	}

	public class NoConnectionDialog extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			builder.setMessage(R.string.alert)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								// Open Settings to enable wifi
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									startActivity(new Intent(
											Settings.ACTION_WIFI_SETTINGS));

								}
							})
					.setNegativeButton("Back",
							new DialogInterface.OnClickListener() {
								// If settings not provided exit
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									getActivity().finish();

								}
							});
			return builder.create();
		}
	}

}
