
package cc.growapp.growapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;


import cc.growapp.growapp.database.DatabaseHelper;
import cc.growapp.growapp.database.Dev_profile;
import cc.growapp.growapp.database.Preferences;
import cc.growapp.growapp.R;
import cc.growapp.growapp.tabs.SlidingTabLayout;





public class Frag_PrefencesSlidingTabs extends Fragment {

    String LOG_TAG="GrowApp";

    int l_control;
    int t_control;
    int h_control;
    int pot1_control;
    int pot2_control;
    int pump1_control;
    int pump2_control;
    int relay1_control;
    int relay2_control;
    int water_control;
    int auto_watering1;
    int auto_watering2;



    //private ProgressDialog pDialog;

    SharedPreferences sPref;
    public static final String APP_PREFERENCES = "GrowAppSettings";
    String controller_id;
    String hash;


    // Database Helper
    DatabaseHelper db;

    String[] periodSpinnerArray;
    int spinner_index;

    Spinner spinner;
    EditText parameter_min_value, parameter_max_value;
    TextView parameter_value;

    CheckBox parameter_notify;
    CheckBox all_notify;

    final int temp_max_value=50;
    final int hum_max_value=90;
    final int pot1_max_value=100;
    final int pot2_max_value=100;
    final int water_max_value=100;



    /**
     * Inflates the {@link View} which will be displayed by getActivity {@link Fragment}, from the app's
     * resources.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {



        return inflater.inflate(R.layout.fragment_sample, container, false);

    }

    // BEGIN_INCLUDE (fragment_onviewcreated)
    /**
     * getActivity is called after the {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has finished.
     * Here we can pick out the {@link View}s we need to configure from the content view.
     *
     * We set the {@link ViewPager}'s adapter to be an instance of {@link SamplePagerAdapter}. The
     * {@link SlidingTabLayout} is then given the {@link ViewPager} so that it can populate itself.
     *
     * @param view View created in {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ViewPager mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SamplePagerAdapter());


        // BEGIN_INCLUDE (setup_slidingtablayout)
        // Give the SlidingTabLayout the ViewPager, getActivity must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);

    }
    // END_INCLUDE (fragment_onviewcreated)

    /**
     * The {@link android.support.v4.view.PagerAdapter} used to display pages in getActivity sample.
     * The individual pages are simple and just display two lines of text. The important section of
     * getActivity class is the {@link #getPageTitle(int)} method which controls what is displayed in the
     * {@link SlidingTabLayout}.
     */
    class SamplePagerAdapter extends PagerAdapter {
        /**
         * @return the number of pages to display
         */
        @Override
        public int getCount() {
            return 2;
        }

        /**
         * @return true if the value returned from {@link #instantiateItem(ViewGroup, int)} is the
         * same object as the {@link View} added to the {@link ViewPager}.
         */
        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return getString(R.string.common_pref);
                case 1:
                    return getString(R.string.auto_watering_pref);
            }

            //return "Item " + (position + 1);
            return "Unknown";
        }
        // END_INCLUDE (pageradapter_getpagetitle)

        /**
         * Instantiate the {@link View} which should be displayed at {@code position}. Here we
         * inflate a layout from the apps resources and then change the text view to signify the position.
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // Inflate a new layout from our resources
            View view = getActivity().getLayoutInflater().inflate(R.layout.pref_common_tab,
                    container, false);



            sPref = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
            //hash = sPref.getString("hash", "");
            Intent intent = getActivity().getIntent();
            controller_id = intent.getStringExtra("controller_id");

            switch (position){
                case 0:

                    db = new DatabaseHelper(getContext());
                    Preferences preferences = db.getPrefProfile(controller_id);
                    Dev_profile dev_profile = db.getDevProfile(controller_id);

                    l_control= dev_profile.get_light_control();
                    t_control= dev_profile.get_t_control();
                    h_control= dev_profile.get_h_control();
                    pot1_control= dev_profile.get_pot1_control();
                    pot2_control= dev_profile.get_pot2_control();
                    pump1_control= dev_profile.get_pump1_control();
                    pump2_control= dev_profile.get_pump2_control();
                    relay1_control= dev_profile.get_relay1_control();
                    relay2_control= dev_profile.get_relay2_control();
                    water_control= dev_profile.get_water_control();
                    db.closeDB();


                    all_notify = (CheckBox) view.findViewById(R.id.cb_notify_all);

                    boolean saved_all_notify= (preferences.get_all_notify()!=0);
                    if(saved_all_notify)all_notify.setChecked(true); else all_notify.setChecked(false);

                    spinner = (Spinner) view.findViewById(R.id.pref_period_spinner);

                    periodSpinnerArray= new String[]{
                            getString(R.string.min15),
                            getString(R.string.min30),
                            getString(R.string.hour),
                            getString(R.string.hour2)
                    };

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_spinner_item, periodSpinnerArray);
                    spinner.setAdapter(adapter);

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            //controller_id = ctrl_ids_data[spinner.getSelectedItemPosition()];
                            //spinner.getSelectedItem();
                            Log.d(LOG_TAG, "Selected period: " + spinner.getSelectedItem());
                            spinner_index = spinner.getSelectedItemPosition();
                        }

                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    //Выцепляем с базы, какие компоненты системы нам доступны

                    //Log.d("Tag Name", dev_profile.getTagName());

                    int period = sPref.getInt("ServicePeriod",60);
                    int index=0;
                    switch (period){
                        case 15:index=0;break;
                        case 30:index=1;break;
                        case 60:index=2;break;
                        case 120:index=3;break;
                    }
                    spinner.setSelection(index);

                    //Наполним форму сохранеными настройками
                    if(controller_id!=null && !controller_id.isEmpty()){

                        //Наполним настройками
                        GridLayout gl = (GridLayout) view.findViewById(R.id.pref_gl);
                        if(gl!=null){
                            gl.removeAllViews();
                            int column = 5;
                            int row = 0;
                            int current_row=0;

                            if(l_control!=0)row++;
                            if(t_control!=0)row+=2;
                            if(h_control!=0)row+=2;
                            if(pot1_control!=0)row+=2;
                            if(pot2_control!=0)row+=2;
                            if(pump1_control!=0 || pump2_control!=0)row++;
                            if(relay1_control!=0 || relay2_control!=0)row++;
                            if(water_control!=0)row+=2;

                            gl.setColumnCount(column);
                            gl.setRowCount(row);

                            /*GridLayout.LayoutParams layoutParams =new GridLayout.LayoutParams();
                            layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
                            layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;*/

                            if(t_control!=0){
                                add_views_to_common_pref_layout(gl,  "t_control", current_row);
                                current_row+=2;
                            }

                            if(h_control!=0){
                                add_views_to_common_pref_layout(gl,  "h_control", current_row);
                                current_row+=2;
                            }

                            if(pot1_control!=0){
                                add_views_to_common_pref_layout(gl,"pot1_h_control", current_row);
                                current_row+=2;

                            }
                            if(pot2_control!=0){
                                add_views_to_common_pref_layout(gl,"pot2_h_control", current_row);
                                current_row+=2;
                            }

                            if(water_control!=0){
                                add_views_to_common_pref_layout(gl,  "wl_control", current_row);
                                current_row+=2;
                            }

                            if(l_control!=0){
                                add_views_to_common_pref_layout(gl, "l_control", current_row);
                                current_row++;
                            }

                            if(pump1_control!=0 || pump2_control!=0){
                                add_views_to_common_pref_layout(gl, "pumps_control", current_row);
                                current_row++;
                            }

                            if(relay1_control!=0 || relay2_control!=0){
                                add_views_to_common_pref_layout(gl, "relays_control", current_row);
                            }
                        }
                    }


                    break;
                case 1:
                    view = getActivity().getLayoutInflater().inflate(R.layout.pref_auto_watering_tab,
                            container, false);

                    //Наполним форму сохранеными настройками
                    if(controller_id!=null && !controller_id.isEmpty()){

                        //Наполним настройками
                        GridLayout gl = (GridLayout) view.findViewById(R.id.pref_gl);
                        if(gl!=null){
                            gl.removeAllViews();
                            int column = 2;
                            int row = 0;
                            int current_row=0;

                            if(pot1_control!=0)row+=2;
                            if(pot2_control!=0)row+=2;

                            TextView note = (TextView) view.findViewById(R.id.pref_tv_note);
                            gl.setColumnCount(column);
                            gl.setRowCount(row);
                            if((pump1_control!=0 || pump2_control!=0)&&(pot1_control!=0 || pot2_control!=0))
                            note.setText(getString(R.string.auto_watering_note_on));
                            else note.setText(getString(R.string.auto_watering_note_off));

                            if(pot1_control!=0 && pump1_control!=0){
                                add_views_to_aw_pref_layout(gl, "auto_watering1", current_row);
                                current_row+=2;
                            }
                            if(pot2_control!=0  && pump2_control!=0){
                                add_views_to_aw_pref_layout(gl,  "auto_watering2", current_row);
                                //current_row+=2;
                            }

                        }
                    }
                break;
            }

            container.addView(view);
            return view;
        }

        /**
         * Destroy the item from the {@link ViewPager}. In our case getActivity is simply removing the
         * {@link View}.
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);

        }
    }
    private void add_views_to_common_pref_layout(GridLayout gl, String parameter, int current_row){

        Preferences preferences = db.getPrefProfile(controller_id);
        String saved_t_max = String.valueOf(preferences.get_t_max());
        String saved_t_min = String.valueOf(preferences.get_t_min());
        String saved_h_max = String.valueOf(preferences.get_h_max());
        String saved_h_min = String.valueOf(preferences.get_h_min());
        String saved_pot1_h_max = String.valueOf(preferences.get_pot1_h_max());
        String saved_pot1_h_min = String.valueOf(preferences.get_pot1_h_min());
        String saved_pot2_h_max = String.valueOf(preferences.get_pot2_h_max());
        String saved_pot2_h_min = String.valueOf(preferences.get_pot2_h_min());
        String saved_wl_max = String.valueOf(preferences.get_wl_max());
        String saved_wl_min = String.valueOf(preferences.get_wl_min());
        //boolean saved_all_notify= (preferences.get_all_notify()!=0);
        boolean saved_t_notify = (preferences.get_t_notify()!=0);
        boolean saved_h_notify = (preferences.get_h_notify()!=0);
        boolean saved_pot1_notify = (preferences.get_pot1_notify()!=0);
        boolean saved_pot2_notify = (preferences.get_pot2_notify()!=0);
        boolean saved_wl_notify = (preferences.get_wl_notify()!=0);
        boolean saved_l_notify = (preferences.get_l_notify()!=0);
        boolean saved_relays_notify = (preferences.get_relays_notify()!=0);
        boolean saved_pumps_notify = (preferences.get_pumps_notify()!=0);

        String s="";

        GridLayout.LayoutParams tv_label_layout_params = new GridLayout.LayoutParams();
        TextView tv_parameter_title = new TextView(getContext());

        tv_parameter_title.setTextAppearance(getContext(), android.R.style.TextAppearance_Medium);
        tv_label_layout_params.columnSpec = GridLayout.spec(0,4);
        tv_label_layout_params.rowSpec = GridLayout.spec(current_row);
        tv_parameter_title.setLayoutParams(tv_label_layout_params);
        gl.addView(tv_parameter_title);

        parameter_notify = new CheckBox(getContext());
        parameter_notify.setText(getString(R.string.notification));

        gl.addView(parameter_notify);

        if(!parameter.equals("pumps_control") && !parameter.equals("relays_control") && !parameter.equals("l_control") ){
            // --------------------------------------------------
            TextView parameter_min_title = new TextView(getContext());
            parameter_min_title.setText(getString(R.string.min));
            gl.addView(parameter_min_title);
            parameter_min_value = new EditText(getContext());
            parameter_min_value.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            gl.addView(parameter_min_value);
            // --------------------------------------------------

            TextView parameter_max_title = new TextView(getContext());
            parameter_max_title.setText(getString(R.string.max));
            gl.addView(parameter_max_title);
            parameter_max_value = new EditText(getContext());
            parameter_max_value.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);


            gl.addView(parameter_max_value);
            // --------------------------------------------------

        }

        switch (parameter){
            case "t_control":
                parameter_min_value.setId(R.id.pref_et_temp_min);
                parameter_max_value.setId(R.id.pref_et_temp_max);
                parameter_notify.setId(R.id.pref_cb_temp_notify);
                s=getString(R.string.temp);
                parameter_min_value.setText(saved_t_min);
                parameter_max_value.setText(saved_t_max);
                if(saved_t_notify) parameter_notify.setChecked(true); else parameter_notify.setChecked(false);

                //parameter_min_value.addTextChangedListener(watcher);
                //parameter_max_value.addTextChangedListener(watcher);

                break;

            case "h_control":
                parameter_min_value.setId(R.id.pref_et_hum_min);
                parameter_max_value.setId(R.id.pref_et_hum_max);
                parameter_notify.setId(R.id.pref_cb_hum_notify);
                s=getString(R.string.hum);
                parameter_min_value.setText(saved_h_min);
                parameter_max_value.setText(saved_h_max);
                if(saved_h_notify) parameter_notify.setChecked(true); else parameter_notify.setChecked(false);
                break;

            case "pot1_h_control":
                parameter_min_value.setId(R.id.pref_et_pot1_h_min);
                parameter_max_value.setId(R.id.pref_et_pot1_h_max);
                parameter_notify.setId(R.id.pref_cb_pot1_h_notify);
                s=getString(R.string.pot1_hum);
                parameter_min_value.setText(saved_pot1_h_min);
                parameter_max_value.setText(saved_pot1_h_max);
                if(saved_pot1_notify) parameter_notify.setChecked(true); else parameter_notify.setChecked(false);
                break;
            case "pot2_h_control":
                parameter_min_value.setId(R.id.pref_et_pot2_h_min);
                parameter_max_value.setId(R.id.pref_et_pot2_h_max);
                parameter_notify.setId(R.id.pref_cb_pot2_h_notify);
                s=getString(R.string.pot2_hum);
                parameter_min_value.setText(saved_pot2_h_min);
                parameter_max_value.setText(saved_pot2_h_max);
                if(saved_pot2_notify) parameter_notify.setChecked(true); else parameter_notify.setChecked(false);
                break;
            case "wl_control":
                parameter_min_value.setId(R.id.pref_et_wl_min);
                parameter_max_value.setId(R.id.pref_et_wl_max);
                parameter_notify.setId(R.id.pref_cb_wl_notify);
                s=getString(R.string.water_level);
                parameter_min_value.setText(saved_wl_min);
                parameter_max_value.setText(saved_wl_max);
                if(saved_wl_notify) parameter_notify.setChecked(true); else parameter_notify.setChecked(false);
                break;
            case "l_control":
                parameter_notify.setId(R.id.pref_cb_light_notify);
                s=getString(R.string.light_control);
                if(saved_l_notify) parameter_notify.setChecked(true); else parameter_notify.setChecked(false);
                break;
            case "pumps_control":
                parameter_notify.setId(R.id.pref_cb_pumps_notify);
                s=getString(R.string.pumps_control);
                if(saved_pumps_notify) parameter_notify.setChecked(true); else parameter_notify.setChecked(false);
                break;
            case "relays_control":
                parameter_notify.setId(R.id.pref_cb_relays_notify);
                s=getString(R.string.relays_control);
                if(saved_relays_notify) parameter_notify.setChecked(true); else parameter_notify.setChecked(false);
                break;
        }
        tv_parameter_title.setText(s);

    }
    private void add_views_to_aw_pref_layout(GridLayout gl, String parameter, int current_row){

        Dev_profile dev_profile = db.getDevProfile(controller_id);
        String saved_aw1 = String.valueOf(dev_profile.get_auto_watering1());
        String saved_aw2 = String.valueOf(dev_profile.get_auto_watering2());


        String s="";

        GridLayout.LayoutParams tv_label_layout_params = new GridLayout.LayoutParams();
        TextView tv_parameter_title = new TextView(getContext());

        tv_parameter_title.setTextAppearance(getContext(), android.R.style.TextAppearance_Medium);
        tv_label_layout_params.columnSpec = GridLayout.spec(0,1);
        tv_label_layout_params.rowSpec = GridLayout.spec(current_row);
        tv_parameter_title.setLayoutParams(tv_label_layout_params);
        gl.addView(tv_parameter_title);

        //parameter_notify = new CheckBox(getContext());
        //parameter_notify.setText(getString(R.string.notification));

        //gl.addView(parameter_notify);


        // --------------------------------------------------
        parameter_value = new TextView(getContext());
        gl.addView(parameter_value);
        // --------------------------------------------------

        switch (parameter){
            case "auto_watering1":
                parameter_value.setId(R.id.pref_tv_auto_watering1);
                s=getString(R.string.pot1_hum);
                parameter_value.setText(saved_aw1);


                break;
            case "auto_watering2":
                parameter_value.setId(R.id.pref_tv_auto_watering2);
                s=getString(R.string.pot2_hum);
                parameter_value.setText(saved_aw2);

                break;
        }
        tv_parameter_title.setText(s);

    }

/*
    private TextWatcher watcher= new TextWatcher() {
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            boolean temp_max_error=false;
            if(parameter_max_value!=null && !parameter_max_value.getText().toString().equals("")){
                try {
                    int et_value=Integer.parseInt(parameter_max_value.getText().toString());
                    if(!(et_value>=0 && et_value<=temp_max_value))temp_max_error=true;
                } catch (NumberFormatException nfe) {
                    temp_max_error=true;
                }
            }
            if(temp_max_error){
                Toast.makeText(getActivity(), getString(R.string.temp_range), Toast.LENGTH_SHORT).show();
            }

            boolean temp_min_error=false;
            if(parameter_min_value!=null && !parameter_min_value.getText().toString().equals("")){
                try {
                    int et_value=Integer.parseInt(parameter_min_value.getText().toString());
                    if(!(et_value>=0 &&  et_value<=temp_max_value))temp_min_error=true;
                } catch (NumberFormatException nfe) {
                    temp_min_error=true;
                }
            }
            if(temp_min_error){
                Toast.makeText(getActivity(), getString(R.string.temp_range), Toast.LENGTH_SHORT).show();
            }

            boolean hum_max_error=false;
            if(hum_max_view!=null && !hum_max_view.getText().toString().equals("")){
                try {
                    int et_value=Integer.parseInt(hum_max_view.getText().toString());
                    if(!(et_value>=0 &&  et_value<=hum_max_value))hum_max_error=true;
                } catch (NumberFormatException nfe) {
                    hum_max_error=true;
                }
            }
            if(hum_max_error){
                Toast.makeText(getActivity(), getString(R.string.hum_range), Toast.LENGTH_SHORT).show();
            }

            boolean hum_min_error=false;
            if(hum_min_view!=null && !hum_min_view.getText().toString().equals("")){
                try {
                    int et_value=Integer.parseInt(hum_min_view.getText().toString());
                    if(!(et_value>=0 &&  et_value<=hum_max_value))hum_min_error=true;
                } catch (NumberFormatException nfe) {
                    hum_min_error=true;
                }
            }
            if(hum_min_error){
                Toast.makeText(getActivity(), getString(R.string.hum_range), Toast.LENGTH_SHORT).show();
            }


            boolean pot1_max_error=false;
            if(pot1_max_view!=null && !pot1_max_view.getText().toString().equals("")){
                try {
                    int et_value=Integer.parseInt(pot1_max_view.getText().toString());
                    if(!(et_value>=0 &&  et_value<=pot1_max_value))pot1_max_error=true;
                } catch (NumberFormatException nfe) {
                    pot1_max_error=true;
                }
            }
            if(pot1_max_error){
                Toast.makeText(getActivity(), getString(R.string.pot1_range), Toast.LENGTH_SHORT).show();
            }

            boolean pot1_min_error=false;
            if(pot1_min_view!=null && !pot1_min_view.getText().toString().equals("")){
                try {
                    int et_value=Integer.parseInt(pot1_min_view.getText().toString());
                    if(!(et_value>=0 &&  et_value<=pot1_max_value))pot1_min_error=true;
                } catch (NumberFormatException nfe) {
                    pot1_min_error=true;
                }
            }
            if(pot1_min_error){
                Toast.makeText(getActivity(), getString(R.string.pot1_range), Toast.LENGTH_SHORT).show();
            }

            boolean pot2_max_error=false;
            if(pot2_max_view!=null && !pot2_max_view.getText().toString().equals("")){
                try {
                    int et_value=Integer.parseInt(pot2_max_view.getText().toString());
                    if(!(et_value>=0 &&  et_value<=pot2_max_value))pot2_max_error=true;
                    else pot2_max_error=true;
                } catch (NumberFormatException nfe) {
                    pot2_max_error=true;
                }
            }
            if(pot2_max_error){
                Toast.makeText(getActivity(), getString(R.string.pot2_range), Toast.LENGTH_SHORT).show();
            }

            boolean pot2_min_error=false;
            if(pot2_min_view!=null && !pot2_min_view.getText().toString().equals("")){
                try {
                    int et_value=Integer.parseInt(pot2_min_view.getText().toString());
                    if(!(et_value>=0 &&  et_value<=pot2_max_value))pot2_min_error=true;
                } catch (NumberFormatException nfe) {
                    pot2_min_error=true;
                }
            }
            if(pot2_min_error){
                Toast.makeText(getActivity(), getString(R.string.pot2_range), Toast.LENGTH_SHORT).show();
            }


            boolean water_max_error=false;
            if(water_max_view!=null && !water_max_view.getText().toString().equals("")){
                try {
                    int et_value=Integer.parseInt(water_max_view.getText().toString());
                    if(!(et_value>=0 &&  et_value<=water_max_value))water_max_error=true;
                } catch (NumberFormatException nfe) {
                    water_max_error=true;
                }
            }
            if(water_max_error){
                Toast.makeText(getActivity(), getString(R.string.water_range), Toast.LENGTH_SHORT).show();
            }

            boolean water_min_error=false;
            if(water_min_view!=null && !water_min_view.getText().toString().equals("")){
                try {
                    int et_value=Integer.parseInt(water_min_view.getText().toString());
                    if(!(et_value>=0 &&  et_value<=water_max_value))water_min_error=true;
                } catch (NumberFormatException nfe) {
                    water_min_error=true;
                }
            }
            if(water_min_error){
                Toast.makeText(getActivity(), getString(R.string.water_range), Toast.LENGTH_SHORT).show();
            }
        }

        public void afterTextChanged(Editable s) {

        }
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //Do something or nothing.
        }
    };
*/


}
