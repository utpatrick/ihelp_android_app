package com.utexas.ee382v.ihelp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
 * Created by kyle on 11/17/17.
 */

public class INeedHelp extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ineed_help, container, false);
        String url = MainActivity.getEndpoint()+ "/android/i_need_help";
        getAllTasks(url, view);
        setUpCheckBox(view);
        return view;
    }
    private void getAllTasks(final String url, final View parent) {
        JsonArrayRequest jsonRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                ArrayList<TaskCard> items = new ArrayList<>();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        items.add(new TaskCard(obj.getString("task_title"),
                                obj.getString("task_detail"), obj.getString("task_owner")));
                    }
                    TaskListAdapter adapter = new TaskListAdapter(getActivity(), R.layout.task_card,items);
                    ListView lv = parent.findViewById(R.id.ineed_help_listview);
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
        final CheckBox foodBox = view.findViewById(R.id.ineed_help_cb1);
        final CheckBox drinkBox = view.findViewById(R.id.ineed_help_cb2);
        final CheckBox rideBox = view.findViewById(R.id.ineed_help_cb3);
        final CheckBox otherBox = view.findViewById(R.id.ineed_help_cb4);

        foodBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox box = (CheckBox) v;
                String url = MainActivity.getEndpoint()+ "/android/i_need_help";
                if (box.isChecked()) {
                    drinkBox.setChecked(false);
                    rideBox.setChecked(false);
                    otherBox.setChecked(false);
                    url += "?category=Food";
                }
                getAllTasks(url, view);
            }
        });

        drinkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox box = (CheckBox) v;
                String url = MainActivity.getEndpoint()+ "/android/i_need_help";
                if (box.isChecked()) {
                    foodBox.setChecked(false);
                    rideBox.setChecked(false);
                    otherBox.setChecked(false);
                    url += "?category=Drink";
                }
                getAllTasks(url, view);
            }
        });

        rideBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox box = (CheckBox) v;
                String url = MainActivity.getEndpoint()+ "/android/i_need_help";
                if (box.isChecked()) {
                    drinkBox.setChecked(false);
                    foodBox.setChecked(false);
                    otherBox.setChecked(false);
                    url += "?category=Ride";
                }
                getAllTasks(url, view);
            }
        });

        otherBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox box = (CheckBox) v;
                String url = MainActivity.getEndpoint()+ "/android/i_need_help";
                if (box.isChecked()) {
                    drinkBox.setChecked(false);
                    rideBox.setChecked(false);
                    foodBox.setChecked(false);
                    url += "?category=Other";
                }
                getAllTasks(url, view);
            }
        });
    }
}
