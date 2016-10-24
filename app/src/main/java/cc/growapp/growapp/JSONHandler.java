package cc.growapp.growapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.growapp.growapp.database.Dev_profile;
import cc.growapp.growapp.database.Preferences;
import cc.growapp.growapp.database.SystemState;

public class JSONHandler
{
    // Database Helper
    String LOG_TAG = "GrowApp";

    public Preferences ParseJSONProfile(String JSONString) {
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

                //int ctrl_id = Integer.parseInt(profile.getString("ctrl_id"));
                String ctrl_id = profile.getString("ctrl_id");
                //int version = Integer.parseInt(profile.getString("version"));
                int t_max = Integer.parseInt(profile.getString("t_max"));
                int t_min = Integer.parseInt(profile.getString("t_min"));
                int h_max = Integer.parseInt(profile.getString("h_max"));
                int h_min = Integer.parseInt(profile.getString("h_min"));
                int pot1_h_max = Integer.parseInt(profile.getString("pot1_h_max"));
                int pot1_h_min = Integer.parseInt(profile.getString("pot1_h_min"));
                int pot2_h_max = Integer.parseInt(profile.getString("pot2_h_max"));
                int pot2_h_min = Integer.parseInt(profile.getString("pot2_h_min"));
                int wl_max = Integer.parseInt(profile.getString("wl_max"));
                int wl_min = Integer.parseInt(profile.getString("wl_min"));
                int all_notify = Integer.parseInt(profile.getString("all_notify"));
                int t_notify = Integer.parseInt(profile.getString("t_notify"));
                int h_notify = Integer.parseInt(profile.getString("h_notify"));
                int pot1_notify = Integer.parseInt(profile.getString("pot1_notify"));
                int pot2_notify = Integer.parseInt(profile.getString("pot2_notify"));
                int wl_notify = Integer.parseInt(profile.getString("wl_notify"));
                int l_notify = Integer.parseInt(profile.getString("l_notify"));
                int relays_notify = Integer.parseInt(profile.getString("relays_notify"));
                int pumps_notify = Integer.parseInt(profile.getString("pumps_notify"));



                return new Preferences(ctrl_id,t_max,t_min,h_max,h_min,
                        pot1_h_max,pot1_h_min,pot2_h_max,pot2_h_min,wl_max,wl_min,all_notify,
                        t_notify,h_notify,pot1_notify,pot2_notify,wl_notify,l_notify,
                        relays_notify, pumps_notify);
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
    public Dev_profile ParseJSONDevProfile(String JSONString) {
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

                // получаем обьекты с JSON Array
                //int ctrl_id = Integer.parseInt(dev_profile_json.getString("ctrl_id"));
                String ctrl_id = dev_profile_json.getString("ctrl_id");
                int light_control = Integer.parseInt(dev_profile_json.getString("light_control"));
                int t_control = Integer.parseInt(dev_profile_json.getString("t_control"));
                int h_control = Integer.parseInt(dev_profile_json.getString("h_control"));
                int pot1_control = Integer.parseInt(dev_profile_json.getString("pot1_control"));
                int pot2_control = Integer.parseInt(dev_profile_json.getString("pot2_control"));
                int relay1_control = Integer.parseInt(dev_profile_json.getString("relay1_control"));
                int relay2_control = Integer.parseInt(dev_profile_json.getString("relay2_control"));
                int pump1_control = Integer.parseInt(dev_profile_json.getString("pump2_control"));
                int pump2_control = Integer.parseInt(dev_profile_json.getString("pump2_control"));
                int water_control = Integer.parseInt(dev_profile_json.getString("water_control"));
                int auto_watering1 = Integer.parseInt(dev_profile_json.getString("auto_watering1"));
                int auto_watering2 = Integer.parseInt(dev_profile_json.getString("auto_watering2"));

                return new Dev_profile(ctrl_id,light_control,t_control,h_control,
                        pot1_control,pot2_control,relay1_control,relay2_control,pump1_control,pump2_control,
                        water_control,auto_watering1,auto_watering2);

            }else{
                Log.d(LOG_TAG, "Данные не найдены!");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SystemState ParseJSONSystemState(String JSONString) {
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
                String ctrl_id = system_state_json.getString("ctrl_id");
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
                String date = system_state_json.getString("date");

                 return new SystemState(ctrl_id,light_state,t,h,
                        pot1_h,pot2_h,relay1_state,relay2_state,pump1_state,pump2_state,
                        water_level, date);
            }else{
                Log.d(LOG_TAG, "Данные не найдены!");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


}
