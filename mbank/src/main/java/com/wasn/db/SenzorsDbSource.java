package com.wasn.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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

        long id = db.insert(SenzorsDbContract.Transaction.TABLE_NAME, null, values);
        db.close();

    }

    public ArrayList<Transaction> getAllTransactions(){
        ArrayList<Transaction> sensorList = new ArrayList();

        SQLiteDatabase db = SenzorsDbHelper.getInstance(context).getReadableDatabase();

        // join query to retrieve data
        String query = "SELECT * " +
                "FROM " +SenzorsDbContract.Transaction.TABLE_NAME+
                "";
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

        // extract attributes
        while (cursor.moveToNext()) {
            HashMap<String, String> senzAttributes = new HashMap<>();

            // get senz attributes
            id = cursor.getInt(cursor.getColumnIndex(SenzorsDbContract.Transaction._ID));
            clientName = cursor.getString(cursor.getColumnIndex(SenzorsDbContract.Transaction.COLUMN_NAME_CLIENTNAME));

            clientAccountNo = cursor.getString(cursor.getColumnIndex(SenzorsDbContract.Transaction.COLUMN_NAME_clientAccountNo));
            previousBalance = cursor.getString(cursor.getColumnIndex(SenzorsDbContract.Transaction.COLUMN_NAME_previousBalance));
            transactionAmount = cursor.getInt(cursor.getColumnIndex(SenzorsDbContract.Transaction.COLUMN_NAME_transactionAmount));
            transactionTime = cursor.getString(cursor.getColumnIndex(SenzorsDbContract.Transaction.COLUMN_NAME_transactionTime));
            transactionType = cursor.getString(cursor.getColumnIndex(SenzorsDbContract.Transaction.COLUMN_NAME_transactionType));
            clientNic = cursor.getString(cursor.getColumnIndex(SenzorsDbContract.Transaction.COLUMN_NAME_clientNIC));

            Transaction transaction=new Transaction(id,clientName,clientNic,clientAccountNo,previousBalance,transactionAmount,transactionTime,transactionType);
            //senzAttributes.put(_senzName, _senzValue);


            // fill senz list
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

    public int getSummeryAmmount(){
        ArrayList<Transaction> transactions=getAllTransactions();
        int total=0;
        for (int i=0; i<transactions.size(); i++) {
          total=total+  transactions.get(i).getTransactionAmount();
        }
        return total;
    }

}
