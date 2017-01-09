package cc.growapp.growapp.database;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MyContentProvider extends ContentProvider {



    // Logcat tag
    private static final String LOG_TAG = "ContentProvider";

    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "GrowApp.db";


    // // Uri
    // authority
    static final String AUTHORITY = "cc.growapp.growapp.providers.database";



    // path
    static final String CTRLS_PATH = "controllers";
    static final String PREF_PATH = "preferences";
    static final String DEV_PROFILE_PATH = "dev_profile";
    static final String MAIN_PATH = "main";
    static final String LOCAL_PATH = "local";

    // Common URIs
    public static final Uri CTRLS_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + CTRLS_PATH);
    public static final Uri PREF_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + PREF_PATH);
    public static final Uri DEV_PROFILE_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + DEV_PROFILE_PATH);
    public static final Uri MAIN_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + MAIN_PATH);
    public static final Uri LOCAL_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + LOCAL_PATH);

    // Типы данных
    // набор строк
    static final String CTRLS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + CTRLS_PATH;
    // одна строка
    static final String CTRLS_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + CTRLS_PATH;


    static final String PREF_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + PREF_PATH;
    // одна строка
    static final String PREF_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + PREF_PATH;

    static final String DEV_PROFILE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + DEV_PROFILE_PATH;
    // одна строка
    static final String DEV_PROFILE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + DEV_PROFILE_PATH;

    static final String MAIN_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + MAIN_PATH;
    // одна строка
    static final String MAIN_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + MAIN_PATH;

    static final String LOCAL_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + LOCAL_PATH;
    // одна строка
    static final String LOCAL_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + LOCAL_PATH;

    //// UriMatcher
    // общий Uri
    static final int URI_CTRLS = 1;
    static final int URI_CTRLS_ID = 2;

    static final int URI_PREF = 3;
    static final int URI_PREF_ID = 4;

    static final int URI_DEV_PROFILE = 5;
    static final int URI_DEV_PROFILE_ID = 6;

    static final int URI_MAIN = 7;
    static final int URI_MAIN_ID = 8;


    static final int URI_LOCAL = 9;
    static final int URI_LOCAL_ID = 10;
    // описание и создание UriMatcher
    private static final UriMatcher uriMatcher;
    static {

        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, CTRLS_PATH, URI_CTRLS);
        uriMatcher.addURI(AUTHORITY, CTRLS_PATH + "/#", URI_CTRLS_ID);
        
        uriMatcher.addURI(AUTHORITY, PREF_PATH, URI_PREF);
        uriMatcher.addURI(AUTHORITY, PREF_PATH + "/#", URI_PREF_ID);

        uriMatcher.addURI(AUTHORITY, DEV_PROFILE_PATH, URI_DEV_PROFILE);
        uriMatcher.addURI(AUTHORITY, DEV_PROFILE_PATH + "/#",URI_DEV_PROFILE_ID);

        uriMatcher.addURI(AUTHORITY, MAIN_PATH, URI_MAIN);
        uriMatcher.addURI(AUTHORITY, MAIN_PATH + "/#",URI_MAIN_ID);

        uriMatcher.addURI(AUTHORITY, LOCAL_PATH, URI_LOCAL);
        uriMatcher.addURI(AUTHORITY, LOCAL_PATH + "/#",URI_LOCAL_ID);
    }

    DBHelper dbHelper;
    SQLiteDatabase db;



    // Table Names
    private static final String TABLE_CTRLS = "controllers";
    private static final String TABLE_PREF = "preferences";
    private static final String TABLE_DEV_PROFILE = "dev_profile";
    private static final String TABLE_MAIN = "main";
    private static final String TABLE_LOCAL = "local";


    // CTRLS Table - column names
    public static final String KEY_CTRL_ID = "ctrl_id";
    public static final String KEY_CTRL_NAME = "name";
    public static final String KEY_CTRL_AN = "an";

    // PREFERENCES Table - column names
    public static final String KEY_PREF_CTRL_ID = "ctrl_id";
    public static final String KEY_PREF_VERSION = "version";
    public static final String KEY_PREF_T_MAX = "t_max";
    public static final String KEY_PREF_T_MIN = "t_min";
    public static final String KEY_PREF_H_MAX = "h_max";
    public static final String KEY_PREF_H_MIN = "h_min";
    public static final String KEY_PREF_POT1_H_MAX = "pot1_h_max";
    public static final String KEY_PREF_POT1_H_MIN = "pot1_h_min";
    public static final String KEY_PREF_POT2_H_MAX = "pot2_h_max";
    public static final String KEY_PREF_POT2_H_MIN = "pot2_h_min";
    public static final String KEY_PREF_WL_MAX = "wl_max";
    public static final String KEY_PREF_WL_MIN = "wl_min";

    public static final String KEY_PREF_ALL_NOTIFY = "all_notify";
    public static final String KEY_PREF_T_NOTIFY = "t_notify";
    public static final String KEY_PREF_H_NOTIFY = "h_notify";
    public static final String KEY_PREF_POT1_NOTIFY = "pot1_notify";
    public static final String KEY_PREF_POT2_NOTIFY = "pot2_notify";
    public static final String KEY_PREF_WL_NOTIFY = "wl_notify";
    public static final String KEY_PREF_L_NOTIFY = "l_notify";
    public static final String KEY_PREF_RELAYS_NOTIFY = "relays_notify";
    public static final String KEY_PREF_PUMPS_NOTIFY = "pumps_notify";

    public static final String KEY_PREF_PERIOD = "period";
    public static final String KEY_PREF_SOUND = "sound";
    public static final String KEY_PREF_VIBRATE = "vibrate";
    public static final String KEY_PREF_COLOR = "color";

    // DEV Profile Table - column names
    public static final String KEY_DEV_CTRL_ID = "ctrl_id";
    public static final String KEY_DEV_LIGHT_CONTROL = "light_control";
    public static final String KEY_DEV_T_CONTROL = "t_control";
    public static final String KEY_DEV_H_CONTROL = "h_control";
    public static final String KEY_DEV_POT1_CONTROL = "pot1_control";
    public static final String KEY_DEV_POT2_CONTROL = "pot2_control";
    public static final String KEY_DEV_RELAY1_CONTROL = "relay1_control";
    public static final String KEY_DEV_RELAY2_CONTROL = "relay2_control";
    public static final String KEY_DEV_PUMP1_CONTROL = "pump1_control";
    public static final String KEY_DEV_PUMP2_CONTROL = "pump2_control";
    public static final String KEY_DEV_WATER_CONTROL = "water_control";
    public static final String KEY_DEV_AUTO_WATERING1 = "auto_watering1";
    public static final String KEY_DEV_AUTO_WATERING2 = "auto_watering2";
    public static final String KEY_DEV_PUMP_TIME = "pump_time";

    // MAIN Table - column names
    public static final String KEY_MAIN_CTRL_ID = "ctrl_id";
    public static final String KEY_MAIN_LIGHT_STATE = "light_state";
    public static final String KEY_MAIN_T = "t";
    public static final String KEY_MAIN_H = "h";
    public static final String KEY_MAIN_POT1_H = "pot1_h";
    public static final String KEY_MAIN_POT2_H = "pot2_h";
    public static final String KEY_MAIN_RELAY1_STATE = "relay1_state";
    public static final String KEY_MAIN_RELAY2_STATE = "relay2_state";
    public static final String KEY_MAIN_PUMP1_STATE = "pump1_state";
    public static final String KEY_MAIN_PUMP2_STATE = "pump2_state";
    public static final String KEY_MAIN_WATER_LEVEL = "water_level";
    public static final String KEY_MAIN_DATE = "date";

    //LOCAL TABLE
    public static final String KEY_LOCAL_CTRL_ID = "ctrl_id";
    public static final String KEY_LOCAL_START_TIME = "start_time";
    
    // Table Create Statements
    private static final String CREATE_TABLE_CTRLS = "CREATE TABLE "
            + TABLE_CTRLS + "(" + KEY_CTRL_ID + " TEXT PRIMARY KEY," +
            KEY_CTRL_NAME + " TEXT," +
            KEY_CTRL_AN + " TEXT" +")";

    
    private static final String CREATE_TABLE_LOCAL = "CREATE TABLE "
            + TABLE_LOCAL + "(" + KEY_LOCAL_CTRL_ID + " TEXT PRIMARY KEY," +
            KEY_LOCAL_START_TIME + " TEXT" +")";

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
            KEY_PREF_PUMPS_NOTIFY + " INTEGER," +
            KEY_PREF_PERIOD + " INTEGER," +
            KEY_PREF_SOUND + " TEXT," +
            KEY_PREF_VIBRATE + " TEXT," +
            KEY_PREF_COLOR + " INTEGER" +

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
            KEY_DEV_AUTO_WATERING2 + " INTEGER," +
            KEY_DEV_PUMP_TIME + " INTEGER" +
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


    public boolean onCreate() {
        Log.d(LOG_TAG, "Context provider method: onCreate");
        dbHelper = new DBHelper(getContext());
        return true;
    }

    // чтение
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {
        Log.d(LOG_TAG, "query, " + uri.toString());
        String id;
        db = dbHelper.getWritableDatabase();
        Cursor cursor;
        // проверяем Uri
        switch (uriMatcher.match(uri)) {
            case URI_CTRLS: // общий Uri
                Log.d(LOG_TAG, "URI_CTRLS");
                // если сортировка не указана, ставим свою - по имени
                if (TextUtils.isEmpty(sortOrder))sortOrder = KEY_CTRL_NAME + " ASC";
                cursor = db.query(TABLE_CTRLS, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case URI_CTRLS_ID: // Uri с ID
                id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_CTRLS_ID, " + id);
                // добавляем ID к условию выборки
                if (TextUtils.isEmpty(selection))selection = KEY_CTRL_ID + " = " + id;
                else selection = selection + " AND " + KEY_CTRL_ID + " = " + id;
                cursor = db.query(TABLE_CTRLS, projection, selection, selectionArgs, null, null, sortOrder);
            break;
            
            case URI_PREF: // общий Uri
                Log.d(LOG_TAG, "URI_PREF");
                // если сортировка не указана, ставим свою - по имени
                if (TextUtils.isEmpty(sortOrder))sortOrder = KEY_PREF_CTRL_ID + " ASC";
                cursor = db.query(TABLE_PREF, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case URI_PREF_ID: // Uri с ID
                id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_CTRLS_ID, " + id);
                // добавляем ID к условию выборки
                if (TextUtils.isEmpty(selection))selection = KEY_PREF_CTRL_ID + " = " + id;
                else selection = selection + " AND " + KEY_PREF_CTRL_ID + " = " + id;
                cursor = db.query(TABLE_PREF, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case URI_DEV_PROFILE: // общий Uri
                Log.d(LOG_TAG, "URI_DEV_PROFILE");
                // если сортировка не указана, ставим свою - по имени
                if (TextUtils.isEmpty(sortOrder))sortOrder = KEY_DEV_CTRL_ID + " ASC";
                cursor = db.query(TABLE_DEV_PROFILE, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case URI_DEV_PROFILE_ID: // Uri с ID
                id = uri.getLastPathSegment();

                // добавляем ID к условию выборки
                if(!id.isEmpty())selection = KEY_DEV_CTRL_ID + " = " + id;
                Log.d(LOG_TAG, "URI_DEV_PROFILE_ID, " + id + " selection:" + selection);
                cursor = db.query(TABLE_DEV_PROFILE, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            
            case URI_MAIN: // общий Uri
                Log.d(LOG_TAG, "URI_MAIN");
                // если сортировка не указана, ставим свою - по имени
                if (TextUtils.isEmpty(sortOrder))sortOrder = KEY_MAIN_CTRL_ID + " ASC";
                cursor = db.query(TABLE_MAIN, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case URI_MAIN_ID: // Uri с ID
                id = uri.getLastPathSegment();
                // добавляем ID к условию выборки
                //if (TextUtils.isEmpty(selection))selection = KEY_MAIN_CTRL_ID + " = " + id;
                if(!id.isEmpty())selection = KEY_MAIN_CTRL_ID + " = " + id;
                Log.d(LOG_TAG, "URI_MAIN_ID, " + id + " selection:" + selection);
                //else selection = selection + " AND " + KEY_MAIN_CTRL_ID + " = " + id;
                cursor = db.query(TABLE_MAIN, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case URI_LOCAL: // общий Uri
                Log.d(LOG_TAG, "URI_LOCAL");
                // если сортировка не указана, ставим свою - по имени
                if (TextUtils.isEmpty(sortOrder))sortOrder = KEY_LOCAL_CTRL_ID + " ASC";
                cursor = db.query(TABLE_LOCAL, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case URI_LOCAL_ID: // Uri с ID
                id = uri.getLastPathSegment();
                // добавляем ID к условию выборки
                //if (TextUtils.isEmpty(selection))selection = KEY_LOCAL_CTRL_ID + " = " + id;
                if(!id.isEmpty())selection = KEY_LOCAL_CTRL_ID + " = " + id;
                Log.d(LOG_TAG, "URI_LOCAL_ID, " + id + " selection:" + selection);
                //else selection = selection + " AND " + KEY_LOCAL_CTRL_ID + " = " + id;
                cursor = db.query(TABLE_LOCAL, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }


        // просим ContentResolver уведомлять этот курсор об изменениях данных в CTRLS_CONTENT_URI
        cursor.setNotificationUri(getContext().getContentResolver(), CTRLS_CONTENT_URI);
        return cursor;
    }

    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Log.d(LOG_TAG, "Getting URI: " + uri.toString());
        //if (uriMatcher.match(uri) != URI_CTRLS)
        db = dbHelper.getWritableDatabase();
        long rowID;
        Uri resultUri;
        switch (uriMatcher.match(uri)){
            case URI_CTRLS:
                rowID = db.insert(TABLE_CTRLS, null, values);
                resultUri = ContentUris.withAppendedId(CTRLS_CONTENT_URI, rowID);
                break;
            case URI_PREF:
                rowID = db.insert(TABLE_PREF, null, values);
                resultUri = ContentUris.withAppendedId(PREF_CONTENT_URI, rowID);
                break;
            case URI_DEV_PROFILE:
                rowID = db.insert(TABLE_DEV_PROFILE, null, values);
                resultUri = ContentUris.withAppendedId(DEV_PROFILE_CONTENT_URI, rowID);
                break;
            case URI_MAIN:
                rowID = db.insert(TABLE_MAIN, null, values);
                resultUri = ContentUris.withAppendedId(MAIN_CONTENT_URI, rowID);
                break;
            case URI_LOCAL:
                rowID = db.insert(TABLE_LOCAL, null, values);
                resultUri = ContentUris.withAppendedId(LOCAL_CONTENT_URI, rowID);
                break;
            default:throw new IllegalArgumentException("Wrong URI: " + uri);
        }


        // уведомляем ContentResolver, что данные по адресу resultUri изменились
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "delete, " + uri.toString());
        String id;
        int cnt;
        db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case URI_CTRLS:
                Log.d(LOG_TAG, "URI_CTRLS");
                cnt = db.delete(TABLE_CTRLS, selection, selectionArgs);
                break;
            case URI_CTRLS_ID:
                id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_CTRLS_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = KEY_CTRL_ID + " = " + id;
                } else {
                    selection = selection + " AND " + KEY_CTRL_ID + " = " + id;
                }
                cnt = db.delete(TABLE_CTRLS, selection, selectionArgs);
                break;
            case URI_PREF:
                Log.d(LOG_TAG, "URI_PREF");
                cnt = db.delete(TABLE_PREF, selection, selectionArgs);
                break;
            
            case URI_PREF_ID:
                id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_PREF_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = KEY_PREF_CTRL_ID + " = " + id;
                } else {
                    selection = selection + " AND " + KEY_PREF_CTRL_ID + " = " + id;
                }
                cnt = db.delete(TABLE_PREF, selection, selectionArgs);
                break;
            case URI_DEV_PROFILE:
                Log.d(LOG_TAG, "URI_DEV_PROFILE");
                cnt = db.delete(TABLE_DEV_PROFILE, selection, selectionArgs);
                break;

            case URI_DEV_PROFILE_ID:
                id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_DEV_PROFILE_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = KEY_DEV_CTRL_ID + " = " + id;
                } else {
                    selection = selection + " AND " + KEY_DEV_CTRL_ID + " = " + id;
                }
                cnt = db.delete(TABLE_DEV_PROFILE, selection, selectionArgs);
                break;
            case URI_MAIN:
                Log.d(LOG_TAG, "URI_MAIN");
                cnt = db.delete(TABLE_MAIN, selection, selectionArgs);
                break;

            case URI_MAIN_ID:
                id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_MAIN_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = KEY_MAIN_CTRL_ID + " = " + id;
                } else {
                    selection = selection + " AND " + KEY_MAIN_CTRL_ID + " = " + id;
                }
                cnt = db.delete(TABLE_MAIN, selection, selectionArgs);
                break;
            case URI_LOCAL:
                Log.d(LOG_TAG, "URI_LOCAL");
                cnt = db.delete(TABLE_LOCAL, selection, selectionArgs);
                break;

            case URI_LOCAL_ID:
                id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_LOCAL_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = KEY_LOCAL_CTRL_ID + " = " + id;
                } else {
                    selection = selection + " AND " + KEY_LOCAL_CTRL_ID + " = " + id;
                }
                cnt = db.delete(TABLE_LOCAL, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }


        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "update, " + uri.toString());
        db = dbHelper.getWritableDatabase();
        String id;
        int cnt=0;
        switch (uriMatcher.match(uri)) {
            case URI_CTRLS:
                Log.d(LOG_TAG, "UPDATE URI_CTRLS");

                break;

            case URI_CTRLS_ID:
                id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "UPDATE URI_CTRLS_ID, " + id);
                if (TextUtils.isEmpty(selection))selection = KEY_CTRL_ID + " = " + id;
                else selection = selection + " AND " + KEY_CTRL_ID + " = " + id;
                if (!id.isEmpty())selection = KEY_CTRL_ID + " = " + id;
                cnt = db.update(TABLE_CTRLS, values, selection, selectionArgs);
                break;
            case URI_DEV_PROFILE:
                Log.d(LOG_TAG, "UPDATE URI_DEV_PROFILE");

                break;
            case URI_DEV_PROFILE_ID:
                id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "UPDATE URI_DEV_PROFILE_ID, " + id);
                if (TextUtils.isEmpty(selection))selection = KEY_DEV_CTRL_ID + " = " + id;
                else selection = selection + " AND " + KEY_DEV_CTRL_ID + " = " + id;
                cnt = db.update(TABLE_DEV_PROFILE, values, selection, selectionArgs);
                break;
            case URI_PREF:
                Log.d(LOG_TAG, "UPDATE URI_PREF");

                break;
            case URI_PREF_ID:
                id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "UPDATE URI_PREF_ID, " + id);
                if (TextUtils.isEmpty(selection))selection = KEY_PREF_CTRL_ID + " = " + id;
                else selection = selection + " AND " + KEY_PREF_CTRL_ID + " = " + id;
                cnt = db.update(TABLE_PREF, values, selection, selectionArgs);
                break;
            case URI_MAIN:
                Log.d(LOG_TAG, "UPDATE URI_MAIN");

                break;
            case URI_MAIN_ID:
                id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "UPDATE URI_MAIN_ID, " + id);
                if (TextUtils.isEmpty(selection))selection = KEY_MAIN_CTRL_ID + " = " + id;
                else selection = selection + " AND " + KEY_MAIN_CTRL_ID + " = " + id;
                cnt = db.update(TABLE_MAIN, values, selection, selectionArgs);
                break;
            case URI_LOCAL:
                Log.d(LOG_TAG, "UPDATE URI_LOCAL");

                break;
            case URI_LOCAL_ID:
                id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "UPDATE URI_LOCAL_ID, " + id);
                if (TextUtils.isEmpty(selection))selection = KEY_LOCAL_CTRL_ID + " = " + id;
                else selection = selection + " AND " + KEY_LOCAL_CTRL_ID + " = " + id;
                cnt = db.update(TABLE_LOCAL, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        

        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    public String getType(Uri uri) {
        Log.d(LOG_TAG, "getType, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_CTRLS:
                return CTRLS_CONTENT_TYPE;
            case URI_CTRLS_ID:
                return CTRLS_CONTENT_ITEM_TYPE;
            case URI_PREF:
                return PREF_CONTENT_TYPE;
            case URI_PREF_ID:
                return PREF_CONTENT_ITEM_TYPE;
            case URI_DEV_PROFILE:
                return DEV_PROFILE_CONTENT_TYPE;
            case URI_DEV_PROFILE_ID:
                return DEV_PROFILE_CONTENT_ITEM_TYPE;
            case URI_MAIN:
                return MAIN_CONTENT_TYPE;
            case URI_MAIN_ID:
                return MAIN_CONTENT_ITEM_TYPE;
            case URI_LOCAL:
                return LOCAL_CONTENT_TYPE;
            case URI_LOCAL_ID:
                return LOCAL_CONTENT_ITEM_TYPE;


        }
        return null;
    }

    private class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // creating required tables
            db.execSQL(CREATE_TABLE_CTRLS);
            db.execSQL(CREATE_TABLE_PREF);
            db.execSQL(CREATE_TABLE_DEV_PROFILE);
            db.execSQL(CREATE_TABLE_MAIN);
            db.execSQL(CREATE_TABLE_LOCAL);
        }

        public void onTrancuate(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CTRLS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREF);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEV_PROFILE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAIN);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCAL);
            // create new tables
            onCreate(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // on upgrade drop older tables
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CTRLS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREF);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEV_PROFILE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAIN);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCAL);
            // create new tables
            onCreate(db);
        }

    }



}
