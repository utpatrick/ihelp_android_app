package com.utexas.ee382v.ihelp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
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

    static String MESSAGE = "com.utexas.ee382v.ihelp.icanhelp";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ican_help, container, false);
        ListView listView = (ListView) view.findViewById(R.id.ican_help_listview);
        String url = MainActivity.getBaseUrl();
        getAllTasks(url, view);
//        TaskListAdapter listAdapter = new TaskListAdapter(getActivity(), R.layout.task_card, items);
//        listView.setAdapter(listAdapter);
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
                        items.add(new TaskCard(obj.getString("title"),
                                obj.getString("content"), obj.getString("iconurl")));
                    }
                    TaskListAdapter adapter = new TaskListAdapter(getActivity(), R.layout.task_card,items);
                    ListView gv = (ListView) parent.findViewById(R.id.ican_help_listview);
                    gv.setAdapter(adapter);
                    gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getActivity(), ViewTask.class);
                            TextView textView = view.findViewById(R.id.task_card_title);
                            intent.putExtra(MESSAGE, textView.getTag().toString());
                            startActivity(intent);
                        }
                    });
                } catch (org.json.JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        Volley.newRequestQueue(getActivity()).add(jsonRequest);

//        tasks.add(new TaskCard("Do something awesome",
//                "This is the content, this is more content", "/static/images"));
//        tasks.add(new TaskCard("Do something bad",
//                "This is the content, this is more content", "/static/images"));
//        tasks.add(new TaskCard("Do something wired",
//                "This is the content, this is more content", "/static/images"));
//        return tasks;
    }
}
