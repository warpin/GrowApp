package cc.growapp.growapp.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;


import java.util.LinkedList;
import java.util.List;

import cc.growapp.growapp.DataBroker;
import cc.growapp.growapp.database.Controllers;
import cc.growapp.growapp.database.DatabaseHelper;
import cc.growapp.growapp.database.Dev_profile;
import cc.growapp.growapp.database.SystemState;
import cc.growapp.growapp.fragments.Frag_hum;
import cc.growapp.growapp.fragments.Frag_light_off;
import cc.growapp.growapp.fragments.Frag_light_on;
import cc.growapp.growapp.fragments.Frag_pot1;
import cc.growapp.growapp.fragments.Frag_pot2;
import cc.growapp.growapp.fragments.Frag_temp;
import cc.growapp.growapp.fragments.Frag_wcan1_off;
import cc.growapp.growapp.fragments.Frag_wcan1_on;
import cc.growapp.growapp.fragments.Frag_wcan2_off;
import cc.growapp.growapp.fragments.Frag_wcan2_on;
import cc.growapp.growapp.JSONHandler;
import cc.growapp.growapp.MyAlarmReceiver;
import cc.growapp.growapp.R;


public class MainActivity extends AppCompatActivity implements
        View.OnTouchListener,
        DataBroker.set_action.onSetActionComplete,
        DataBroker.get_system_state.onGetSystemStateComplete{

    boolean prog_toggle=true;
    private static final String LOG_TAG = "GrowApp";
    private static final String LOG_TAG_FRAG = "Frag";



    SharedPreferences sPref;
    public static final String APP_PREFERENCES = "GrowAppSettings";

    // Progress Dialog
    private ProgressDialog pDialog;


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
    int auto_watering1;
    int auto_watering2;

    // Database Helper
    DatabaseHelper db;

    FrameLayout wcan1;
    FrameLayout wcan2;
    private TextView answerField,actionField;
    TextView tv_wcan1_type,tv_wcan2_type;


    String hash;


    private String controller_id;
    private String [] ctrl_spinner_data;
    private String [] ctrl_ids_data;
    private List<String> ctrl_ids_list =new LinkedList<>();
    private List<String> ctrl_names_list =new LinkedList<>();

    float x1=0;
    float y1=0;
    float x2=0;
    float y2=0;

    boolean pump1_state=false;
    boolean pump2_state=false;

    long last_refresh_time;

    // Progress Dialog
    //private ProgressDialog pDialog;

    FragmentTransaction fTrans;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //scheduleAlarm();
        //cancelAlarm();

        setContentView(R.layout.activity_main);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.main_activity_layout);

        wcan1 = (FrameLayout) findViewById(R.id.frgm_wcan1);
        wcan2 = (FrameLayout) findViewById(R.id.frgm_wcan2);

        tv_wcan1_type = new TextView(this);
        tv_wcan2_type = new TextView(this);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,Gravity.END|Gravity.RIGHT);
        tv_wcan2_type.setLayoutParams(params);



        wcan1.addView(tv_wcan1_type);
        wcan2.addView(tv_wcan2_type);


        actionField = (TextView) findViewById(R.id.tv_main_action);
        answerField = (TextView) findViewById(R.id.tv_main_answer);

        rl.setOnTouchListener(this);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.app_name));
        }

        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        hash = sPref.getString("hash", "");

        db = new DatabaseHelper(getApplicationContext());
        pDialog = new ProgressDialog(MainActivity.this);



        Switch relay1 = (Switch) findViewById(R.id.tbtn_sw1);
        Switch relay2 = (Switch) findViewById(R.id.tbtn_sw2);

        // ------------------------------ Обработчик переключателя №1 ---------------------------
        relay1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (controller_id!=null && !controller_id.isEmpty() && !prog_toggle) {
                    Log.d(LOG_TAG, "Manual SW 1 State Change");
                    if (isChecked) {
                        launch_set_action("11");
                        //new ActionHandler(getApplicationContext(), statusField, reciviedField).execute(user_hash, controller_id, "11"); //Сигнал Arduino на реле 1 включить
                        // The toggle is enabled
                    } else {
                        launch_set_action("10");
                        //new ActionHandler(getApplicationContext(), statusField, reciviedField).execute(user_hash, controller_id, "10"); //Сигнал Arduino на реле 2 выключить
                        // The toggle is disabled
                    }
                    //call_to_refresh();
                }
            }
        });
        // -----------------------------------------------------------------------------------------
        // ------------------------------ Обработчик переключателя №2 ---------------------------
        relay2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (controller_id!=null && !controller_id.isEmpty() && !prog_toggle) {
                    Log.d(LOG_TAG, "Manual SW 2 State Change");
                    if (isChecked) {
                        launch_set_action("21");
                        //new ActionHandler(getApplicationContext(), statusField, reciviedField).execute(user_hash, controller_id, "21"); //Сигнал Arduino на реле 1 включить
                        // The toggle is enabled
                    } else {
                        launch_set_action("20");
                        //new ActionHandler(getApplicationContext(), statusField, reciviedField).execute(user_hash, controller_id, "20"); //Сигнал Arduino на реле 2 выключить
                        // The toggle is disabled
                    }
                    //call_to_refresh();
                }
            }
        });
        // -----------------------------------------------------------------------------------------
        // -------------------------------- Спиннер контроллеров -----------------------------------
        final Spinner spinner = (Spinner) findViewById(R.id.ctrl_spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                controller_id = ctrl_ids_data[spinner.getSelectedItemPosition()];
                //spinner.getSelectedItem().toString();
                Log.d(LOG_TAG, "Selected controller: " + controller_id);

                if (controller_id!=null && !controller_id.isEmpty()) populateFragments(controller_id);
                //new GetSystemState().execute();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        if(GetControllersListFromSQL())populateSpinner();
        // -----------------------------------------------------------------------------------------
        // -------------------------------- Обновление показателей каждые 5 минут -----------------
        /*Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(5000*60);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    new GetSystemState().execute();
                                } catch (NullPointerException e){
                                }
                                // update TextView here!
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();*/
        // -----------------------------------------------------------------------------------------
    }
    public void populateSpinner(){
        Spinner spinner = (Spinner) findViewById(R.id.ctrl_spinner);
        Log.d(LOG_TAG, "Spinner [0]=" + ctrl_spinner_data[0]);
        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ctrl_spinner_data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        Intent intent = getIntent();
        //Нужно для того, если вывана активити с уведомления
        if (intent.hasExtra("controller_id")) {
            //spinner.setSelection(ctrl_ids_list.indexOf(getIntent().getExtras().getString("controller_id")));
            controller_id = intent.getStringExtra("controller_id");
            //Если вызвана с уведомления, то запоминаем ИД и удаляем его, чтоб правильно отображались фрагменты
            intent.removeExtra("controller_id");
        }
        if (controller_id!=null && !controller_id.isEmpty()) {
            //spinner.setSelection(ctrl_ids_list.indexOf(String.valueOf("controller_id")));
            spinner.setSelection(ctrl_ids_list.indexOf(controller_id));
            //Log.d(LOG_TAG, "!!!!!!!!!!!!!!!!!!!!!!!!" + controller_id + " " + ctrl_ids_list.indexOf(controller_id));
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        GetControllersListFromSQL();
        populateSpinner();
        if (controller_id != null && !controller_id.isEmpty())populateFragments(controller_id);
    }

    private void launch_set_action(String action) {
        actionField.setText(action);


        //Запишем в локльную БД, состояние измененых реле, насосов, чтоб после обновления экрана
        //изменения отобразились на экране
        SystemState state = db.getSystemState(controller_id);
        switch (action){
            case "10":state.set_relay1_state(0);break;
            case "11":state.set_relay1_state(1);break;
            case "20":state.set_relay2_state(0);break;
            case "21":state.set_relay2_state(1);break;
            case "30":state.set_pump1_state(0);break;
            case "31":state.set_pump1_state(1);break;
            case "40":state.set_pump2_state(0);break;
            case "41":state.set_pump2_state(1);break;
        }


        new DataBroker.set_action(this).execute(String.valueOf(controller_id), hash, action);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Операции для выбранного пункта меню
        switch (item.getItemId())
        {
            case R.id.pref:
                preferences();
                return true;
            case R.id.account:
                Intent account_intent = new Intent(MainActivity.this,AccountActivity.class);
                startActivity(account_intent);
                return true;
            case R.id.info:
                Intent info_intent = new Intent(MainActivity.this,InfoActivity.class);
                startActivity(info_intent);
                return true;
            case R.id.graphs:
                if(controller_id!=null && !controller_id.isEmpty()){
                    String ctrl_id_string=String.valueOf(controller_id);
                    Intent intent = new Intent(MainActivity.this,GraphsActivity.class);
                    intent.putExtra("controller_id", ctrl_id_string);
                    startActivity(intent);
                }
                //showHelp();
                return true;
            case R.id.exit:
                finish();
            case  android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (pDialog != null) {
            pDialog.dismiss();
//            pDialog = null;
        }

    }

    public void preferences() {
        Log.d(LOG_TAG, "Активити настроек");
        if(controller_id!=null && !controller_id.isEmpty()){
            String ctrl_id_string=String.valueOf(controller_id);
            Intent intent = new Intent(MainActivity.this,PreferencesActivity.class);
            intent.putExtra("controller_id", ctrl_id_string);
            startActivity(intent);
        }


    }

    public void populateFragments(String ctrl_id){

        TextView water_level_label = (TextView) findViewById(R.id.textView14);
        TextView water_level = (TextView) findViewById(R.id.tv_wl_data);

        TextView date = (TextView) findViewById(R.id.tv_date);
        Switch relay1 = (Switch) findViewById(R.id.tbtn_sw1);
        Switch relay2 = (Switch) findViewById(R.id.tbtn_sw2);

        FrameLayout light = (FrameLayout) findViewById(R.id.frgm_light);
        FrameLayout t = (FrameLayout) findViewById(R.id.frgm_temp);
        FrameLayout h = (FrameLayout) findViewById(R.id.frgm_hum);
        FrameLayout pot1 = (FrameLayout) findViewById(R.id.frgm_pot1);
        FrameLayout pot2 = (FrameLayout) findViewById(R.id.frgm_pot2);


        //Выцепляем с базы, какие компоненты системы нам доступны
        //После чего заполняем экран фрагментами, соответсвующие комплектации
        Dev_profile dev_profile = db.getDevProfile(ctrl_id);
        //Log.d("Tag Name", dev_profile.getTagName());
        l_control= dev_profile.get_light_control();
        t_control= dev_profile.get_t_control();
        h_control= dev_profile.get_h_control();
        pot1_control= dev_profile.get_pot1_control();
        pot2_control= dev_profile.get_pot2_control();
        pump1_control= dev_profile.get_pump1_control();
        pump2_control= dev_profile.get_pump2_control();
        relay1_control= dev_profile.get_relay1_control();
        relay2_control= dev_profile.get_relay2_control();
        water_control= dev_profile.get_water_control();
        auto_watering1= dev_profile.get_auto_watering1();
        auto_watering2= dev_profile.get_auto_watering2();

        SystemState state = db.getSystemState(ctrl_id);
        int l_result= state.get_light_state();
        int t_result= state.get_t();
        int h_result= state.get_h();
        int pot1_h_result= state.get_pot1_h();
        int pot2_h_result= state.get_pot2_h();
        int p1_result= state.get_pump1_state();
        int p2_result= state.get_pump2_state();
        int relay1_result= state.get_relay1_state();
        int relay2_result= state.get_relay2_state();
        int w_result= state.get_water_level();
        String date_result= state.get_date();

        light.setVisibility(View.VISIBLE);
        t.setVisibility(View.VISIBLE);
        h.setVisibility(View.VISIBLE);
        pot1.setVisibility(View.VISIBLE);
        pot2.setVisibility(View.VISIBLE);

        relay1.setVisibility(View.VISIBLE);
        relay2.setVisibility(View.VISIBLE);
        water_level.setVisibility(View.VISIBLE);
        water_level_label.setVisibility(View.VISIBLE);

        //Инициализация фрагментов в Активити
        Frag_temp frag_temp;
        Frag_hum frag_hum;
        Frag_pot1 frag_pot1;
        Frag_pot2 frag_pot2;

        Frag_light_on frag_light_on;
        Frag_light_off frag_light_off;

        frag_temp = new Frag_temp();
        frag_hum = new Frag_hum();
        frag_pot1 = new Frag_pot1();
        frag_pot2 = new Frag_pot2();

        frag_light_on = new Frag_light_on();
        frag_light_off = new Frag_light_off();

        //Начинаем транзакцию фрагментов
        fTrans = getFragmentManager().beginTransaction();
        fTrans.disallowAddToBackStack();

        // ---------------------- Работаем с фрагментом "СВЕТ" -----------------------------
        String frag_l_on_tag = "frag_l_on";
        String frag_l_off_tag = "frag_l_off";
        Frag_light_on f_l_on = (Frag_light_on) getFragmentManager().findFragmentByTag(frag_l_on_tag);
        Frag_light_off f_l_off = (Frag_light_off) getFragmentManager().findFragmentByTag(frag_l_off_tag);
        Log.d(LOG_TAG, "Состояние света = " + l_result);

        if(l_control==1){
            //Если свет включен
            if(l_result==1) {
                //Если нашли фрагмент "нет света"
                if(f_l_off != null){
                    //Если  фрагмент "нет света" добавлен
                    if(f_l_off.isAdded()){
                        //То заменяем на "есть свет"
                        fTrans.replace(R.id.frgm_light, frag_light_on, frag_l_on_tag);
                        Log.d(LOG_TAG_FRAG,"Фрагмент SUN добавлен заменой");
                    }
                } else {
                    //Если фрамент "есть свет" не добавлен
                    if(f_l_on==null){
                        Log.d(LOG_TAG_FRAG, "Фрагмент SUN добавлен");
                        fTrans.add(R.id.frgm_light, frag_light_on, frag_l_on_tag);
                    }
                }
                //toggle.setChecked(true);
                //light_state.setText("Включен");
            }
            else {
                if( f_l_on != null){
                    if(f_l_on.isAdded()){
                        fTrans.replace(R.id.frgm_light, frag_light_off, frag_l_off_tag);
                        Log.d(LOG_TAG_FRAG, "Фрагмент MOON добавлен заменой");
                    }
                } else {
                    if(f_l_off==null){
                        Log.d(LOG_TAG_FRAG,"Фрагмент MOON добавлен");
                        fTrans.add(R.id.frgm_light, frag_light_off,frag_l_off_tag);
                    }
                }
            }

        } else {
            light.setVisibility(View.INVISIBLE);
            if(f_l_off != null)fTrans.remove(f_l_off);
            if(f_l_on != null)fTrans.remove(f_l_on);
        }

        // ---------------------------------------------------------------------------------
        // ---------------------- Работаем с фрагментом "T and H" --------------------------
        String frag_temp_tag = "frag_temp";
        String frag_hum_tag = "frag_hum";
        Frag_temp f_temp = (Frag_temp) getFragmentManager().findFragmentByTag(frag_temp_tag);
        Frag_hum f_hum = (Frag_hum) getFragmentManager().findFragmentByTag(frag_hum_tag);


        Log.d(LOG_TAG, "Температура получена = " + t_result + "`C");
        Log.d(LOG_TAG, "Влажнось получена = " + h_result + "%");

        if(t_control==1){
            if( f_temp == null){
                fTrans.add(R.id.frgm_temp, frag_temp, frag_temp_tag);
                Log.d(LOG_TAG_FRAG,"Фрагмент TEMP добавлен");
            }
        } else t.setVisibility(View.INVISIBLE);
        if(h_control==1){
            if( f_hum == null){
                fTrans.add(R.id.frgm_hum, frag_hum, frag_hum_tag);
                Log.d(LOG_TAG_FRAG, "Фрагмент HUM добавлен");
            }
        } else h.setVisibility(View.INVISIBLE);
        // ---------------------------------------------------------------------------------
        // ---------------------- Работаем с фрагментом "Горшки" ---------------------------
        String frag_pot1_tag = "frag_pot1";
        String frag_pot2_tag = "frag_pot2";
        Frag_pot1 f_pot1 = (Frag_pot1) getFragmentManager().findFragmentByTag(frag_pot1_tag);
        Frag_pot2 f_pot2 = (Frag_pot2) getFragmentManager().findFragmentByTag(frag_pot2_tag);

        Log.d(LOG_TAG, "Влажнось почвы 1 = " + pot1_h_result + "%");
        Log.d(LOG_TAG, "Влажнось почвы 2 = " + pot2_h_result + "%");
        if(pot1_control==1){
            if( f_pot1 == null){
                fTrans.add(R.id.frgm_pot1, frag_pot1, frag_pot1_tag);
                Log.d(LOG_TAG_FRAG,"Фрагмент POT1 добавлен");
            }
        } else pot1.setVisibility(View.INVISIBLE);
       
        if(pot2_control==1){
            if( f_pot2 == null){
                fTrans.add(R.id.frgm_pot2, frag_pot2, frag_pot2_tag);
                Log.d(LOG_TAG_FRAG, "Фрагмент POT2 добавлен");
            }
        }else pot2.setVisibility(View.INVISIBLE);
        //pot2.setVisibility(View.INVISIBLE);
        // ---------------------------------------------------------------------------------

        //Подтвердим наполнение фрагментами
        fTrans.commit();


        // ---------------------- Работаем с фрагментами "Лейка" ---------------------------
        if(p1_result==1) {
            pump1_state = true;
            add_wcan1_fragment(true);
        } else {
            pump1_state = false;
            add_wcan1_fragment(false);
        }
        if(p2_result==1) {
            pump2_state = true;
            add_wcan2_fragment(true);
        } else {
            pump2_state = false;
            add_wcan2_fragment(false);
        }
        // ---------------------------------------------------------------------------------

        //Проверяем все ли фрагменты обработались менеджером фрагментов
        if(getFragmentManager().executePendingTransactions()){
            f_temp = (Frag_temp) getFragmentManager().findFragmentByTag(frag_temp_tag);
            f_hum = (Frag_hum) getFragmentManager().findFragmentByTag(frag_hum_tag);
            f_pot1 = (Frag_pot1) getFragmentManager().findFragmentByTag(frag_pot1_tag);
            f_pot2 = (Frag_pot2) getFragmentManager().findFragmentByTag(frag_pot2_tag);
            //Если да, заполним их данными
            if(f_temp!=null)f_temp.setText(t_result+"'C");
            if(f_hum!=null)f_hum.setText(h_result+"%");
            if(f_pot1!=null)f_pot1.setText(pot1_h_result+"%");
            if(f_pot2!=null)f_pot2.setText(pot2_h_result+"%");
        }


        Log.d(LOG_TAG, "Уровень воды = " + w_result + "%");
        if(water_control==1){
            String w_result_string = String.valueOf(w_result)+"%";
            water_level.setText(w_result_string);
        } else{
            water_level.setVisibility(View.INVISIBLE);
            water_level_label.setVisibility(View.INVISIBLE);
        }


        prog_toggle=true;
        Log.d(LOG_TAG, "Состояние реле 1 = " + relay1_result);
        Log.d(LOG_TAG, "Состояние реле 2 = " + relay2_result);
        if(relay1_control==1){
            if(relay1_result==1)relay1.setChecked(true);else relay1.setChecked(false);
        } else relay1.setVisibility(View.INVISIBLE);
        if(relay2_control==1){
            if(relay2_result==1)relay2.setChecked(true);else relay2.setChecked(false);
        } else relay2.setVisibility(View.INVISIBLE);

        prog_toggle=false;

        //Запишем состояние системы в файл настроек, чтоб сервис не проверял, то что мы итак сдеалали сами
        //String refresh_date = date_result;
        date.setText(date_result);
        Log.d(LOG_TAG, "Дата получена = " + date_result);

        db.closeDB();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1=event.getX();
                y1=event.getY();
                x2=event.getX();
                y2=event.getY();
                //Log.d(LOG_TAG, String.valueOf(event.getX()));
                break;
            case MotionEvent.ACTION_MOVE:
                //Log.d(LOG_TAG, String.valueOf(event.getX())+" "+String.valueOf(event.getY()))
                x2=event.getX();
                y2=event.getY();
                break;
            case MotionEvent.ACTION_UP:
                Log.d(LOG_TAG, String.valueOf(Math.abs(y2 - y1)));
                if(Math.abs(y2 - y1)>=400) {
                    //Если прошло больше 10 секунд, с последнего изменения интерфейса, то разрешим обновится (см. call_to_refresh)
                    if(System.currentTimeMillis()-last_refresh_time>5000){

                        pDialog.show();
                        pDialog.setMessage(getString(R.string.loadingsystemstate));
                        //Запускаем процесс получения данных датчиков
                        new DataBroker.get_system_state(this).execute(controller_id, hash);
                        //new GetSystemState().execute();
                        last_refresh_time=System.currentTimeMillis();
                        call_to_refresh();
                    }
                }

                break;
        }
        return true;

    }
    // --------------- Загрузка доступных контроллеров с БД ----------------------------
    public boolean GetControllersListFromSQL() {

        ctrl_names_list.clear();
        ctrl_ids_list.clear();

        List<Controllers> allCtrls = db.getAllCtrls();
        for (Controllers ctrl : allCtrls) {
            //Log.d(LOG_TAG, "CTRLS Name = "+ctrl.get_name());
            Log.d(LOG_TAG, "ID контроллера: " + String.valueOf(ctrl.get_ctrl_id()));
            Log.d(LOG_TAG, "Name контроллера: " + ctrl.get_name());

            ctrl_ids_list.add(String.valueOf(ctrl.get_ctrl_id()));
            ctrl_names_list.add(ctrl.get_name());
        }

        //Преобразуем в массив
        ctrl_ids_data = ctrl_ids_list.toArray(new String[ctrl_ids_list.size()]);
        ctrl_spinner_data = ctrl_names_list.toArray(new String[ctrl_names_list.size()]);

        db.closeDB();

        return true;
    }
    // ---------------------------------------------------------------------------------------------
    public void pump1_control(){
        //Log.d(LOG_TAG, "PUMP 1 State: "+ String.valueOf(pump1_state));
        if (controller_id!=null && !controller_id.isEmpty()) {
            if (!pump1_state) {
                pump1_state=true;
                Log.d(LOG_TAG, "PUMP 1 On");
                add_wcan1_fragment(true);
                launch_set_action("31");
                //new ActionHandler(getApplicationContext(), statusField, reciviedField).execute(user_hash, controller_id, "31"); //Сигнал Arduino на насос 1 включить
            } else {
                pump1_state=false;
                add_wcan1_fragment(false);
                Log.d(LOG_TAG, "PUMP 1 Off");
                launch_set_action("30");
                //new ActionHandler(getApplicationContext(), statusField, reciviedField).execute(user_hash, controller_id, "30"); //Сигнал Arduino на насос 1 выключить
            }
            //call_to_refresh();
        }
    }
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    public void pump2_control(){
        //Log.d(LOG_TAG, "PUMP 2 State: "+ String.valueOf(pump1_state));
        if (controller_id!=null && !controller_id.isEmpty()) {
            if (!pump2_state) {
                pump2_state=true;
                Log.d(LOG_TAG, "PUMP 2 On");
                add_wcan2_fragment(true);
                launch_set_action("41");
                //new ActionHandler(getApplicationContext(), statusField, reciviedField).execute(user_hash, controller_id, "41"); //Сигнал Arduino на насос 2 включить
            } else {
                pump2_state=false;
                add_wcan2_fragment(false);
                Log.d(LOG_TAG, "PUMP 2 Off");
                launch_set_action("40");
                //new ActionHandler(getApplicationContext(), statusField, reciviedField).execute(user_hash, controller_id, "40"); //Сигнал Arduino на насос 2 выключить
            }
            //call_to_refresh();
        }
    }

    private void call_to_refresh(){
        launch_set_action("3");
        //new ActionHandler(getApplicationContext(),statusField, reciviedField).execute(user_hash,controller_id, "3"); //Сигнал Arduino на сбор данных
        last_refresh_time=System.currentTimeMillis();
    }



    @Override
    public void onSetActionCompleteMethod(String s) {
        if (s != null) {
            Log.d(LOG_TAG, "Callback, action: " + s);
            answerField.setText(s);

        } else {
            //Toast.makeText(this,R.string.no_data, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGetSystemStateCompleteMethod(String s) {
        Log.d(LOG_TAG, "Callback, system state: " + s);
        if(s!=null){
            SystemState result= new JSONHandler().ParseJSONSystemState(s);
            if(result!=null){
                pDialog.setMessage("Получение данных датчиков: успех");
                db.updateSystemState(result);
                populateFragments(controller_id);
            } else {
                pDialog.dismiss();
                //Toast.makeText(this,R.string.no_data, Toast.LENGTH_SHORT).show();
            }
        } else {
            // Toast.makeText(this,R.string.no_data, Toast.LENGTH_SHORT).show();
        }
        pDialog.dismiss();
    }

    private void add_wcan1_fragment(boolean state){
        // ---------------------- Работаем с фрагментами "Лейка" ---------------------------

        //FrameLayout wcan1 = (FrameLayout) findViewById(R.id.frgm_pot1);

        Frag_wcan1_off frag_wcan1_off;
        Frag_wcan1_on frag_wcan1_on;

        frag_wcan1_off = new Frag_wcan1_off();
        frag_wcan1_on = new Frag_wcan1_on();


        String frag_wcan1_on_tag = "frag_wcan1_on";
        String frag_wcan1_off_tag = "frag_wcan1_off";


        Frag_wcan1_on wcan1_on = (Frag_wcan1_on) getFragmentManager().findFragmentByTag(frag_wcan1_on_tag);
        Frag_wcan1_off wcan1_off = (Frag_wcan1_off) getFragmentManager().findFragmentByTag(frag_wcan1_off_tag);

        fTrans = getFragmentManager().beginTransaction();
        fTrans.disallowAddToBackStack();


        if(pump1_control==1){
            //Начинаем транзакцию фрагментов
            wcan1.setVisibility(View.VISIBLE);
            if(state){
                //Если нашли фрагмент "нет полива"
                if(wcan1_off != null){
                    //Если  фрагмент "нет полива" добавлен
                    if(wcan1_off.isAdded()){
                        //То заменяем на "есть полив"
                        fTrans.replace(R.id.frgm_wcan1, frag_wcan1_on, frag_wcan1_on_tag);
                        Log.d(LOG_TAG_FRAG,"Фрагмент ACT WCAN1 добавлен заменой");

                    }
                } else {
                    //Если фрамент "есть полив" не добавлен
                    if(wcan1_on==null){
                        Log.d(LOG_TAG_FRAG, "Фрагмент ACT WCAN1 добавлен");
                        fTrans.add(R.id.frgm_wcan1, frag_wcan1_on, frag_wcan1_on_tag);
                    }
                }

            } else {
                if( wcan1_on != null){
                    if(wcan1_on.isAdded()){
                        fTrans.replace(R.id.frgm_wcan1, frag_wcan1_off, frag_wcan1_off_tag);
                        Log.d(LOG_TAG_FRAG, "Фрагмент WCAN1 добавлен заменой");
                    }
                } else {
                    if(wcan1_off==null){
                        Log.d(LOG_TAG_FRAG,"Фрагмент WCAN1 добавлен");
                        fTrans.add(R.id.frgm_wcan1, frag_wcan1_off, frag_wcan1_off_tag);
                    }
                }
            }

        } else{
            if( wcan1_on != null)fTrans.remove(wcan1_on);
            if( wcan1_off != null)fTrans.remove(wcan1_off);
            wcan1.setVisibility(View.INVISIBLE);
        }
        //Подтвердим наполнение фрагментами
        fTrans.commit();


        if((pot1_control==1 || pot2_control==1) && pump1_control==1 && auto_watering1>0)tv_wcan1_type.setText("A");
        else tv_wcan1_type.setText("M");



    }
    private void add_wcan2_fragment(boolean state){
        // ---------------------- Работаем с фрагментами "Лейка" ---------------------------
        //FrameLayout wcan2 = (FrameLayout) findViewById(R.id.frgm_wcan_right);

        Frag_wcan2_off frag_wcan2_off;
        Frag_wcan2_on frag_wcan2_on;

        frag_wcan2_off = new Frag_wcan2_off();
        frag_wcan2_on = new Frag_wcan2_on();


        String frag_wcan2_on_tag = "frag_wcan2_on";
        String frag_wcan2_off_tag = "frag_wcan2_off";


        Frag_wcan2_on wcan2_on = (Frag_wcan2_on) getFragmentManager().findFragmentByTag(frag_wcan2_on_tag);
        Frag_wcan2_off wcan2_off = (Frag_wcan2_off) getFragmentManager().findFragmentByTag(frag_wcan2_off_tag);

        //Начинаем транзакцию фрагментов
        fTrans = getFragmentManager().beginTransaction();


        fTrans.disallowAddToBackStack();
        if(pump2_control==1){

            wcan2.setVisibility(View.VISIBLE);
            if(state){
                //Если нашли фрагмент "нет полива"
                if(wcan2_off != null){
                    //Если  фрагмент "нет полива" добавлен
                    if(wcan2_off.isAdded()){
                        //То заменяем на "есть полив"
                        fTrans.replace(R.id.frgm_wcan2, frag_wcan2_on, frag_wcan2_on_tag);
                        Log.d(LOG_TAG_FRAG,"Фрагмент ACT WCAN2 добавлен заменой");

                    }
                } else {
                    //Если фрамент "есть полив" не добавлен
                    if(wcan2_on==null){
                        Log.d(LOG_TAG_FRAG, "Фрагмент ACT WCAN2 добавлен");
                        fTrans.add(R.id.frgm_wcan2, frag_wcan2_on, frag_wcan2_on_tag);
                    }
                }

            } else {
                if( wcan2_on != null){
                    if(wcan2_on.isAdded()){
                        fTrans.replace(R.id.frgm_wcan2, frag_wcan2_off, frag_wcan2_off_tag);
                        Log.d(LOG_TAG_FRAG, "Фрагмент WCAN2 добавлен заменой");
                    }
                } else {
                    if(wcan2_off==null){
                        Log.d(LOG_TAG_FRAG,"Фрагмент WCAN2 добавлен");
                        fTrans.add(R.id.frgm_wcan2, frag_wcan2_off, frag_wcan2_off_tag);
                    }
                }
            }
        } else {
            if( wcan2_on != null)fTrans.remove(wcan2_on);
            if( wcan2_off != null)fTrans.remove(wcan2_off);
            wcan2.setVisibility(View.INVISIBLE);
        }
        //Подтвердим наполнение фрагментами
        fTrans.commit();

        if((pot1_control==1 || pot2_control==1) && pump2_control==1 && auto_watering2>0)tv_wcan2_type.setText("A");
        else tv_wcan2_type.setText("M");

    }


    // -------------------------------- Изменение названия устройства ----------------------------
    public void EditCtrl(View v){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.main_activity_layout);
        final View dialogView = inflater.inflate(R.layout.dialog_edit_ctrl_name, rl,false);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.et_dev_id);

        dialogBuilder.setTitle("Изменение названия");
        dialogBuilder.setMessage("Новое название");
        dialogBuilder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //String url="http://growapp.e-nk.ru/core/add_data/edit_ctrl_name.php";

                String new_name=edt.getText().toString();

                //Изменим название в списке, котрым наполняем спиннер
                int index=ctrl_ids_list.indexOf(String.valueOf(controller_id));
                ctrl_names_list.set(index, new_name);
                ctrl_spinner_data[index]=new_name;

                //Изменим название в БД
                Controllers controllers = new Controllers(controller_id,new_name);
                db.updateControllerName(controllers);
                db.closeDB();

                new DataBroker.set_ctrl_name().execute(String.valueOf(controller_id), hash, new_name);

                //GetControllersListFromSQL();

                //Наполним спиннер, измененными данными
                populateSpinner();

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

