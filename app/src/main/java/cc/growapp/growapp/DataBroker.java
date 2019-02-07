package cc.growapp.growapp;


import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class DataBroker  {

    static String LOG_TAG="DataBroker";

    public static final String HOSTNAME = "http://growapp.e-nk.ru";
    //public static final String LOGIN_URI = HOSTNAME+"/core/login.php";
    //public static final String REGISTRATION_URI = "http://"+post_params[0]+"/core/registration.php";
    //public static final String ACTIVATE_DEV_URI = "http://"+post_params[0]+"/core/get_data/activate_dev.php";

    //public static final String GET_CTRLS_URI = "http://"+post_params[0]+"/core/get_data/get_ctrl_list.php";
    //public static final String GET_DEV_PROFILE_URI = "http://"+post_params[0]+"/core/get_data/get_dev_profile.php";
    //public static final String GET_PREF_URI = "http://"+post_params[0]+"/core/get_data/get_pref_profile.php";
    //public static final String GET_SYSTEM_STATE_URI = "http://"+post_params[0]+"/core/get_data/get_system_state.php";
    //public static final String GET_GRAPHS_DATA_URI = "http://"+post_params[0]+"/core/get_data/get_graphs_data.php";
    //public static final String ADD_TASK_URI = "http://"+post_params[0]+"/core/add_data/add_task.php";
    //public static final String SAVE_PREF_URI = "http://"+post_params[0]+"/core/add_data/save_profile.php";
    //public static final String UPDATE_CTRL_NAME_URI = "http://"+post_params[0]+"/core/add_data/edit_ctrl_name.php";
    
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
        protected String doInBackground(String... get_params) {
            // params comes from the execute() call: params[0] is the url.
            try {
                String url= "http://"+get_params[0]+"/core/login.php";
                params.put("username", get_params[1]);
                params.put("password", get_params[2]);
                //Log.d(LOG_TAG,"Authentication string: " + url);
                //Log.d(LOG_TAG,"get_params[1] " + get_params[1]);
                //Log.d(LOG_TAG,"get_params[2] " + get_params[2]);
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

            String user_hash = result.trim();
            //Если длина хеша правильная (32)
            if (user_hash.length() == 32) {
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
        protected String doInBackground(String... get_params) {

            // params comes from the execute() call: params[0] is the url.
            try {
                String url="http://"+get_params[0]+"/core/get_data/get_ctrl_list.php";
                params.put("username", get_params[1]);
                params.put("hash", get_params[2]);
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
        protected String doInBackground(String... get_params) {
            // params comes from the execute() call: params[0] is the url.
            try {
                String url="http://"+get_params[0]+"/core/get_data/get_pref_profile.php";
                params.put("ctrl_id", get_params[1]);
                params.put("hash", get_params[2]);
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
        protected String doInBackground(String... get_params) {
            // params comes from the execute() call: params[0] is the url.
            try {
                String url="http://"+get_params[0]+"/core/get_data/get_dev_profile.php";
                params.put("ctrl_id", get_params[1]);
                params.put("hash", get_params[2]);
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
        protected String doInBackground(String... get_params) {
            // params comes from the execute() call: params[0] is the url.
            try {
                Log.d(LOG_TAG,get_params[0]);
                Log.d(LOG_TAG,get_params[1]);
                Log.d(LOG_TAG,get_params[2]);
                String url="http://"+get_params[0]+"/core/get_data/get_system_state.php";
                params.put("ctrl_id", get_params[1]);
                params.put("hash", get_params[2]);
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
        protected String doInBackground(String... get_params) {
            // params comes from the execute() call: params[0] is the url.
            try {
                String url="http://"+get_params[0]+"/core/get_data/get_graphs_data.php";
                params.put("ctrl_id", get_params[1]);
                params.put("hash", get_params[2]);
                params.put("param", get_params[3]);
                params.put("period", get_params[4]);

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
        protected String doInBackground(String... get_params) {
            // params comes from the execute() call: params[0] is the url.
            try {
                String url="http://"+get_params[0]+"/core/add_data/add_task.php";
                params.put("ctrl_id", get_params[1]);
                params.put("hash", get_params[2]);
                params.put("action_id", get_params[3]);

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
        protected String doInBackground(String... get_params) {
            // params comes from the execute() call: params[0] is the url.
            try {
                String url="http://"+get_params[0]+"/core/add_data/save_profile.php";
                params.put("ctrl_id", get_params[1]);
                params.put("hash", get_params[2]);
                params.put("t_max", get_params[3]);
                params.put("t_min", get_params[4]);
                params.put("h_max", get_params[5]);
                params.put("h_min", get_params[6]);
                params.put("pot1_h_max", get_params[7]);
                params.put("pot1_h_min", get_params[8]);
                params.put("pot2_h_max", get_params[9]);
                params.put("pot2_h_min", get_params[10]);
                params.put("wl_max", get_params[11]);
                params.put("wl_min", get_params[12]);
                params.put("all_notify", get_params[13]);
                params.put("t_notify", get_params[14]);
                params.put("h_notify", get_params[15]);
                params.put("pot1_notify", get_params[16]);
                params.put("pot2_notify", get_params[17]);
                params.put("wl_notify", get_params[18]);
                params.put("l_notify", get_params[19]);
                params.put("relays_notify", get_params[20]);
                params.put("pumps_notify", get_params[21]);
                params.put("period", get_params[22]);
                params.put("vibrate", get_params[23]);
                params.put("color", get_params[24]);
                //params.put("sound", post_params[24]);

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
        protected String doInBackground(String... get_params) {
            // params comes from the execute() call: params[0] is the url.
            try {
                String url="http://"+get_params[0]+"/core/add_data/edit_ctrl_name.php";
                params.put("ctrl_id", get_params[1]);
                params.put("hash", get_params[2]);
                params.put("ctrl_name", get_params[3]);

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
        protected String doInBackground(String... get_params) {
            // params comes from the execute() call: params[0] is the url.
            try {
                String url="http://"+get_params[0]+"/core/registration.php";
                params.put("username", get_params[1]);
                params.put("email", get_params[2]);
                params.put("password", get_params[3]);

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
        protected String doInBackground(String... get_params) {
            // params comes from the execute() call: params[0] is the url.
            try {
                String url="http://"+get_params[0]+"/core/get_data/activate_dev.php";
                params.put("username", get_params[1]);
                params.put("hash", get_params[2]);
                params.put("ctrl_id", get_params[3]);
                params.put("ctrl_name", get_params[4]);
                params.put("ctrl_an", get_params[5]);
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

            StringBuilder Data = new StringBuilder();

            for (Map.Entry<String,Object>  param : params.entrySet()) {
                if (Data.length() != 0) Data.append('&');
                Data.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                Data.append('=');
                Data.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }

            byte[] DataBytes = Data.toString().getBytes("UTF-8");

            myurl=myurl + "?" + Data.toString();
            Log.d(LOG_TAG,"Final URL: " + myurl);

            URL url = new URL(myurl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            //conn.setReadTimeout(3000 /* milliseconds */);
            //conn.setConnectTimeout(6000 /* milliseconds */);

            //Below property for POST request
            //conn.setRequestMethod("POST");
            //conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //conn.setDoOutput(true);
            //conn.setDoInput(true);
            //conn.setRequestProperty("Content-Length", String.valueOf(DataBytes.length));

            //conn.getOutputStream().write(DataBytes);

            // Starts the query
            //conn.connect();

            int response_code = conn.getResponseCode();
            if(response_code != 200)return null;
            Log.d(LOG_TAG, "The response code is: " + response_code);
            is = conn.getInputStream();


            //len=is.available();
            //len=1024;
            //Log.d(LOG_TAG, "conn.getInputStream() is: " + is);
            // Convert the InputStream into a string

            String contentAsString = readIt(is, buffer_size);
            is.close();
            //Log.d(LOG_TAG, "contentAsString " + is);

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
    private static String readIt(InputStream stream, int len) throws IOException {
        Reader reader;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];

        reader.read(buffer);
        //Log.d(LOG_TAG, "Reader: " + buffer);
        return new String(buffer);
    }
}
