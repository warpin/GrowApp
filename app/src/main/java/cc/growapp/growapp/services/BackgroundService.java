package cc.growapp.growapp.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cc.growapp.growapp.DataBroker;
import cc.growapp.growapp.R;
import cc.growapp.growapp.activities.MainActivity;
import cc.growapp.growapp.database.Controllers;
import cc.growapp.growapp.database.DatabaseHelper;
import cc.growapp.growapp.database.Dev_profile;
import cc.growapp.growapp.database.Preferences;
import cc.growapp.growapp.database.SystemState;

import static android.net.ConnectivityManager.TYPE_MOBILE;

public class BackgroundService extends Service implements
        DataBroker.get_system_state.onGetSystemStateComplete
{

    String LOG_TAG = "GrowAppService";
    NotificationManager nm;
    SharedPreferences sPref;
    int period;
    String ringtone;
    int color;
    long[] vibrate;
    public static final String APP_PREFERENCES = "GrowAppSettings";
    // JSON parser class
    private int notif_id = 0;

    // Database Helper
    DatabaseHelper db;

    static final public String ACTION = "cc.growapp.growapp.InfoActivity.ACTION";
    static final public String START_TIME = "cc.growapp.growapp.InfoActivity.START_TIME";
    static final public String LAST_TIME = "cc.growapp.growapp.InfoActivity.LAST_TIME";

    long REPEAT_TIME;
    long start_time;
    long ServiceStartAt,ServiceLastStartAt;


    @Override
    public IBinder onBind(Intent intent) {
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //broadcaster = LocalBroadcastManager.getInstance(this);
    }


    public void sendResult(long start_time,long last_time) {
        Intent intent = new Intent(ACTION);
        //if(message != null){
            intent.putExtra(START_TIME, start_time);
            intent.putExtra(LAST_TIME, last_time);
        //}
        sendBroadcast(intent);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "Service initialization!");
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        db = new DatabaseHelper(getApplicationContext());

        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        period = sPref.getInt("ServicePeriod", 900);
        ServiceStartAt = sPref.getLong("ServiceStartAt", 0);
        ServiceLastStartAt = sPref.getLong("ServiceLastStartAt", 0);

        String default_ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString();
        ringtone = sPref.getString("RingTone",default_ringtone);
        String vibrator_type = sPref.getString("Vibrator","Short");
        color = sPref.getInt("NotifColor",-16711936);
        switch(vibrator_type){
            case "No":vibrate= new long[] {0};break;
            case "Short":vibrate= new long[] {1000,1000};break;
            case "Long":vibrate= new long[] {1000,1000,1000,1000};break;
        }

        REPEAT_TIME = 1000 * period;
        start_time = System.currentTimeMillis() + REPEAT_TIME;


        Log.d(LOG_TAG,"----------------------------------------------------");
        Log.d(LOG_TAG,"Service period :"+String.valueOf(period)+" second(s)");
        Log.d(LOG_TAG, "Service starts at: " + new Date(ServiceStartAt));
        Log.d(LOG_TAG, "Service last start at: " + new Date(ServiceLastStartAt));
        Log.d(LOG_TAG, "Difference: " + (ServiceStartAt-ServiceLastStartAt));

        if((ServiceStartAt-ServiceLastStartAt)>=REPEAT_TIME){
            sPref.edit().putLong("ServiceLastStartAt", ServiceStartAt).apply();
            Log.d(LOG_TAG, "Service created!");
            try {
                GetControllersListFromSQL();
            } catch (Exception e) {
                Log.d(LOG_TAG,"--- GetControllersListFromSQL() Failed ---");
            }
        }


        stopSelf();
        //return START_REDELIVER_INTENT;
        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        AlarmManager service = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //Intent i = new Intent(context, MyStartServiceReceiver.class);
        PendingIntent pending = PendingIntent.getService(this, 0, new Intent(this, BackgroundService.class), PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar cal = Calendar.getInstance();
        // start 30 seconds after boot completed
        //cal.add(Calendar.SECOND, period);
        //service.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), REPEAT_TIME, pending);

        service.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + period * 1000,
                period * 1000, pending);

        //Запишем в SP, для информации, когда запустится сервис
        Log.d(LOG_TAG, "Next start time: " + new Date(start_time));
        sPref.edit().putLong("ServiceStartAt", start_time).apply();
        sendResult(start_time,ServiceLastStartAt);


    }


    void sendNotif(String ctrl_id, String message) {
        Log.d(LOG_TAG, "Starting notification");

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("controller_id", ctrl_id);

        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Context context = getApplicationContext();
        Resources res = context.getResources();
        Notification.Builder builder = new Notification.Builder(context);

        //Достанем имя контроллера с БД
        Controllers ctrl = db.getControllerName(ctrl_id);
        String ctrl_name=ctrl.get_name();

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
                .setContentText(message); // Текст уведомления



        //Notification notification = builder.getNotification(); // до API 16
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();

            //notification.defaults = Notification.DEFAULT_ALL;
            notification.sound = Uri.parse(ringtone);
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
    //TODO: Частиный дубляж с Main Activity
    // --------------- Загрузка доступных контроллеров с БД ----------------------------
    public boolean GetControllersListFromSQL() {

        List<Controllers> allCtrls = db.getAllCtrls();
        for (Controllers ctrl : allCtrls) {
            String ctrl_id=ctrl.get_ctrl_id();
            //Log.d(LOG_TAG, "CTRLS Name = "+ctrl.get_name());
            Log.d(LOG_TAG, "Controller ID: " + String.valueOf(ctrl_id));
            Log.d(LOG_TAG, "Controller NAME: " + ctrl.get_name());

            //Запускаем процесс получения данных датчиков
            String hash = sPref.getString("hash", "");
            new DataBroker.get_system_state(this).execute(String.valueOf(ctrl_id), hash);
        }
        db.closeDB();
        return true;
    }

    //TODO: Частичный дубляж с Main Activity и LoginActivity
    @Override
    public void onGetSystemStateCompleteMethod(String s) {


        Log.d(LOG_TAG, "Server answer: " + s);
        if(s!=null){
            Log.d(LOG_TAG, "Callback, system state: " + s);
            try {
                JSONObject json = new JSONObject(s);
                int success;
                String TAG_SUCCESS="success";
                success = json.getInt(TAG_SUCCESS);
                if (success == 1){
                    // Успешно получены данные
                    JSONArray DevProfileObj = json.getJSONArray("data");
                    Log.d(LOG_TAG, "JSON data has been received ...");

                    // получаем первый обьект с JSON Array
                    JSONObject dev_profile_json = DevProfileObj.getJSONObject(0);
                    Log.d(LOG_TAG, "First JSON object has been recevied = " + dev_profile_json);

                    // получаем обьекты с JSON Array
                    String ctrl_id = dev_profile_json.getString("ctrl_id").trim();
                    //String ctrl_id_string = String.valueOf(ctrl_id);
                    int light_state = Integer.parseInt(dev_profile_json.getString("light_state"));
                    int t = Integer.parseInt(dev_profile_json.getString("t"));
                    int h = Integer.parseInt(dev_profile_json.getString("h"));
                    int pot1_h = Integer.parseInt(dev_profile_json.getString("pot1_h"));
                    int pot2_h = Integer.parseInt(dev_profile_json.getString("pot2_h"));
                    int relay1_state = Integer.parseInt(dev_profile_json.getString("relay1_state"));
                    int relay2_state = Integer.parseInt(dev_profile_json.getString("relay2_state"));
                    int pump1_state = Integer.parseInt(dev_profile_json.getString("pump2_state"));
                    int pump2_state = Integer.parseInt(dev_profile_json.getString("pump2_state"));
                    int water_level = Integer.parseInt(dev_profile_json.getString("water_level"));
                    String date = dev_profile_json.getString("date");

                    Preferences preferences = db.getPrefProfile(ctrl_id);

                    int saved_t_max = preferences.get_t_max();
                    int saved_t_min = preferences.get_t_min();
                    int saved_h_max = preferences.get_h_max();
                    int saved_h_min = preferences.get_h_min();
                    int saved_pot1_h_max = preferences.get_pot1_h_max();
                    int saved_pot1_h_min = preferences.get_pot1_h_min();
                    int saved_pot2_h_max = preferences.get_pot2_h_max();
                    int saved_pot2_h_min = preferences.get_pot2_h_min();
                    int saved_wl_max = preferences.get_wl_max();
                    int saved_wl_min = preferences.get_wl_min();
                    boolean saved_all_notify= (preferences.get_all_notify()!=0);
                    boolean saved_t_notify = (preferences.get_t_notify()!=0);
                    boolean saved_h_notify = (preferences.get_h_notify()!=0);
                    boolean saved_pot1_notify = (preferences.get_pot1_notify()!=0);
                    boolean saved_pot2_notify = (preferences.get_pot2_notify()!=0);
                    boolean saved_wl_notify = (preferences.get_wl_notify()!=0);
                    boolean saved_l_notify = (preferences.get_l_notify()!=0);
                    boolean saved_relays_notify = (preferences.get_relays_notify()!=0);
                    boolean saved_pumps_notify = (preferences.get_pumps_notify()!=0);


                    SystemState state = db.getSystemState(ctrl_id);
                    int l_result= state.get_light_state();
                    //int t_result= state.get_t();
                    //int h_result= state.get_h();
                    //int pot1_h_result= state.get_pot1_h();
                    //int pot2_h_result= state.get_pot2_h();
                    int p1_result= state.get_pump1_state();
                    int p2_result= state.get_pump2_state();
                    int relay1_result= state.get_relay1_state();
                    int relay2_result= state.get_relay2_state();
                    //int w_result= state.get_water_level();
                    String date_result= state.get_date();


                    //Выцепляем с базы, какие компоненты системы нам доступны
                    //После чего отсылаем уведомления, соответсвующие комплектации
                    Dev_profile dev_profile = db.getDevProfile(ctrl_id);
                    //Log.d("Tag Name", dev_profile.getTagName());
                    int l_control= dev_profile.get_light_control();
                    int t_control= dev_profile.get_t_control();
                    int h_control= dev_profile.get_h_control();
                    int pot1_control= dev_profile.get_pot1_control();
                    int pot2_control= dev_profile.get_pot2_control();
                    int pump1_control= dev_profile.get_pump1_control();
                    int pump2_control= dev_profile.get_pump2_control();
                    int relay1_control= dev_profile.get_relay1_control();
                    int relay2_control= dev_profile.get_relay2_control();
                    int water_control= dev_profile.get_water_control();

                    Log.d(LOG_TAG, "Date from external DB = " + date);
                    Log.d(LOG_TAG, "Date from local DB  = " +date_result);
                    Log.d(LOG_TAG, "t from external DB = " + t);
                    Log.d(LOG_TAG, "t from local DB = " +saved_t_max);

                    if (!saved_all_notify && !date.equals(date_result) && !date_result.isEmpty() && !ctrl_id.equals("")) {
                        if ((t > saved_t_max || t < saved_t_min) && saved_t_notify && t_control==1)
                            sendNotif(ctrl_id, "Температура воздуха " + t + "!");
                        if ((h > saved_h_max || h < saved_h_min) && saved_h_notify && h_control==1)
                            sendNotif(ctrl_id, "Влажность воздуха " + h + "!");
                        if ((pot1_h > saved_pot1_h_max || pot1_h < saved_pot1_h_min) && saved_pot1_notify && pot1_control==1)
                            sendNotif(ctrl_id, "Влажность 1 горшка " + pot1_h + "!");
                        if ((pot2_h > saved_pot2_h_max || pot2_h < saved_pot2_h_min) && saved_pot2_notify && pot2_control==1)
                            sendNotif(ctrl_id, "Влажность 2 горшка " + pot2_h + "!");
                        if ((water_level > saved_wl_max || water_level < saved_wl_min) && saved_wl_notify && water_control==1)
                            sendNotif(ctrl_id, "Уровень дыма " + water_level + "!");

                        if (l_result != light_state && saved_l_notify && l_control==1) {
                            if (light_state == 1) sendNotif(ctrl_id, "Свет включился!");
                            if (light_state == 0) sendNotif(ctrl_id, "Свет выключился!");
                        }
                        if (relay1_result != relay1_state && saved_relays_notify  && relay1_control==1) {
                            if (relay1_state == 1) sendNotif(ctrl_id, "Реле 1 включился!");
                            if (relay1_state == 0) sendNotif(ctrl_id, "Реле 1 выключился!");
                        }
                        if (relay2_result != relay2_state && saved_relays_notify  && relay2_control==1) {
                            if (relay2_state == 1) sendNotif(ctrl_id, "Реле 2 включился!");
                            if (relay2_state == 0) sendNotif(ctrl_id, "Реле 2 выключился!");
                        }
                        if (p1_result != pump1_state && saved_pumps_notify && pump1_control==1) {
                            if (pump1_state == 1) sendNotif(ctrl_id, "Насос 1 включился!");
                            if (pump1_state == 0) sendNotif(ctrl_id, "Насос 1 выключился!");
                        }
                        if (p2_result != pump2_state && saved_pumps_notify && pump2_control==1) {
                            if (pump2_state == 1) sendNotif(ctrl_id, "Насос 2 включился!");
                            if (pump2_state == 0) sendNotif(ctrl_id, "Насос 2 выключился!");
                        }

                    }

                    SystemState systemState = new SystemState(ctrl_id,light_state,t,h,
                            pot1_h,pot2_h,relay1_state,relay2_state,pump1_state,pump2_state,
                            water_level, date);
                    db.updateSystemState(systemState);

                    // Don't forget to close database connection
                    db.closeDB();



                }else{
                    Log.d(LOG_TAG, "Data not found");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(LOG_TAG, "Server does not answer");
        }

    }

}
// ---------------------------------------------------------------------------------------------

