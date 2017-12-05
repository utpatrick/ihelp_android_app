package com.utexas.ee382v.ihelp;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Patrick on 11/27/17.
 */

public class Manage extends Fragment {

    private final static int EDIT_PROFILE_CODE = 7635;
    private ImageButton edit_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.manage, container, false);
        ListView listView = (ListView) view.findViewById(R.id.manage_listview);
        ArrayList<TaskCard> items = getAllTasks();
        TaskListAdapter listAdapter = new TaskListAdapter(getActivity(), R.layout.task_card, items);
        listView.setAdapter(listAdapter);
        final String image_url = MainActivity.getEndpoint() + "/android/profile_image?user_email=" + MainActivity.getUserEmail();
        ImageView profileImage = (ImageView) view.findViewById(R.id.profile_image);
        Picasso.with(getContext()).load(image_url).fit().into(profileImage);

        edit_btn = (ImageButton) view.findViewById(R.id.edit_btn);
        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile();
            }
        });
        return view;
    }

    public void editProfile() {
        Intent intent = new Intent(this.getActivity(), EditProfile.class);
        startActivityForResult(intent, EDIT_PROFILE_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_PROFILE_CODE && resultCode == Activity.RESULT_OK && null != data) {
            // waiting for fragment refresh implementation
        }
    }

    private ArrayList<TaskCard> getAllTasks() {
        ArrayList<TaskCard> tasks = new ArrayList<>();
        tasks.add(new TaskCard("Do something awesome",
                "This is i need help, this is more content", "/static/images"));
        tasks.add(new TaskCard("Do something bad",
                "This is i need help, this is more content", "/static/images"));
        tasks.add(new TaskCard("Do something wired",
                "This is i need help, this is more content", "/static/images"));
        tasks.add(new TaskCard("Do something wired",
                "This is i need help, this is more content", "/static/images"));
        tasks.add(new TaskCard("Do something wired",
                "This is i need help, this is more content", "/static/images"));
        return tasks;
    }
}