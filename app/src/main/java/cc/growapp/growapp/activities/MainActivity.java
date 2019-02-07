package cc.growapp.growapp.activities;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
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
import android.widget.Toast;


import java.util.LinkedList;
import java.util.List;

import cc.growapp.growapp.DataBroker;
import cc.growapp.growapp.GrowappConstants;
import cc.growapp.growapp.database.MyContentProvider;
import cc.growapp.growapp.fragments.Frag_co2;
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
import cc.growapp.growapp.R;
import cc.growapp.growapp.services.BackgroundService;


public class MainActivity extends AppCompatActivity implements
        View.OnTouchListener,
        DataBroker.set_action.onSetActionComplete


        {

    boolean prog_toggle=true;
    private static final String LOG_TAG = "MainActivity";
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

    int l_result=0;
    int t_result=0;
    int h_result=0;
    int pot1_h_result=0;
    int pot2_h_result=0;
    int p1_result=0;
    int p2_result=0;
    int relay1_result=0;
    int relay2_result=0;
    int w_result=0;
    String date_result="";

    FrameLayout wcan1;
    FrameLayout wcan2;
    private TextView answerField,actionField;
    TextView tv_wcan1_type,tv_wcan2_type;


    String hash;


    private String controller_id,controller_name;
    private String [] ctrl_names_data;
    private String [] ctrl_ids_data;
    private List<String> ctrl_ids_list =new LinkedList<>();
    private List<String> ctrl_names_list =new LinkedList<>();

    float x1=0;
    float y1=0;
    float x2=0;
    float y2=0;

    boolean pump1_active = false;
    boolean pump2_active = false;

    long last_refresh_time;

    FragmentTransaction fTrans;

    static boolean active = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainObserver MainObserver = new MainObserver(new Handler());
        getContentResolver().registerContentObserver(MyContentProvider.MAIN_CONTENT_URI, true, MainObserver);
        setContentView(R.layout.activity_main);



        RelativeLayout rl = findViewById(R.id.main_activity_layout);

        wcan1 = findViewById(R.id.frgm_wcan1);
        wcan2 = findViewById(R.id.frgm_wcan2);

        tv_wcan1_type = new TextView(this);
        tv_wcan2_type = new TextView(this);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,Gravity.END);
        tv_wcan2_type.setLayoutParams(params);



        wcan1.addView(tv_wcan1_type);
        wcan2.addView(tv_wcan2_type);


        actionField = findViewById(R.id.tv_main_action);
        answerField = findViewById(R.id.tv_main_answer);

        rl.setOnTouchListener(this);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.app_name));
        }

        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        hash = sPref.getString("hash", "");

        pDialog = new ProgressDialog(MainActivity.this);



        Switch relay1 = findViewById(R.id.tbtn_sw1);
        Switch relay2 = findViewById(R.id.tbtn_sw2);

        // ------------------------------ Обработчик переключателя №1 ---------------------------
        relay1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (controller_id!=null && !controller_id.isEmpty() && !prog_toggle) {
                    Log.d(LOG_TAG, "Manual SW 1 State Change");
                    if (isChecked) {
                        //Log.d(LOG_TAG, "Checked");
                        launch_set_action("11");

                        // The toggle is enabled
                    } else {
                        launch_set_action("10");

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
        final Spinner spinner = findViewById(R.id.ctrl_spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                controller_id = ctrl_ids_data[spinner.getSelectedItemPosition()];
                controller_name = ctrl_names_data[spinner.getSelectedItemPosition()];
                //spinner.getSelectedItem().toString();
                Log.d(LOG_TAG, "Selected controller: " + controller_id);

                if (controller_id!=null && !controller_id.isEmpty()) {
                    get_main_data(controller_id);

                    populateFragments();
                }
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
        Spinner spinner = findViewById(R.id.ctrl_spinner);
        Log.d(LOG_TAG, "Spinner [0]=" + ctrl_names_data[0]);
        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ctrl_names_data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        Intent intent = getIntent();
        //Нужно для того, если вывана активити с уведомления
        if (intent.hasExtra("controller_id")) {
            //spinner.setSelection(ctrl_ids_list.indexOf(getIntent().getExtras().getString("controller_id")));
            controller_id = intent.getStringExtra("controller_id");
            //TODO Maybe needs name of ctrl
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
    protected void onPause() {
        super.onPause();
        active=false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        active=true;
        GetControllersListFromSQL();
        populateSpinner();
        if (controller_id != null && !controller_id.isEmpty()){
            get_main_data(controller_id);
            populateFragments();
        }
    }

    private void launch_set_action(String action) {
        actionField.setText(action);
        String saved_hostname = sPref.getString("hostname", GrowappConstants.DEFAULT_HOSTNAME);
        new DataBroker.set_action(this).execute(saved_hostname,controller_id, hash, action);
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
                Log.d(LOG_TAG, "Активити настроек");
                if(controller_id!=null && !controller_id.isEmpty()){


                    Intent pref_intent = new Intent(MainActivity.this,PreferencesActivity.class);
                    pref_intent.putExtra("controller_id", controller_id);
                    pref_intent.putExtra("controller_name", controller_name);

                    //intent.putExtra("ver", ctrl_id_string);
                    startActivity(pref_intent);
                }
                return true;
            case R.id.account:
                Intent account_intent = new Intent(MainActivity.this,AccountActivity.class);
                account_intent.putExtra("controller_id", controller_id);
                account_intent.putExtra("controller_name", controller_name);
                startActivity(account_intent);
                return true;
            case R.id.info:
                Intent info_intent = new Intent(MainActivity.this,InfoActivity.class);
                info_intent.putExtra("controller_id", controller_id);
                info_intent.putExtra("controller_name", controller_name);
                startActivity(info_intent);
                return true;
            case R.id.graphs:
                if(controller_id!=null && !controller_id.isEmpty()){
                    //String ctrl_id_string=String.valueOf(controller_id);
                    Intent intent = new Intent(MainActivity.this,GraphsActivity.class);
                    intent.putExtra("controller_id", controller_id);
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


    public void populateFragments(){

        //TextView water_level_label = (TextView) findViewById(R.id.textView14);
        //TextView water_level = (TextView) findViewById(R.id.tv_wl_data);

        TextView date = findViewById(R.id.tv_date);
        Switch relay1 = findViewById(R.id.tbtn_sw1);
        Switch relay2 = findViewById(R.id.tbtn_sw2);

        FrameLayout light = findViewById(R.id.frgm_light);
        FrameLayout t = findViewById(R.id.frgm_temp);
        FrameLayout h = findViewById(R.id.frgm_hum);
        FrameLayout pot1 = findViewById(R.id.frgm_pot1);
        FrameLayout pot2 = findViewById(R.id.frgm_pot2);
        FrameLayout co2 = findViewById(R.id.frgm_co2);





        light.setVisibility(View.VISIBLE);
        t.setVisibility(View.VISIBLE);
        h.setVisibility(View.VISIBLE);
        pot1.setVisibility(View.VISIBLE);
        pot2.setVisibility(View.VISIBLE);
        co2.setVisibility(View.VISIBLE);
        relay1.setVisibility(View.VISIBLE);
        relay2.setVisibility(View.VISIBLE);

        //water_level.setVisibility(View.VISIBLE);
        //water_level_label.setVisibility(View.VISIBLE);

        //Инициализация фрагментов в Активити
        Frag_temp frag_temp;
        Frag_hum frag_hum;
        Frag_pot1 frag_pot1;
        Frag_pot2 frag_pot2;
        Frag_co2 frag_co2;

        Frag_light_on frag_light_on;
        Frag_light_off frag_light_off;

        frag_temp = new Frag_temp();
        frag_hum = new Frag_hum();
        frag_pot1 = new Frag_pot1();
        frag_pot2 = new Frag_pot2();
        frag_co2 = new Frag_co2();

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
        String frag_co2_tag = "frag_co2";
        Frag_temp f_temp = (Frag_temp) getFragmentManager().findFragmentByTag(frag_temp_tag);
        Frag_hum f_hum = (Frag_hum) getFragmentManager().findFragmentByTag(frag_hum_tag);
        Frag_co2 f_co2 = (Frag_co2) getFragmentManager().findFragmentByTag(frag_co2_tag);


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

        Log.d(LOG_TAG, "Уровень воды = " + w_result + "%");
        if(water_control==1){
            if( f_co2 == null){
                fTrans.add(R.id.frgm_co2, frag_co2, frag_co2_tag);
                Log.d(LOG_TAG_FRAG, "Фрагмент CO2 добавлен");
            }
        } else co2.setVisibility(View.INVISIBLE);
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
            pump1_active = true;
            add_wcan1_fragment(true);
        } else {
            pump1_active = false;
            add_wcan1_fragment(false);
        }
        if(p2_result==1) {
            pump2_active = true;
            add_wcan2_fragment(true);
        } else {
            pump2_active = false;
            add_wcan2_fragment(false);
        }
        // ---------------------------------------------------------------------------------

        //Проверяем все ли фрагменты обработались менеджером фрагментов
        if(getFragmentManager().executePendingTransactions()){
            f_temp = (Frag_temp) getFragmentManager().findFragmentByTag(frag_temp_tag);
            f_hum = (Frag_hum) getFragmentManager().findFragmentByTag(frag_hum_tag);
            f_co2 = (Frag_co2) getFragmentManager().findFragmentByTag(frag_co2_tag);
            f_pot1 = (Frag_pot1) getFragmentManager().findFragmentByTag(frag_pot1_tag);
            f_pot2 = (Frag_pot2) getFragmentManager().findFragmentByTag(frag_pot2_tag);

            //Если да, заполним их данными
            if(f_temp!=null)f_temp.setText(t_result+"'C");
            if(f_hum!=null)f_hum.setText(h_result+"%");
            if(f_co2!=null)f_co2.setText(w_result+"%");
            if(f_pot1!=null)f_pot1.setText(pot1_h_result+"%");
            if(f_pot2!=null)f_pot2.setText(pot2_h_result+"%");
        }



        /*if(water_control==1){
            String w_result_string = String.valueOf(w_result)+"%";
            water_level.setText(w_result_string);
        } else{
            water_level.setVisibility(View.INVISIBLE);
            water_level_label.setVisibility(View.INVISIBLE);
        }*/


        prog_toggle=true;
        Log.d(LOG_TAG, "Состояние реле 1 = " + relay1_result);
        Log.d(LOG_TAG, "Состояние реле 2 = " + relay2_result);
        if(relay1_control==1){
            if(relay1_result==0)relay1.setChecked(true);else relay1.setChecked(false);
        } else relay1.setVisibility(View.INVISIBLE);
        if(relay2_control==1){
            if(relay2_result==0)relay2.setChecked(true);else relay2.setChecked(false);
        } else relay2.setVisibility(View.INVISIBLE);

        prog_toggle=false;

        //Запишем состояние системы в файл настроек, чтоб сервис не проверял, то что мы итак сдеалали сами
        //String refresh_date = date_result;
        date.setText(date_result);
        Log.d(LOG_TAG, "Дата получена = " + date_result);

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
                    if(System.currentTimeMillis()-last_refresh_time> GrowappConstants.short_period){

                        Toast.makeText(this,getString(R.string.loadingsystemstate),Toast.LENGTH_SHORT).show();

                        //pDialog.show();
                        //pDialog.setMessage(getString(R.string.loadingsystemstate));
                        //Запускаем процесс получения данных датчиков
                        //new DataBroker.get_system_state(this).execute(controller_id, hash);
                        //new GetSystemState().execute();
                        last_refresh_time=System.currentTimeMillis();
                        launch_set_action("3");

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

        Cursor cursor = getContentResolver().query(MyContentProvider.CTRLS_CONTENT_URI, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()){
                do{

                    Log.d(LOG_TAG, "ID контроллера: " + cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.KEY_CTRL_ID)));
                    Log.d(LOG_TAG, "Name контроллера: " + cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.KEY_CTRL_NAME)));

                    ctrl_ids_list.add(cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.KEY_CTRL_ID)));
                    ctrl_names_list.add(cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.KEY_CTRL_NAME)));

                    // do what ever you want here
                }while(cursor.moveToNext());
            }
            cursor.close();
        }



        //List<Controllers> allCtrls = db.getAllCtrls();
        /*for (Controllers ctrl : mCursor) {
            //Log.d(LOG_TAG, "CTRLS Name = "+ctrl.get_name());
            Log.d(LOG_TAG, "ID контроллера: " + String.valueOf(ctrl.get_ctrl_id()));
            Log.d(LOG_TAG, "Name контроллера: " + ctrl.get_name());

            ctrl_ids_list.add(String.valueOf(ctrl.get_ctrl_id()));
            ctrl_names_list.add(ctrl.get_name());
        }*/

        //Преобразуем в массив
        ctrl_ids_data = ctrl_ids_list.toArray(new String[ctrl_ids_list.size()]);
        ctrl_names_data = ctrl_names_list.toArray(new String[ctrl_names_list.size()]);



        return true;
    }
    // ---------------------------------------------------------------------------------------------
    public void pump1_control(){
        //Log.d(LOG_TAG, "PUMP 1 State: "+ String.valueOf(pump1_active));
        if (controller_id!=null && !controller_id.isEmpty()) {
            if (!pump1_active) {
                pump1_active =true;
                Log.d(LOG_TAG, "PUMP 1 On");
                add_wcan1_fragment(true);
                launch_set_action("31");
                //new ActionHandler(getApplicationContext(), statusField, reciviedField).execute(user_hash, controller_id, "31"); //Сигнал Arduino на насос 1 включить
            } else {
                pump1_active =false;
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
        //Log.d(LOG_TAG, "PUMP 2 State: "+ String.valueOf(pump1_active));
        if (controller_id!=null && !controller_id.isEmpty()) {
            if (!pump2_active) {
                pump2_active =true;
                Log.d(LOG_TAG, "PUMP 2 On");
                add_wcan2_fragment(true);
                launch_set_action("41");
                //new ActionHandler(getApplicationContext(), statusField, reciviedField).execute(user_hash, controller_id, "41"); //Сигнал Arduino на насос 2 включить
            } else {
                pump2_active =false;
                add_wcan2_fragment(false);
                Log.d(LOG_TAG, "PUMP 2 Off");
                launch_set_action("40");
                //new ActionHandler(getApplicationContext(), statusField, reciviedField).execute(user_hash, controller_id, "40"); //Сигнал Arduino на насос 2 выключить
            }
            //call_to_refresh();
        }
    }

    /*private void call_to_refresh(){

    }*/





    private void add_wcan1_fragment(boolean state){
        // ---------------------- Работаем с фрагментами "Лейка" ---------------------------

        //FrameLayout wcan1 = (FrameLayout) findViewById(R.id.frgm_pot1);

        Frag_wcan1_off frag_wcan1_off = new Frag_wcan1_off();
        Frag_wcan1_on frag_wcan1_on = new Frag_wcan1_on();


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
        RelativeLayout rl = findViewById(R.id.main_activity_layout);
        final View dialogView = inflater.inflate(R.layout.dialog_edit_ctrl_name, rl,false);
        dialogBuilder.setView(dialogView);

        final EditText edt = dialogView.findViewById(R.id.et_dev_id);

        dialogBuilder.setTitle("Изменение названия");
        dialogBuilder.setMessage("Новое название");
        dialogBuilder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //String url="http://growapp.e-nk.ru/core/add_data/edit_ctrl_name.php";

                String new_name = edt.getText().toString();

                //Изменим название в списке, котрым наполняем спиннер
                int index = ctrl_ids_list.indexOf(String.valueOf(controller_id));
                ctrl_names_list.set(index, new_name);
                ctrl_names_data[index] = new_name;

                //Изменим название в БД
                ContentValues cv = new ContentValues();
                cv.put(MyContentProvider.KEY_CTRL_NAME, new_name);
                Uri newUri = ContentUris.withAppendedId(MyContentProvider.CTRLS_CONTENT_URI, Long.parseLong(controller_id));
                int cnt = getContentResolver().update(newUri, cv, null, null);
                Log.d(LOG_TAG, "Update URI to dispatch: " + (newUri.toString()));


                new DataBroker.set_ctrl_name().execute(String.valueOf(controller_id), hash, new_name);

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

    ContentValues get_main_data(String ctrl_id){
        Cursor cursor_dev_profile = getContentResolver().query(Uri.parse(MyContentProvider.DEV_PROFILE_CONTENT_URI+"/"+ctrl_id), null, null, null, null);
        if(cursor_dev_profile!=null){
            if(cursor_dev_profile.moveToFirst()){
                l_control= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_LIGHT_CONTROL));
                t_control= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_T_CONTROL));
                h_control= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_H_CONTROL));
                pot1_control= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_POT1_CONTROL));
                pot2_control= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_POT2_CONTROL));
                pump1_control= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_PUMP1_CONTROL));
                pump2_control= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_PUMP2_CONTROL));
                relay1_control= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_RELAY1_CONTROL));
                relay2_control= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_RELAY2_CONTROL));
                water_control= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_WATER_CONTROL));
                auto_watering1= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_AUTO_WATERING1));
                auto_watering2= cursor_dev_profile.getInt(cursor_dev_profile.getColumnIndexOrThrow(MyContentProvider.KEY_DEV_AUTO_WATERING2));

            }
            cursor_dev_profile.close();
        }

        Cursor cursor_main = getContentResolver().query(Uri.parse(MyContentProvider.MAIN_CONTENT_URI+"/"+ctrl_id),null, null, null, null);
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
            }
            cursor_main.close();
        }

        ContentValues old_main_data = new ContentValues();
        old_main_data.put("l_result",l_result);
        old_main_data.put("t_result",t_result);
        old_main_data.put("h_result",h_result);
        old_main_data.put("pot1_h_result",pot1_h_result);
        old_main_data.put("pot2_h_result",pot2_h_result);
        old_main_data.put("p1_result",p1_result);
        old_main_data.put("p2_result",p2_result);
        old_main_data.put("relay1_result",relay1_result);
        old_main_data.put("relay2_result", relay2_result);
        old_main_data.put("w_result",w_result);
        old_main_data.put("date_result", date_result);
        return old_main_data;
    }

    private class MainObserver extends ContentObserver {
        MainObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange,null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            get_main_data(controller_id);
            if(active)populateFragments();
        }

    }
    @Override
    public void onSetActionCompleteMethod(String s) {
        if (s != null) {
            Log.d(LOG_TAG, "Callback, action: " + s);
            answerField.setText(s);


            Intent intent = new Intent(this,BackgroundService.class);
            intent.putExtra("controller_id",controller_id);
            startService(intent);
            last_refresh_time=System.currentTimeMillis();

        }
    }

}

