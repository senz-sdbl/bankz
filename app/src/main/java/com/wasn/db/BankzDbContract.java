package com.wasn.db;

import android.provider.BaseColumns;

class BankzDbContract {

    public static abstract class Transaction implements BaseColumns {
        static final String TABLE_NAME = "Trans";
        static final String COLUMN_NAME_UID = "uid";
        static final String COLUMN_NAME_CUSTOMER_NAME = "customer_name";
        static final String COLUMN_NAME_CUSTOMER_ACCOUNT_NO = "customer_account";
        static final String COLUMN_NAME_CUSTOMER_NIC = "customer_nic";
        static final String COLUMN_NAME_CUSTOMER_MOBILE = "customer_mobile";
        static final String COLUMN_NAME_AMOUNT = "amount";
        static final String COLUMN_NAME_BALANCE = "balance";
        static final String COLUMN_NAME_TIME = "time";
        static final String COLUMN_NAME_TYPE = "type";
    }
}
