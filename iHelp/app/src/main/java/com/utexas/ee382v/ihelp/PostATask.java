package com.utexas.ee382v.ihelp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Patrick and Xiaocheng on 11/17/17.
 */

public class PostATask extends Fragment implements View.OnClickListener{
    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    ImageView GiveHelp;
    ImageView NeedHelp;
    ToggleButton b;
    public EditText title;
    public EditText taskLocation;
    public EditText destination;
    public EditText textDetails;
    public ImageButton exchange;
    public Button draft;
    public Button post;
    private String task_status;

    public String task_location;
    public String task_destination;

    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.post_a_task, container,false);
        GiveHelp = view.findViewById(R.id.imageGiveHelp);
        NeedHelp = view.findViewById(R.id.imageNeedHelp);
        title = view.findViewById(R.id.task_title);
        taskLocation = view.findViewById(R.id.Task_location);
        destination = view.findViewById(R.id.Destination);
        textDetails = view.findViewById(R.id.task_details);
        exchange = view.findViewById(R.id.exchange);
        draft = view.findViewById(R.id.save_draft);
        post = view.findViewById(R.id.post_now);

        draft.setOnClickListener(this);
        post.setOnClickListener(this);

        exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task_location = taskLocation.getText().toString();
                task_destination = destination.getText().toString();

                String tmp = task_location;
                task_location = task_destination;
                task_destination = tmp;

                taskLocation.setText(task_location);
                destination.setText(task_destination);

            }
        });


        GiveHelp.setVisibility(View.INVISIBLE);
        NeedHelp.setVisibility(View.INVISIBLE);
        b = view.findViewById(R.id.toggleButton2);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((ToggleButton) v).isChecked();
                if(checked) {
                    NeedHelp.setVisibility(View.VISIBLE);
                    GiveHelp.setVisibility(View.INVISIBLE);
                }
                else {
                    NeedHelp.setVisibility(View.INVISIBLE);
                    GiveHelp.setVisibility(View.VISIBLE);

                }

            }
        });

        spinner = (Spinner) view.findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this.getContext(),R.array.task_names,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        return view;
    }

    public String get_task_title(){
        title = getView().findViewById(R.id.task_title);
        return title.getText().toString();
    }

    public String get_task_category(){
        spinner = getView().findViewById(R.id.spinner);
        Log.d("spinner_value", spinner.getSelectedItem().toString());
        return spinner.getSelectedItem().toString();
    }

    public String get_task_detail(){
        textDetails = getView().findViewById(R.id.task_details);
        return textDetails.getText().toString();
    }

    public String get_task_location(){
        taskLocation = getView().findViewById(R.id.Task_location);
        return taskLocation.getText().toString();
    }

    public String get_destination_location(){
        destination = getView().findViewById(R.id.Destination);
        return destination.getText().toString();
    }

    public String get_task_type(){
        GiveHelp = getView().findViewById(R.id.imageGiveHelp);
        NeedHelp = getView().findViewById(R.id.imageNeedHelp);
        if(GiveHelp.isShown()) return "provide_help";
        else if(NeedHelp.isShown()) return "seek_help";
        // not need to return this "not_selected"
        else return "not_selected";
    }

    @Override
    public void onClick(View view) {
        Log.d("uploading", "clicked");
        if (view.getId() == R.id.save_draft || view.getId() == R.id.post_now) {

            Log.d("uploading", "uploading......");
            if(view.getId() == R.id.save_draft) task_status = "Drafting";
            if(view.getId() == R.id.post_now) task_status = "Posted";

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Posting, please wait...");
            progressDialog.show();

            final String request_url = MainActivity.getEndpoint() + "/android/post_a_task";

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, request_url, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String resultResponse = new String(response.data);
                    try {
                        JSONObject result = new JSONObject(resultResponse);
                        String status = result.getString("status");

                        if (status.equals("ok")) {
                            if (progressDialog.isShowing()) progressDialog.dismiss();
                        }
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setTitle("Post Successful!");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Go To View",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        //Intent intent = new Intent(ViewAll.this, ViewOneStream.class);
                                        //intent.putExtra(ViewAllStream.SELECTED_STREAM, streamName);
                                        //startActivity(intent);
                                    }
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Continue Uploading",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
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
                    params.put("task_title", get_task_title());
                    params.put("task_category", get_task_category());
                    params.put("task_type", get_task_type());
                    params.put("task_detail", get_task_detail());
                    params.put("task_location", get_task_location());
                    params.put("desitination_location", get_destination_location());
                    params.put("task_status", task_status);
                    params.put("task_onwer", MainActivity.getUserEmail());
                    params.put("credit", "100");
                    params.put("owner_name", MainActivity.getUserName());
                    return params;
                }
            };

            VolleySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(multipartRequest);

        }
    }
}



