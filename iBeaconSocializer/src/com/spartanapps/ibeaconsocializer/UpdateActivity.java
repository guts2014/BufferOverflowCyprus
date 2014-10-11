package com.spartanapps.ibeaconsocializer;

import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.parse.Parse;
import com.parse.ParseObject;

public class UpdateActivity extends Activity {

	private int MY_MAJOR;
	private int MY_MINOR;
	private String MY_UUID;
	private String MY_ID;
	//513:30919
	
	private static final String MyPREFERENCES = "HACKATHON_IBEACON_APP";
	private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
	private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", ESTIMOTE_PROXIMITY_UUID, null, null);

	//VIEWS VARS
	EditText etAge;
	EditText etGender;
	EditText etStatus;
	
	SharedPreferences sharedpreferences;	
	private BeaconManager beaconManager;
	private NotificationManager notificationManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);
		
		//VIEWS VARS
		etAge = (EditText) findViewById(R.id.etAge);
		etGender = (EditText) findViewById(R.id.etGender);
		etStatus = (EditText) findViewById(R.id.etStatus);
		
		//PARSE INIT
		Parse.initialize(this, "O5hlONu2LBd04JvEdl8dEAwdVgDNj2lrBvawFXQS", "EJTqizil9I3PcjpIuTs2jPzYLOpR2vDrFwx7EoIi");
		
		//INITIALIZATION OF VARS
		beaconManager = new BeaconManager(getApplicationContext());
        notificationManager = (NotificationManager)getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);	
        
        //GET YOUR SAVED ID
        if (sharedpreferences.contains("BeaconID"))
        {
          MY_ID = sharedpreferences.getString("BeaconID", "");

        }
        
        //---by default you scan 5s and then wait 25s
        // for this demo, you will scan more
        // frequently---
        beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 0);
        
      
                
     
		
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
		    @Override public void onServiceReady() {
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

	
	public void btnConfigureClicked(View target){
		//INITIALIZE LISTENER
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
        	
            @Override public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
            	
            	
            	//LOOP THROUGH BEACONS LIST
            	int x;
            	for(x = 0;x<beacons.size();x++){
            		//Toast.makeText(getApplicationContext(), Double.toString(Utils.computeAccuracy(beacons.get(0))), Toast.LENGTH_SHORT).show();
            		
            		//Toast.makeText(getApplicationContext(), beacons.get(x).getMajor() + ":"+ beacons.get(x).getMinor(), Toast.LENGTH_SHORT).show();
            		if (Utils.computeAccuracy(beacons.get(x))< 0.5)  {
            			//Toast.makeText(getApplicationContext(), "FOUND MY BEACON", Toast.LENGTH_SHORT).show();
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
            	if (x==beacons.size()){
            		
            		MY_ID = MY_UUID + ":" +MY_MAJOR +":" + MY_MINOR;
            		Editor editor = sharedpreferences.edit();
            		editor.putString("BeaconID", MY_ID);
            		editor.commit();
            		
                    Toast.makeText(getApplicationContext(), MY_ID, Toast.LENGTH_SHORT).show();

            	}
            	
            	
            	
            }
          });
	}
	
	public void btnUpdateClicked(View target){
		
		//QUERY
		
		ParseObject beaconObject = new ParseObject("StatusFlow");
		beaconObject.put("BeaconID", MY_ID);
		beaconObject.put("Age",etAge.getText().toString());
		beaconObject.put("Gender",etGender.getText().toString());
		beaconObject.put("Status",etStatus.getText().toString());
		beaconObject.saveInBackground();
	}
}
