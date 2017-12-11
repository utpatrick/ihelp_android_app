package com.utexas.ee382v.ihelp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

                    helper_email = response.getString("helper_email");
                    helpee_email = response.getString("helpee_email");

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
                    locationView.setText("Location: " + location);
                    TextView destView = findViewById(R.id.view_task_dest);
                    destView.setText("Destination: " + destination);
                    TextView contentView = findViewById(R.id.view_task_content);
                    contentView.setText(detail);
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
                        statusView.setText(R.string.posted);
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
        TextView nameView = findViewById(R.id.view_task_name);
        String owner = (String) nameView.getTag();
        TextView titleView = findViewById(R.id.view_task_title);
        String title = (String) titleView.getText();
        TextView statusView = findViewById(R.id.view_task_status);
        String status = (String) statusView.getText();
        String url = MainActivity.getEndpoint() + "/android/change_status?" +
                "owner=" + owner + "&task_title=" + title + "&requestee=" +
                MainActivity.getUserEmail() + "&status=" + status;
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
