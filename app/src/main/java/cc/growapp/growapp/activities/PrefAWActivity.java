package cc.growapp.growapp.activities;


import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.GridLayout;
import android.widget.TextView;

import cc.growapp.growapp.GrowappConstants;
import cc.growapp.growapp.R;
import cc.growapp.growapp.database.MyContentProvider;


public class PrefAWActivity extends AppCompatActivity
{

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
    int saved_aw1;
    int saved_aw2;
    int pump_time;
    
    String LOG_TAG="PreferencesActivity";

    String hash;

    String controller_id;

    SharedPreferences sPref;

    TextView parameter_value;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pref_aw_activity);

        sPref = getSharedPreferences(GrowappConstants.APP_PREFERENCES, MODE_PRIVATE);
        hash = sPref.getString("hash", "");

        controller_id = getIntent().getStringExtra("controller_id");

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
            saved_aw1= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_AUTO_WATERING1));
            saved_aw2= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_AUTO_WATERING2));
            pump_time= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_PUMP_TIME));
            cursor_dev_profile.close();
        }

        //Наполним форму сохранеными настройками
        if(controller_id!=null && !controller_id.isEmpty()){

            //Наполним настройками
            GridLayout gl = (GridLayout) findViewById(R.id.pref_gl);
            if(gl!=null){
                gl.removeAllViews();
                int column = 2;
                int row = 0;
                int current_row=0;

                if(pot1_control!=0)row+=2;
                if(pot2_control!=0)row+=2;
                if(pump_time!=0)row+=2;

                TextView note = (TextView) findViewById(R.id.pref_tv_note);
                gl.setColumnCount(column);
                gl.setRowCount(row);
                if((pump1_control!=0 || pump2_control!=0)&&(pot1_control!=0 || pot2_control!=0))
                    note.setText(getString(R.string.auto_watering_note_on));
                else note.setText(getString(R.string.auto_watering_note_off));

                if(pot1_control!=0){
                    add_views_to_aw_pref_layout(gl, "auto_watering1", current_row);
                    current_row+=2;
                }
                if(pot2_control!=0){
                    add_views_to_aw_pref_layout(gl, "auto_watering2", current_row);
                    current_row+=2;
                }
                if((pot1_control!=0 || pot2_control!=0) && pump_time!=0){
                    add_views_to_aw_pref_layout(gl,  "pump_time", current_row);
                    //current_row+=2;
                }

            }
        }


        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getIntent().getStringExtra("controller_name")+" "+getString(R.string.auto_watering_pref));

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

    protected void onDestroy() {
        super.onDestroy();

    }

    // ------------------------------------------------------------------------------------
    private void add_views_to_aw_pref_layout(GridLayout gl, String parameter, int current_row){

        String s="";

        GridLayout.LayoutParams tv_label_layout_params = new GridLayout.LayoutParams();
        TextView tv_parameter_title = new TextView(this);

        tv_parameter_title.setTextAppearance(this, android.R.style.TextAppearance_Medium);
        tv_label_layout_params.columnSpec = GridLayout.spec(0,1);
        tv_label_layout_params.rowSpec = GridLayout.spec(current_row);
        tv_parameter_title.setLayoutParams(tv_label_layout_params);
        gl.addView(tv_parameter_title);




        // --------------------------------------------------
        parameter_value = new TextView(this);
        gl.addView(parameter_value);
        // --------------------------------------------------

        switch (parameter){
            case "auto_watering1":
                parameter_value.setId(R.id.pref_tv_auto_watering1);
                s=getString(R.string.pot1_hum);
                parameter_value.setText(String.valueOf(saved_aw1));
                break;
            case "auto_watering2":
                parameter_value.setId(R.id.pref_tv_auto_watering2);
                s=getString(R.string.pot2_hum);
                parameter_value.setText(String.valueOf(saved_aw2));
                break;
            case "pump_time":
                parameter_value.setId(R.id.pref_tv_pump_time);
                s=getString(R.string.pump_time);
                parameter_value.setText(String.valueOf(pump_time)+" second(s)");
                break;
        }
        tv_parameter_title.setText(s);

    }
}
