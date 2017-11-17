package com.utexas.ee382v.ihelp;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kyle on 11/16/17.
 */

public class TaskListAdapter extends ArrayAdapter<TaskCard>{

    static class ViewHolder {
        TextView titleView;
        TextView contentView;
        ImageView imageView;
    }

    private Context mContext;
    private ArrayList<TaskCard> items = new ArrayList<>();
    private int resource;

    public TaskListAdapter(@NonNull Context context, @LayoutRes int resource, ArrayList<TaskCard> items) {
        super(context, resource, items);
        this.mContext = context;
        this.resource = resource;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if (view == null) {
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            view = inflater.inflate(resource,parent,false);
            holder = new ViewHolder();
            holder.titleView = (TextView) view.findViewById(R.id.task_card_title);
            holder.contentView = (TextView) view.findViewById(R.id.task_card_content);
            holder.imageView = (ImageView) view.findViewById(R.id.task_card_image);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        TaskCard item = items.get(position);
        holder.titleView.setText(item.getTitle());
        holder.contentView.setText(item.getContent());
        if (item.getTitle() != null) holder.titleView.setTag(item.getTitle());
        holder.imageView.setTag(item.getIconUrl());
        String link = item.getIconUrl();
        if (link == null || link.length() < 1 || link.startsWith("/static/images/")) {
            Picasso.with(mContext).load(R.drawable.default_image).into(holder.imageView);
        } else {
            Picasso.with(mContext).load(item.getIconUrl()).into(holder.imageView);
        }
        return view;
    }
}
