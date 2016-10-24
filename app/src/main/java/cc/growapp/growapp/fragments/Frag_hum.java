package cc.growapp.growapp.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cc.growapp.growapp.R;


public class Frag_hum extends Fragment {

    TextView hum;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.frag_hum, container, false);
        hum = (TextView) v.findViewById(R.id.tv_h_data);
        return v;
        //return inflater.inflate(R.layout.frag_light_on, null);
    }
    public void setText(String data){
        hum.setText(data);
    }

}
