package com.marakana.yamba;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import winterwell.jtwitter.Twitter.Status;

import java.util.List;

public class UpdaterService extends Service {
    private static final String TAG = UpdaterService.class.getSimpleName();
    public static final String ACTION_NEW_STATUS = "Yamba.NewStatus";
    private Updater updater;
    YambaApplication yamba;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        yamba = (YambaApplication) getApplication();
        updater = new Updater();

        Log.d(TAG, "onCreate'd");
    }

    @Override
    public synchronized void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        // start the updater
        if (!yamba.isServicerunning()){
            updater.start();

        }

        Log.d(TAG, "onStart'd");
    }

    @Override
    public synchronized void onDestroy() {
        super.onDestroy();

        // stop the updater
        if(yamba.isServicerunning()){
            updater.interrupt();
        }

        updater = null;
        Log.d(TAG, "onDestroy'd");
    }

    class Updater extends Thread {

        static final long DELAY = 60000;


        public Updater() {
            super("Updater");

        }

        @Override
        public void run() {
            boolean hasNewStatuses = false;
            yamba.setServicerunning(true);
            while (yamba.isServicerunning()) {
                try {
                    // do something
                    Log.d(TAG, "Updater run'ing");

                    // get friends statuses
                    List<Status> statuses = yamba.getTwitter().getFriendsTimeline();


                    for (Status status: statuses){
                        long id;
                        if ((id = yamba.statusData.insert(status)) > 0){
                            hasNewStatuses = true;
                        }

                        Log.d(TAG, String.format("%s: %s (%d)", status.user.name, status.text, id));
                    }

                    // check if there are new statuses
                    if (hasNewStatuses){
                        sendBroadcast(new Intent(ACTION_NEW_STATUS));
                    }

                    // sleep
                    Thread.sleep(DELAY);

                } catch (InterruptedException e) {
                  // interrupted
                  yamba.setServicerunning(false);
                }
            } // while
        }

    }
}

