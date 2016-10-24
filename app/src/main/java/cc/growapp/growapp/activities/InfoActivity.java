package cc.growapp.growapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import cc.growapp.growapp.R;
import cc.growapp.growapp.services.BackgroundService;

public class InfoActivity extends AppCompatActivity  {
    private static final String LOG_TAG = "GrowApp";
    SharedPreferences sPref;
    public static final String APP_PREFERENCES = "GrowAppSettings";


    // Database Helper
    //DatabaseHelper db;
    BroadcastReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);


        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.info));
        }

        final TextView tv_servicetime = (TextView) findViewById(R.id.info_tv_serviceStartTime);
        TextView tv_serviceperiod = (TextView) findViewById(R.id.info_tv_period);

        sPref = getSharedPreferences(APP_PREFERENCES,MODE_PRIVATE);
        final String ServiceStartAt = sPref.getString("ServiceStartAt","");
        int ServicePeriod = sPref.getInt("ServicePeriod", 60);
        switch (ServicePeriod){
            case 15:tv_serviceperiod.setText(getString(R.string.min15));break;
            case 30:tv_serviceperiod.setText(getString(R.string.min30));break;
            case 60:tv_serviceperiod.setText(getString(R.string.hour));break;
            case 120:tv_serviceperiod.setText(getString(R.string.hour2));break;
        }

        if(!ServiceStartAt.isEmpty())tv_servicetime.setText(ServiceStartAt);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra(BackgroundService.MESSAGE);
                //Log.d(LOG_TAG, "------------------------------------------------------------");
                if(!s.isEmpty())tv_servicetime.setText(s);
            }
        };


    }

    public void ServiceStart(View v){
        startService(new Intent(this, BackgroundService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver((receiver),
                new IntentFilter(BackgroundService.ACTION)
        );
    }

    @Override
    protected void onStop() {
        unregisterReceiver(receiver);
        super.onStop();
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


}
