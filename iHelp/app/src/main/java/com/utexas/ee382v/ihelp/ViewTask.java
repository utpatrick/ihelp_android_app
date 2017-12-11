package com.utexas.ee382v.ihelp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class ViewTask extends AppCompatActivity {

    private Button chat_btn;
    private static String helper_email;
    private static String helpee_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);
        Intent intent = getIntent();
        String extra = intent.getStringExtra(ViewAll.MESSAGE);
        String[] contents = extra.split(",");
        String url = MainActivity.getEndpoint() + "/android/view_task?task_owner="
                + contents[0] + "&task_title=" + contents[1];

        chat_btn = (Button) findViewById(R.id.chat_btn);
        chat_btn.setVisibility(View.GONE);
        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openConversation(view);
            }
        });

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

                    helper_email = response.getString("helper_email");
                    helpee_email = response.getString("helpee_email");
                    RatingBar ratingBar = findViewById(R.id.view_task_rating);
                    ratingBar.setRating(Float.valueOf(rating));
                    ratingBar.setIsIndicator(true);
                    TextView nameView = findViewById(R.id.view_task_name);
                    nameView.setText(owner_name);
                    // slot for saving helper_email
                    nameView.setTag(helper_email);
                    ImageView imageView = findViewById(R.id.view_task_image);
                    String url = MainActivity.getEndpoint() +
                            "/android/profile_image?user_email=" + owner_email;
                    Picasso.with(getApplicationContext()).load(url).fit().into(imageView);
                    TextView titleView = findViewById(R.id.view_task_title);
                    titleView.setText(title);
                    // slot for saving helper_email
                    titleView.setTag(helpee_email);
                    TextView locationView = findViewById(R.id.view_task_location);
                    locationView.setText(location);
                    locationView.setTextColor(getResources().getColor(R.color.coral));
                    TextView destView = findViewById(R.id.view_task_dest);
                    destView.setText(destination);
                    destView.setTextColor(getResources().getColor(R.color.coral));
                    TextView contentView = findViewById(R.id.view_task_content);
                    contentView.setText(detail);
                    contentView.setTag(owner_email);
                    TextView statusView = findViewById(R.id.view_task_status);
                    Button action = findViewById(R.id.view_task_action_btn);

                    if(!nameView.getTag().toString().equals("null") && !titleView.getTag().toString().equals("null")) {
                        chat_btn.setVisibility(View.VISIBLE);
                    }

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
                        typeView.setText(getResources().getString(R.string.provide_help));
                    } else {
                        TextView typeView = findViewById(R.id.view_task_type);
                        typeView.setText(getResources().getString(R.string.need_help));
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

    private void openConversation(View view){

        Intent intent = new Intent(this, ConversationActivity.class);
        String contact_id = "";
        if(helper_email.equals(MainActivity.getUserEmail())){
            contact_id = helpee_email;
        }else if(helpee_email.equals(MainActivity.getUserEmail())){
            contact_id = helper_email;
        }
        intent.putExtra(ConversationUIService.USER_ID, contact_id);
        intent.putExtra(ConversationUIService.DISPLAY_NAME, "Receiver display name"); //put it for displaying the title.
        intent.putExtra(ConversationUIService.TAKE_ORDER, true); //Skip chat list for showing on back press
        startActivity(intent);
    }

    public void sumbitRequest(View view) {
        TextView contentView = findViewById(R.id.view_task_content);
        String owner = (String) contentView.getTag();
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
