package cc.growapp.growapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import cc.growapp.growapp.DataBroker;
import cc.growapp.growapp.GrowappConstants;
import cc.growapp.growapp.R;
import cc.growapp.growapp.database.MyContentProvider;


public class PreferencesActivity extends AppCompatActivity implements 
        DataBroker.save_user_profile.onSaveUserProfileComplete

{

    String LOG_TAG="PreferencesActivity";

    SharedPreferences sPref;
    int pref_version;
    String controller_id;
    String hash;





    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);


        sPref = getSharedPreferences(GrowappConstants.APP_PREFERENCES, MODE_PRIVATE);
        hash = sPref.getString("hash", "");

        Intent intent = getIntent();
        controller_id = intent.getStringExtra("controller_id");

        Cursor cursor_pref = getContentResolver().query(Uri.parse(MyContentProvider.PREF_CONTENT_URI + "/" + controller_id), null, null, null, null);

        if(cursor_pref!=null) {
            if(cursor_pref.moveToFirst()){
                pref_version = cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_VERSION));
            }
        }




        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getIntent().getStringExtra("controller_name")+" "+getString(R.string.pref));

        }
    }
    protected void onResume() {
        Log.d(LOG_TAG, "Resuming PrefActivity");
        super.onResume();

        Cursor cursor_pref = getContentResolver().query(Uri.parse(MyContentProvider.PREF_CONTENT_URI + "/" + controller_id), null, null, null, null);
        if(cursor_pref!=null){
            if(cursor_pref.moveToFirst()){
                int new_version = cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_VERSION));

                if(pref_version!=new_version){
                    String t_min_value = cursor_pref.getString(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_T_MIN));
                    String t_max_value = cursor_pref.getString(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_T_MAX));


                    String h_min_value = cursor_pref.getString(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_H_MIN));
                    String h_max_value = cursor_pref.getString(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_H_MAX));


                    String pot1_h_min_value = cursor_pref.getString(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_POT1_H_MIN));
                    String pot1_h_max_value = cursor_pref.getString(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_POT1_H_MAX));


                    String pot2_h_min_value = cursor_pref.getString(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_POT2_H_MIN));
                    String pot2_h_max_value = cursor_pref.getString(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_POT2_H_MAX));


                    String wl_min_value = cursor_pref.getString(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_WL_MIN));
                    String wl_max_value = cursor_pref.getString(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_WL_MAX));

                    String l_notify_value = (cursor_pref.getString(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_L_NOTIFY)));
                    String t_notify_value = (cursor_pref.getString(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_T_NOTIFY)));
                    String h_notify_value = (cursor_pref.getString(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_H_NOTIFY)));
                    String pot1_notify_value = (cursor_pref.getString(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_POT1_NOTIFY)));
                    String pot2_notify_value = (cursor_pref.getString(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_POT2_NOTIFY)));
                    String wl_notify_value = (cursor_pref.getString(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_WL_NOTIFY)));
                    String pumps_notify_value = (cursor_pref.getString(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_PUMPS_NOTIFY)));
                    String relays_notify_value = (cursor_pref.getString(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_RELAYS_NOTIFY)));
                    String all_notify_value= (cursor_pref.getString(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_ALL_NOTIFY)));

                    String period = cursor_pref.getString(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_PERIOD));
                    //String sound = cursor_pref.getString(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_SOUND));
                    String vibrator_type = cursor_pref.getString(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_VIBRATE));
                    String color = cursor_pref.getString(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_COLOR));

                    cursor_pref.close();
                    Log.d(LOG_TAG, "pref_version: " + pref_version);
                    Log.d(LOG_TAG, "version: " + new_version);
                    //Log.d(LOG_TAG, "Version: : " + version);

                    String saved_hostname = sPref.getString("hostname", GrowappConstants.DEFAULT_HOSTNAME);

                    new DataBroker.save_user_profile(this).execute(saved_hostname,controller_id, hash,
                            t_max_value, t_min_value,
                            h_max_value, h_min_value,
                            pot1_h_max_value, pot1_h_min_value,
                            pot2_h_max_value, pot2_h_min_value,
                            wl_max_value, wl_min_value,
                            all_notify_value,
                            t_notify_value, h_notify_value,
                            pot1_notify_value, pot2_notify_value,
                            wl_notify_value, l_notify_value,
                            relays_notify_value, pumps_notify_value, period, vibrator_type, color);


                    Toast.makeText(this, R.string.save, Toast.LENGTH_SHORT).show();

                    pref_version=new_version;
                }

            }

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

    public void startCommonPrefActivity(View v){
        Intent intent = new Intent(PreferencesActivity.this,PrefCommonActivity.class);
        intent.putExtra("controller_id",controller_id);
        intent.putExtra("controller_name",getIntent().getStringExtra("controller_name"));
        startActivity(intent);
        Log.d(LOG_TAG, "Switching to PrefCommonActivity");
    }

    public void startCritPrefActivity(View v){
        Intent intent = new Intent(PreferencesActivity.this,PrefCriticalActivity.class);
        intent.putExtra("controller_id",controller_id);
        intent.putExtra("controller_name",getIntent().getStringExtra("controller_name"));
        startActivity(intent);
        Log.d(LOG_TAG, "Switching to PrefCriticalActivity");
    }
    public void startAWPrefActivity(View v){
        Intent intent = new Intent(PreferencesActivity.this,PrefAWActivity.class);
        intent.putExtra("controller_id",controller_id);
        intent.putExtra("controller_name",getIntent().getStringExtra("controller_name"));
        startActivity(intent);
        Log.d(LOG_TAG, "Switching to PrefAWActivity");
    }
    public void startThemePrefActivity(View v){
        Toast.makeText(getBaseContext(),"startThemePrefActivity", Toast.LENGTH_SHORT).show();
    }
    public void startAccountPrefActivity(View v){
        Intent account_intent = new Intent(PreferencesActivity.this,AccountActivity.class);
        startActivity(account_intent);
        Log.d(LOG_TAG, "Switching to AccountActivity");
    }


    @Override
    public void onSaveUserProfileCompleteMethod(String s) {
        if(s!=null){
            Toast.makeText(this,R.string.saved,Toast.LENGTH_SHORT).show();
        }
    }
}
