package com.marakana.yamba;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import winterwell.jtwitter.Twitter.Status;

public class StatusData {
    private static final String TAG = StatusData.class.getSimpleName();

    public static final String C_ID = BaseColumns._ID;
    public static final String C_CREATED_AT = "yamba_createdAt";
    public static final String C_USER = "yamba_user";
    public static final String C_TEXT = "yamba_text";


    Context context;
    DbHelper dbHelper;

    public StatusData(Context context) {
        this.context = context;
        dbHelper = new DbHelper();
    }


    public void close() {
        dbHelper.close();
    }

    /**
     * Insert into database
     * @param values  Name/Value pairs data
     */
    public long insert(ContentValues values){
        // open database

        SQLiteDatabase db = dbHelper.getWritableDatabase();


        long ret;


        try {
            ret = db.insertOrThrow(DbHelper.TABLE, null, values);
        } catch (Exception e) {
            ret = -1;
        }  finally {

        // close database
        db.close();
        }
        return ret;
    }


    /**
     * Insert into database
     * @param status    Status data as provided by online service.
     */
    public long insert(Status status){

        ContentValues values = new ContentValues();

        values.put(StatusData.C_ID, status.id);
        values.put(StatusData.C_CREATED_AT, status.createdAt.getTime());
        values.put(StatusData.C_USER, status.user.name);
        values.put(StatusData.C_TEXT, status.text);

        return this.insert(values);

    }

    /**
     * Deletes ALL the  data.
     */
    public void delete() {
        // open database
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // delete data
        db.delete(DbHelper.TABLE, null, null);

        // close database
        db.close();

    }


    public Cursor query() {
        // open database
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return  db.query(DbHelper.TABLE, null, null, null, null, null, C_CREATED_AT + " DESC");

    }

    private class DbHelper extends SQLiteOpenHelper {

        public static final String DB_NAME = "timeline.db";
        public static final int DB_VERSION = 1;
        public static final String TABLE = "statuses";


        public DbHelper() {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = String.format("create table %s (%s INT primary key, %s INT, %s TEXT, %s TEXT)", TABLE, C_ID, C_CREATED_AT, C_USER, C_TEXT);

            Log.d(TAG, "onCreate sql: " + sql);

            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i2) {
            db.execSQL("drop table if exists " + TABLE);
            Log.d(TAG, "onUpdate dropped table " + TABLE);
            this.onCreate(db);
        }
    }

}
