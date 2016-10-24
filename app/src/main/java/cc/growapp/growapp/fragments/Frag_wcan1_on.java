package cc.growapp.growapp.fragments;


import android.app.Fragment;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import cc.growapp.growapp.activities.MainActivity;
import cc.growapp.growapp.R;


public class Frag_wcan1_on extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.frag_wcan1_on, container, false);


        ImageView f = (ImageView) v.findViewById(R.id.frag_wcan1_img);
        f.setImageResource(R.drawable.anim_wcan1);

        //v.setBackgroundResource(R.drawable.anim_left_shower);
        // Get the background, which has been compiled to an AnimationDrawable object.
        //AnimationDrawable frameAnimation = (AnimationDrawable) v.getBackground();
        AnimationDrawable frameAnimation = (AnimationDrawable) f.getDrawable();
        // Start the animation (looped playback by default).
        frameAnimation.start();

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
