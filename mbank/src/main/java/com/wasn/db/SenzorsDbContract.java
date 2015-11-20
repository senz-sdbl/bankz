package com.wasn.db;

import android.provider.BaseColumns;

/**
 * Created by root on 11/19/15.
 */
public class SenzorsDbContract {

    public static abstract class Transaction implements BaseColumns {
        public static final String TABLE_NAME = "transaction_table";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_CLIENTNAME = "client_name";
        public static final String COLUMN_NAME_clientAccountNo = "client_account_no";
        public static final String COLUMN_NAME_previousBalance = "previous_balance";
        public static final String COLUMN_NAME_transactionAmount = "transaction_amount";
        public static final String COLUMN_NAME_transactionTime = "transaction_time";
        public static final String COLUMN_NAME_transactionType = "transaction_type";
        public static final String COLUMN_NAME_clientNIC = "client_nic";


    }
    public static abstract class MetaData implements BaseColumns {
        public static final String TABLE_NAME = "metadata";
        public static final String COLUMN_NAME_DATA = "data_name";
        public static final String COLUMN_NAME_VALUE = "value";



    }
}
