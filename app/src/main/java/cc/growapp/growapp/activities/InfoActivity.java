package cc.growapp.growapp.activities;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

import cc.growapp.growapp.R;
import cc.growapp.growapp.database.MyContentProvider;
import cc.growapp.growapp.services.BackgroundService;

import static cc.growapp.growapp.activities.MainActivity.active;


public class InfoActivity extends AppCompatActivity  {


    private static final String LOG_TAG = "InfoActivity";

    String controller_id,controller_name;


    TextView tv_serviceStartAt,tv_serviceperiod;
    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        InfoObserver InfoObserver = new InfoObserver(new Handler());
        getContentResolver().registerContentObserver(MyContentProvider.LOCAL_CONTENT_URI, true,InfoObserver);

        controller_id = getIntent().getStringExtra("controller_id");
        controller_name = getIntent().getStringExtra("controller_name");




        tv_serviceStartAt = findViewById(R.id.info_tv_serviceStartTime);
        tv_serviceperiod = findViewById(R.id.info_tv_period);


        Cursor cursor_pref = getContentResolver().query(Uri.parse(MyContentProvider.PREF_CONTENT_URI + "/" + controller_id), null, null, null, null);
        if(cursor_pref!=null) {
            cursor_pref.moveToFirst();
            String period = String.valueOf(cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_PERIOD)));

            cursor_pref.close();

            switch (period){
                case "60":tv_serviceperiod.setText(getString(R.string.min1));break;
                case "900":tv_serviceperiod.setText(getString(R.string.min15));break;
                case "3600":tv_serviceperiod.setText(getString(R.string.hour));break;
                case "7200":tv_serviceperiod.setText(getString(R.string.hour2));break;
            }
        }



        //if(!ServiceStartAt.isEmpty())


        DataPut();
        //tv_serviceLastStartAt.setText(new Date(ServiceLastStartAt).toString());

        /*receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Long start_time = intent.getLongExtra(BackgroundService.START_TIME,0);
                Long last_start = intent.getLongExtra(BackgroundService.LAST_TIME,0);
                //Log.d(LOG_TAG, "------------------------------------------------------------");
                tv_serviceStartAt.setText(new Date(start_time).toString());
                tv_serviceLastStartAt.setText(new Date(last_start).toString());
            }
        };
*/

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getIntent().getStringExtra("controller_name")+" "+getString(R.string.info));
        }


    }

    public void CrashApp(View v){

        throw new RuntimeException("App is crashed");
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {

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

    public void ServiceStart(View view) {

        Log.d(LOG_TAG,"Starting growapp_service.");
        Intent intent = new Intent(this, BackgroundService.class);
        startService(intent);
    }


    private class InfoObserver extends ContentObserver {
        InfoObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            Log.d(LOG_TAG, "Change detected!");
            DataPut();

        }

    }

    void DataPut(){
        long ServiceStartAt=0;
        Cursor cursor_local = getContentResolver().query(Uri.parse(MyContentProvider.LOCAL_CONTENT_URI + "/" + controller_id), null, null, null, null);
        if(cursor_local!=null) {
            if(cursor_local.moveToFirst())
                ServiceStartAt = Long.parseLong(cursor_local.getString(cursor_local.getColumnIndexOrThrow(MyContentProvider.KEY_LOCAL_START_TIME)));
            cursor_local.close();
        }
        tv_serviceStartAt.setText(new Date(ServiceStartAt).toString());
    }

}
