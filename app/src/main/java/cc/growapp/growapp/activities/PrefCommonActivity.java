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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import cc.growapp.growapp.GrowappConstants;
import cc.growapp.growapp.R;
import cc.growapp.growapp.database.MyContentProvider;


public class PrefCommonActivity extends AppCompatActivity
{


    String LOG_TAG="PreferencesActivity";

    String hash;
    int saved_period,version;
    String saved_sound;
    String controller_id;
    String saved_vibrator_type;
    int saved_color;


    int period;
    String sound;
    String vibrator_type;
    int color;



    CheckBox all_notify;


    SharedPreferences sPref;



    String[] periodSpinnerArray;
    int spinner_index;
    Spinner spinner;
    TextView tv_ringtone_title,tv_vibrator_title,tv_notifcolor;

    boolean is_modified=false;
    boolean saved_all_notify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pref_common_activity);

        sPref = getSharedPreferences(GrowappConstants.APP_PREFERENCES, MODE_PRIVATE);

        hash = sPref.getString("hash", "");
        controller_id = getIntent().getStringExtra("controller_id");


        Cursor cursor_pref = getContentResolver().query(Uri.parse(MyContentProvider.PREF_CONTENT_URI+"/"+controller_id), null, null, null, null);
        if(cursor_pref!=null) {
            if(cursor_pref.moveToFirst()){
                version = cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_VERSION));
                saved_period = cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_PERIOD));
                saved_sound = cursor_pref.getString(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_SOUND));
                saved_vibrator_type = cursor_pref.getString(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_VIBRATE));
                saved_color = cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_COLOR));
                saved_all_notify=(cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_ALL_NOTIFY))!=0);



                Log.d(LOG_TAG,"All notify: "+saved_all_notify);
                all_notify = (CheckBox) findViewById(R.id.cb_notify_all);
                if(saved_all_notify)all_notify.setChecked(true); else all_notify.setChecked(false);

                if(saved_sound==null) {
                    Uri default_sound_uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    saved_sound=String.valueOf(default_sound_uri);
                }

                //Need, if nothing was changed
                period=saved_period;
                sound=saved_sound;
                vibrator_type=saved_vibrator_type;
                color=saved_color;

                String ringtone_title = RingtoneManager.getRingtone(this, Uri.parse(saved_sound)).getTitle(this);


                String color_name="Green";
                switch (saved_color){
                    case -65536:color_name="Red";break;
                    case -16711936:color_name="Green";break;
                    case -256:color_name="Yellow";break;
                    case -16776961:color_name="Blue";break;
                }


                Log.d(LOG_TAG,"Version: "+version);
                Log.d(LOG_TAG,"Period: "+saved_period);
                Log.d(LOG_TAG,"Ringtone title: "+ringtone_title);
                Log.d(LOG_TAG,"Vibrator type: "+saved_vibrator_type);
                Log.d(LOG_TAG,"Color: "+color_name);

                tv_ringtone_title = (TextView) findViewById(R.id.pref_tv_setnotifsound);
                tv_ringtone_title.setText(ringtone_title);

                tv_vibrator_title = (TextView) findViewById(R.id.pref_tv_setnotifvibrate);
                tv_vibrator_title.setText(saved_vibrator_type);

                tv_notifcolor = (TextView) findViewById(R.id.pref_tv_setnotifcolor);
                tv_notifcolor.setText(color_name);


                spinner = (Spinner) findViewById(R.id.pref_period_spinner);

                periodSpinnerArray= new String[]{
                        getString(R.string.min1),
                        getString(R.string.min15),
                        getString(R.string.hour),
                        getString(R.string.hour2)
                };

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, periodSpinnerArray);

                spinner.setAdapter(adapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        Log.d(LOG_TAG, "Selected period: " + spinner.getSelectedItem());
                        spinner_index = spinner.getSelectedItemPosition();
                    }

                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                int index=0;
                switch (saved_period){
                    case 60:index=0;break;
                    case 900:index=1;break;
                    case 3600:index=2;break;
                    case 7200:index=3;break;
                }
                spinner.setSelection(index);
            }
            cursor_pref.close();



        }


        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getIntent().getStringExtra("controller_name")+" "+getString(R.string.common_pref));

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
        Spinner spinner = (Spinner) findViewById(R.id.pref_period_spinner);
        int spinner_index = spinner.getSelectedItemPosition();

        switch(spinner_index){
            case 0:period=60;break;
            case 1:period=900;break;
            case 2:period=3600;break;
            case 3:period=7200;break;
        }

        all_notify = (CheckBox) findViewById(R.id.cb_notify_all);
        int all_notify_value = all_notify.isChecked() ? 1 : 0;

        Log.d(LOG_TAG, "all_notify_value : " + all_notify_value );
        Log.d(LOG_TAG, "saved_all_notify : " + (saved_all_notify ? 1 : 0));

        Log.d(LOG_TAG, "sound: " + sound );
        Log.d(LOG_TAG, "saved_sound: " + saved_sound );


        if(period!=saved_period)is_modified = true;
        if(!sound.equals(saved_sound))is_modified = true;
        if(!vibrator_type.equals(saved_vibrator_type))is_modified = true;
        if(color!=saved_color)is_modified = true;
        if(all_notify_value!=(saved_all_notify ? 1 : 0))is_modified = true;

        if(is_modified){
            version++;
            Log.d(LOG_TAG, "Saving notif preferences in internal DB, version: " + version);


            ContentValues cv = new ContentValues();
            cv.put(MyContentProvider.KEY_PREF_CTRL_ID, controller_id);
            cv.put(MyContentProvider.KEY_PREF_VERSION, version);
            cv.put(MyContentProvider.KEY_PREF_ALL_NOTIFY, all_notify_value);
            cv.put(MyContentProvider.KEY_PREF_PERIOD, period);
            cv.put(MyContentProvider.KEY_PREF_SOUND, sound);
            cv.put(MyContentProvider.KEY_PREF_VIBRATE, vibrator_type);
            cv.put(MyContentProvider.KEY_PREF_COLOR, color);

            Uri newUri = ContentUris.withAppendedId(MyContentProvider.PREF_CONTENT_URI, Long.parseLong(controller_id));
            int cnt = getContentResolver().update(newUri, cv, null, null);
            Log.d(LOG_TAG, "Update URI to dispatch: " + (newUri.toString()));

        }


    }

    @Override

    protected void onDestroy() {
        super.onDestroy();

    }

    // ---------------- Choosing notification sound ---------------------------
    public void SetNotifSound(View v){
        Log.d(LOG_TAG, "Choosing a notification sound");
        Intent intent=new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        //intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        //intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
        // for existing ringtone


                //sPref.getString("RingTone", String.valueOf(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)));
        if (saved_sound!= null) {
            if (saved_sound.length() == 0) {
                // Select "Silent"
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
            } else {
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(saved_sound));
            }
        } else {
            // No ringtone has been selected, set to the default
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Settings.System.DEFAULT_NOTIFICATION_URI);
        }
        this.startActivityForResult(intent, 1);

    }
    // ---------------- Choosing notification vibrate ---------------------------
    public void SetNotifVibrate(View v){
        Log.d(LOG_TAG, "Choosing the vibration for notification");
        // -------------------------------- Set vibrate dialog ----------------------------
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.pref);
        final View dialogView = inflater.inflate(R.layout.dialog_select_vibarator, rl,false);

        dialogBuilder.setView(dialogView);

        final RadioButton no_vibrator = (RadioButton) dialogView.findViewById(R.id.dialog_no_vibration);
        final RadioButton short_vibrator = (RadioButton) dialogView.findViewById(R.id.dialog_short_vibration);
        final RadioButton long_vibrator = (RadioButton) dialogView.findViewById(R.id.dialog_long_vibration);




        //sPref.getString("Vibrator", "Short");

        switch (saved_vibrator_type){
            case "No":no_vibrator.setChecked(true);break;
            case "Short":short_vibrator.setChecked(true);break;
            case "Long":long_vibrator.setChecked(true);break;

        }

        dialogBuilder.setTitle("Вибрация");
        //dialogBuilder.setMessage("Новое название");
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {



                if (no_vibrator.isChecked()) vibrator_type = "No";
                if (short_vibrator.isChecked()) vibrator_type = "Short";
                if (long_vibrator.isChecked()) vibrator_type = "Long";

                Log.d(LOG_TAG, "Vibrator type: " + vibrator_type);

                TextView tv_vibrator_title = (TextView) findViewById(R.id.pref_tv_setnotifvibrate);
                tv_vibrator_title.setText(vibrator_type);
            }
        });
        dialogBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });

        AlertDialog b = dialogBuilder.create();
        b.show();
    }
    // ---------------- Choosing notification color ---------------------------
    public void SetNotifColor(View v){
        Log.d(LOG_TAG, "Choosing the color for notification");
        // -------------------------------- Set color dialog ----------------------------
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.pref);
        final View dialogView = inflater.inflate(R.layout.dialog_select_color, rl,false);

        dialogBuilder.setView(dialogView);

        final RadioButton rbtn_red = (RadioButton) dialogView.findViewById(R.id.dialog_red);
        final RadioButton rbtn_green = (RadioButton) dialogView.findViewById(R.id.dialog_green);
        final RadioButton rbtn_yellow = (RadioButton) dialogView.findViewById(R.id.dialog_yellow);
        final RadioButton rbtn_blue = (RadioButton) dialogView.findViewById(R.id.dialog_blue);

        //sPref.getInt("NotifColor", -16711936);

        switch(saved_color){
            case -65536:rbtn_red.setChecked(true);break;
            case -16711936:rbtn_green.setChecked(true);break;
            case -256:rbtn_yellow.setChecked(true);break;
            case -16776961:rbtn_blue.setChecked(true);break;

        }


        dialogBuilder.setTitle("Выберите цвет");
        //dialogBuilder.setMessage("Новое название");
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {


                if (rbtn_red.isChecked()) color = -65536;
                if (rbtn_green.isChecked()) color = -16711936;
                if (rbtn_yellow.isChecked()) color = -256;
                if (rbtn_blue.isChecked()) color = -16776961;
                String color_name="Green";
                switch (color){
                    case -65536:color_name="Red";break;
                    case -16711936:color_name="Green";break;
                    case -256:color_name="Yellow";break;
                    case -16776961:color_name="Blue";break;
                }

                Log.d(LOG_TAG, "Notification color: " + color_name);
                TextView tv_color_title = (TextView) findViewById(R.id.pref_tv_setnotifcolor);
                tv_color_title.setText(color_name);
            }
        });
        dialogBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });

        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    Parcelable ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    Log.d(LOG_TAG, "Notification sound is: " + ringtone);
                    //sPref.edit().putString("RingTone", String.valueOf(ringtone)).apply();
                    TextView tv_ringtone_title = (TextView) this.findViewById(R.id.pref_tv_setnotifsound);
                    tv_ringtone_title.setText(RingtoneManager.getRingtone(this, (Uri) ringtone).getTitle(this));
                    sound=String.valueOf(ringtone);
                    // Toast.makeText(getBaseContext(),RingtoneManager.URI_COLUMN_INDEX,
                    // Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }


}
