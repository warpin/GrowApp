package cc.growapp.growapp.activities;


import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import cc.growapp.growapp.DataBroker;
import cc.growapp.growapp.R;
import cc.growapp.growapp.database.MyContentProvider;
import cc.growapp.growapp.services.BackgroundService;


public class PrefCriticalActivity extends AppCompatActivity {


    String LOG_TAG="PreferencesActivity";

    String hash;
    int version;
    String controller_id;



    SharedPreferences sPref;
    public static final String APP_PREFERENCES = "GrowAppSettings";

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

    String t_max_value = "0";
    String t_min_value = "0";
    String h_max_value = "0";
    String h_min_value = "0";
    String pot1_h_max_value = "0";
    String pot1_h_min_value = "0";
    String pot2_h_max_value = "0";
    String pot2_h_min_value = "0";
    String wl_max_value = "0";
    String wl_min_value = "0";

    boolean t_notify_value;
    boolean h_notify_value ;
    boolean pot1_notify_value ;
    boolean pot2_notify_value ;
    boolean wl_notify_value ;
    boolean l_notify_value ;
    boolean relays_notify_value ;
    boolean pumps_notify_value ;
    
    
    EditText parameter_min_value, parameter_max_value;
    CheckBox parameter_notify;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pref_critical_activity);

        
        

        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        hash = sPref.getString("hash", "");

        controller_id = getIntent().getStringExtra("controller_id");
        get_pref_data_from_sql();

        //Наполним форму сохранеными настройками
        if(controller_id!=null && !controller_id.isEmpty()){

            //Наполним настройками
            GridLayout gl = (GridLayout) findViewById(R.id.pref_gl);
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

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getIntent().getStringExtra("controller_name")+" "+getString(R.string.pref_crit_notif));

        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Операции для выбранного пункта меню
        switch (item.getItemId())
        {
            case  android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        version++;


        /*Log.d(LOG_TAG, "Saving notif preferences in internal DB, version: " + version);
            ContentValues cv = new ContentValues();
            cv.put(MyContentProvider.KEY_PREF_CTRL_ID, controller_id);
            cv.put(MyContentProvider.KEY_PREF_VERSION, version);

            cv.put(MyContentProvider.KEY_PREF_L_NOTIFY, l_notify_value);
            cv.put(MyContentProvider.KEY_PREF_T_MIN, t_min_value);
            cv.put(MyContentProvider.KEY_PREF_T_MAX, t_max_value);
            cv.put(MyContentProvider.KEY_PREF_T_NOTIFY, t_notify_value);

            cv.put(MyContentProvider.KEY_PREF_H_MIN, h_min_value);
            cv.put(MyContentProvider.KEY_PREF_H_MAX, h_max_value);
            cv.put(MyContentProvider.KEY_PREF_H_NOTIFY, h_notify_value);
            cv.put(MyContentProvider.KEY_PREF_POT1_H_MIN, pot1_h_min_value);
            cv.put(MyContentProvider.KEY_PREF_POT1_H_MAX, pot1_h_max_value);
            cv.put(MyContentProvider.KEY_PREF_POT1_NOTIFY, pot1_notify_value);
            cv.put(MyContentProvider.KEY_PREF_POT2_H_MIN, pot2_h_min_value);
            cv.put(MyContentProvider.KEY_PREF_POT2_H_MAX, pot2_h_max_value);
            cv.put(MyContentProvider.KEY_PREF_POT2_NOTIFY, pot2_notify_value);
            cv.put(MyContentProvider.KEY_PREF_WL_MIN, wl_min_value);
            cv.put(MyContentProvider.KEY_PREF_WL_MAX, wl_max_value);
            cv.put(MyContentProvider.KEY_PREF_WL_NOTIFY, wl_notify_value);

            cv.put(MyContentProvider.KEY_PREF_PUMPS_NOTIFY, pumps_notify_value);
            cv.put(MyContentProvider.KEY_PREF_RELAYS_NOTIFY, relays_notify_value);




            Uri newUri = ContentUris.withAppendedId(MyContentProvider.PREF_CONTENT_URI, Long.parseLong(controller_id));
            int cnt = getContentResolver().update(newUri, cv, null, null);
            Log.d(LOG_TAG, "Update URI to dispatch: " + (newUri.toString()));*/

    }
    @Override

    protected void onDestroy() {
        super.onDestroy();

    }
    private void get_pref_data_from_sql(){
        Cursor cursor_dev_profile = getContentResolver().query(Uri.parse(MyContentProvider.DEV_PROFILE_CONTENT_URI+"/"+controller_id), null, null, null, null);
        if(cursor_dev_profile!=null){
            cursor_dev_profile.moveToFirst();
            l_control= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_LIGHT_CONTROL));
            t_control= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_T_CONTROL));
            h_control= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_H_CONTROL));
            pot1_control= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_POT1_CONTROL));
            pot2_control= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_POT2_CONTROL));
            pump1_control= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_PUMP1_CONTROL));
            pump2_control= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_PUMP2_CONTROL));
            relay1_control= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_RELAY1_CONTROL));
            relay2_control= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_RELAY2_CONTROL));
            water_control= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_WATER_CONTROL));
            cursor_dev_profile.close();
        }

        Cursor cursor_pref = getContentResolver().query(Uri.parse(MyContentProvider.PREF_CONTENT_URI+"/"+controller_id), null, null, null, null);
        if(cursor_pref!=null){
            cursor_pref.moveToFirst();
            version = cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_VERSION));

            t_min_value = String.valueOf(cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_T_MIN)));
            t_max_value = String.valueOf(cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_T_MAX)));
            

            h_min_value = String.valueOf(cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_H_MIN)));
            h_max_value = String.valueOf(cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_H_MAX)));
           

            pot1_h_min_value = String.valueOf(cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_POT1_H_MIN)));
            pot1_h_max_value = String.valueOf(cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_POT1_H_MAX)));
            

            pot2_h_min_value = String.valueOf(cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_POT2_H_MIN)));
            pot2_h_max_value = String.valueOf(cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_POT2_H_MAX)));
            
            
            wl_min_value = String.valueOf(cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_WL_MIN)));
            wl_max_value = String.valueOf(cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_WL_MAX)));
            
            l_notify_value = (cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_L_NOTIFY))!=0);
            t_notify_value = (cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_T_NOTIFY))!=0);
            h_notify_value = (cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_H_NOTIFY))!=0);
            pot1_notify_value = (cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_POT1_NOTIFY))!=0);
            pot2_notify_value = (cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_POT2_NOTIFY))!=0);
            wl_notify_value = (cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_WL_NOTIFY))!=0);
            pumps_notify_value = (cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_PUMPS_NOTIFY))!=0);
            relays_notify_value = (cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_RELAYS_NOTIFY))!=0);

            cursor_pref.close();
            Log.d(LOG_TAG, "T notify: " + t_notify_value);
            Log.d(LOG_TAG, "Version: : " + version);
        }
    }

    private void add_views_to_common_pref_layout(GridLayout gl, String parameter, int current_row){

        String s="";

        GridLayout.LayoutParams tv_label_layout_params = new GridLayout.LayoutParams();
        TextView tv_parameter_title = new TextView(this);

        tv_parameter_title.setTextAppearance(this, android.R.style.TextAppearance_Medium);
        tv_label_layout_params.columnSpec = GridLayout.spec(0,4);
        tv_label_layout_params.rowSpec = GridLayout.spec(current_row);
        tv_parameter_title.setLayoutParams(tv_label_layout_params);
        gl.addView(tv_parameter_title);

        parameter_notify = new CheckBox(this);
        parameter_notify.setText(getString(R.string.notification));

        gl.addView(parameter_notify);

        if(!parameter.equals("pumps_control") && !parameter.equals("relays_control") && !parameter.equals("l_control") ){
            // --------------------------------------------------
            TextView parameter_min_title = new TextView(this);
            parameter_min_title.setText(getString(R.string.min));
            gl.addView(parameter_min_title);
            parameter_min_value = new EditText(this);
            parameter_min_value.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            gl.addView(parameter_min_value);
            // --------------------------------------------------

            TextView parameter_max_title = new TextView(this);
            parameter_max_title.setText(getString(R.string.max));
            gl.addView(parameter_max_title);
            parameter_max_value = new EditText(this);
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
                parameter_min_value.setText(t_min_value);
                parameter_max_value.setText(t_max_value);
                if(t_notify_value) parameter_notify.setChecked(true); else parameter_notify.setChecked(false);

                //parameter_min_value.addTextChangedListener(watcher);
                //parameter_max_value.addTextChangedListener(watcher);

                break;

            case "h_control":
                parameter_min_value.setId(R.id.pref_et_hum_min);
                parameter_max_value.setId(R.id.pref_et_hum_max);
                parameter_notify.setId(R.id.pref_cb_hum_notify);
                s=getString(R.string.hum);
                parameter_min_value.setText(h_min_value);
                parameter_max_value.setText(h_max_value);
                if(h_notify_value) parameter_notify.setChecked(true); else parameter_notify.setChecked(false);
                break;

            case "pot1_h_control":
                parameter_min_value.setId(R.id.pref_et_pot1_h_min);
                parameter_max_value.setId(R.id.pref_et_pot1_h_max);
                parameter_notify.setId(R.id.pref_cb_pot1_h_notify);
                s=getString(R.string.pot1_hum);
                parameter_min_value.setText(pot1_h_min_value);
                parameter_max_value.setText(pot1_h_max_value);
                if(pot1_notify_value) parameter_notify.setChecked(true); else parameter_notify.setChecked(false);
                break;
            case "pot2_h_control":
                parameter_min_value.setId(R.id.pref_et_pot2_h_min);
                parameter_max_value.setId(R.id.pref_et_pot2_h_max);
                parameter_notify.setId(R.id.pref_cb_pot2_h_notify);
                s=getString(R.string.pot2_hum);
                parameter_min_value.setText(pot2_h_min_value);
                parameter_max_value.setText(pot2_h_max_value);
                if(pot2_notify_value) parameter_notify.setChecked(true); else parameter_notify.setChecked(false);
                break;
            case "wl_control":
                parameter_min_value.setId(R.id.pref_et_wl_min);
                parameter_max_value.setId(R.id.pref_et_wl_max);
                parameter_notify.setId(R.id.pref_cb_wl_notify);
                s=getString(R.string.water_level);
                parameter_min_value.setText(wl_min_value);
                parameter_max_value.setText(wl_max_value);
                if(wl_notify_value) parameter_notify.setChecked(true); else parameter_notify.setChecked(false);
                break;
            case "l_control":
                parameter_notify.setId(R.id.pref_cb_light_notify);
                s=getString(R.string.light_control);
                if(l_notify_value) parameter_notify.setChecked(true); else parameter_notify.setChecked(false);
                break;
            case "pumps_control":
                parameter_notify.setId(R.id.pref_cb_pumps_notify);
                s=getString(R.string.pumps_control);
                if(pumps_notify_value) parameter_notify.setChecked(true); else parameter_notify.setChecked(false);
                break;
            case "relays_control":
                parameter_notify.setId(R.id.pref_cb_relays_notify);
                s=getString(R.string.relays_control);
                if(relays_notify_value) parameter_notify.setChecked(true); else parameter_notify.setChecked(false);
                break;
        }
        tv_parameter_title.setText(s);

    }

}
