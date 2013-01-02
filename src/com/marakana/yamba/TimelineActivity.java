package com.marakana.yamba;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;


public class TimelineActivity extends BaseActivity {
    private static final String TAG = TimelineActivity.class.getSimpleName();
    ListView listStatus;


    Cursor cursor;
    SimpleCursorAdapter adapter;
    TimelineReceiver receiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set up UI
        setContentView(R.layout.timeline);
        listStatus = (ListView) findViewById(R.id.listStatus);

        // setup List
        this.setupList();

        // check if preferences are set
        if (yamba.prefs.getString("username", null) == null) {
            startActivity(new Intent(this, PrefsActivity.class));
            Toast.makeText(this, R.string.msgSetupPrefs, Toast.LENGTH_LONG).show();
            return;
        }
        Log.d(TAG, "onCreate'd");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register TimelineReceiver
        if (receiver == null){
            receiver = new TimelineReceiver();
        }
        registerReceiver(receiver, new IntentFilter(UpdaterService.ACTION_NEW_STATUS));

    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister TimelineReceiver
        unregisterReceiver(receiver);
    }

    private void setupList(){

        // get the data
        cursor = yamba.statusData.query();
        startManagingCursor(cursor);

        // Setup adapter
        String[] from = {StatusData.C_USER, StatusData.C_TEXT, StatusData.C_CREATED_AT};

        int[] to = { R.id.textUser, R.id.textText, R.id.textCreatedAt};

        adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, from, to);
        adapter.setViewBinder(VIEW_BINDER);
        listStatus.setAdapter(adapter);

    }

    /*
         *  Our custom binder to bind createdAt column to its view
         *  and change data from timestamp to relative time
         */
    static final ViewBinder VIEW_BINDER = new ViewBinder() {


        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

            if (cursor.getColumnIndex(StatusData.C_CREATED_AT) != columnIndex) {
                // we are not processing anything other than createdAt column here
                return false;
            } else {
                long timestamp =  cursor.getLong(columnIndex);
                CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(timestamp).toString();

                ((TextView)view).setText(relativeTime);
                return true;
            }


        }
    };

    /*
     * TimelineReceiver wakes up when there is a new status
     */
    class TimelineReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // refresh the list once there is a new status
            setupList();
        }
    }
}
