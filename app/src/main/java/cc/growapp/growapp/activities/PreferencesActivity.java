package cc.growapp.growapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.Toast;

import cc.growapp.growapp.DataBroker;
import cc.growapp.growapp.database.DatabaseHelper;
import cc.growapp.growapp.database.Dev_profile;
import cc.growapp.growapp.database.Preferences;
import cc.growapp.growapp.R;
import cc.growapp.growapp.fragments.Frag_PrefencesSlidingTabs;


public class PreferencesActivity extends AppCompatActivity implements
        DataBroker.save_user_profile.onSaveUserProfileComplete
{

    String LOG_TAG="GrowApp";

    SharedPreferences sPref;
    public static final String APP_PREFERENCES = "GrowAppSettings";
    String controller_id;
    String hash;

    // Database Helper
    DatabaseHelper db;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);


        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        hash = sPref.getString("hash", "");

        Intent intent = getIntent();
        controller_id = intent.getStringExtra("controller_id");

        db = new DatabaseHelper(this);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Frag_PrefencesSlidingTabs fragment = new Frag_PrefencesSlidingTabs();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.pref));

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

    public boolean SavePref(View v){
        Log.d(LOG_TAG, "Сохраняю настройки для контроллера ID:"+controller_id);

        if(!validateFields())return false;

        Spinner spinner = (Spinner) findViewById(R.id.pref_period_spinner);
        int spinner_index = spinner.getSelectedItemPosition();

        switch(spinner_index){
            case 0:sPref.edit().putInt("ServicePeriod", 60).apply();break;
            case 1:sPref.edit().putInt("ServicePeriod", 900).apply();break;
            case 2:sPref.edit().putInt("ServicePeriod", 3600).apply();break;
            case 3:sPref.edit().putInt("ServicePeriod", 7200).apply();break;
        }

        Log.d(LOG_TAG,"+"+sPref.getInt("ServicePeriod", 900));

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

        String t_notify_value = "0";
        String h_notify_value = "0";
        String pot1_notify_value = "0";
        String pot2_notify_value = "0";
        String wl_notify_value = "0";
        String all_notify_value = "0";
        String l_notify_value = "0";
        String relays_notify_value = "0";
        String pumps_notify_value = "0";


        //Выцепляем с базы, какие компоненты системы нам доступны
        Dev_profile dev_profile = db.getDevProfile(controller_id);
        //Log.d("Tag Name", dev_profile.getTagName());
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

        if(t_control!=0){
            EditText temp_max_view = (EditText) findViewById(R.id.pref_et_temp_max);
            EditText temp_min_view = (EditText) findViewById(R.id.pref_et_temp_min);
            CheckBox temp_notify = (CheckBox) findViewById(R.id.pref_cb_temp_notify);

            t_max_value = temp_max_view.getText().toString();
            t_min_value = temp_min_view.getText().toString();
            t_notify_value = String.valueOf(temp_notify.isChecked() ? 1 : 0);
        }


        if(h_control!=0){
            EditText hum_max_view = (EditText) findViewById(R.id.pref_et_hum_max);
            EditText hum_min_view = (EditText) findViewById(R.id.pref_et_hum_min);
            CheckBox hum_notify = (CheckBox) findViewById(R.id.pref_cb_hum_notify);
            
            h_max_value = hum_max_view.getText().toString();
            h_min_value = hum_min_view.getText().toString();
            h_notify_value = String.valueOf(hum_notify.isChecked() ? 1 : 0);
        }
        if(pot1_control!=0){
            EditText pot1_h_max_view = (EditText) findViewById(R.id.pref_et_pot1_h_max);
            EditText pot1_h_min_view = (EditText) findViewById(R.id.pref_et_pot1_h_min);
            CheckBox pot1_h_notify = (CheckBox) findViewById(R.id.pref_cb_pot1_h_notify);
            
            pot1_h_max_value = pot1_h_max_view.getText().toString();
            pot1_h_min_value = pot1_h_min_view.getText().toString();
            pot1_notify_value = String.valueOf(pot1_h_notify.isChecked() ? 1 : 0);
        }
        if(pot2_control!=0){
            EditText pot2_h_max_view = (EditText) findViewById(R.id.pref_et_pot2_h_max);
            EditText pot2_h_min_view = (EditText) findViewById(R.id.pref_et_pot2_h_min);
            CheckBox pot2_h_notify = (CheckBox) findViewById(R.id.pref_cb_pot2_h_notify);
            
            pot2_h_max_value = pot2_h_max_view.getText().toString();
            pot2_h_min_value = pot2_h_min_view.getText().toString();
            pot2_notify_value = String.valueOf(pot2_h_notify.isChecked() ? 1 : 0);
        }
        if(water_control!=0){
            EditText wl_max_view = (EditText) findViewById(R.id.pref_et_wl_max);
            EditText wl_min_view = (EditText) findViewById(R.id.pref_et_wl_min);
            CheckBox wl_notify = (CheckBox) findViewById(R.id.pref_cb_wl_notify);
            
            wl_max_value = wl_max_view.getText().toString();
            wl_min_value = wl_min_view.getText().toString();
            wl_notify_value = String.valueOf(wl_notify.isChecked() ? 1 : 0);
        }
        if(pump1_control!=0 || pump2_control!=0){
            CheckBox pumps_notify = (CheckBox) findViewById(R.id.pref_cb_pumps_notify);
            pumps_notify_value = String.valueOf(pumps_notify.isChecked() ? 1 : 0);
        }
        if(relay1_control!=0 || relay2_control!=0){
            CheckBox relays_notify = (CheckBox) findViewById(R.id.pref_cb_relays_notify);
            relays_notify_value = String.valueOf(relays_notify.isChecked()? 1 : 0);
        }
        if(l_control!=0){
            CheckBox light_notify = (CheckBox) findViewById(R.id.pref_cb_light_notify);
            l_notify_value = String.valueOf(light_notify.isChecked()? 1 : 0);
        }

        CheckBox all_notify = (CheckBox) findViewById(R.id.cb_notify_all);
        all_notify_value = String.valueOf(all_notify.isChecked() ? 1 : 0);


        new DataBroker.save_user_profile(this).execute(String.valueOf(controller_id), hash,
                t_max_value, t_min_value,
                h_max_value, h_min_value,
                pot1_h_max_value, pot1_h_min_value,
                pot2_h_max_value, pot2_h_min_value,
                wl_max_value, wl_min_value,
                all_notify_value,
                t_notify_value, h_notify_value,
                pot1_notify_value, pot2_notify_value,
                wl_notify_value, l_notify_value,
                relays_notify_value, pumps_notify_value);



        Preferences preference = new Preferences(controller_id,
                Integer.parseInt(t_max_value),
                Integer.parseInt(t_min_value),
                Integer.parseInt(h_max_value),
                Integer.parseInt(h_min_value),
                Integer.parseInt(pot1_h_max_value),
                Integer.parseInt(pot1_h_min_value),
                Integer.parseInt(pot2_h_max_value),
                Integer.parseInt(pot2_h_min_value),
                Integer.parseInt(wl_max_value),
                Integer.parseInt(wl_min_value),
                Integer.parseInt(all_notify_value),
                Integer.parseInt(t_notify_value),
                Integer.parseInt(h_notify_value),
                Integer.parseInt(pot1_notify_value),
                Integer.parseInt(pot2_notify_value),
                Integer.parseInt(wl_notify_value),
                Integer.parseInt(l_notify_value),
                Integer.parseInt(relays_notify_value),
                Integer.parseInt(pumps_notify_value));

        db.updateUserProfile(preference);
        db.closeDB();
        return true;
    }
    public void CancelPref(View v){
        Log.d(LOG_TAG, "Отмена");
        finish();
    }


    //----------------------------------------------------------------------------------------------

    @Override
    public void onSaveUserProfileCompleteMethod(String s) {
        if(s!=null){
            if(s.equals("answer=1")){
                Toast.makeText(this, "Настройки сохранены", Toast.LENGTH_SHORT).show();
                finish();
            } else Toast.makeText(this, "Настройки не сохранены, проверьте соединение с интернетом.", Toast.LENGTH_SHORT).show();
        }
    }


    //TODO: Оптимизировать функции, собрать в кучу
    //Данная функция получает текст с со всех полей EditText
    private EditText traverseEditTexts(ViewGroup v)
    {
        EditText invalid = null;
        for (int i = 0; i < v.getChildCount(); i++)
        {
            Object child = v.getChildAt(i);
            if (child instanceof EditText)
            {
                EditText e = (EditText)child;
                if(e.getText().length() == 0)    // Whatever logic here to determine if valid.
                {
                    return e;   // Stops at first invalid one. But you could add getActivity to a list.
                }
            }
            else if(child instanceof ViewGroup)
            {
                invalid = traverseEditTexts((ViewGroup)child);  // Recursive call.
                if(invalid != null)
                {
                    break;
                }
            }
        }
        return invalid;
    }
    // Данная функция проверяет поля EditText
    private boolean validateFields()
    {
        GridLayout myGridLayout = (GridLayout) findViewById(R.id.pref_gl);
        EditText emptyText = traverseEditTexts(myGridLayout);
        if(emptyText != null)
        {
            Toast.makeText(this, "Это поле не заполнено!", Toast.LENGTH_SHORT).show();
            emptyText.requestFocus();      // Scrolls view to this field.
        }
        return emptyText == null;
    }

}
