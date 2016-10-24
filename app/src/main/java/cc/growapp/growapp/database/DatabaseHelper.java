package cc.growapp.growapp.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG_TAG = "GrowApp";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "GrowApp.db";

    // Table Names
    private static final String TABLE_CTRLS = "controllers";
    private static final String TABLE_PREF = "preferences";
    private static final String TABLE_DEV_PROFILE = "dev_profile";
    private static final String TABLE_MAIN = "main";

    //private static final String TABLE_TAG = "tags";
    //private static final String TABLE_TODO_TAG = "todo_tags";

    // Common column names

    //private static final String KEY_CREATED_AT = "created_at";

    // NOTES Table - column names

    //private static final String KEY_STATUS = "status";

    // CTRLS Table - column names
    private static final String KEY_CTRL_ID = "ctrl_id";
    private static final String KEY_CTRL_NAME = "name";
    private static final String KEY_CTRL_AN = "an";

    // PREFERENCES Table - column names
    private static final String KEY_PREF_CTRL_ID = "ctrl_id";
    private static final String KEY_PREF_VERSION = "version";
    private static final String KEY_PREF_T_MAX = "t_max";
    private static final String KEY_PREF_T_MIN = "t_min";
    private static final String KEY_PREF_H_MAX = "h_max";
    private static final String KEY_PREF_H_MIN = "h_min";
    private static final String KEY_PREF_POT1_H_MAX = "pot1_h_max";
    private static final String KEY_PREF_POT1_H_MIN = "pot1_h_min";
    private static final String KEY_PREF_POT2_H_MAX = "pot2_h_max";
    private static final String KEY_PREF_POT2_H_MIN = "pot2_h_min";
    private static final String KEY_PREF_WL_MAX = "wl_max";
    private static final String KEY_PREF_WL_MIN = "wl_min";

    private static final String KEY_PREF_ALL_NOTIFY = "all_notify";
    private static final String KEY_PREF_T_NOTIFY = "t_notify";
    private static final String KEY_PREF_H_NOTIFY = "h_notify";
    private static final String KEY_PREF_POT1_NOTIFY = "pot1_notify";
    private static final String KEY_PREF_POT2_NOTIFY = "pot2_notify";
    private static final String KEY_PREF_WL_NOTIFY = "wl_notify";
    private static final String KEY_PREF_L_NOTIFY = "l_notify";
    private static final String KEY_PREF_RELAYS_NOTIFY = "relays_notify";
    private static final String KEY_PREF_PUMPS_NOTIFY = "pumps_notify";

    // DEV Profile Table - column names
    private static final String KEY_DEV_CTRL_ID = "ctrl_id";
    private static final String KEY_DEV_LIGHT_CONTROL = "light_control";
    private static final String KEY_DEV_T_CONTROL = "t_control";
    private static final String KEY_DEV_H_CONTROL = "h_control";
    private static final String KEY_DEV_POT1_CONTROL = "pot1_control";
    private static final String KEY_DEV_POT2_CONTROL = "pot2_control";
    private static final String KEY_DEV_RELAY1_CONTROL = "relay1_control";
    private static final String KEY_DEV_RELAY2_CONTROL = "relay2_control";
    private static final String KEY_DEV_PUMP1_CONTROL = "pump1_control";
    private static final String KEY_DEV_PUMP2_CONTROL = "pump2_control";
    private static final String KEY_DEV_WATER_CONTROL = "water_control";
    private static final String KEY_DEV_AUTO_WATERING1 = "auto_watering1";
    private static final String KEY_DEV_AUTO_WATERING2 = "auto_watering2";

    // MAIN Table - column names
    private static final String KEY_MAIN_CTRL_ID = "ctrl_id";
    private static final String KEY_MAIN_LIGHT_STATE = "light_state";
    private static final String KEY_MAIN_T = "t";
    private static final String KEY_MAIN_H = "h";
    private static final String KEY_MAIN_POT1_H = "pot1_h";
    private static final String KEY_MAIN_POT2_H = "pot2_h";
    private static final String KEY_MAIN_RELAY1_STATE = "relay1_state";
    private static final String KEY_MAIN_RELAY2_STATE = "relay2_state";
    private static final String KEY_MAIN_PUMP1_STATE = "pump1_state";
    private static final String KEY_MAIN_PUMP2_STATE = "pump2_state";
    private static final String KEY_MAIN_WATER_LEVEL = "water_level";
    private static final String KEY_MAIN_DATE = "date";

    // NOTE_TAGS Table - column names
    //private static final String KEY_TODO_ID = "todo_id";
    //private static final String KEY_TAG_ID = "tag_id";

    // Table Create Statements
    private static final String CREATE_TABLE_CTRLS = "CREATE TABLE "
            + TABLE_CTRLS + "(" + KEY_CTRL_ID + " TEXT PRIMARY KEY," +
            KEY_CTRL_NAME + " TEXT," +
            KEY_CTRL_AN + " TEXT" +")";

    private static final String CREATE_TABLE_PREF = "CREATE TABLE "
            + TABLE_PREF + "(" + KEY_PREF_CTRL_ID + " TEXT PRIMARY KEY," +
            KEY_PREF_VERSION + " INTEGER," +
            KEY_PREF_T_MAX + " INTEGER," +
            KEY_PREF_T_MIN + " INTEGER," +
            KEY_PREF_H_MIN + " INTEGER," +
            KEY_PREF_H_MAX + " INTEGER," +
            KEY_PREF_POT1_H_MIN + " INTEGER," +
            KEY_PREF_POT1_H_MAX + " INTEGER," +
            KEY_PREF_POT2_H_MIN + " INTEGER," +
            KEY_PREF_POT2_H_MAX + " INTEGER," +
            KEY_PREF_WL_MIN + " INTEGER," +
            KEY_PREF_WL_MAX + " INTEGER," +
            KEY_PREF_ALL_NOTIFY + " INTEGER," +
            KEY_PREF_T_NOTIFY + " INTEGER," +
            KEY_PREF_H_NOTIFY + " INTEGER," +
            KEY_PREF_POT1_NOTIFY + " INTEGER," +
            KEY_PREF_POT2_NOTIFY + " INTEGER," +
            KEY_PREF_L_NOTIFY + " INTEGER," +
            KEY_PREF_WL_NOTIFY + " INTEGER," +
            KEY_PREF_RELAYS_NOTIFY + " INTEGER," +
            KEY_PREF_PUMPS_NOTIFY + " INTEGER" +

            ")";
    private static final String CREATE_TABLE_DEV_PROFILE = "CREATE TABLE "
            + TABLE_DEV_PROFILE + "(" + KEY_DEV_CTRL_ID + " TEXT PRIMARY KEY," +
            KEY_DEV_LIGHT_CONTROL + " INTEGER," +
            KEY_DEV_T_CONTROL + " INTEGER," +
            KEY_DEV_H_CONTROL + " INTEGER," +
            KEY_DEV_POT1_CONTROL + " INTEGER," +
            KEY_DEV_POT2_CONTROL + " INTEGER," +
            KEY_DEV_RELAY1_CONTROL + " INTEGER," +
            KEY_DEV_RELAY2_CONTROL + " INTEGER," +
            KEY_DEV_PUMP1_CONTROL + " INTEGER," +
            KEY_DEV_PUMP2_CONTROL + " INTEGER," +
            KEY_DEV_WATER_CONTROL + " INTEGER," +
            KEY_DEV_AUTO_WATERING1 + " INTEGER," +
            KEY_DEV_AUTO_WATERING2 + " INTEGER" +
            ")";
    private static final String CREATE_TABLE_MAIN = "CREATE TABLE "
            + TABLE_MAIN + "(" + KEY_MAIN_CTRL_ID + " TEXT PRIMARY KEY," +
            KEY_MAIN_LIGHT_STATE + " INTEGER," +
            KEY_MAIN_T + " INTEGER," +
            KEY_MAIN_H + " INTEGER," +
            KEY_MAIN_POT1_H + " INTEGER," +
            KEY_MAIN_POT2_H + " INTEGER," +
            KEY_MAIN_RELAY1_STATE + " INTEGER," +
            KEY_MAIN_RELAY2_STATE + " INTEGER," +
            KEY_MAIN_PUMP1_STATE + " INTEGER," +
            KEY_MAIN_PUMP2_STATE + " INTEGER," +
            KEY_MAIN_WATER_LEVEL + " INTEGER," +
            KEY_MAIN_DATE + " DATETIME" +
            ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_CTRLS);
        db.execSQL(CREATE_TABLE_PREF);
        db.execSQL(CREATE_TABLE_DEV_PROFILE);
        db.execSQL(CREATE_TABLE_MAIN);
    }

    public void onTrancuate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CTRLS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREF);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEV_PROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAIN);
        // create new tables
        onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CTRLS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREF);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_DEV_PROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAIN);
        // create new tables
        onCreate(db);
    }


    // Creating a ctrl
    public long createCtrl(Controllers controllers) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CTRL_ID, controllers.get_ctrl_id());
        values.put(KEY_CTRL_NAME, controllers.get_name());
        values.put(KEY_CTRL_AN, controllers.get_an());

        // insert row
        long ctrl_id = db.insert(TABLE_CTRLS, null, values);

        if(ctrl_id==-1){
            Log.d(LOG_TAG,"Inserting to "+TABLE_CTRLS+" result: failed");
        } else Log.d(LOG_TAG,"Inserting to "+TABLE_CTRLS+" result: success");

        return ctrl_id;
    }

    // Creating a pref
    public long createPref(Preferences preferences) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PREF_CTRL_ID, preferences.get_ctrl_id());
        values.put(KEY_PREF_VERSION, preferences.get_version());
        values.put(KEY_PREF_T_MAX, preferences.get_t_max());
        values.put(KEY_PREF_T_MIN, preferences.get_t_min());
        values.put(KEY_PREF_H_MAX, preferences.get_h_max());
        values.put(KEY_PREF_H_MIN, preferences.get_h_min());
        values.put(KEY_PREF_POT1_H_MAX, preferences.get_pot1_h_max());
        values.put(KEY_PREF_POT1_H_MIN, preferences.get_pot1_h_min());
        values.put(KEY_PREF_POT2_H_MAX, preferences.get_pot2_h_max());
        values.put(KEY_PREF_POT2_H_MIN, preferences.get_pot2_h_min());
        values.put(KEY_PREF_WL_MAX, preferences.get_wl_max());
        values.put(KEY_PREF_WL_MIN, preferences.get_wl_min());

        values.put(KEY_PREF_ALL_NOTIFY, preferences.get_all_notify());
        values.put(KEY_PREF_T_NOTIFY, preferences.get_t_notify());
        values.put(KEY_PREF_H_NOTIFY, preferences.get_h_notify());
        values.put(KEY_PREF_POT1_NOTIFY, preferences.get_pot1_notify());
        values.put(KEY_PREF_POT2_NOTIFY, preferences.get_pot2_notify());
        values.put(KEY_PREF_L_NOTIFY, preferences.get_l_notify());
        values.put(KEY_PREF_WL_NOTIFY, preferences.get_wl_notify());
        values.put(KEY_PREF_RELAYS_NOTIFY, preferences.get_relays_notify());
        values.put(KEY_PREF_PUMPS_NOTIFY, preferences.get_pumps_notify());


        // insert row
        long result = db.insert(TABLE_PREF, null, values);

        if(result==-1){
            Log.d(LOG_TAG,"Inserting to "+TABLE_PREF+" result: failed");
        } else Log.d(LOG_TAG,"Inserting to "+TABLE_PREF+" result: success");
        return result;
    }

    //Get user profile
    public Preferences getPrefProfile(String ctrl_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_PREF + " WHERE "
                + KEY_PREF_CTRL_ID + " = " + ctrl_id;
        Log.d(LOG_TAG, "getDevProfile query = " + selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        Preferences preferences = new Preferences();

        if (c.moveToFirst()){
            preferences.set_ctrl_id(c.getString(c.getColumnIndex(KEY_PREF_CTRL_ID)));
            preferences.set_t_max(c.getInt(c.getColumnIndex(KEY_PREF_T_MAX)));
            preferences.set_t_min(c.getInt(c.getColumnIndex(KEY_PREF_T_MIN)));
            preferences.set_h_max(c.getInt(c.getColumnIndex(KEY_PREF_H_MAX)));
            preferences.set_h_min(c.getInt(c.getColumnIndex(KEY_PREF_H_MIN)));
            preferences.set_pot1_h_max(c.getInt(c.getColumnIndex(KEY_PREF_POT1_H_MAX)));
            preferences.set_pot1_h_min(c.getInt(c.getColumnIndex(KEY_PREF_POT1_H_MIN)));
            preferences.set_pot2_h_max(c.getInt(c.getColumnIndex(KEY_PREF_POT2_H_MAX)));
            preferences.set_pot2_h_min(c.getInt(c.getColumnIndex(KEY_PREF_POT2_H_MIN)));
            preferences.set_wl_max(c.getInt(c.getColumnIndex(KEY_PREF_WL_MAX)));
            preferences.set_wl_min(c.getInt(c.getColumnIndex(KEY_PREF_WL_MIN)));
            preferences.set_all_notify(c.getInt(c.getColumnIndex(KEY_PREF_ALL_NOTIFY)));
            preferences.set_l_notify(c.getInt(c.getColumnIndex(KEY_PREF_L_NOTIFY)));
            preferences.set_t_notify(c.getInt(c.getColumnIndex(KEY_PREF_T_NOTIFY)));
            preferences.set_h_notify(c.getInt(c.getColumnIndex(KEY_PREF_H_NOTIFY)));
            preferences.set_pot1_notify(c.getInt(c.getColumnIndex(KEY_PREF_POT1_NOTIFY)));
            preferences.set_pot2_notify(c.getInt(c.getColumnIndex(KEY_PREF_POT2_NOTIFY)));
            preferences.set_relays_notify(c.getInt(c.getColumnIndex(KEY_PREF_RELAYS_NOTIFY)));
            preferences.set_pumps_notify(c.getInt(c.getColumnIndex(KEY_PREF_PUMPS_NOTIFY)));
            preferences.set_wl_notify(c.getInt(c.getColumnIndex(KEY_PREF_WL_NOTIFY)));
        }
        c.close();
        return preferences;
    }

    // Updating a pref
    public int updateUserProfile(Preferences preferences) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CTRL_ID, preferences.get_ctrl_id());
        values.put(KEY_PREF_VERSION, preferences.get_version());
        values.put(KEY_PREF_T_MAX, preferences.get_t_max());
        values.put(KEY_PREF_T_MIN, preferences.get_t_min());
        values.put(KEY_PREF_H_MAX, preferences.get_h_max());
        values.put(KEY_PREF_H_MIN, preferences.get_h_min());
        values.put(KEY_PREF_POT1_H_MAX, preferences.get_pot1_h_max());
        values.put(KEY_PREF_POT1_H_MIN, preferences.get_pot1_h_min());
        values.put(KEY_PREF_POT2_H_MAX, preferences.get_pot2_h_max());
        values.put(KEY_PREF_POT2_H_MIN, preferences.get_pot2_h_min());
        values.put(KEY_PREF_WL_MAX, preferences.get_wl_max());
        values.put(KEY_PREF_WL_MIN, preferences.get_wl_min());

        values.put(KEY_PREF_ALL_NOTIFY, preferences.get_all_notify());
        values.put(KEY_PREF_T_NOTIFY, preferences.get_t_notify());
        values.put(KEY_PREF_H_NOTIFY, preferences.get_h_notify());
        values.put(KEY_PREF_POT1_NOTIFY, preferences.get_pot1_notify());
        values.put(KEY_PREF_POT2_NOTIFY, preferences.get_pot2_notify());
        values.put(KEY_PREF_L_NOTIFY, preferences.get_l_notify());
        values.put(KEY_PREF_WL_NOTIFY, preferences.get_wl_notify());
        values.put(KEY_PREF_RELAYS_NOTIFY, preferences.get_relays_notify());
        values.put(KEY_PREF_PUMPS_NOTIFY, preferences.get_pumps_notify());

        int row = db.update(TABLE_PREF,values,KEY_PREF_CTRL_ID + " = ?",new String[] { String.valueOf(preferences.get_ctrl_id()) });
        Log.d(LOG_TAG, "updateUserProfile result = " + row);
        return row;
    }


    // Creating a dev profile
    public long createDevProfile(Dev_profile dev_profile) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DEV_CTRL_ID, dev_profile.get_ctrl_id());
        values.put(KEY_DEV_LIGHT_CONTROL, dev_profile.get_light_control());
        values.put(KEY_DEV_T_CONTROL, dev_profile.get_t_control());
        values.put(KEY_DEV_H_CONTROL, dev_profile.get_h_control());
        values.put(KEY_DEV_POT1_CONTROL, dev_profile.get_pot1_control());
        values.put(KEY_DEV_POT2_CONTROL, dev_profile.get_pot2_control());
        values.put(KEY_DEV_RELAY1_CONTROL, dev_profile.get_relay1_control());
        values.put(KEY_DEV_RELAY2_CONTROL, dev_profile.get_relay2_control());
        values.put(KEY_DEV_PUMP1_CONTROL, dev_profile.get_pump1_control());
        values.put(KEY_DEV_PUMP2_CONTROL, dev_profile.get_pump2_control());
        values.put(KEY_DEV_WATER_CONTROL, dev_profile.get_water_control());
        values.put(KEY_DEV_AUTO_WATERING1, dev_profile.get_auto_watering1());
        values.put(KEY_DEV_AUTO_WATERING2, dev_profile.get_auto_watering2());

        // insert row
        long result = db.insert(TABLE_DEV_PROFILE, null, values);

        if(result==-1){
            Log.d(LOG_TAG,"Inserting to "+TABLE_DEV_PROFILE+" result: failed");
        } else Log.d(LOG_TAG,"Inserting to "+TABLE_DEV_PROFILE+" result: success");
        return result;
    }
    // Creating a main
    public long createSystemState(SystemState systemState) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MAIN_CTRL_ID, systemState.get_ctrl_id());
        values.put(KEY_MAIN_LIGHT_STATE, systemState.get_light_state());
        values.put(KEY_MAIN_T, systemState.get_t());
        values.put(KEY_MAIN_H, systemState.get_h());
        values.put(KEY_MAIN_POT1_H, systemState.get_pot1_h());
        values.put(KEY_MAIN_POT2_H, systemState.get_pot2_h());
        values.put(KEY_MAIN_RELAY1_STATE, systemState.get_relay1_state());
        values.put(KEY_MAIN_RELAY2_STATE, systemState.get_relay2_state());
        values.put(KEY_MAIN_PUMP1_STATE, systemState.get_pump1_state());
        values.put(KEY_MAIN_PUMP2_STATE, systemState.get_pump2_state());
        values.put(KEY_MAIN_WATER_LEVEL, systemState.get_water_level());
        values.put(KEY_MAIN_DATE, systemState.get_date());

        // insert row
        long result = db.insert(TABLE_MAIN, null, values);

        if(result==-1){
            Log.d(LOG_TAG,"Inserting to "+TABLE_MAIN+" result: failed");
        } else Log.d(LOG_TAG,"Inserting to "+TABLE_MAIN+" result: success");
        return result;
    }

    //Get ctrls
    public List<Controllers> getAllCtrls() {
        List<Controllers> ctrls = new ArrayList<Controllers>();
        String selectQuery = "SELECT  * FROM " + TABLE_CTRLS;

        Log.d(LOG_TAG, "getAllCtrls query = " + selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Controllers ctrl = new Controllers();
                ctrl.set_ctrl_id(c.getString((c.getColumnIndex(KEY_CTRL_ID))));
                ctrl.set_name(c.getString(c.getColumnIndex(KEY_CTRL_NAME)));
                ctrl.set_an(c.getString(c.getColumnIndex(KEY_CTRL_AN)));

                ctrls.add(ctrl);
            } while (c.moveToNext());
        }

        c.close();
        return ctrls;
    }
    //Get Controler name
    public Controllers getControllerName(String ctrl_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_CTRLS + " WHERE "
                + KEY_CTRL_ID + " = " + ctrl_id;
        Log.d(LOG_TAG, "getCtrlName query = " + selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        Controllers controllers = new Controllers();

        if (c.moveToFirst()){
            controllers.set_name(c.getString(c.getColumnIndex(KEY_CTRL_NAME)));
        }
        c.close();
        return controllers;
    }

    //Update controllers
    public void updateControllerName(Controllers controllers) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        //values.put(KEY_CTRL_ID, controllers.get_ctrl_id());
        values.put(KEY_CTRL_NAME, controllers.get_name());

        int row = db.update(TABLE_CTRLS,values,KEY_CTRL_ID + " = ?",new String[] { String.valueOf(controllers.get_ctrl_id()) });

        Log.d(LOG_TAG, "updateControllerName result = " + row);
    }

    //Get device profile
    public Dev_profile getDevProfile(String ctrl_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_DEV_PROFILE + " WHERE "
                + KEY_DEV_CTRL_ID + " = " + ctrl_id;
        Log.d(LOG_TAG, "getDevProfile query = " + selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        Dev_profile dev_profile = new Dev_profile();

        if (c.moveToFirst()){

            dev_profile.set_ctrl_id(c.getString(c.getColumnIndex(KEY_DEV_CTRL_ID)));
            dev_profile.set_light_control(c.getInt(c.getColumnIndex(KEY_DEV_LIGHT_CONTROL)));
            dev_profile.set_t_control(c.getInt(c.getColumnIndex(KEY_DEV_T_CONTROL)));
            dev_profile.set_h_control(c.getInt(c.getColumnIndex(KEY_DEV_H_CONTROL)));
            dev_profile.set_pot1_control(c.getInt(c.getColumnIndex(KEY_DEV_POT1_CONTROL)));
            dev_profile.set_pot2_control(c.getInt(c.getColumnIndex(KEY_DEV_POT2_CONTROL)));
            dev_profile.set_relay1_control(c.getInt(c.getColumnIndex(KEY_DEV_RELAY1_CONTROL)));
            dev_profile.set_relay2_control(c.getInt(c.getColumnIndex(KEY_DEV_RELAY2_CONTROL)));
            dev_profile.set_pump1_control(c.getInt(c.getColumnIndex(KEY_DEV_PUMP1_CONTROL)));
            dev_profile.set_pump2_control(c.getInt(c.getColumnIndex(KEY_DEV_PUMP2_CONTROL)));
            dev_profile.set_water_control(c.getInt(c.getColumnIndex(KEY_DEV_WATER_CONTROL)));
            dev_profile.set_auto_watering1(c.getInt(c.getColumnIndex(KEY_DEV_AUTO_WATERING1)));
            dev_profile.set_auto_watering2(c.getInt(c.getColumnIndex(KEY_DEV_AUTO_WATERING2)));
        }
        c.close();
        return dev_profile;
    }




    //Get system state
    public SystemState getSystemState(String ctrl_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_MAIN + " WHERE "
                + KEY_MAIN_CTRL_ID + " = " + ctrl_id;

        Log.d(LOG_TAG, "getSystemState query = "+selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);


        SystemState state = new SystemState();
        if (c.moveToFirst()){
            state.set_ctrl_id(c.getString(c.getColumnIndex(KEY_MAIN_CTRL_ID)));
            state.set_light_state(c.getInt(c.getColumnIndex(KEY_MAIN_LIGHT_STATE)));
            state.set_t(c.getInt(c.getColumnIndex(KEY_MAIN_T)));
            state.set_h(c.getInt(c.getColumnIndex(KEY_MAIN_H)));
            state.set_pot1_h(c.getInt(c.getColumnIndex(KEY_MAIN_POT1_H)));
            state.set_pot2_h(c.getInt(c.getColumnIndex(KEY_MAIN_POT2_H)));
            state.set_relay1_state(c.getInt(c.getColumnIndex(KEY_MAIN_RELAY1_STATE)));
            state.set_relay2_state(c.getInt(c.getColumnIndex(KEY_MAIN_RELAY2_STATE)));
            state.set_pump1_state(c.getInt(c.getColumnIndex(KEY_MAIN_PUMP1_STATE)));
            state.set_pump2_state(c.getInt(c.getColumnIndex(KEY_MAIN_PUMP2_STATE)));
            state.set_water_level(c.getInt(c.getColumnIndex(KEY_MAIN_WATER_LEVEL)));
            state.set_date(c.getString(c.getColumnIndex(KEY_MAIN_DATE)));
        }
        c.close();
        return state;
    }

    //Update system state
    public void updateSystemState(SystemState systemState) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MAIN_CTRL_ID, systemState.get_ctrl_id());
        values.put(KEY_MAIN_LIGHT_STATE, systemState.get_light_state());
        values.put(KEY_MAIN_T, systemState.get_t());
        values.put(KEY_MAIN_H, systemState.get_h());
        values.put(KEY_MAIN_POT1_H, systemState.get_pot1_h());
        values.put(KEY_MAIN_POT2_H, systemState.get_pot2_h());
        values.put(KEY_MAIN_RELAY1_STATE, systemState.get_relay1_state());
        values.put(KEY_MAIN_RELAY2_STATE, systemState.get_relay2_state());
        values.put(KEY_MAIN_PUMP1_STATE, systemState.get_pump1_state());
        values.put(KEY_MAIN_PUMP2_STATE, systemState.get_pump2_state());
        values.put(KEY_MAIN_WATER_LEVEL, systemState.get_water_level());
        values.put(KEY_MAIN_DATE, systemState.get_date());

        int row = db.update(TABLE_MAIN,values,KEY_MAIN_CTRL_ID + " = ?",new String[] { String.valueOf(systemState.get_ctrl_id()) });

        Log.d(LOG_TAG, "updateSystemState result = "+row);
    }



    //Delete system state table
    public void delSystemState(int ctrl_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MAIN, KEY_MAIN_CTRL_ID + " = ?",
                new String[] { String.valueOf(ctrl_id) });
    }

        // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
