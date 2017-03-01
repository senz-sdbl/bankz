package com.wasn.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by root on 11/19/15.
 */
public class SenzorsDbHelper extends SQLiteOpenHelper {

    private static final String TAG = SenzorsDbHelper.class.getName();

    // we use singleton database
    private static SenzorsDbHelper senzorsDbHelper;

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "Senz2.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String NUMBER_TYPE = " NUM";


    private static final String SQL_CREATE_TRANSACTION =
            "CREATE TABLE IF NOT EXISTS " + SenzorsDbContract.Transaction.TABLE_NAME + " (" +
                    SenzorsDbContract.Transaction._ID + " " + " INTEGER PRIMARY KEY AUTOINCREMENT" + ", " +
                    SenzorsDbContract.Transaction.COLUMN_NAME_clientAccountNo + " " + NUMBER_TYPE + " NOT NULL" + ", " +
                    SenzorsDbContract.Transaction.COLUMN_NAME_CLIENTNAME + " " + TEXT_TYPE + ", " +
                    SenzorsDbContract.Transaction.COLUMN_NAME_previousBalance + " " + NUMBER_TYPE + ", " +
                    SenzorsDbContract.Transaction.COLUMN_NAME_transactionAmount + " " + NUMBER_TYPE + " NOT NULL" + ", " +
                    SenzorsDbContract.Transaction.COLUMN_NAME_transactionTime + " " + NUMBER_TYPE + " NOT NULL" + ", " +
                    SenzorsDbContract.Transaction.COLUMN_NAME_transactionType + " " + TEXT_TYPE + "," +
                    SenzorsDbContract.Transaction.COLUMN_NAME_clientNIC + " " + TEXT_TYPE +
                    ")";

    public SenzorsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "DB done");
    }

    synchronized static SenzorsDbHelper getInstance(Context context) {
        if (senzorsDbHelper == null) {
            senzorsDbHelper = new SenzorsDbHelper(context.getApplicationContext());
        }
        return (senzorsDbHelper);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, SQL_CREATE_TRANSACTION);
        try {
            sqLiteDatabase.execSQL(SQL_CREATE_TRANSACTION);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_CREATE_TRANSACTION);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        //db.setForeignKeyConstraintsEnabled(true);//ToDo minApi error
    }
}
