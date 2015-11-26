package com.wasn.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.wasn.pojos.Summary;
import com.wasn.pojos.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by root on 11/19/15.
 */
public class SenzorsDbSource {
    private static final String TAG = SenzorsDbSource.class.getName();
    private static Context context;

    public SenzorsDbSource(Context context) {
        Log.d(TAG, "Init: db source");
        this.context = context;
    }

    public void createTransaction(Transaction transaction){
        SQLiteDatabase db = SenzorsDbHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SenzorsDbContract.Transaction.COLUMN_NAME_clientAccountNo, transaction.getClientAccountNo());
        values.put(SenzorsDbContract.Transaction.COLUMN_NAME_CLIENTNAME, transaction.getClientName());
        values.put(SenzorsDbContract.Transaction.COLUMN_NAME_previousBalance, transaction.getPreviousBalance());
        values.put(SenzorsDbContract.Transaction.COLUMN_NAME_transactionAmount, transaction.getTransactionAmount());
        values.put(SenzorsDbContract.Transaction.COLUMN_NAME_transactionTime, transaction.getTransactionTime());
        values.put(SenzorsDbContract.Transaction.COLUMN_NAME_transactionType, transaction.getTransactionType());
        values.put(SenzorsDbContract.Transaction.COLUMN_NAME_clientNIC, transaction.getClientNic());
        values.put(SenzorsDbContract.Transaction.COLUMN_NAME_ID, transaction.getId());

        long id = db.insert(SenzorsDbContract.Transaction.TABLE_NAME, null, values);
        db.close();

    }

    public ArrayList<Transaction> getAllTransactions(){
        ArrayList<Transaction> sensorList = new ArrayList();

        SQLiteDatabase db = SenzorsDbHelper.getInstance(context).getReadableDatabase();

        // join query to retrieve data
        String query = "SELECT * " +
                "FROM " +SenzorsDbContract.Transaction.TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);
// sensor/user attributes
        int id;
        String clientName;
        String clientNic;
        String clientAccountNo;
        String previousBalance;
        int transactionAmount;
        String transactionTime;
        String transactionType;
        Log.e(TAG,cursor.getCount()+"f");
        // extract attributes
        while (cursor.moveToNext()) {
            HashMap<String, String> senzAttributes = new HashMap<>();

            // get senz attributes

            id = cursor.getInt(cursor.getColumnIndex(SenzorsDbContract.Transaction.COLUMN_NAME_ID));

            clientName = cursor.getString(cursor.getColumnIndex(SenzorsDbContract.Transaction.COLUMN_NAME_CLIENTNAME));

            clientAccountNo = cursor.getString(cursor.getColumnIndex(SenzorsDbContract.Transaction.COLUMN_NAME_clientAccountNo));

            previousBalance = cursor.getString(cursor.getColumnIndex(SenzorsDbContract.Transaction.COLUMN_NAME_previousBalance));

            transactionAmount = cursor.getInt(cursor.getColumnIndex(SenzorsDbContract.Transaction.COLUMN_NAME_transactionAmount));

            transactionTime = cursor.getString(cursor.getColumnIndex(SenzorsDbContract.Transaction.COLUMN_NAME_transactionTime));

            transactionType = cursor.getString(cursor.getColumnIndex(SenzorsDbContract.Transaction.COLUMN_NAME_transactionType));

            clientNic = cursor.getString(cursor.getColumnIndex(SenzorsDbContract.Transaction.COLUMN_NAME_clientNIC));
            System.out.println(id+" "+clientName+" "+clientAccountNo+" "+previousBalance+" "+transactionAmount+" "+transactionTime+" "+transactionType);
            Transaction transaction=new Transaction(id,clientName,clientNic,clientAccountNo,previousBalance,transactionAmount,transactionTime,transactionType);
            //senzAttributes.put(_senzName, _senzValue);

/*

            Log.d(TAG,cursor.getColumnIndex(SenzorsDbContract.Transaction.COLUMN_NAME_ID)+"");
            Log.d(TAG,cursor.getColumnIndex(SenzorsDbContract.Transaction.COLUMN_NAME_previousBalance)+"");
            Log.d(TAG,cursor.getColumnIndex(SenzorsDbContract.Transaction.COLUMN_NAME_transactionTime)+"");
            Log.d(TAG,cursor.getColumnIndex(SenzorsDbContract.Transaction.COLUMN_NAME_transactionType)+"");
            Log.d(TAG,cursor.getColumnIndex(SenzorsDbContract.Transaction.COLUMN_NAME_clientAccountNo)+"");
            Log.d(TAG,cursor.getColumnIndex(SenzorsDbContract.Transaction.COLUMN_NAME_CLIENTNAME)+"");
            Log.d(TAG,cursor.getColumnIndex(SenzorsDbContract.Transaction.COLUMN_NAME_clientNIC)+"");
            Log.d(TAG,cursor.getColumnIndex(SenzorsDbContract.Transaction.COLUMN_NAME_transactionAmount)+"");

*/

            // fill senz list
            System.out.println("Done in Create object");
            sensorList.add(transaction);
        }

        // clean
        cursor.close();
        db.close();


        return sensorList;

    }

    public void clearTable() {
        SQLiteDatabase db = SenzorsDbHelper.getInstance(context).getWritableDatabase();

        // delete senz of given user

        db.close();
    }

    public Summary getSummeryAmmount(){
        SQLiteDatabase db = SenzorsDbHelper.getInstance(context).getWritableDatabase();
        String query = "SELECT COUNT("+SenzorsDbContract.Transaction.COLUMN_NAME_ID+") AS trcount, SUM("+SenzorsDbContract.Transaction.COLUMN_NAME_transactionAmount+") AS total" +
                " FROM " +SenzorsDbContract.Transaction.TABLE_NAME;
        Log.e(TAG,query);
        Cursor cursor = db.rawQuery(query, null);
        String tcount;
        String tamount;
        if(cursor.moveToFirst()){
            tcount = cursor.getString(cursor.getColumnIndex("trcount"));

            tamount = cursor.getString(cursor.getColumnIndex("total"));
        }
        else{
            tcount = "0";

            tamount = "0";
        }
        Summary tempsum=new Summary("10255",tcount,tamount,"");



        return tempsum;
    }

}
