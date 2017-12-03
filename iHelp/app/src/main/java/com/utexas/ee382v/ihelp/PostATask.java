package com.utexas.ee382v.ihelp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

/**
 * Created by kyle on 11/17/17.
 */

public class PostATask extends Fragment {
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

    public String task_location;
    public String task_destination;

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



}



