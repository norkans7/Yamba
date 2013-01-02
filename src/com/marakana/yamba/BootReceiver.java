package com.marakana.yamba;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = BootReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        boolean startAtBoot = prefs.getBoolean("startAtBoot", false);

        if (startAtBoot) {
            context.startService(new Intent(context, UpdaterService.class));
        }
        Log.d(TAG, "onReceived prefs.startAtBoot" + startAtBoot);
    }
}
