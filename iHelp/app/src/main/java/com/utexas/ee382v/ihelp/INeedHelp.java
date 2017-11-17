package com.utexas.ee382v.ihelp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by kyle on 11/17/17.
 */

public class INeedHelp extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ineed_help, container, false);
        ListView listView = (ListView) view.findViewById(R.id.ineed_help_listview);
        ArrayList<TaskCard> items = getAllTasks();
        TaskListAdapter listAdapter = new TaskListAdapter(getActivity(), R.layout.task_card, items);
        listView.setAdapter(listAdapter);
        return view;
    }

    private ArrayList<TaskCard> getAllTasks() {
        ArrayList<TaskCard> tasks = new ArrayList<>();
        tasks.add(new TaskCard("Do something awesome",
                "This is i need help, this is more content", "/static/images"));
        tasks.add(new TaskCard("Do something bad",
                "This is i need help, this is more content", "/static/images"));
        tasks.add(new TaskCard("Do something wired",
                "This is i need help, this is more content", "/static/images"));
        return tasks;
    }
}
