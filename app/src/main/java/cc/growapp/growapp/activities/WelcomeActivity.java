package cc.growapp.growapp.activities;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.growapp.growapp.GrowappConstants;
import cc.growapp.growapp.DataBroker;
import cc.growapp.growapp.database.MyContentProvider;
import cc.growapp.growapp.JSONHandler;
import cc.growapp.growapp.R;


public class WelcomeActivity extends AppCompatActivity implements
        DataBroker.authentication.onAuthComplete,
        DataBroker.get_controllers.onGetCtrlsComplete,
        DataBroker.get_pref_profile.onGetPrefVersionComplete,
        DataBroker.get_dev_profile.onGetDevProfileComplete,
        DataBroker.get_system_state.onGetSystemStateComplete,
        DataBroker.registration.onRegistrationComplete {



    String LOG_TAG="WelcomeActivity";

    SharedPreferences sPref;

    String hash;

    int ctrls_count =0;
    int ctrls_processed=0;

    String saved_user,saved_pass,saved_hostname;
    String session_user,session_pass,session_hostname;
    TextView proc;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
            //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    //WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }


        proc = (TextView) findViewById(R.id.welcome_tv_proc);
        proc.setText("");

        sPref = getSharedPreferences(GrowappConstants.APP_PREFERENCES, MODE_PRIVATE);
        saved_user = sPref.getString("user", "");
        saved_pass = sPref.getString("pass", "");
        saved_hostname = sPref.getString("hostname", GrowappConstants.DEFAULT_HOSTNAME);


        /*if(!sPref.contains("RingTone"))sPref.edit().putString("RingTone", String.valueOf(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))).apply();
        if(!sPref.contains("Vibrator"))sPref.edit().putString("Vibrator", "Short").apply();
        if(!sPref.contains("NotifColor"))sPref.edit().putInt("NotifColor", -16711936).apply();*/


        if(!saved_user.equals("") && !saved_pass.equals("") && !saved_hostname.equals("")){
            if(check_network()){
                proc.setText(getString(R.string.auth));
                new DataBroker.authentication(this).execute(saved_hostname,saved_user, saved_pass);
            } else {
                Toast.makeText(this,"No network", Toast.LENGTH_SHORT).show();
            }
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //pDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        session_user="";
        session_pass="";
        proc.setText("");
    }

    public void SignIn(View v) {
        //proc.setText("");
        if(!session_user.isEmpty() && !session_pass.isEmpty()){
            if(check_network()){

                ctrls_count =0;
                ctrls_processed=0;

                new DataBroker.authentication(this).execute(session_hostname, session_user, session_pass);
            } else {
                Toast.makeText(this,"No network", Toast.LENGTH_SHORT).show();
            }
        } else  {
            // -------------------------------- Форма аутенфикации ----------------------------
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();

            RelativeLayout rl = (RelativeLayout) findViewById(R.id.welcome_rl);
            final View dialogView = inflater.inflate(R.layout.dialog_auth_form, rl,false);

            dialogBuilder.setView(dialogView);

            final EditText edt_user = (EditText) dialogView.findViewById(R.id.edit_username);
            final EditText edt_pass = (EditText) dialogView.findViewById(R.id.edit_password);
            final EditText edt_host = (EditText) dialogView.findViewById(R.id.edit_hostname);
            final CheckBox checkBox = (CheckBox) dialogView.findViewById(R.id.chkBoxLogin);

            if(!saved_user.equals(""))edt_user.setText(sPref.getString("user", ""));
            if(!saved_pass.equals(""))edt_pass.setText(sPref.getString("pass", ""));
            if(!saved_hostname.equals(""))edt_host.setText(sPref.getString("hostname", GrowappConstants.DEFAULT_HOSTNAME));

            if(!saved_user.equals("") && !saved_pass.equals("") && !saved_hostname.equals(""))checkBox.setChecked(true);
            else checkBox.setChecked(false);

            dialogBuilder.setTitle("Введите учетные данные");
            //dialogBuilder.setMessage("Новое название");
            dialogBuilder.setPositiveButton("Войти", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    session_user = edt_user.getText().toString().trim();
                    session_pass = edt_pass.getText().toString().trim();
                    session_hostname = edt_host.getText().toString().trim();


                    SharedPreferences.Editor ed = sPref.edit();

                    ed.putString("user", session_user);
                    ed.putString("hostname", session_hostname);

                    if (checkBox.isChecked()) {
                        ed.putString("pass", session_pass);

                        saved_user=session_user;
                        saved_pass=session_pass;
                        saved_hostname=session_hostname;
                    } else {
                        ed.remove("pass");
                        ed.remove("hash");
                        saved_pass="";
                    }
                    ed.apply();

                    Button btn_signin = (Button) findViewById(R.id.btn_signin);
                    btn_signin.performClick();
                    btn_signin.setPressed(true);
                    btn_signin.invalidate();
                    btn_signin.setPressed(false);
                    btn_signin.invalidate();


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
    }
    public void Registration(View v) {
        // -------------------------------- Форма регистрации ----------------------------
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        //final View dialogView = inflater.inflate(R.layout.dialog_add_dev, null);

        //LayoutInflater inflater = this.getLayoutInflater();
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.welcome_rl);
        final View dialogView = inflater.inflate(R.layout.dialog_registration, rl,false);

        final EditText username = (EditText) dialogView.findViewById(R.id.et_reg_login);
        final EditText email = (EditText) dialogView.findViewById(R.id.et_reg_email);
        final EditText pass1 = (EditText) dialogView.findViewById(R.id.et_reg_pass1);
        final EditText pass2 = (EditText) dialogView.findViewById(R.id.et_reg_pass2);


        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Введите учетные данные");
        dialogBuilder.setPositiveButton("Подтвердить", null);
        dialogBuilder.setNegativeButton("Отмена", null);
        final AlertDialog b = dialogBuilder.create();


        b.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {

                Button okButton = b.getButton(AlertDialog.BUTTON_POSITIVE);
                okButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        if (username.getText() != null && email.getText() != null
                                && pass1.getText() != null && pass2.getText() != null) {
                            String username_data, email_data, pass1_data, pass2_data;

                            username_data = username.getText().toString().trim();
                            email_data = email.getText().toString().trim();
                            pass1_data = pass1.getText().toString().trim();
                            pass2_data = pass2.getText().toString().trim();

                            if (username_data.equals("")){
                                username.requestFocus();
                                Toast.makeText(WelcomeActivity.this, "Введите логин", Toast.LENGTH_SHORT).show();
                            }
                            else if (email_data.equals("")){
                                email.requestFocus();
                                Toast.makeText(WelcomeActivity.this, "Введите email", Toast.LENGTH_SHORT).show();
                            }
                            else if (pass1_data.equals("")){
                                pass1.requestFocus();
                                Toast.makeText(WelcomeActivity.this, "Пароль не может быть пустым", Toast.LENGTH_SHORT).show();
                            }
                            else if (pass2_data.equals("")){
                                Toast.makeText(WelcomeActivity.this, "Пароль не может быть пустым", Toast.LENGTH_SHORT).show();
                                pass2.requestFocus();
                            }
                            else {
                                if (pass1_data.equals(pass2_data)) {
                                    b.dismiss();
                                    new DataBroker.registration(WelcomeActivity.this).execute(saved_hostname,username_data, email_data, pass1_data);
                                } else
                                    Toast.makeText(WelcomeActivity.this, "Пароли несовпадают", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
        b.show();
    }

    @Override
    public void onAuthCompleteMethod(String s) {
        Log.d(LOG_TAG, "Callback, user hash: " + s);
        if(s!=null){
            proc.setText("Аутенфикация: успех");

            sPref = getSharedPreferences(GrowappConstants.APP_PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString("hash", s);
            ed.apply();
            String saved_user = sPref.getString("user", "");
            hash = sPref.getString("hash", "");
            new DataBroker.get_controllers(this).execute(saved_hostname,saved_user, s);
        } else {
            session_user="";
            session_pass="";
            session_hostname="";
            proc.setText("");
            Toast.makeText(this,getString(R.string.no_auth), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onGetCtrlsCompleteMethod(String s) {
        if (s != null) {
            //pDialog.setMessage("Получения списка контроллеров");
            proc.setText("Cписок устройств: успех");

            //Clean DB from orphaned devices
            getContentResolver().delete(MyContentProvider.CTRLS_CONTENT_URI, null, null);

            try {
                JSONObject json = new JSONObject(s);
                int success;
                String TAG_SUCCESS="success";
                success = json.getInt(TAG_SUCCESS);
                if (success == 1){

                    // Check your log cat for JSON reponse
                    Log.d(LOG_TAG, "JSON строка = "+json.toString());
                    // Успешно получены данные
                    JSONArray CtrlObj = json.getJSONArray("data");
                    Log.d(LOG_TAG, "JSON данные получены ...");

                    ctrls_count=CtrlObj.length();
                    Log.d(LOG_TAG, "Количество контроллеров = "+ctrls_count);

                    // получаем обьекты с JSON Array
                    for(int i=0;i<CtrlObj.length();i++){
                        JSONObject ctrl = CtrlObj.getJSONObject(i);

                        //int ctrl_id = Integer.parseInt(ctrl.getString("ctrl_id"));
                        String ctrl_id = ctrl.getString("ctrl_id");
                        String ctrl_name = ctrl.getString("name");
                        String ctrl_an = ctrl.getString("an");

                        Log.d(LOG_TAG, i + " объект JSON получен = " + ctrl);
                        Log.d(LOG_TAG, i + " Контроллер ИД = " + ctrl_id);
                        Log.d(LOG_TAG, i + " Имя контроллера = " + ctrl_name);
                        Log.d(LOG_TAG, i + " AN контроллера = " + ctrl_an);

                        //Запихиваем в БД
                        //Controllers controller = new Controllers(ctrl_id,ctrl_name,ctrl_an);
                        //db.createCtrl(controller);


                        ContentValues cv = new ContentValues();
                        cv.put(MyContentProvider.KEY_CTRL_ID, ctrl_id);
                        cv.put(MyContentProvider.KEY_CTRL_NAME, ctrl_name);
                        cv.put(MyContentProvider.KEY_CTRL_AN, ctrl_an);



                        Uri newUri = getContentResolver().insert(MyContentProvider.CTRLS_CONTENT_URI, cv);
                        Log.d(LOG_TAG, "URI to dispatch: " + (newUri != null ? newUri.toString() : null));


                        //Запускаем процесс получения настроек пользователя
                        new DataBroker.get_pref_profile(this).execute(saved_hostname,ctrl_id, hash);
                        //Запускаем процесс получения комплектации устройства
                        new DataBroker.get_dev_profile(this).execute(saved_hostname,ctrl_id, hash);
                        //Запускаем процесс получения данных датчиков
                        new DataBroker.get_system_state(this).execute(saved_hostname,ctrl_id, hash);
                    }
                    // Don't forget to close database connection

                }else{
                    //pDialog.dismiss();
                    //Если контроллеров не нашли, переходим в аккаунт инфо, чтоб добавить контроллеры
                    Intent intent = new Intent(WelcomeActivity.this,AccountActivity.class);
                    startActivity(intent);
                    Log.d(LOG_TAG, "Данные не найдены!");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                //pDialog.dismiss();
                //Toast.makeText(this,R.string.no_data, Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this,R.string.no_data, Toast.LENGTH_SHORT).show();
            //pDialog.dismiss();
        }
    }

    @Override
    public void onGetPrefProfileCompleteMethod(String s) {
        Log.d(LOG_TAG, "Callback, pref data: " + s);
        if(s!=null){
            ContentValues result= new JSONHandler().ParseJSONProfile(s);
            if(result.size()>0){
                String ctrl_id = result.getAsString("ctrl_id");
                Uri newUri = ContentUris.withAppendedId(MyContentProvider.PREF_CONTENT_URI, Long.parseLong(ctrl_id));
                int cnt = getContentResolver().update(newUri, result, null, null);
                Log.d(LOG_TAG, "Cnt updated: " + cnt);

                if (cnt == 0) {
                    newUri = getContentResolver().insert(MyContentProvider.PREF_CONTENT_URI, result);
                    Log.d(LOG_TAG, "URI to dispatch: " + (newUri != null ? newUri.toString() : null));
                }

                proc.setText("Настройка пользователя: успех");



            }
        }
    }
    @Override
    public void onGetDevProfileCompleteMethod(String s) {
        Log.d(LOG_TAG, "Callback, dev profile: " + s);
        if(s!=null){
            ContentValues result= new JSONHandler().ParseJSONDevProfile(s);
            if(result.size()>0){

                proc.setText("Профиль устройства: успех");



                String ctrl_id = result.getAsString("ctrl_id");
                Uri newUri = ContentUris.withAppendedId(MyContentProvider.DEV_PROFILE_CONTENT_URI, Long.parseLong(ctrl_id));
                int cnt = getContentResolver().update(newUri, result, null, null);
                Log.d(LOG_TAG, "Cnt updated: " + cnt);
                if(cnt==0){
                    newUri = getContentResolver().insert(MyContentProvider.DEV_PROFILE_CONTENT_URI, result);
                    Log.d(LOG_TAG, "URI to dispatch: " + (newUri != null ? newUri.toString() : null));
                }


            }
        }
    }
    @Override
    public void onGetSystemStateCompleteMethod(String s) {
        Log.d(LOG_TAG, "Callback, system state: " + s);
        if(s!=null){

            ContentValues result= new JSONHandler().ParseJSONSystemState(s);
            if(result!=null){

                proc.setText("Данные датчиков: успех");


                String ctrl_id = result.getAsString("ctrl_id");
                Uri newUri = ContentUris.withAppendedId(MyContentProvider.MAIN_CONTENT_URI, Long.parseLong(ctrl_id));
                int cnt = getContentResolver().update(newUri, result, null, null);
                Log.d(LOG_TAG, "Cnt updated: " + cnt);

                if(cnt==0){
                    newUri = getContentResolver().insert(MyContentProvider.MAIN_CONTENT_URI, result);
                    Log.d(LOG_TAG, "URI to dispatch: " + (newUri != null ? newUri.toString() : null));
                }


            }
        }


        ctrls_processed=ctrls_processed+1;
        if(ctrls_processed == ctrls_count) {
            //pDialog.setMessage("Запускаю MainActivity");
            //pDialog.dismiss();
            proc.setText("");
            Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onRegistrationCompleteMethod(String s) {
        if(s!=null){
            if(s.equals("success")){
                Toast.makeText(this, "Регистрация завершена успешна", Toast.LENGTH_SHORT).show();

                //Очищаем БД
                truncate_db();

                //Если успешно создан аккаунт, то
                //Если контроллеров не нашли, переходим в аккаунт инфо, чтоб добавить контроллеры
                Intent intent = new Intent(WelcomeActivity.this,AccountActivity.class);
                startActivity(intent);
            } else Toast.makeText(this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
        }
    }
    public boolean check_network(){
        proc.setText(R.string.check_network);
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    public void truncate_db(){
        //Clearing database
        int delRows = getContentResolver().delete(MyContentProvider.CTRLS_CONTENT_URI, null, null);
        Log.d(LOG_TAG, "Controllers deleted: " + delRows);
        delRows = getContentResolver().delete(MyContentProvider.PREF_CONTENT_URI, null, null);
        Log.d(LOG_TAG, "Preferences deleted: " + delRows);
        delRows = getContentResolver().delete(MyContentProvider.DEV_PROFILE_CONTENT_URI, null, null);
        Log.d(LOG_TAG, "Device profile deleted: " + delRows);
        delRows = getContentResolver().delete(MyContentProvider.MAIN_CONTENT_URI, null, null);
        Log.d(LOG_TAG, "Main deleted: " + delRows);
    }

}
