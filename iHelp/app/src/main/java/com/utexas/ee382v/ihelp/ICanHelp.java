package com.utexas.ee382v.ihelp;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kyle on 11/16/17.
 */

public class ICanHelp extends Fragment {

    private Location location;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ican_help, container, false);
        String url = MainActivity.getEndpoint()+ "/android/i_can_help";
        location = ViewAll.getLastLocation();
        getAllTasks(url, view);
        setUpCheckBox(view);
        return view;
    }

    private void getAllTasks(String url, final View parent) {
        double latitude = 0, longitude = 0;
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        url += "?latitude="+ latitude + "&longitude=" + longitude;
        Log.d("geolocation", url);

        JsonArrayRequest jsonRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                ArrayList<TaskCard> items = new ArrayList<>();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        TaskCard card = new TaskCard(obj.getString("task_title"),
                                obj.getString("task_detail"), obj.getString("task_owner"));
                        card.setTaskID(obj.getString("task_id"));
                        items.add(card);
                    }
                    TaskListAdapter adapter = new TaskListAdapter(getActivity(), R.layout.task_card,items);
                    ListView lv = parent.findViewById(R.id.ican_help_listview);
                    lv.setAdapter(adapter);
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getActivity(), ViewTask.class);
                            ImageView imageView = view.findViewById(R.id.task_card_image);
                            TextView textView = view.findViewById(R.id.task_card_title);
                            String message = imageView.getTag().toString() + "," + textView.getTag().toString();
                            intent.putExtra(ViewAll.MESSAGE, message);
                            startActivity(intent);
                        }
                    });
                } catch (org.json.JSONException e) {
                    e.printStackTrace();
                } finally {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        Volley.newRequestQueue(getActivity()).add(jsonRequest);
    }

    private void setUpCheckBox(final View view) {
        final CheckBox foodBox = view.findViewById(R.id.ican_help_cb1);
        final CheckBox drinkBox = view.findViewById(R.id.ican_help_cb2);
        final CheckBox rideBox = view.findViewById(R.id.ican_help_cb3);
        final CheckBox otherBox = view.findViewById(R.id.ican_help_cb4);
        double latitude = 0, longitude = 0;
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        final String url = MainActivity.getEndpoint()+ "/android/i_can_help" +
                "?latitude="+ latitude + "&longitude=" + longitude;
        foodBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox box = (CheckBox) v;
                String new_url = url;
                if (box.isChecked()) {
                    drinkBox.setChecked(false);
                    rideBox.setChecked(false);
                    otherBox.setChecked(false);
                    new_url += "&category=Food";
                }
                getAllTasks(new_url, view);
            }
        });

        drinkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox box = (CheckBox) v;
                String new_url = url;
                if (box.isChecked()) {
                    foodBox.setChecked(false);
                    rideBox.setChecked(false);
                    otherBox.setChecked(false);
                    new_url += "&category=Drink";
                }
                getAllTasks(new_url, view);
            }
        });

        rideBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox box = (CheckBox) v;
                String new_url = url;
                if (box.isChecked()) {
                    drinkBox.setChecked(false);
                    foodBox.setChecked(false);
                    otherBox.setChecked(false);
                    new_url += "&category=Ride";
                }
                getAllTasks(new_url, view);
            }
        });

        otherBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox box = (CheckBox) v;
                String new_url = url;
                if (box.isChecked()) {
                    drinkBox.setChecked(false);
                    rideBox.setChecked(false);
                    foodBox.setChecked(false);
                    new_url += "&category=Other";
                }
                getAllTasks(new_url, view);
            }
        });
    }
}
