package com.spartanapps.ibeaconsocializer;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class StatusListArrayAdapter extends ArrayAdapter<StatusFlowItem> {

	private StatusFlowActivity context;
	private List<StatusFlowItem> values;
	private int innerResource;

	public StatusListArrayAdapter(StatusFlowActivity context, int resource,
			List<StatusFlowItem> myvalues) {
		super(context, R.layout.list_status_flow_item, myvalues);
		this.context = context;
		this.values = myvalues;
		this.innerResource = resource;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View rowView;

		LayoutInflater vi;
		vi = LayoutInflater.from(getContext());
		rowView = vi.inflate(R.layout.list_status_flow_item, null);

		rowView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// Toast.makeText(context, values.get(position).getStatus(),
				// Toast.LENGTH_SHORT).show();

				Intent myIntent = new Intent(context, MessagesActivity.class);

				myIntent.putExtra("PRIMARY_KEY", values.get(position).getID());
				context.startActivity(myIntent);

			}

		});

		TextView tvStatus = (TextView) rowView.findViewById(R.id.tvFlowStatus);
		TextView tvGender = (TextView) rowView.findViewById(R.id.tvFlowGender);
		TextView tvAge = (TextView) rowView.findViewById(R.id.tvFlowAge);
		ImageView ivLogo = (ImageView) rowView.findViewById(R.id.ivFlowLogo);

		tvStatus.setText(values.get(position).getStatus());
		tvGender.setText(values.get(position).getGender());
		tvAge.setText(values.get(position).getAge());

		return rowView;
	}
}