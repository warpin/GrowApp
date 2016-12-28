package cc.growapp.growapp.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import cc.growapp.growapp.DataBroker;
import cc.growapp.growapp.GrowappConstants;
import cc.growapp.growapp.JSONHandler;
import cc.growapp.growapp.R;
import cc.growapp.growapp.activities.MainActivity;
import cc.growapp.growapp.database.MyContentProvider;

public class BackgroundService extends IntentService implements
        DataBroker.get_system_state.onGetSystemStateComplete
{

    String LOG_TAG = "GrowAppService";
    NotificationManager nm;
    SharedPreferences sPref;
    int period;
    int version;
    String vibrator_type,ringtone_title,color_name,sound;
    int color;
    long[] vibrate;
    private int notif_id = 0;
    long start_time;
    String controller_id;

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

    int t_min_value,t_max_value,h_min_value,h_max_value,pot1_h_min_value,pot1_h_max_value,pot2_h_min_value,pot2_h_max_value;
    int wl_min_value,wl_max_value;
    boolean l_notify_value,t_notify_value,h_notify_value,pot1_notify_value,pot2_notify_value,wl_notify_value,pumps_notify_value,relays_notify_value,all_notify_value;

    int l_result,t_result,h_result,pot1_h_result,pot2_h_result,p1_result,p2_result,relay1_result,relay2_result,w_result;
    String date_result;

    public BackgroundService() {
        super("GrowApp service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "Service initialization!");
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        String emergency_ctrl_id = intent.getStringExtra("controller_id");
        Boolean emergency_call = intent.getBooleanExtra("emergency_call",false);
        intent.removeExtra("emergency_call");
        //intent.removeExtra("controller_id");

        long current_time =  System.currentTimeMillis();



        /*service.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + period * 1000,
                period * 1000, pending);
*/
        //Запишем в SP, для информации, когда запустится сервис
        long earliest_time=0;
        long period_in_ms=0;

        Cursor cursor_local = getContentResolver().query(MyContentProvider.LOCAL_CONTENT_URI, null, null, null, null);
        if(cursor_local!=null) {
            if (cursor_local.moveToFirst()) {
                do {
                    controller_id = cursor_local.getString(cursor_local.getColumnIndexOrThrow(MyContentProvider.KEY_LOCAL_CTRL_ID));
                    start_time = Long.parseLong(cursor_local.getString(cursor_local.getColumnIndexOrThrow(MyContentProvider.KEY_LOCAL_START_TIME)));
                    //earliest_time=start_time;
                    Cursor cursor_pref = getContentResolver().query(Uri.parse(MyContentProvider.PREF_CONTENT_URI+"/"+controller_id), null, null, null, null);
                    if(cursor_pref!=null) {
                        if(cursor_pref.moveToFirst()){
                            period = cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_PERIOD));
                            if(emergency_call && emergency_ctrl_id.equals(controller_id))period=GrowappConstants.get_data_timeout;
                            period_in_ms=period*1000;
                        }
                        cursor_pref.close();
                    }


                    Log.d(LOG_TAG, "-------------------------------------------------");
                    Log.d(LOG_TAG, "Controller ID: "+controller_id);
                    Log.d(LOG_TAG, "Service start time: "+ new Date(start_time));
                    Log.d(LOG_TAG, "Current time: " + new Date(current_time));
                    Log.d(LOG_TAG, "Period: " + period);


                    //Ordering is important
                    //1 Если время старта, больше чем текущее время + период
                    long diffrence = current_time - start_time;
                    Log.d(LOG_TAG, "Difference: "+ diffrence + " Period in ms: "+ period_in_ms);

                    //period*1000
                    if(start_time==0 || diffrence==0 || Math.abs(diffrence)>=period_in_ms || start_time<current_time){

                        //Обрабатываем контроллер
                        Log.d(LOG_TAG, "Set new start time for the device: "+ controller_id);
                        // И его время старта

                        start_time=current_time+period_in_ms;
                        Log.d(LOG_TAG, "New start time : "+ new Date(start_time));

                        ContentValues cv = new ContentValues();
                        cv.put("ctrl_id", controller_id);
                        cv.put("start_time", String.valueOf(start_time));

                        Uri newUri = ContentUris.withAppendedId(MyContentProvider.LOCAL_CONTENT_URI, Long.parseLong(controller_id));
                        int cnt = getContentResolver().update(newUri, cv, null, null);
                        Log.d(LOG_TAG, "Rows updated: " + cnt);


                        //Запускаем процесс получения данных датчиков
                        sPref = getSharedPreferences(GrowappConstants.APP_PREFERENCES, MODE_PRIVATE);
                        String hash = sPref.getString("hash", "");

                        Toast.makeText(this, controller_id, Toast.LENGTH_SHORT).show();
                        new DataBroker.get_system_state(this).execute(String.valueOf(controller_id), hash);

                    } else Log.d(LOG_TAG, "Skipping the device: "+ controller_id);
                    //2 Но если есть старт тайм меньше или равно 0 или прошлого минимального
                    // а также больше текущего
                    // то старт тайм = минимальному

                    if((start_time<earliest_time && start_time>current_time) || earliest_time==0)earliest_time=start_time;
                    else start_time=earliest_time;


                }while (cursor_local.moveToNext());
                cursor_local.close();
            }else{


                Cursor cursor = getContentResolver().query(MyContentProvider.CTRLS_CONTENT_URI, null, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            String ctrl_id = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.KEY_CTRL_ID));
                            String ctrl_name = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.KEY_CTRL_NAME));
                            String ctrl_an = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.KEY_CTRL_AN));

                            Log.d(LOG_TAG, "Fill start time rows for ctrl_id: " + ctrl_id);
                            Log.d(LOG_TAG, "Current time: " + current_time);


                            ContentValues cv = new ContentValues();
                            cv.put("ctrl_id", ctrl_id);
                            cv.put("start_time", String.valueOf(start_time));

                            Uri newUri = getContentResolver().insert(MyContentProvider.LOCAL_CONTENT_URI, cv);
                            Log.d(LOG_TAG, "URI to dispatch: " + (newUri != null ? newUri.toString() : null));
                            // do what ever you want here
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }

            }
            AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            PendingIntent pending = PendingIntent.getService(this, 0, new Intent(this, BackgroundService.class) , PendingIntent.FLAG_CANCEL_CURRENT);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarm.setExact(AlarmManager.RTC_WAKEUP, start_time, pending);
            } else alarm.set(AlarmManager.RTC_WAKEUP, start_time, pending);
            //Запишем в SP, для информации, когда запустится сервис
            Log.d(LOG_TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!Next start time: " + new Date(start_time));
        }


        sPref = getSharedPreferences(GrowappConstants.APP_PREFERENCES, MODE_PRIVATE);

        return START_NOT_STICKY;
    }




    void sendNotif(String ctrl_id, String message) {
        Log.d(LOG_TAG, "Starting notification");


        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("controller_id", ctrl_id);

        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Context context = getApplicationContext();
        Resources res = context.getResources();
        Notification.Builder builder = new Notification.Builder(context);

        Cursor cursor_pref = getContentResolver().query(Uri.parse(MyContentProvider.PREF_CONTENT_URI + "/" + ctrl_id), null, null, null, null);
        if(cursor_pref!=null) {
            cursor_pref.moveToFirst();
            sound = cursor_pref.getString(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_SOUND));
            vibrator_type = cursor_pref.getString(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_VIBRATE));
            color = cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_COLOR));

            ringtone_title = RingtoneManager.getRingtone(this,Uri.parse(sound)).getTitle(this);

            color_name="Green";
            switch (color){
                case -65536:color_name="Red";break;
                case -16711936:color_name="Green";break;
                case -256:color_name="Yellow";break;
                case -16776961:color_name="Blue";break;
            }


            switch(vibrator_type){
                case "No":vibrate= new long[] {0};break;
                case "Short":vibrate= new long[] {1000,1000};break;
                case "Long":vibrate= new long[] {1000,1000,1000,1000};break;
            }
            cursor_pref.close();
        }


        //Достанем имя контроллера с БД
        Cursor cursor = getContentResolver().query(Uri.parse(MyContentProvider.CTRLS_CONTENT_URI+"/"+ctrl_id), null, null, null, null);
        if(cursor!=null) {
            cursor.moveToFirst();
            String ctrl_name = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.KEY_CTRL_NAME));
            cursor.close();

            

            builder.setContentIntent(pIntent)
                    .setSmallIcon(R.drawable.ic_stat_notify)
                            // большая картинка
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_hdpi_72))
                            //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
                    .setTicker("Уведомление GrowApp")
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                            //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                    .setContentTitle("Устройство : " + ctrl_name)
                            //.setContentText(res.getString(R.string.notifytext))
                    .setContentText(message); // Текст уведомления*/



            //Notification notification = builder.getNotification(); // до API 16
            Notification notification = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                notification = builder.build();

                //notification.defaults = Notification.DEFAULT_ALL;
                notification.sound = Uri.parse(sound);
                notification.vibrate = vibrate;
                notification.ledARGB = color;
                notification.ledOffMS = 1000;
                notification.ledOnMS = 1000;
                notification.flags = notification.flags | Notification.FLAG_SHOW_LIGHTS;
            }


            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notif_id, notification);
            //Если уведомления должны быть уникальны
            notif_id++;

        }




    }



    @Override
    public void onGetSystemStateCompleteMethod(String s) {

        Log.d(LOG_TAG, "Callback, system state: " + s);
        if(s!=null){
            ContentValues result= new JSONHandler().ParseJSONSystemState(s);
            if(result!=null){

                String ctrl_id = result.getAsString("ctrl_id");
                int light_state = Integer.parseInt(result.getAsString("light_state"));
                int t = Integer.parseInt(result.getAsString("t"));
                int h = Integer.parseInt(result.getAsString("h"));
                int pot1_h = Integer.parseInt(result.getAsString("pot1_h"));
                int pot2_h = Integer.parseInt(result.getAsString("pot2_h"));
                int relay1_state = Integer.parseInt(result.getAsString("relay1_state"));
                int relay2_state = Integer.parseInt(result.getAsString("relay2_state"));
                int pump1_state = Integer.parseInt(result.getAsString("pump2_state"));
                int pump2_state = Integer.parseInt(result.getAsString("pump2_state"));
                int water_level = Integer.parseInt(result.getAsString("water_level"));
                String date = result.getAsString("date");


                Cursor cursor_dev_profile = getContentResolver().query(Uri.parse(MyContentProvider.DEV_PROFILE_CONTENT_URI + "/" + ctrl_id), null, null, null, null);
                if(cursor_dev_profile!=null){
                    if(cursor_dev_profile.moveToFirst()){
                        l_control = cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_LIGHT_CONTROL));
                        t_control= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_T_CONTROL));
                        h_control= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_H_CONTROL));
                        pot1_control= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_POT1_CONTROL));
                        pot2_control= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_POT2_CONTROL));
                        pump1_control= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_PUMP1_CONTROL));
                        pump2_control= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_PUMP2_CONTROL));
                        relay1_control= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_RELAY1_CONTROL));
                        relay2_control= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_RELAY2_CONTROL));
                        water_control= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_WATER_CONTROL));
                    }

                    cursor_dev_profile.close();
                }

                Cursor cursor_pref = getContentResolver().query(Uri.parse(MyContentProvider.PREF_CONTENT_URI + "/" + ctrl_id), null, null, null, null);
                if(cursor_pref!=null){
                    if(cursor_pref.moveToFirst()){
                        //version = cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_VERSION));


                        t_min_value = cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_T_MIN));
                        t_max_value = cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_T_MAX));


                        h_min_value = cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_H_MIN));
                        h_max_value = cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_H_MAX));


                        pot1_h_min_value = cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_POT1_H_MIN));
                        pot1_h_max_value = cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_POT1_H_MAX));


                        pot2_h_min_value = cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_POT2_H_MIN));
                        pot2_h_max_value = cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_POT2_H_MAX));


                        wl_min_value = cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_WL_MIN));
                        wl_max_value = cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_WL_MAX));

                        l_notify_value = (cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_L_NOTIFY))!=0);
                        t_notify_value = (cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_T_NOTIFY))!=0);
                        h_notify_value = (cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_H_NOTIFY))!=0);
                        pot1_notify_value = (cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_POT1_NOTIFY))!=0);
                        pot2_notify_value = (cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_POT2_NOTIFY))!=0);
                        wl_notify_value = (cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_WL_NOTIFY))!=0);
                        pumps_notify_value = (cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_PUMPS_NOTIFY))!=0);
                        relays_notify_value = (cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_RELAYS_NOTIFY))!=0);
                        all_notify_value=(cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_ALL_NOTIFY))!=0);


                        version = cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_VERSION));
                        period = cursor_pref.getInt(cursor_pref.getColumnIndexOrThrow(MyContentProvider.KEY_PREF_PERIOD));

                    }


                    cursor_pref.close();
                    Log.d(LOG_TAG, "T notify: " + t_notify_value);
                    //Log.d(LOG_TAG, "Version: : " + version);

                    Log.d(LOG_TAG, "t from external DB = " + t);
                    Log.d(LOG_TAG, "t from local DB = " + t_max_value);
                }

                Cursor cursor_main = getContentResolver().query(Uri.parse(MyContentProvider.MAIN_CONTENT_URI + "/" + ctrl_id), null, null, null, null);
                if(cursor_main!=null){
                    if(cursor_main.moveToFirst()){
                        l_result=cursor_main.getInt(cursor_main.getColumnIndexOrThrow(MyContentProvider.KEY_MAIN_LIGHT_STATE));
                        t_result=cursor_main.getInt(cursor_main.getColumnIndexOrThrow(MyContentProvider.KEY_MAIN_T));
                        h_result=cursor_main.getInt(cursor_main.getColumnIndexOrThrow(MyContentProvider.KEY_MAIN_H));
                        pot1_h_result=cursor_main.getInt(cursor_main.getColumnIndexOrThrow(MyContentProvider.KEY_MAIN_POT1_H));
                        pot2_h_result=cursor_main.getInt(cursor_main.getColumnIndexOrThrow(MyContentProvider.KEY_MAIN_POT2_H));
                        p1_result=cursor_main.getInt(cursor_main.getColumnIndexOrThrow(MyContentProvider.KEY_MAIN_PUMP1_STATE));
                        p2_result=cursor_main.getInt(cursor_main.getColumnIndexOrThrow(MyContentProvider.KEY_MAIN_PUMP2_STATE));
                        relay1_result=cursor_main.getInt(cursor_main.getColumnIndexOrThrow(MyContentProvider.KEY_MAIN_RELAY1_STATE));
                        relay2_result=cursor_main.getInt(cursor_main.getColumnIndexOrThrow(MyContentProvider.KEY_MAIN_RELAY2_STATE));
                        w_result=cursor_main.getInt(cursor_main.getColumnIndexOrThrow(MyContentProvider.KEY_MAIN_WATER_LEVEL));
                        date_result=cursor_main.getString(cursor_main.getColumnIndexOrThrow(MyContentProvider.KEY_MAIN_DATE));
                        Log.d(LOG_TAG, "Date from external DB = " + date);
                        Log.d(LOG_TAG, "Date from local DB  = " + date_result);

                    }
                    cursor_main.close();
                }



                if (!all_notify_value && !date.equals(date_result) && !date_result.isEmpty() && !ctrl_id.equals("")) {
                    if ((t > t_max_value || t < t_min_value) && t_notify_value && t_control==1)
                        sendNotif(ctrl_id, "Температура воздуха " + t + "!");
                    if ((h > h_max_value || h < h_min_value) && h_notify_value && h_control==1)
                        sendNotif(ctrl_id, "Влажность воздуха " + h + "!");
                    if ((pot1_h > pot1_h_max_value || pot1_h < pot1_h_min_value) && pot1_notify_value && pot1_control==1)
                        sendNotif(ctrl_id, "Влажность 1 горшка " + pot1_h + "!");
                    if ((pot2_h > pot2_h_max_value || pot2_h < pot2_h_min_value) && pot2_notify_value && pot2_control==1)
                        sendNotif(ctrl_id, "Влажность 2 горшка " + pot2_h + "!");
                    if ((water_level > wl_max_value || water_level < wl_min_value) && wl_notify_value && water_control==1)
                        sendNotif(ctrl_id, "Уровень дыма " + water_level + "!");

                    if (l_result != light_state && l_notify_value && l_control==1) {
                        if (light_state == 1) sendNotif(ctrl_id, "Свет включился!");
                        if (light_state == 0) sendNotif(ctrl_id, "Свет выключился!");
                    }
                    if (relay1_result != relay1_state && relays_notify_value  && relay1_control==1) {
                        if (relay1_state == 0) sendNotif(ctrl_id, "Реле 1 включился!");
                        if (relay1_state == 1) sendNotif(ctrl_id, "Реле 1 выключился!");
                    }
                    if (relay2_result != relay2_state && relays_notify_value  && relay2_control==1) {
                        if (relay2_state == 0) sendNotif(ctrl_id, "Реле 2 включился!");
                        if (relay2_state == 1) sendNotif(ctrl_id, "Реле 2 выключился!");
                    }
                    if (p1_result != pump1_state && pumps_notify_value && pump1_control==1) {
                        if (pump1_state == 1) sendNotif(ctrl_id, "Насос 1 включился!");
                        if (pump1_state == 0) sendNotif(ctrl_id, "Насос 1 выключился!");
                    }
                    if (p2_result != pump2_state && pumps_notify_value && pump2_control==1) {
                        if (pump2_state == 1) sendNotif(ctrl_id, "Насос 2 включился!");
                        if (pump2_state == 0) sendNotif(ctrl_id, "Насос 2 выключился!");
                    }

                }


                //TODO Записывает даже если даты одинаковые, теряется производительность
                Log.d(LOG_TAG, "Callback, for ctrl_id: " + result.getAsString("ctrl_id"));
                Uri uri = ContentUris.withAppendedId(MyContentProvider.MAIN_CONTENT_URI, Long.parseLong(result.getAsString("ctrl_id")));
                int cnt = getContentResolver().update(uri, result, null, null);
                Log.d(LOG_TAG, "Number of device updated: " + cnt);


            }
        }


    }



}
// ---------------------------------------------------------------------------------------------

