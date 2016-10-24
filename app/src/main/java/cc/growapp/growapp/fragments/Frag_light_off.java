package cc.growapp.growapp.fragments;


import android.app.Fragment;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cc.growapp.growapp.R;


public class Frag_light_off extends Fragment {
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_light_off, container, false);
        v.setBackgroundResource(R.drawable.anim_moon);
        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) v.getBackground();
        // Start the animation (looped playback by default).
        frameAnimation.start();


        //v.setBackgroundResource(R.drawable.ic_moon_64);
        return v;
    }

}
