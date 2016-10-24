package cc.growapp.growapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import cc.growapp.growapp.services.BackgroundService;


public class BootAndUpdateReceiver extends BroadcastReceiver {
    //String LOG_TAG="GrowApp";

    private static final String TAG = "BootAndUpdateReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED") ||
                intent.getAction().equals("android.intent.action.MY_PACKAGE_REPLACED")) {
            Intent startServiceIntent = new Intent(context, BackgroundService.class);
            context.startService(startServiceIntent);
        }
    }
}