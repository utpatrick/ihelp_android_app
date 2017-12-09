package com.utexas.ee382v.ihelp;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
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
        TextView idView;
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
            holder.idView = (TextView) view.findViewById(R.id.task_card_id);
            holder.contentView = (TextView) view.findViewById(R.id.task_card_content);
            holder.imageView = (ImageView) view.findViewById(R.id.task_card_image);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        TaskCard item = items.get(position);
        holder.titleView.setText(item.getTitle());
        holder.idView.setText(item.getTaskID());
        Log.d("task_id_when_creating", item.getTaskID());
        holder.contentView.setText(item.getContent());

        String category = item.getCategory();
        TextView categoryView = view.findViewById(R.id.task_card_category);
        View container = view.findViewById(R.id.task_card_layout);
        View innerContainer = view.findViewById(R.id.card_view);
        if (category != null) {
            categoryView.setText(category);
            switch (category) {
                case "Food":
                    container.setBackgroundColor(mContext.getResources().getColor(R.color.food_card_color));
                    innerContainer.setBackgroundColor(mContext.getResources().getColor(R.color.food_card_color));
                    categoryView.setTextColor(mContext.getResources().getColor(R.color.food_card_category_color));
                    break;
                case "Drink":
                    container.setBackgroundColor(mContext.getResources().getColor(R.color.drink_card_color));
                    innerContainer.setBackgroundColor(mContext.getResources().getColor(R.color.drink_card_color));
                    categoryView.setTextColor(mContext.getResources().getColor(R.color.drink_card_category_color));
                    break;
                case "Ride":
                    container.setBackgroundColor(mContext.getResources().getColor(R.color.ride_card_color));
                    innerContainer.setBackgroundColor(mContext.getResources().getColor(R.color.ride_card_color));
                    categoryView.setTextColor(mContext.getResources().getColor(R.color.ride_card_category_color));
                    break;
                default:
                    container.setBackgroundColor(mContext.getResources().getColor(R.color.other_card_color));
                    innerContainer.setBackgroundColor(mContext.getResources().getColor(R.color.other_card_color));
                    categoryView.setTextColor(mContext.getResources().getColor(R.color.other_card_category_color));
                    break;
            }
        }

        if (item.getTitle() != null) holder.titleView.setTag(item.getTitle());
        holder.imageView.setTag(item.getOwnerEmail());
        String link = MainActivity.getEndpoint() + "/android/profile_image?user_email=" + item.getOwnerEmail();
        Log.d("image_link", link);
        if (link == null || link.length() < 1 || link.startsWith("/static/images/")) {
            Picasso.with(mContext).load(R.drawable.active_dots).into(holder.imageView);
        } else {
            Log.d("image_plotted", "successful!");
            Picasso.with(mContext).load(link).fit().into(holder.imageView);
        }
        return view;
    }
}
