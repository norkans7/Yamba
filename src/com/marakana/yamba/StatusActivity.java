package com.marakana.yamba;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import winterwell.jtwitter.TwitterException;

public class StatusActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = StatusActivity.class.getSimpleName();
    static final int DIALOG_ID = 47;

    EditText editStatus;
    Button buttonUpdate;

    YambaApplication yamba;



    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Debug.startMethodTracing("Yamba.trace");

        setContentView(R.layout.status);



        editStatus = (EditText) findViewById(R.id.editStatus);
        buttonUpdate = (Button) findViewById(R.id.buttonUpdate);

        buttonUpdate.setOnClickListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        //Debug.stopMethodTracing();
    }

    ////////// Dialog stuff

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id){
            case DIALOG_ID: {
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage( StatusActivity.this.getString(R.string.msgPleaseWaitWhilePosting));
                dialog.setIndeterminate(true);
                dialog.setCancelable(true);
                return dialog;

            }
        }
        return null;
    }





    /////////// button stuff

    @Override
    public void onClick(View view) {


        String status = editStatus.getText().toString();

        showDialog(DIALOG_ID);
        new PostToTwitter().execute(status);

        Log.d(TAG, "OnClicked with status: " + status );
    }

    private class PostToTwitter extends AsyncTask <String, String, String>  {

        @Override
        protected String doInBackground(String... status) {
            String result;

            try {

                yamba.getTwitter().setStatus(status[0]);

                result = StatusActivity.this.getString(R.string.msgStatusUpdatedSuccessfully);
            } catch (TwitterException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                result = StatusActivity.this.getString(R.string.msgStatusUpdateFailed);
            }


            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);    //To change body of overridden methods use File | Settings | File Templates.

            dismissDialog(DIALOG_ID);
            Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG).show();

        }
    }

}
