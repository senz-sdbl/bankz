package com.wasn.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class BankzDbHelper extends SQLiteOpenHelper {

    private static final String TAG = BankzDbHelper.class.getName();

    private static BankzDbHelper senzorsDbHelper;

    private static final int DATABASE_VERSION = 8;
    private static final String DATABASE_NAME = "bankz.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String NUMBER_TYPE = " NUM";

    private static final String SQL_CREATE_TRANS =
            "CREATE TABLE IF NOT EXISTS " + BankzDbContract.Transaction.TABLE_NAME + " (" +
                    BankzDbContract.Transaction._ID + " " + " INTEGER PRIMARY KEY AUTOINCREMENT" + ", " +
                    BankzDbContract.Transaction.COLUMN_NAME_CUSTOMER_ACCOUNT_NO + " " + NUMBER_TYPE + " NOT NULL" + ", " +
                    BankzDbContract.Transaction.COLUMN_NAME_CUSTOMER_NAME + " " + TEXT_TYPE + ", " +
                    BankzDbContract.Transaction.COLUMN_NAME_CUSTOMER_NIC + " " + TEXT_TYPE + ", " +
                    BankzDbContract.Transaction.COLUMN_NAME_CUSTOMER_MOBILE + " " + TEXT_TYPE + ", " +
                    BankzDbContract.Transaction.COLUMN_NAME_AMOUNT + " " + NUMBER_TYPE + " NOT NULL" + ", " +
                    BankzDbContract.Transaction.COLUMN_NAME_BALANCE + " " + NUMBER_TYPE + ", " +
                    BankzDbContract.Transaction.COLUMN_NAME_TIME + " " + NUMBER_TYPE + " NOT NULL" + ", " +
                    BankzDbContract.Transaction.COLUMN_NAME_TYPE + " " + TEXT_TYPE +
                    ")";

    private static final String SQL_DELETE_TRANS =
            "DROP TABLE IF EXISTS " + BankzDbContract.Transaction.TABLE_NAME;

    private BankzDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    synchronized static BankzDbHelper getInstance(Context context) {
        if (senzorsDbHelper == null) {
            senzorsDbHelper = new BankzDbHelper(context.getApplicationContext());
        }

        return (senzorsDbHelper);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, SQL_CREATE_TRANS);
        sqLiteDatabase.execSQL(SQL_CREATE_TRANS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d(TAG, SQL_DELETE_TRANS);
        sqLiteDatabase.execSQL(SQL_DELETE_TRANS);
        onCreate(sqLiteDatabase);
    }

}
