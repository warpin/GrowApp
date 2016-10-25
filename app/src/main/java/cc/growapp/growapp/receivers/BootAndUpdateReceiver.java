package cc.growapp.growapp.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


import java.io.IOException;
import java.net.InetAddress;

import cc.growapp.growapp.services.BackgroundService;


public class BootAndUpdateReceiver extends BroadcastReceiver {
    String LOG_TAG="Service";

    private static final String TAG = "BootAndUpdateReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {


        Log.d(LOG_TAG, "BootAndUpdateReceiver");

        Intent startServiceIntent = new Intent(context, BackgroundService.class);
        context.startService(startServiceIntent);
    }
}