package com.spartanapps.ibeaconsocializer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.R;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ConversationsListArrayAdapter extends
		ArrayAdapter<ConversationItem> {

	private Context context;
	private List<ConversationItem> values;
	private int innerResource;

	public ConversationsListArrayAdapter(Context context, int resource,
			List<ConversationItem> myvalues) {
		super(context, R.layout.conversation_item, myvalues);
		this.context = context	;
		this.values = myvalues;
		this.innerResource = R.layout.conversation_item;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View rowView;

		LayoutInflater vi;
		vi = LayoutInflater.from(getContext());
		rowView = vi.inflate(R.layout.conversation_item, null);

		LinearLayout l = (LinearLayout) rowView.findViewById(R.id.layoutParent);

		TextView tvDate = (TextView) rowView.findViewById(R.id.tvConDate);
		TextView tvLastMessage = (TextView) rowView
				.findViewById(R.id.tvConLastMessage);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
		Date resultdate = new Date(new Long(values.get(position)
				.getInnerLastMessageDate()));

		if (values.get(position).getInnerUser1()
				.equals(values.get(position).getInnerUser2()))
			l.setBackgroundColor(context.getResources().getColor(R.color.telos));

		tvDate.setText(sdf.format(resultdate));
		tvLastMessage.setText(values.get(position).getInnerMessage());

		return rowView;
	}
}
