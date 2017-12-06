package com.utexas.ee382v.ihelp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import android.content.Context;

import java.util.ArrayList;


import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CropingOptionAdapter extends ArrayAdapter {
	private ArrayList mOptions;
	private LayoutInflater mInflater;

	public CropingOptionAdapter(Context context, ArrayList options) {
		super(context, R.layout.croping_selector, options);

		mOptions  = options;

		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup group) {
		if (convertView == null)
			convertView = mInflater.inflate(R.layout.croping_selector, null);

		CropingOption item = (CropingOption) mOptions.get(position);

		if (item != null) {
			((ImageView) convertView.findViewById(R.id.img_icon)).setImageDrawable(item.icon);
			((TextView) convertView.findViewById(R.id.txt_name)).setText(item.title);

			return convertView;
		}

		return null;
	}
}