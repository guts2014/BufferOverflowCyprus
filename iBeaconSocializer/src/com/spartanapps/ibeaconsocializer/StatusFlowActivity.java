package com.spartanapps.ibeaconsocializer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class StatusFlowActivity extends Activity {

	StatusListArrayAdapter myStatusListAdapter;
	ListView StatusList;
	ArrayList<StatusFlowItem> myStatusList = new ArrayList<StatusFlowItem>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status_flow);
		
		StatusList = (ListView) findViewById(R.id.lvStatus);
		
		//PARSE INIT
		Parse.initialize(this, "O5hlONu2LBd04JvEdl8dEAwdVgDNj2lrBvawFXQS", "EJTqizil9I3PcjpIuTs2jPzYLOpR2vDrFwx7EoIi");

//		StatusFlowItem myCurrentItem = new StatusFlowItem("ID","M",20,"Status");
		
		myStatusListAdapter = new StatusListArrayAdapter(getApplicationContext(),R.layout.list_status_flow_item,myStatusList);
		StatusList.setAdapter(myStatusListAdapter);
		
		//PARSE QUERY
		ParseQuery<ParseObject> query = ParseQuery.getQuery("StatusFlow");
		 query.findInBackground(new FindCallback<ParseObject>() {
		     public void done(List<ParseObject> objects, ParseException e) {
		         if (e == null) {
		        	 int x;
		        	 for ( x =0;x<objects.size();x++){
		        		 StatusFlowItem myCurrentItem = new StatusFlowItem(objects.get(x).getString("BeaconID"),objects.get(x).getString("Gender"),objects.get(x).getInt("Age"),objects.get(x).getString("Status"));
		        		 myStatusList.add(myCurrentItem);
		        	 }
		        	 
		        	 myStatusListAdapter.notifyDataSetChanged();
		        	 
		         } else {
		             Toast.makeText(getApplicationContext(), "Error retrievindg status list...", Toast.LENGTH_SHORT).show();
		         }
		     }
		 });
		 
		 
	}

	public void menuUpdateClicked(View target){
		Intent myIntent = new Intent(StatusFlowActivity.this,UpdateActivity.class);
		startActivity(myIntent);
		
		
	}
}
