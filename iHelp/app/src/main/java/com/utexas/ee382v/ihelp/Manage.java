package com.utexas.ee382v.ihelp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.os.SystemClock.sleep;

/**
 * Created by Patrick on 11/27/17.
 */

public class Manage extends Fragment {

    private final static int EDIT_PROFILE_CODE = 7635;
    private ImageButton edit_btn;
    private CheckBox draftingBox;
    private CheckBox postedBox;
    private CheckBox ongoingBox;
    private CheckBox finishedBox;
    private static String url;
    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.manage, container, false);

        draftingBox = view.findViewById(R.id.drafting_filter);
        postedBox = view.findViewById(R.id.posted_filter);
        ongoingBox = view.findViewById(R.id.ongoing_filter);
        finishedBox = view.findViewById(R.id.finished_filter);
        getAllTasks(url, view);
        setUpCheckBox(view);
        String image_url = MainActivity.getEndpoint()
                + "/android/profile_image?user_email=" + MainActivity.getUserEmail()
                + "&time=" + Double.toString(System.nanoTime());
        Log.d("system_time", Double.toString(System.nanoTime()));
        url = MainActivity.getEndpoint()+ "/android/manage_task?owner_email=" + MainActivity.getUserEmail();
        swipeRefreshLayout = view.findViewById(R.id.manage_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllTasks(url, view);
            }
        });
        getAllTasks(url, view);
        setUpCheckBox(view);

        ImageView profileImage = (ImageView) view.findViewById(R.id.profile_image);
        Picasso.with(getContext()).load(image_url).fit().into(profileImage);

        edit_btn = (ImageButton) view.findViewById(R.id.edit_btn);
        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile();
            }
        });

        final String task_url = MainActivity.getEndpoint() + "/android/delete_task?task_id=" + MainActivity.getUserEmail();
        String rating_url = MainActivity.getEndpoint() + "/android/get_rating?user_email=" + MainActivity.getUserEmail();
        TextView rating = view.findViewById(R.id.manage_rating);
        getRating(rating_url, rating);

        return view;
    }

    public void editProfile() {
        Intent intent = new Intent(this.getActivity(), EditProfile.class);
        startActivityForResult(intent, EDIT_PROFILE_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.d("requestCode", Integer.toString(requestCode));
        //Log.d("requestCode_edit", Integer.toString(EDIT_PROFILE_CODE));
        //Log.d("resultCode", Integer.toString(resultCode));
        //Log.d("resultCode_ok", Integer.toString(Activity.RESULT_OK));
        if (requestCode == EDIT_PROFILE_CODE && resultCode == Activity.RESULT_OK) {
            Log.d("status", "ok");
            getAllTasks(url, view);
            String image_url = MainActivity.getEndpoint()
                    + "/android/profile_image?user_email=" + MainActivity.getUserEmail()
                    + "&time=" + Double.toString(System.nanoTime());
            Log.d("image_url", image_url);
            ImageView profileImage = (ImageView) view.findViewById(R.id.profile_image);
            Log.d("image", profileImage.toString());
            Picasso.with(this.getContext()).load(image_url).fit().into(profileImage);
        }
    }

    private void getRating(final String url, final TextView ratingView) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            String rating = response.getString("rating");
                            if (rating != null) {
                                ratingView.setText(String.format("%.2f", Float.valueOf(rating)) + " / 5.0");
                            } else {
                                ratingView.setText("N/A");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        Volley.newRequestQueue(getActivity()).add(jsObjRequest);
    }

    private void getAllTasks(final String url, final View parent) {
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
                        if (obj.getString("task_category") != null) {
                            card.setCategory(obj.getString("task_category"));
                        }
                        items.add(card);
                    }
                    TaskListAdapter adapter = new TaskListAdapter(getActivity(), R.layout.task_card,items);
                    ListView lv = parent.findViewById(R.id.manage_listview);
                    lv.setAdapter(adapter);
                    lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            TextView textView2 = view.findViewById(R.id.task_card_id);
                            String task_id = textView2.getText().toString();
                            Log.d("task_id", task_id);
                            deleteTaskOption(task_id);
                            return true;
                        }
                    });

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getActivity(), ViewTask.class);
                            ImageView imageView = view.findViewById(R.id.task_card_image);
                            TextView textView = view.findViewById(R.id.task_card_title);
                            String message = imageView.getTag().toString() + "," + textView.getText().toString();
                            intent.putExtra(ViewAll.MESSAGE, message);
                            startActivity(intent);
                        }
                    });
                    swipeRefreshLayout.setRefreshing(false);
                } catch (org.json.JSONException e) {
                    e.printStackTrace();
                    swipeRefreshLayout.setRefreshing(false);
                } finally {
                    swipeRefreshLayout.setRefreshing(false);
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

    private void deleteTaskOption(final String task_id) {
        final CharSequence[] items = { "Delete Task", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Be Careful!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Delete Task")) {
                    deleteTasks(task_id);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
                updateTaskAfterDelete(view);
            }
        });
        builder.show();
    }

    private void updateTaskAfterDelete(View view){
        if(draftingBox.isSelected()){
            String new_url = url;
            new_url += "&task_status=Drafting";
            getAllTasks(new_url, view);
        }else if(postedBox.isSelected()){
            String new_url = url;
            new_url += "&task_status=Posted";
            getAllTasks(new_url, view);
        }else if(ongoingBox.isSelected()){
            String new_url = url;
            new_url += "&task_status=Ongoing";
            getAllTasks(new_url, view);
        }else if(finishedBox.isSelected()){
            String new_url = url;
            new_url += "&task_status=Completed";
            getAllTasks(new_url, view);
        }else{
            String new_url = url;
            getAllTasks(new_url, view);
        }
    }

    private String deleteTasks(final String task_id){
        final String request_url = MainActivity.getEndpoint() + "/android/delete_task";

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, request_url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    String status = result.getString("status");
                    sleep(100);
                    getAllTasks(url, view);
                    Log.d("delete_status", status);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        Log.e("Error Status", status);
                        Log.e("Error Message", message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("owner_email", MainActivity.getUserEmail());
                Log.d("id_now", task_id);
                params.put("task_id", task_id);
                return params;
            }
        };

        VolleySingleton.getInstance(getContext()).addToRequestQueue(multipartRequest);

        return "";
    }

    private void setUpCheckBox(final View view) {
        final CheckBox draftingBox = view.findViewById(R.id.drafting_filter);
        final CheckBox postedBox = view.findViewById(R.id.posted_filter);
        final CheckBox ongoingBox = view.findViewById(R.id.ongoing_filter);
        final CheckBox finishedBox = view.findViewById(R.id.finished_filter);

        draftingBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox box = (CheckBox) v;
                String new_url = url;
                if (box.isChecked()) {
                    postedBox.setChecked(false);
                    ongoingBox.setChecked(false);
                    finishedBox.setChecked(false);
                    new_url += "&task_status=Drafting";
                }
                getAllTasks(new_url, view);
            }
        });

        postedBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox box = (CheckBox) v;
                String new_url = url;
                if (box.isChecked()) {
                    draftingBox.setChecked(false);
                    ongoingBox.setChecked(false);
                    finishedBox.setChecked(false);
                    new_url += "&task_status=Posted";
                }
                getAllTasks(new_url, view);
            }
        });

        ongoingBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox box = (CheckBox) v;
                String new_url = url;
                if (box.isChecked()) {
                    draftingBox.setChecked(false);
                    postedBox.setChecked(false);
                    finishedBox.setChecked(false);
                    new_url += "&task_status=Ongoing";
                }
                getAllTasks(new_url, view);
            }
        });

        finishedBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox box = (CheckBox) v;
                String new_url = url;
                if (box.isChecked()) {
                    draftingBox.setChecked(false);
                    postedBox.setChecked(false);
                    ongoingBox.setChecked(false);
                    new_url += "&task_status=Completed";
                }
                getAllTasks(new_url, view);
            }
        });
    }
}