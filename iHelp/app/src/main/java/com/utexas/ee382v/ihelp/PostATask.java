package com.utexas.ee382v.ihelp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.post_a_task, container,false);
        GiveHelp = view.findViewById(R.id.imageGiveHelp);
        NeedHelp = view.findViewById(R.id.imageNeedHelp);
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



