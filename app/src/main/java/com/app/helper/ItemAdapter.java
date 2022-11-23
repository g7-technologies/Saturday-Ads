package com.app.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.wallafy.R;

public class ItemAdapter extends ArrayAdapter<String> {

	private final Context context;
	private final String[] Ids;
	private final int rowResourceId;

	public ItemAdapter(Context context, int textViewResourceId, String[] objects) {

		super(context, textViewResourceId, objects);

		this.context = context;
		this.Ids = objects;
		this.rowResourceId = textViewResourceId;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(rowResourceId, parent, false);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
		TextView textView = (TextView) rowView.findViewById(R.id.textView);
		ImageView adIcon = (ImageView) rowView.findViewById(R.id.adIcon);

		int id = Integer.parseInt(Ids[position]);
		//Log.v("id",""+id);

		//String name = context.getResources().getString(Model.GetbyId(id).Name).toString();
		String name = Model.GetbyId(id).name;
		textView.setText(name);
		// get input stream
		imageView.setImageResource(Model.GetbyId(id).IconFile);
		if (name.equals(context.getResources().getString(R.string.my_promotions))){
			adIcon.setVisibility(View.VISIBLE);
		} else {
			adIcon.setVisibility(View.GONE);
		}
		return rowView;

	}

}