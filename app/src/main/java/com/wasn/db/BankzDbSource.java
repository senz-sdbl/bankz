package com.wasn.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.wasn.pojos.Summary;
import com.wasn.pojos.Transaction;

import java.util.ArrayList;

public class BankzDbSource {
    private static final String TAG = BankzDbSource.class.getName();
    private Context context;

    public BankzDbSource(Context context) {
        Log.d(TAG, "Init: db source");

        this.context = context;
    }

    public void createTransaction(Transaction transaction) {
        SQLiteDatabase db = BankzDbHelper.getInstance(context).getWritableDatabase();

        // content values to inset
        ContentValues values = new ContentValues();
        values.put(BankzDbContract.Transaction.COLUMN_NAME_CUSTOMER_NAME, transaction.getClientName());
        values.put(BankzDbContract.Transaction.COLUMN_NAME_CUSTOMER_ACCOUNT_NO, transaction.getClientAccountNo());
        values.put(BankzDbContract.Transaction.COLUMN_NAME_CUSTOMER_NIC, transaction.getClientNic());
        values.put(BankzDbContract.Transaction.COLUMN_NAME_CUSTOMER_MOBILE, transaction.getClientMobile());
        values.put(BankzDbContract.Transaction.COLUMN_NAME_AMOUNT, transaction.getTransactionAmount());
        values.put(BankzDbContract.Transaction.COLUMN_NAME_BALANCE, transaction.getPreviousBalance());
        values.put(BankzDbContract.Transaction.COLUMN_NAME_TIME, transaction.getTransactionTime());
        values.put(BankzDbContract.Transaction.COLUMN_NAME_TYPE, transaction.getTransactionType());

        // insert the new row, if fails throw an error
        db.insertOrThrow(BankzDbContract.Transaction.TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<Transaction> getAllTransactions() {
        SQLiteDatabase db = BankzDbHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(BankzDbContract.Transaction.TABLE_NAME, // table
                null, // columns
                null,
                null, // selection
                null, // order by
                null, // group by
                null); // join

        ArrayList<Transaction> transactions = new ArrayList<>();

        // transaction attributes
        int id;
        String clientName;
        String clientAccountNo;
        String clientNic;
        String clientMobile;
        String balance;
        int transactionAmount;
        String transactionTime;
        String transactionType;

        // extract attributes
        while (cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndex(BankzDbContract.Transaction._ID));
            clientName = cursor.getString(cursor.getColumnIndex(BankzDbContract.Transaction.COLUMN_NAME_CUSTOMER_NAME));
            clientAccountNo = cursor.getString(cursor.getColumnIndex(BankzDbContract.Transaction.COLUMN_NAME_CUSTOMER_ACCOUNT_NO));
            clientNic = cursor.getString(cursor.getColumnIndex(BankzDbContract.Transaction.COLUMN_NAME_CUSTOMER_NIC));
            clientMobile = cursor.getString(cursor.getColumnIndex(BankzDbContract.Transaction.COLUMN_NAME_CUSTOMER_MOBILE));
            transactionAmount = cursor.getInt(cursor.getColumnIndex(BankzDbContract.Transaction.COLUMN_NAME_AMOUNT));
            balance = cursor.getString(cursor.getColumnIndex(BankzDbContract.Transaction.COLUMN_NAME_BALANCE));
            transactionTime = cursor.getString(cursor.getColumnIndex(BankzDbContract.Transaction.COLUMN_NAME_TIME));
            transactionType = cursor.getString(cursor.getColumnIndex(BankzDbContract.Transaction.COLUMN_NAME_TYPE));

            Transaction transaction = new Transaction(id,
                    clientName,
                    clientAccountNo,
                    clientNic,
                    clientMobile,
                    transactionAmount,
                    balance,
                    transactionTime,
                    transactionType);
            transactions.add(transaction);
        }

        // clean
        cursor.close();
        db.close();

        return transactions;
    }

    public Summary getTransactionSummary() {
        SQLiteDatabase db = BankzDbHelper.getInstance(context).getWritableDatabase();
        String sql = "SELECT COUNT(" + BankzDbContract.Transaction._ID + ") AS tcount," +
                " SUM(" + BankzDbContract.Transaction.COLUMN_NAME_AMOUNT + ") AS tsum" +
                " FROM " + BankzDbContract.Transaction.TABLE_NAME;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            int count = cursor.getInt(cursor.getColumnIndex("tcount"));
            int sum = cursor.getInt(cursor.getColumnIndex("tsum"));

            return new Summary("", Integer.toString(count), Integer.toString(sum), "");
        }

        return null;
    }

    public void deleteAllTransactions() {
        SQLiteDatabase db = BankzDbHelper.getInstance(context).getWritableDatabase();
        db.delete(BankzDbContract.Transaction.TABLE_NAME, null, null);
    }

}
