package cc.growapp.growapp.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cc.growapp.growapp.R;


public class Frag_pot2 extends Fragment {
    TextView pot2_h;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.frag_pot2, container, false);
        pot2_h = (TextView) v.findViewById(R.id.tv_hgor2_data);
        //v.setBackgroundResource(R.drawable.ic_moon_64);
        return v;
        //return inflater.inflate(R.layout.frag_light_on, null);
    }
    public void setText(String data){
        pot2_h.setText(data);
    }
}
