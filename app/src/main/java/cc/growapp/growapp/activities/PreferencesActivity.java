package cc.growapp.growapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import cc.growapp.growapp.GrowappConstants;
import cc.growapp.growapp.R;


public class PreferencesActivity extends AppCompatActivity

{

    String LOG_TAG="PreferencesActivity";

    SharedPreferences sPref;

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






        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getIntent().getStringExtra("controller_name")+" "+getString(R.string.pref));

        }
    }
    protected void onResume() {
        Log.d(LOG_TAG, "Resuming PrefActivity");
        super.onResume();


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


}
