package cc.growapp.growapp.activities;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import cc.growapp.growapp.DataBroker;
import cc.growapp.growapp.GrowappConstants;
import cc.growapp.growapp.database.MyContentProvider;
import cc.growapp.growapp.JSONHandler;
import cc.growapp.growapp.R;


public class AccountActivity extends AppCompatActivity implements
        DataBroker.add_device.onAddDeviceComplete,
        DataBroker.get_dev_profile.onGetDevProfileComplete,
        DataBroker.get_system_state.onGetSystemStateComplete,
        DataBroker.get_pref_profile.onGetPrefVersionComplete {

    SharedPreferences sPref;
    String LOG_TAG = "AccountActivity";

    // Progress Dialog
    private ProgressDialog pDialog;

    // Database Helper
    MyContentProvider db;
    TableLayout tl;


    String ctrl_id,ctrl_name,ctrl_an,user,hash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        sPref = getSharedPreferences(GrowappConstants.APP_PREFERENCES,MODE_PRIVATE);
        pDialog = new ProgressDialog(AccountActivity.this);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.account));
        }
        user = sPref.getString("user","");
        hash = sPref.getString("hash", "");

        tl = (TableLayout) findViewById(R.id.info_tl_dev);


        Cursor cursor = getContentResolver().query(MyContentProvider.CTRLS_CONTENT_URI, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()){
                do{
                    String ctrl_id = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.KEY_CTRL_ID));
                    String ctrl_name = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.KEY_CTRL_NAME));
                    String ctrl_an = cursor.getString(cursor.getColumnIndexOrThrow(MyContentProvider.KEY_CTRL_AN));

                    Log.d(LOG_TAG, "Device ID: " + ctrl_id);
                    Log.d(LOG_TAG, "Device name: " + ctrl_name);
                    Log.d(LOG_TAG, "Device AN: " + ctrl_an);


                    add_row_to_table(ctrl_id, ctrl_name, ctrl_an);


                    // do what ever you want here
                }while(cursor.moveToNext());
            }
            cursor.close();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pDialog.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Операции для выбранного пункта меню
        switch (item.getItemId())
        {
            case  android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void add_device(View v){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        //final View dialogView = inflater.inflate(R.layout.dialog_add_dev, null);

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.account_rl);
        final View dialogView = inflater.inflate(R.layout.dialog_add_dev, rl,false);

        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.et_dev_id);
        final EditText edt2 = (EditText) dialogView.findViewById(R.id.et_an);
        final EditText edt3 = (EditText) dialogView.findViewById(R.id.et_name);

        dialogBuilder.setTitle("Введите данные");
        //dialogBuilder.setMessage("Новое название");
        dialogBuilder.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ctrl_id = edt.getText().toString().trim();
                ctrl_an = edt2.getText().toString().trim();
                ctrl_name = edt3.getText().toString().trim();

                if(!user.isEmpty() && !hash.isEmpty() && !ctrl_id.isEmpty() && !ctrl_an.isEmpty()){
                    launch_add_device();
                }


                //new EditCtrlAsync().execute(url);
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

    void launch_add_device(){
        new DataBroker.add_device(this).execute(user,hash,ctrl_id,ctrl_name,ctrl_an);
    }

    @Override
    public void onAddDeviceCompleteMethod(String s) {
        if(s.equals("success")){
            pDialog.show();
            //Toast.makeText(this,"Успешно добавлено",Toast.LENGTH_SHORT).show();
            /*TODO Content Provider/*TODO Content Provider
            Controllers controller = new Controllers(ctrl_id,ctrl_name,ctrl_an);

            db.createCtrl(controller);*/
            add_row_to_table(ctrl_id,ctrl_name,ctrl_an);

            //TODO: Дубляж с loginactivity
            //Запускаем процесс получения настроек пользователя
            new DataBroker.get_pref_profile(this).execute(ctrl_id, hash);
            //Запускаем процесс получения комплектации устройства
            new DataBroker.get_dev_profile(this).execute(ctrl_id, hash);
            //Запускаем процесс получения данных датчиков
            new DataBroker.get_system_state(this).execute(ctrl_id, hash);

        } else Toast.makeText(this,"Ошибка добавления",Toast.LENGTH_SHORT).show();
    }

    void add_row_to_table(String ctrl_id_row, String ctrl_name_row, String ctrl_an_row){
        // Create a new row to be added.
        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));


        TextView ctrl_name_tv = new TextView(this);
        TextView ctrl_id_tv = new TextView(this);
        TextView ctrl_an_tv = new TextView(this);

        ctrl_id_tv.setText(ctrl_id_row);
        ctrl_name_tv.setText(ctrl_name_row);
        ctrl_an_tv.setText(ctrl_an_row);

        int left_margin=5;
        int top_margin=5;
        int right_margin=5;
        int bottom_margin=5;
        ctrl_id_tv.setPadding(left_margin,top_margin,right_margin,bottom_margin);
        ctrl_name_tv.setPadding(left_margin, top_margin, right_margin, bottom_margin);
        ctrl_an_tv.setPadding(left_margin, top_margin, right_margin, bottom_margin);





        ctrl_id_tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        ctrl_name_tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        ctrl_an_tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        tr.addView(ctrl_id_tv);
        tr.addView(ctrl_name_tv);
        tr.addView(ctrl_an_tv);

        // Add row to TableLayout.
        tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }


    //TODO: Дубляж с WelomeActivity

    @Override
    public void onGetPrefProfileCompleteMethod(String s) {
        Log.d(LOG_TAG, "Callback, pref data: " + s);
        if(s!=null){

            ContentValues result= new JSONHandler().ParseJSONProfile(s);
            if(result!=null){
                pDialog.setMessage("Получение настроек: успех");
                Uri newUri = getContentResolver().insert(MyContentProvider.PREF_CONTENT_URI, result);
                Log.d(LOG_TAG, "URI to dispatch: " + (newUri != null ? newUri.toString() : null));

            } else {
                pDialog.dismiss();
                Toast.makeText(this,R.string.no_data, Toast.LENGTH_SHORT).show();
            }
        } else {
            pDialog.dismiss();
        }
    }
    @Override
    public void onGetDevProfileCompleteMethod(String s) {
        if(s!=null){

            Log.d(LOG_TAG, "Callback, dev profile: " + s);
            ContentValues result= new JSONHandler().ParseJSONDevProfile(s);
            if(result.size()>0){
                pDialog.setMessage("Получение профиля устройства: успех");

                Uri newUri = getContentResolver().insert(MyContentProvider.DEV_PROFILE_CONTENT_URI, result);
                Log.d(LOG_TAG, "URI to dispatch: " + (newUri != null ? newUri.toString() : null));

            } else {
                pDialog.dismiss();
                Toast.makeText(this,R.string.no_data, Toast.LENGTH_SHORT).show();
            }
        } else {
            pDialog.dismiss();
        }
    }
    @Override
    public void onGetSystemStateCompleteMethod(String s) {
        if(s!=null){
            //pDialog.setMessage(getString(R.string.success));

            Log.d(LOG_TAG, "Callback, system state: " + s);
            ContentValues result= new JSONHandler().ParseJSONSystemState(s);
            if(result.size()>0){
                pDialog.setMessage("Получение данных датчиков: успех");

                Uri newUri = getContentResolver().insert(MyContentProvider.MAIN_CONTENT_URI, result);
                Log.d(LOG_TAG, "URI to dispatch: " + (newUri != null ? newUri.toString() : null));

            } else {
                pDialog.dismiss();
                Toast.makeText(this,R.string.no_data, Toast.LENGTH_SHORT).show();
            }
        }
        pDialog.dismiss();
    }

}