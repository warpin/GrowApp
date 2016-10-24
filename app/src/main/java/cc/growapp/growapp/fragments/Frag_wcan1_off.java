package cc.growapp.growapp.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import cc.growapp.growapp.activities.MainActivity;
import cc.growapp.growapp.R;


public class Frag_wcan1_off extends Fragment {
    //boolean pressed=false;
    //private static final String LOG_TAG = "GrowApp";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.frag_wcan1_off, container, false);



        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:

                        ((MainActivity) getActivity()).pump1_control();
                        break;
                }
                return true;
            }
        });


        return v;
        //return inflater.inflate(R.layout.frag_light_on, null);
    }


}
