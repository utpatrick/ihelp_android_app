package com.utexas.ee382v.ihelp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class ViewTask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);
        Intent intent = getIntent();
        String extra = intent.getStringExtra(ViewAll.MESSAGE);
        String[] contents = extra.split(",");
        String url = MainActivity.getEndpoint() + "/android/view_task?task_owner="
                + contents[0] + "&task_title=" + contents[1];

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String title = response.getString("task_title");
                    String type = response.getString("task_type");
                    String detail = response.getString("task_detail");
                    String location = response.getString("task_location");
                    String destination = response.getString("destination");
                    String owner_name = response.getString("owner_name");

                    String owner_email = response.getString("task_owner");
                    String credit = response.getString("extra_credit");
                    String status = response.getString("task_status");
                    String rating = response.getString("rating");

                    RatingBar ratingBar = findViewById(R.id.view_task_rating);
                    ratingBar.setRating(Float.valueOf(rating));
                    ratingBar.setIsIndicator(true);
                    TextView nameView = findViewById(R.id.view_task_name);
                    nameView.setText(owner_name);
                    nameView.setTag(owner_email);
                    ImageView imageView = findViewById(R.id.view_task_image);
                    String url = MainActivity.getEndpoint() +
                            "/android/profile_image?user_email=" + owner_email;
                    Picasso.with(getApplicationContext()).load(url).fit().into(imageView);
                    TextView titleView = findViewById(R.id.view_task_title);
                    titleView.setText(title);
                    TextView locationView = findViewById(R.id.view_task_location);
                    locationView.setText(location);
                    locationView.setTextColor(getResources().getColor(R.color.coral));
                    TextView destView = findViewById(R.id.view_task_dest);
                    destView.setText(destination);
                    destView.setTextColor(getResources().getColor(R.color.coral));
                    TextView contentView = findViewById(R.id.view_task_content);
                    contentView.setText(detail);
                    TextView statusView = findViewById(R.id.view_task_status);
                    Button action = findViewById(R.id.view_task_action_btn);

                    if ("Posted".equals(status)) {
                        statusView.setText(R.string.posted);
                        statusView.setTextColor(getResources().getColor(R.color.green));
                        if ("provide_help".equals(type)) {
                            action.setText(R.string.get_help);
                        } else {
                            action.setText(R.string.provide_help);
                        }
                    } else if ("Ongoing".equals(status)){
                        statusView.setText(R.string.ongoing);
                        statusView.setTextColor(getResources().getColor(R.color.yellow));

                        if (owner_email != null && owner_email.equals(MainActivity.getUserEmail())) {
                            action.setText(R.string.complete);
                        } else {
                            action.setText(R.string.no_activity);
                            action.setEnabled(false);
                        }

                    } else {
                        if ("Completed".equals(status)) {
                            statusView.setText(R.string.completed);
                            statusView.setTextColor(getResources().getColor(R.color.blue));
                            action.setText(R.string.no_activity);
                            action.setEnabled(false);
                        } else if ("Drafting".equals(status)){
                            statusView.setText(R.string.drafting);
                            statusView.setTextColor(getResources().getColor(R.color.red));
                        }

                    }

                    if ("provide_help".equals(type)) {
                        TextView typeView = findViewById(R.id.view_task_type);
                        typeView.setText("Type: " + getResources().getString(R.string.provide_help));
                    } else {
                        TextView typeView = findViewById(R.id.view_task_type);
                        typeView.setText("Type: " + getResources().getString(R.string.need_help));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        Volley.newRequestQueue(this).add(jsonRequest);
    }

    public void sumbitRequest(View view) {
        TextView nameView = findViewById(R.id.view_task_name);
        String owner = (String) nameView.getTag();
        TextView titleView = findViewById(R.id.view_task_title);
        String title = (String) titleView.getText();
        TextView statusView = findViewById(R.id.view_task_status);
        String status = (String) statusView.getText();

        final String url = MainActivity.getEndpoint() + "/android/change_status?" +
                "owner=" + owner + "&task_title=" + title + "&requestee=" +
                MainActivity.getUserEmail() + "&status=" + status;

        if ("Ongoing".equals(status)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ViewTask.this);
            LayoutInflater inflater = getLayoutInflater();
            final View ratingDialog = inflater.inflate(R.layout.rating_dialog,null);
            final RatingBar reviewRating = ratingDialog.findViewById(R.id.rating_dialog_ratingbar);
            builder.setTitle(R.string.rate_task)
                    .setView(ratingDialog)
                    .setPositiveButton(R.string.rating_confirm, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    float rating = reviewRating.getRating();
                    String new_url = url + "&rating=" + rating;
                    makeNetworkRequest(new_url);
                    finish();
                }
                    }).setNegativeButton(R.string.rating_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String new_url = url + "&rating=5";
                    makeNetworkRequest(new_url);
                    finish();
                }
            });
            builder.show();
        } else {
            makeNetworkRequest(url);
        }
    }

    private void makeNetworkRequest(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Intent intent = new Intent(getApplicationContext(), ViewAll.class);
                startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(this).add(stringRequest);
    }
}
