package cc.growapp.growapp;

import android.content.ContentValues;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.growapp.growapp.database.MyContentProvider;

public class JSONHandler
{
    // Database Helper
    String LOG_TAG = "GrowApp";

    public ContentValues ParseJSONProfile(String JSONString) {
        try {
            JSONObject json = new JSONObject(JSONString);
            int success;
            String TAG_SUCCESS="success";
            success = json.getInt(TAG_SUCCESS);

            if (success == 1) {

                // Успешно получены данные
                JSONArray ProfileObj = json.getJSONArray("data");
                Log.d(LOG_TAG, "JSON данные получены ...");

                // получаем первый обьект с JSON Array
                JSONObject profile = ProfileObj.getJSONObject(0);
                Log.d(LOG_TAG, "Профиль настроек JSON получен = " + profile);


                ContentValues cv = new ContentValues();


                cv.put(MyContentProvider.KEY_PREF_CTRL_ID, profile.getString("ctrl_id"));
                cv.put(MyContentProvider.KEY_PREF_VERSION, Integer.parseInt(profile.getString("version")));
                cv.put(MyContentProvider.KEY_PREF_T_MAX, Integer.parseInt(profile.getString("t_max")));
                cv.put(MyContentProvider.KEY_PREF_T_MIN, Integer.parseInt(profile.getString("t_min")));
                cv.put(MyContentProvider.KEY_PREF_H_MAX, Integer.parseInt(profile.getString("h_max")));
                cv.put(MyContentProvider.KEY_PREF_H_MIN, Integer.parseInt(profile.getString("h_min")));
                cv.put(MyContentProvider.KEY_PREF_POT1_H_MAX, Integer.parseInt(profile.getString("pot1_h_max")));
                cv.put(MyContentProvider.KEY_PREF_POT1_H_MIN, Integer.parseInt(profile.getString("pot1_h_min")));
                cv.put(MyContentProvider.KEY_PREF_POT2_H_MAX, Integer.parseInt(profile.getString("pot2_h_max")));
                cv.put(MyContentProvider.KEY_PREF_POT2_H_MIN, Integer.parseInt(profile.getString("pot2_h_min")));
                cv.put(MyContentProvider.KEY_PREF_WL_MAX, Integer.parseInt(profile.getString("wl_max")));
                cv.put(MyContentProvider.KEY_PREF_WL_MIN, Integer.parseInt(profile.getString("wl_min")));
                cv.put(MyContentProvider.KEY_PREF_ALL_NOTIFY, Integer.parseInt(profile.getString("all_notify")));
                cv.put(MyContentProvider.KEY_PREF_T_NOTIFY, Integer.parseInt(profile.getString("t_notify")));
                cv.put(MyContentProvider.KEY_PREF_H_NOTIFY, Integer.parseInt(profile.getString("h_notify")));
                cv.put(MyContentProvider.KEY_PREF_POT1_NOTIFY, Integer.parseInt(profile.getString("pot1_notify")));
                cv.put(MyContentProvider.KEY_PREF_POT2_NOTIFY, Integer.parseInt(profile.getString("pot2_notify")));
                cv.put(MyContentProvider.KEY_PREF_WL_NOTIFY, Integer.parseInt(profile.getString("wl_notify")));
                cv.put(MyContentProvider.KEY_PREF_L_NOTIFY, Integer.parseInt(profile.getString("l_notify")));
                cv.put(MyContentProvider.KEY_PREF_RELAYS_NOTIFY, Integer.parseInt(profile.getString("relays_notify")));
                cv.put(MyContentProvider.KEY_PREF_PUMPS_NOTIFY, Integer.parseInt(profile.getString("pumps_notify")));


                int period = Integer.parseInt(profile.getString("period"));
                //String sound = profile.getString("sound");
                String vibrate = profile.getString("vibrate");
                int color = Integer.parseInt(profile.getString("color"));

                //Uri default_sound_uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                if(period==0)period=900;
                //if(sound.length()==0)sound=String.valueOf(default_sound_uri);
                if(vibrate.length()==0)vibrate="Short";
                if(color==0)color=-16711936;


                cv.put(MyContentProvider.KEY_PREF_PERIOD, period);
                //cv.put(MyContentProvider.KEY_PREF_SOUND, sound);
                cv.put(MyContentProvider.KEY_PREF_VIBRATE, vibrate);
                cv.put(MyContentProvider.KEY_PREF_COLOR, color);




                return cv;
                //db.createPref(preference);



            } else{
                Log.d(LOG_TAG, "Данные не найдены!");
                //return profile_data;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public ContentValues ParseJSONDevProfile(String JSONString) {
        try {
            JSONObject json = new JSONObject(JSONString);
            int success;
            String TAG_SUCCESS="success";
            success = json.getInt(TAG_SUCCESS);
            if (success == 1){

                // Успешно получены данные
                JSONArray DevProfileObj = json.getJSONArray("data");
                Log.d(LOG_TAG, "JSON данные получены ...");

                // получаем первый обьект с JSON Array
                JSONObject dev_profile_json = DevProfileObj.getJSONObject(0);
                Log.d(LOG_TAG, "Профиль устройства JSON получен = " + dev_profile_json);


                ContentValues cv = new ContentValues();
                cv.put(MyContentProvider.KEY_DEV_CTRL_ID,dev_profile_json.getString("ctrl_id"));
                cv.put(MyContentProvider.KEY_DEV_LIGHT_CONTROL,Integer.parseInt(dev_profile_json.getString("light_control")));
                cv.put(MyContentProvider.KEY_DEV_T_CONTROL,Integer.parseInt(dev_profile_json.getString("t_control")));
                cv.put(MyContentProvider.KEY_DEV_H_CONTROL,Integer.parseInt(dev_profile_json.getString("h_control")));
                cv.put(MyContentProvider.KEY_DEV_POT1_CONTROL,Integer.parseInt(dev_profile_json.getString("pot1_control")));
                cv.put(MyContentProvider.KEY_DEV_POT2_CONTROL,Integer.parseInt(dev_profile_json.getString("pot2_control")));
                cv.put(MyContentProvider.KEY_DEV_RELAY1_CONTROL,Integer.parseInt(dev_profile_json.getString("relay1_control")));
                cv.put(MyContentProvider.KEY_DEV_RELAY2_CONTROL,Integer.parseInt(dev_profile_json.getString("relay2_control")));
                cv.put(MyContentProvider.KEY_DEV_PUMP1_CONTROL,Integer.parseInt(dev_profile_json.getString("pump1_control")));
                cv.put(MyContentProvider.KEY_DEV_PUMP2_CONTROL,Integer.parseInt(dev_profile_json.getString("pump2_control")));
                cv.put(MyContentProvider.KEY_DEV_WATER_CONTROL,Integer.parseInt(dev_profile_json.getString("water_control")));
                cv.put(MyContentProvider.KEY_DEV_AUTO_WATERING1,Integer.parseInt(dev_profile_json.getString("auto_watering1")));
                cv.put(MyContentProvider.KEY_DEV_AUTO_WATERING2,Integer.parseInt(dev_profile_json.getString("auto_watering2")));
                cv.put(MyContentProvider.KEY_DEV_PUMP_TIME,Integer.parseInt(dev_profile_json.getString("pump_time")));


                return cv;

            }else{
                Log.d(LOG_TAG, "Данные не найдены!");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ContentValues ParseJSONSystemState(String JSONString) {
        try {
            JSONObject json = new JSONObject(JSONString);
            int success;
            String TAG_SUCCESS="success";
            success = json.getInt(TAG_SUCCESS);
            if (success == 1){

                // Успешно получены данные
                JSONArray DevProfileObj = json.getJSONArray("data");
                Log.d(LOG_TAG, "JSON данные получены ...");

                // получаем первый обьект с JSON Array
                JSONObject system_state_json = DevProfileObj.getJSONObject(0);
                Log.d(LOG_TAG, "Перый объект JSON получен = " + system_state_json);

                // получаем обьекты с JSON Array
                //int ctrl_id = Integer.parseInt(dev_profile_json.getString("ctrl_id"));
                /*String ctrl_id = system_state_json.getString("ctrl_id");
                int light_state = Integer.parseInt(system_state_json.getString("light_state"));
                int t = Integer.parseInt(system_state_json.getString("t"));
                int h = Integer.parseInt(system_state_json.getString("h"));
                int pot1_h = Integer.parseInt(system_state_json.getString("pot1_h"));
                int pot2_h = Integer.parseInt(system_state_json.getString("pot2_h"));
                int relay1_state = Integer.parseInt(system_state_json.getString("relay1_state"));
                int relay2_state = Integer.parseInt(system_state_json.getString("relay2_state"));
                int pump1_state = Integer.parseInt(system_state_json.getString("pump2_state"));
                int pump2_state = Integer.parseInt(system_state_json.getString("pump2_state"));
                int water_level = Integer.parseInt(system_state_json.getString("water_level"));
                String date = system_state_json.getString("date");*/

                ContentValues cv = new ContentValues();
                cv.put(MyContentProvider.KEY_MAIN_CTRL_ID,system_state_json.getString("ctrl_id"));
                cv.put(MyContentProvider.KEY_MAIN_LIGHT_STATE,Integer.parseInt(system_state_json.getString("light_state")));
                cv.put(MyContentProvider.KEY_MAIN_T,Integer.parseInt(system_state_json.getString("t")));
                cv.put(MyContentProvider.KEY_MAIN_H,Integer.parseInt(system_state_json.getString("h")));
                cv.put(MyContentProvider.KEY_MAIN_POT1_H,Integer.parseInt(system_state_json.getString("pot1_h")));
                cv.put(MyContentProvider.KEY_MAIN_POT2_H,Integer.parseInt(system_state_json.getString("pot2_h")));
                cv.put(MyContentProvider.KEY_MAIN_RELAY1_STATE,Integer.parseInt(system_state_json.getString("relay1_state")));
                cv.put(MyContentProvider.KEY_MAIN_RELAY2_STATE,Integer.parseInt(system_state_json.getString("relay2_state")));
                cv.put(MyContentProvider.KEY_MAIN_PUMP1_STATE,Integer.parseInt(system_state_json.getString("pump1_state")));
                cv.put(MyContentProvider.KEY_MAIN_PUMP2_STATE,Integer.parseInt(system_state_json.getString("pump2_state")));
                cv.put(MyContentProvider.KEY_MAIN_WATER_LEVEL,Integer.parseInt(system_state_json.getString("water_level")));
                cv.put(MyContentProvider.KEY_MAIN_DATE,system_state_json.getString("date"));



                return cv;
            }else{
                Log.d(LOG_TAG, "Данные не найдены!");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


}
