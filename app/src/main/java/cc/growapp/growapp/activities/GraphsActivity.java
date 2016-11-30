package cc.growapp.growapp.activities;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import cc.growapp.growapp.DataBroker;
import cc.growapp.growapp.database.DatabaseHelper;
import cc.growapp.growapp.database.Dev_profile;
import cc.growapp.growapp.R;


public class GraphsActivity extends AppCompatActivity implements
        DataBroker.get_graphs_data.onGetGraphsDataComplete {

    private LineChart mChart;
    private String LOG_TAG = "GraphsActivity";
    private String[] param_spinner_data;
    private String[] period_spinner_data = {"Сегодня", "Вчера", "Неделя", "Месяц", "Прошлый месяц"};
    private int period_value;

    String param_name, period_name, param_value;



    boolean first_launch = true;

    SharedPreferences sPref;
    public static final String APP_PREFERENCES = "GrowAppSettings";

    String hash, controller_id;

    // Database Helper
    DatabaseHelper db;

    // Progress Dialog
    private ProgressDialog pDialog;
    //private float max_y_value;

    // JSON parser class
    //JSONParser jParser = new JSONParser();
    // JSON Node names

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.graphs));
        }


        setContentView(R.layout.activity_graphs);
        pDialog = new ProgressDialog(GraphsActivity.this);

        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        hash = sPref.getString("hash", "");
        db = new DatabaseHelper(getApplicationContext());

        Intent intent = getIntent();
        controller_id = intent.getStringExtra("controller_id");

        //FLAG_FULLSCREEN - из этого статус бар скрывается
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //     WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mChart = (LineChart) findViewById(R.id.chart);
        mChart = new LineChart(this);
        mChart = (LineChart) findViewById(R.id.chart);


        // -------------------------------- Спиннер параметров -----------------------------------
        final Spinner spinner_param = (Spinner) findViewById(R.id.spinner_parameter);
        spinner_param.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

                param_value = spinner_param.getSelectedItem().toString();

                //Эта проверка нужна для того, чтобы не дублировались данные, так как в начале запускамется два
                //асинхронных процесса и данные дублируются
                //if (!first_launch) {
                    //Очистим значения

                    if (param_value.equals(getString(R.string.temp))) {
                        param_name = "t";
                    }
                    if (param_value.equals(getString(R.string.hum))) {
                        param_name = "h";
                    }
                    if (param_value.equals(getString(R.string.pot1_hum))) {
                        param_name = "h_gor1";
                    }
                    if (param_value.equals(getString(R.string.pot2_hum))) {
                        param_name = "h_gor2";
                    }
                    if (param_value.equals(getString(R.string.water_level))) {
                        param_name = "water_level";
                    }

                    Log.d(LOG_TAG, "Selected parameter: " + param_name);
                    /*mChart.clear();
                    create_line_chart();*/
//                        new GetData4Graph().execute();
                    launch_get_graph_data();
                //}
                //first_launch = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        populateParamSpinner();


        // -------------------------------- Спиннер периодов ----------------------------------
        final Spinner spinner_period = (Spinner) findViewById(R.id.spinner_period);
        //Log.d(LOG_TAG, "Spinner [0]=" + ctrl_spinner_data[0]);
        // адаптер
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, period_spinner_data);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_period.setAdapter(adapter2);
        spinner_period.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                period_value = pos;


                switch (period_value) {
                    //День
                    case 0:
                        period_name = "day";
                        break;
                    //Вчера
                    case 1:
                        period_name = "yesterday";
                        break;
                    //Неделя
                    case 2:
                        period_name = "week";
                        break;
                    //Месяц
                    case 3:
                        period_name = "month";
                        break;
                    //Прошлый месяц
                    case 4:
                        period_name = "lmonth";
                        break;
                }
                Log.d(LOG_TAG, "Selected period: " + period_name);


                launch_get_graph_data();
                //new GetData4Graph().execute();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void launch_get_graph_data() {
        //Log.d(LOG_TAG, "Selected conroller ID: " + controller_id);
        //Log.d(LOG_TAG, "Hash: " + hash);
        //Очистим значения
        pDialog.setMessage(getString(R.string.loadingsystemstate));
        pDialog.show();

        new DataBroker.get_graphs_data(this).execute(controller_id, hash, param_name, period_name);
    }

    @Override
    public void onGetGraphsDataCompleteMethod(String s) {
        if (s != null) {
            //pDialog.setMessage(getString(R.string.d_profile));
            Log.d(LOG_TAG, "Callback, graphs data : " + s);
            try {
                JSONObject json = new JSONObject(s);
                int success;
                String TAG_SUCCESS = "success";
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    mChart.clear();
                    create_line_chart();

                    // Check your log cat for JSON reponse
                    Log.d(LOG_TAG, "JSON строка = " + json.toString());
                    // Успешно получены данные
                    JSONArray JsonObj = json.getJSONArray("data");
                    Log.d(LOG_TAG, "JSON данные получены ...");

                    // получаем обьекты с JSON Array
                    for (int i = 0; i < JsonObj.length(); i++) {

                        JSONObject ctrl = JsonObj.getJSONObject(i);
                        int y = ctrl.getInt("y");
                        Log.d(LOG_TAG, i + " объект JSON получен = " + ctrl);
                        Log.d(LOG_TAG, i + " x = " + ctrl.getString("x"));
                        Log.d(LOG_TAG, i + " y = " + y);
                        //Добавим в график
                        addEntry(ctrl.getString("x"), y);
                        // if(max_y_value<y)max_y_value=y;

                    }

                } else {
                    Log.d(LOG_TAG, "Данные не найдены!");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(this, R.string.no_data, Toast.LENGTH_SHORT).show();

        }
        pDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Очистим значения
        mChart.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Операции для выбранного пункта меню
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void create_line_chart() {

        //customize line chart
        mChart.setDescription("Growapp");
        mChart.setDescriptionColor(Color.WHITE);

        mChart.setNoDataTextDescription("No data for the moment");
        //enable value highlighting
        mChart.setHighlightPerTapEnabled(true);
        //enable touch gestures
        mChart.setTouchEnabled(true);
        //we want also enable dragging and scaling
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        //enable pinch zoom to avoid scaling x and y axis separately
        mChart.setPinchZoom(true);
        //alternative background color
        mChart.setBackgroundColor(Color.BLACK);

        //now. we work on data
        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        //add data to line chart
        mChart.setData(data);
        //get legend object
        Legend l = mChart.getLegend();
        //customize legend
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setDrawAxisLine(true);
        xl.setDrawLabels(true);
        xl.setLabelRotationAngle(90);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setLabelsToSkip(0);
        xl.setTextSize(2f);

        YAxis yl = mChart.getAxisLeft();
        yl.setTextColor(Color.WHITE);
        yl.setTextSize(2f);
        yl.setAxisMinValue(0);
        //yl.setAxisMaxValue(max_y_value);
        yl.setDrawGridLines(false);


        float ll_max_value = 0, ll_min_value = 0, ll_opt_value = 0;
        if(param_value.equals(getString(R.string.temp))){
            ll_max_value = 32f;
            ll_min_value = 16f;
            ll_opt_value = 27f;
        }
        if(param_value.equals(getString(R.string.hum))){
            ll_max_value = 70f;
            ll_min_value = 20f;
            ll_opt_value = 50f;
        }
        if(param_value.equals(getString(R.string.pot1_hum))){
            ll_max_value = 100f;
            ll_min_value = 20f;
            ll_opt_value = 50f;
        }
        if(param_value.equals(getString(R.string.pot2_hum))){
            ll_max_value = 100f;
            ll_min_value = 20f;
            ll_opt_value = 50f;
        }
        if(param_value.equals(getString(R.string.water_level))){
            //ll_max_value = 100f;
            //ll_min_value = 10f;
            //ll_opt_value = 70f;
        }


        yl.removeAllLimitLines();
        if (ll_max_value != 0) {
            //Limit line
            LimitLine ll_max = new LimitLine(ll_max_value, "Максимальная");
            LimitLine ll_min = new LimitLine(ll_min_value, "Минимальная");
            LimitLine ll_opt = new LimitLine(ll_opt_value, "Оптимальная");

            ll_max.setLineColor(Color.RED);
            ll_max.setLineWidth(4f);
            ll_max.setTextColor(Color.WHITE);
            ll_max.setTextSize(8f);
            ll_min.setLineColor(Color.BLUE);
            ll_min.setLineWidth(4f);
            ll_min.setTextColor(Color.WHITE);
            ll_min.setTextSize(8f);
            ll_opt.setLineColor(Color.GREEN);
            ll_opt.setLineWidth(4f);
            ll_opt.setTextColor(Color.WHITE);
            ll_opt.setTextSize(8f);


            yl.addLimitLine(ll_max);
            yl.addLimitLine(ll_min);
            yl.addLimitLine(ll_opt);

        }

        //------
        YAxis yl2 = mChart.getAxisRight();
        yl2.setEnabled(false);

    }

    public class MyValueFormatter implements ValueFormatter {

        //private DecimalFormat mFormat;

       /*public MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,##0.0"); // use one decimal
        }*/

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            // write your logic here
            return "" + ((int) value);
        }
    }

    //we need to create method to add entry to the line chart
    private void addEntry(String x, int y) {
        LineData data = mChart.getData();
        if (data != null) {
            LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
            if (set == null) {
                set = CreateSet();
                data.addDataSet(set);
            }

            //add new value
            data.addXValue(x);
            data.addEntry(new Entry(y, set.getEntryCount()), 0);
            /*data.addEntry(
                    new Entry(((float) Math.random() * 10), set.getEntryCount()), 0);*/
            //notify chart data have changed
            mChart.notifyDataSetChanged();
            //limit number of visible entries
            switch (period_value) {
                //День
                case 0:
                    mChart.setVisibleXRange(1, 24);
                    break;
                //Вчера
                case 1:
                    mChart.setVisibleXRange(1, 24);
                    break;
                //Неделя
                case 2:
                    mChart.setVisibleXRange(1, 7);
                    break;
                //Месяц
                case 3:
                    mChart.setVisibleXRange(1, 31);
                    break;
                //Прошлый месяц
                case 4:
                    mChart.setVisibleXRange(1, 31);
                    break;
            }

            //scroll to the last entry
            mChart.moveViewToX(data.getXValCount());

        }
    }

    //method to create set
    private LineDataSet CreateSet() {
        String legend_name = "Default";

        if(param_value.equals(getString(R.string.temp))){
            legend_name = getString(R.string.temp);
        }
        if(param_value.equals(getString(R.string.hum))){
            legend_name = getString(R.string.hum);
        }
        if(param_value.equals(getString(R.string.pot1_hum))){
            legend_name = getString(R.string.pot1_hum);
        }
        if(param_value.equals(getString(R.string.pot2_hum))){
            legend_name = getString(R.string.pot2_hum);
        }
        if(param_value.equals(getString(R.string.water_level))){
            legend_name = getString(R.string.water_level);
        }

        LineDataSet set = new LineDataSet(null, legend_name);
        //set.setDrawCubic(true);
        //set.setCubicIntensity(0.2f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 177));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(8f);
        set.setValueFormatter(new MyValueFormatter());

        return set;
    }

    public void populateParamSpinner(){
        final Spinner spinner_param = (Spinner) findViewById(R.id.spinner_parameter);
        //Log.d(LOG_TAG, "Spinner [0]=" + param_spinner_data[0]);
        // адаптер
        //Выцепляем с базы, какие компоненты системы нам доступны
        Dev_profile dev_profile = db.getDevProfile(controller_id);
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
        int custom_control= dev_profile.get_water_control();
        db.closeDB();

        //Преобразуем в массив
        List<String> params_names_list =new LinkedList<>();
        if(t_control!=0)params_names_list.add(getString(R.string.temp));
        if(h_control!=0)params_names_list.add(getString(R.string.hum));
        if(pot1_control!=0)params_names_list.add(getString(R.string.pot1_hum));
        if(pot2_control!=0)params_names_list.add(getString(R.string.pot2_hum));
        if(custom_control!=0)params_names_list.add(getString(R.string.water_level));

        param_spinner_data = params_names_list.toArray(new String[params_names_list.size()]);

        //Log.d(LOG_TAG, "Spinner [0]=" + ctrl_spinner_data[0]);
        // адаптер
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, param_spinner_data);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_param.setAdapter(adapter1);
    }
}
