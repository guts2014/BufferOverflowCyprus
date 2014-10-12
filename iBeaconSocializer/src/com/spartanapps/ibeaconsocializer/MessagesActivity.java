package com.spartanapps.ibeaconsocializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.Time;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class MessagesActivity extends Activity {

	private String MY_ID;
	// 513:30919

	private String primaryKey;

	private static final String MyPREFERENCES = "HACKATHON_IBEACON_APP";

	// private EditText input;

	FindCallback<ParseObject> skata = new FindCallback<ParseObject>() {
		public void done(List<ParseObject> objects, ParseException e) {
			if (e == null) {
				int x;
				myConversationsList.clear();
				for (x = 0; x < objects.size(); x++) {

					ConversationItem myCurrentItem = new ConversationItem(
							objects.get(x).getString("User1"), objects.get(x)
									.getString("User2"), objects.get(x)
									.getString("Stauts"), objects.get(x)
									.getString("Message"), objects.get(x)
									.getLong("LastMessageDate"));

					myConversationsList.add(myCurrentItem);

				}

				Collections.sort(myConversationsList,
						ConversationItem.SORT_TEAMS);

				myConversationsListAdapter.notifyDataSetChanged();

			} else {
				Toast.makeText(getApplicationContext(),
						"Error retrievindg status list...", Toast.LENGTH_SHORT)
						.show();
			}
		}
	};

	ListView lvConversations;
	List<ConversationItem> myConversationsList;
	ConversationsListArrayAdapter myConversationsListAdapter;
	SharedPreferences sharedpreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messages);

		Intent i = getIntent();
		primaryKey = (String) i.getExtras().getString("PRIMARY_KEY");

		// VIEWS VARS
		lvConversations = (ListView) findViewById(R.id.lvConversations);

		Button b = (Button) findViewById(R.id.buttonSend);

		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder alert = new AlertDialog.Builder(
						MessagesActivity.this);

				alert.setTitle("Give a Beacon Response");
				alert.setMessage("Say what you wanna say");

				// Set an EditText view to get user input
				final EditText input = new EditText(MessagesActivity.this);
				alert.setView(input);

				alert.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								String value = input.getText().toString();

								ParseObject beaconObject = new ParseObject(
										"Chat");
								beaconObject.put("User1", primaryKey);
								beaconObject.put("User2", MY_ID);
								beaconObject.put("Status", MY_ID); // NA TO
																	// PKIANOUME
																	// POU TO
																	// EXTRAS
								beaconObject.put("Message", value);
								beaconObject.put("LastMessageDate",
										System.currentTimeMillis());

								beaconObject.saveInBackground();
							}
						});

				alert.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// Canceled.
							}
						});

				alert.show();
			}
		});

		myConversationsList = new ArrayList<ConversationItem>();
		sharedpreferences = getSharedPreferences(MyPREFERENCES,
				Context.MODE_PRIVATE);

		myConversationsListAdapter = new ConversationsListArrayAdapter(
				getApplicationContext(), R.layout.conversation_item,
				myConversationsList);
		lvConversations.setAdapter(myConversationsListAdapter);

		if (sharedpreferences.contains("BeaconID")) {
			MY_ID = sharedpreferences.getString("BeaconID", "");
		}

		if (MY_ID == null) {
			Toast.makeText(getApplicationContext(),
					"You have to configure your Beacon Settings",
					Toast.LENGTH_SHORT).show();
			Intent myIntent = new Intent(MessagesActivity.this,
					UpdateActivity.class);
			startActivity(myIntent);
			this.finish();
		} else {

			lvConversations.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub

					// Intent myIntent = new
					// Intent(MessagesActivity.this,ChatActivity.class);
					//
					// startActivity(myIntent);
				}

			});

			// WE HAVE THE ID;
			// PARSE QUERY USER1
			elagiemou();
		}

	}

	public void elagiemou() {
		// Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_SHORT)
		// .show();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Chat");
		query.whereEqualTo("User1", primaryKey);
		query.findInBackground(skata);
		myConversationsListAdapter.notifyDataSetChanged();

	}

	public void menuUpdateClicked(View target) {

		Intent myIntent = new Intent(MessagesActivity.this,
				UpdateActivity.class);
		startActivity(myIntent);

		finish();

	}

	public void tiSkataGinete(View target) {

		elagiemou();
	}
	public void gohome(View target) {

		finish();
	}


}
