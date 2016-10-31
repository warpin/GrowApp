package cc.growapp.growapp;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.SynchronousQueue;


public class DataBroker  {

    static String LOG_TAG="GrowApp";

    // -------------------------- Аутенфикация пользователя и получения хеша -----------------------
    public static class authentication extends AsyncTask<String,Void,String> {
        static Map<String,Object> params = new HashMap<>();
        public interface onAuthComplete {
            void onAuthCompleteMethod(String s);
        }

        private onAuthComplete listener;

        public authentication(onAuthComplete listener){
            this.listener=listener;
        }
        protected void onPreExecute(){
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... post_params) {
            // params comes from the execute() call: params[0] is the url.
            try {
                String url="http://growapp.e-nk.ru/core/login.php";
                params.put("username", post_params[0]);
                params.put("password", post_params[1]);
                return downloadUrl(url,params);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            Log.d(LOG_TAG, "Http answer = " + result);
            params.clear();

            String user_hash=result.trim();
            //Если длина хеша правильная (32)
            if(user_hash.length()==32){
                listener.onAuthCompleteMethod(user_hash);
            } else listener.onAuthCompleteMethod(null);

        }
    }
    // ---------------------------------------------------------------------------------------------
    // -------------------------------- Загрузка доступных контроллеров ----------------------------
    public static class get_controllers extends AsyncTask<String,Void,String> {
        static Map<String,Object> params = new HashMap<>();
        public interface onGetCtrlsComplete {
            void onGetCtrlsCompleteMethod(String s);
        }

        private onGetCtrlsComplete listener;

        public get_controllers(onGetCtrlsComplete listener){
            this.listener=listener;
        }


        @Override
        protected String doInBackground(String... post_params) {

            // params comes from the execute() call: params[0] is the url.
            try {
                String url="http://growapp.e-nk.ru/core/get_data/get_ctrl_list.php";
                params.put("username", post_params[0]);
                params.put("hash", post_params[1]);
                return downloadUrl(url,params);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            params.clear();
            Log.d(LOG_TAG, "Http answer = " + result);
            listener.onGetCtrlsCompleteMethod(result);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // --------------------- Загрузка профиля пользователя с внешней БД ----------------------------
    public static class get_pref_profile extends AsyncTask<String,Void,String> {
        static Map<String,Object> params = new HashMap<>();

        public interface onGetPrefVersionComplete {
            void onGetPrefProfileCompleteMethod(String s);
        }

        private onGetPrefVersionComplete listener;

        public get_pref_profile(onGetPrefVersionComplete listener){
            this.listener=listener;
        }


        @Override
        protected String doInBackground(String... post_params) {
            // params comes from the execute() call: params[0] is the url.
            try {
                String url="http://growapp.e-nk.ru/core/get_data/get_pref_profile.php";
                params.put("ctrl_id", post_params[0]);
                params.put("hash", post_params[1]);
                return downloadUrl(url,params);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            params.clear();
            Log.d(LOG_TAG, "Http answer = "+result);
            listener.onGetPrefProfileCompleteMethod(result);
        }
    }
    // ---------------------------------------------------------------------------------------------
    // --------------------- Загрузка профиля устройства с внешней БД ------------------------------
    public static class get_dev_profile extends AsyncTask<String,Void,String> {
        static Map<String,Object> params = new HashMap<>();
        public interface onGetDevProfileComplete {
            void onGetDevProfileCompleteMethod(String s);
        }

        private onGetDevProfileComplete listener;

        public get_dev_profile(onGetDevProfileComplete listener){
            this.listener=listener;
        }

        @Override
        protected String doInBackground(String... post_params) {
            // params comes from the execute() call: params[0] is the url.
            try {
                String url="http://growapp.e-nk.ru/core/get_data/get_dev_profile.php";
                params.put("ctrl_id", post_params[0]);
                params.put("hash", post_params[1]);
                return downloadUrl(url,params);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            params.clear();
            Log.d(LOG_TAG, "Http answer = "+result);
            listener.onGetDevProfileCompleteMethod(result);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // --------------------- Загрузка данных датчиков внешней БД ------------------------------
    public static class get_system_state extends AsyncTask<String,Void,String> {
        static Map<String,Object> params = new HashMap<>();
        public interface onGetSystemStateComplete {
            void onGetSystemStateCompleteMethod(String s);
        }

        private onGetSystemStateComplete listener;

        public get_system_state(onGetSystemStateComplete listener){
            this.listener=listener;
        }

        @Override
        protected String doInBackground(String... post_params) {
            // params comes from the execute() call: params[0] is the url.
            try {
                String url="http://growapp.e-nk.ru/core/get_data/get_system_state.php";
                params.put("ctrl_id", post_params[0]);
                params.put("hash", post_params[1]);
                return downloadUrl(url,params);
            } catch (IOException e) {
                return null;
            }
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            params.clear();
            Log.d(LOG_TAG, "Http answer = "+result);
            listener.onGetSystemStateCompleteMethod(result);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // --------------------- Загрузка данных датчиков для графиков с внешней БД --------------------
    public static class get_graphs_data extends AsyncTask<String,Void,String> {
        static Map<String,Object> params = new HashMap<>();

        public interface onGetGraphsDataComplete {
            void onGetGraphsDataCompleteMethod(String s);
        }

        private onGetGraphsDataComplete listener;

        public get_graphs_data(onGetGraphsDataComplete listener){
            this.listener=listener;
        }


        @Override
        protected String doInBackground(String... post_params) {
            // params comes from the execute() call: params[0] is the url.
            try {
                String url="http://growapp.e-nk.ru/core/get_data/get_graphs_data.php";
                params.put("ctrl_id", post_params[0]);
                params.put("hash", post_params[1]);
                params.put("param", post_params[2]);
                params.put("period", post_params[3]);

                return downloadUrl(url,params);
            } catch (IOException e) {
                return null;
            }
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            params.clear();
            Log.d(LOG_TAG, "Http answer = "+result);
            listener.onGetGraphsDataCompleteMethod(result);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // --------------------- Запись действий во внешнюю БД -----------------------------------------
    public static class set_action extends AsyncTask<String,Void,String> {

        static Map<String,Object> params = new HashMap<>();
        public interface onSetActionComplete {
            void onSetActionCompleteMethod(String s);
        }

        private onSetActionComplete listener;

        public set_action(onSetActionComplete listener){
            this.listener=listener;
        }

        @Override
        protected String doInBackground(String... post_params) {
            // params comes from the execute() call: params[0] is the url.
            try {
                String url="http://growapp.e-nk.ru/core/add_data/add_task.php";
                params.put("ctrl_id", post_params[0]);
                params.put("hash", post_params[1]);
                params.put("action_id", post_params[2]);

                return downloadUrl(url,params);
            } catch (IOException e) {
                return null;
            }
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            params.clear();
            Log.d(LOG_TAG, "Http answer = " + result);
            listener.onSetActionCompleteMethod(result);


        }
    }
    // --------------------- Запись настроек во внешнюю БД -----------------------------------------
    public static class save_user_profile extends AsyncTask<String,Void,String> {
        static Map<String,Object> params = new HashMap<>();
        public interface onSaveUserProfileComplete {
            void onSaveUserProfileCompleteMethod(String s);
        }

        private onSaveUserProfileComplete listener;

        public save_user_profile(onSaveUserProfileComplete listener){
            this.listener=listener;
        }

        @Override
        protected String doInBackground(String... post_params) {
            // params comes from the execute() call: params[0] is the url.
            try {
                String url="http://growapp.e-nk.ru/core/add_data/save_profile.php";
                params.put("ctrl_id", post_params[0]);
                params.put("hash", post_params[1]);
                params.put("t_max", post_params[2]);
                params.put("t_min", post_params[3]);
                params.put("h_max", post_params[4]);
                params.put("h_min", post_params[5]);
                params.put("pot1_h_max", post_params[6]);
                params.put("pot1_h_min", post_params[7]);
                params.put("pot2_h_max", post_params[8]);
                params.put("pot2_h_min", post_params[9]);
                params.put("wl_max", post_params[10]);
                params.put("wl_min", post_params[11]);
                params.put("all_notify", post_params[12]);
                params.put("t_notify", post_params[13]);
                params.put("h_notify", post_params[14]);
                params.put("pot1_notify", post_params[15]);
                params.put("pot2_notify", post_params[16]);
                params.put("wl_notify", post_params[17]);
                params.put("l_notify", post_params[18]);
                params.put("relays_notify", post_params[19]);
                params.put("pumps_notify", post_params[20]);

                return downloadUrl(url,params);
            } catch (IOException e) {
                return null;
            }
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            params.clear();
            Log.d(LOG_TAG, "Http answer = "+result);
            listener.onSaveUserProfileCompleteMethod(result);
        }
    }
    // --------------------- Изменение названия устройства -----------------------------------------
    public static class set_ctrl_name extends AsyncTask<String,Void,String> {

        /*public interface onCtrlNameComplete {
            void onCtrlNameComplete(String s);
        }
        private onCtrlNameComplete listener;

        public set_ctrl_name(onCtrlNameComplete listener){
            this.listener=listener;
        }*/
        static Map<String,Object> params = new HashMap<>();
        @Override
        protected String doInBackground(String... post_params) {
            // params comes from the execute() call: params[0] is the url.
            try {
                String url="http://growapp.e-nk.ru/core/add_data/edit_ctrl_name.php";
                params.put("ctrl_id", post_params[0]);
                params.put("hash", post_params[1]);
                params.put("ctrl_name", post_params[2]);

                return downloadUrl(url,params);
            } catch (IOException e) {
                return null;
            }
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            params.clear();
            Log.d(LOG_TAG, "Http answer = " + result);
            //listener.onCtrlNameComplete(result);


        }
    }
    // -------------------------- Регистрация пользователя -----------------------------------------
    public static class registration extends AsyncTask<String,Void,String> {
        static Map<String,Object> params = new HashMap<>();
        public interface onRegistrationComplete {
            void onRegistrationCompleteMethod(String s);
        }

        private onRegistrationComplete listener;

        public registration(onRegistrationComplete listener){
            this.listener=listener;
        }

        @Override
        protected String doInBackground(String... post_params) {
            // params comes from the execute() call: params[0] is the url.
            try {
                String url="http://growapp.e-nk.ru/core/registration.php";
                params.put("username", post_params[0]);
                params.put("email", post_params[1]);
                params.put("password", post_params[2]);

                return downloadUrl(url,params);
            } catch (IOException e) {
                return null;
            }
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            params.clear();
            Log.d(LOG_TAG, "Http answer = " + result);
            listener.onRegistrationCompleteMethod(result);


        }
    }
    // -------------------------- Добавление устройства -----------------------------------------
    public static class add_device extends AsyncTask<String,Void,String> {
        static Map<String,Object> params = new HashMap<>();
        public interface onAddDeviceComplete {
            void onAddDeviceCompleteMethod(String s);
        }

        private onAddDeviceComplete listener;

        public add_device(onAddDeviceComplete listener){
            this.listener=listener;
        }

        @Override
        protected String doInBackground(String... post_params) {
            // params comes from the execute() call: params[0] is the url.
            try {
                String url="http://growapp.e-nk.ru/core/get_data/activate_dev.php";
                params.put("username", post_params[0]);
                params.put("hash", post_params[1]);
                params.put("ctrl_id", post_params[2]);
                params.put("ctrl_name", post_params[3]);
                params.put("ctrl_an", post_params[4]);
                return downloadUrl(url,params);
            } catch (IOException e) {
                return null;
            }
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            params.clear();
            Log.d(LOG_TAG, "Http answer = " + result);
            listener.onAddDeviceCompleteMethod(result);


        }
    }
    // ---------------------------------------------------------------------------------------------
    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    static String downloadUrl(String myurl, Map<String, Object> params) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int buffer_size = 4096;

        try {
            //params.put("password", pass1_data);
            //params.put("email", email_data);

            StringBuilder postData = new StringBuilder();

            for (Map.Entry<String,Object>  param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }

            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(3000 /* milliseconds */);
            conn.setConnectTimeout(6000 /* milliseconds */);
            //conn.setRequestMethod("GET");
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);

            // Starts the query
            conn.connect();
            //int response = conn.getResponseCode();
            //Log.d(LOG_TAG, "The response is: " + response);
            is = conn.getInputStream();
            //len=is.available();
            //len=1024;
            //Log.d(LOG_TAG, "conn.getInputStream() is: " + is);
            // Convert the InputStream into a string

            String contentAsString = readIt(is, buffer_size);
            is.close();

            return contentAsString.trim();

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
    // Reads an InputStream and converts it to a String.
    public static String readIt(InputStream stream, int len) throws IOException {
        Reader reader;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}
