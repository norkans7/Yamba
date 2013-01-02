package com.marakana.yamba;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import winterwell.jtwitter.Twitter;


public class YambaApplication extends Application implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = YambaApplication.class.getSimpleName();
    SharedPreferences prefs;
    private Twitter twitter = null;
    StatusData statusData;
    private boolean isServicerunning = false;

    public boolean isServicerunning() {
        return isServicerunning;
    }

    public void setServicerunning(boolean isServiceRunning) {
        isServicerunning = isServiceRunning;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        statusData = new StatusData(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        statusData.close();
    }

    /**
     * Lazily Initializes the connection to the online service
     * @return Twitter object representing connection to online service
     */

    public synchronized Twitter getTwitter() {
        if (twitter==null){
            String username = prefs.getString("username", "");
            String password = prefs.getString("password", "");
            String server = prefs.getString("server", "");
            Log.d(TAG, String.format("%s API", server));
            twitter = new Twitter(username, password);
            twitter.setAPIRootUrl(server);
            Log.d(TAG, String.format("Getting Twitter with %s/%s@%s", username, password, server));

        }
        return twitter;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        // Invalidate twitter
        twitter = null;
    }
}
