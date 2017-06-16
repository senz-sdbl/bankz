package com.wasn.utils;

import android.content.Context;

import com.wasn.db.BankzDbSource;
import com.wasn.exceptions.NoUserException;
import com.wasn.pojos.Summary;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Utility class of transaction activity
 *
 * @author erangaeb@gmail.com (eranga bandara)
 */
public class TransactionUtils {

    /**
     * Get current date and time as transaction time
     * format - yyyy/MM/dd HH:mm:ss
     *
     * @return
     */
    public static String getCurrentTime() {
        //date format
        String DATE_FORMAT_NOW = "yyyy/MM/dd HH:mm:ss";

        // generate time
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_NOW);

        return simpleDateFormat.format(calendar.getTime());
    }

    public static String getTransactionTime(Long timestamp) {
        //date format
        String DATE_FORMAT_NOW = "yyyy/MM/dd HH:mm:ss";

        // generate time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_NOW);

        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * Compare account no of two transaction to determine weather they are same
     *
     * @return
     */
    public static boolean isNewTransaction(String preAccount, String curAccount) {
        return preAccount.equalsIgnoreCase(getTransactionAccount(curAccount));
    }

    /**
     * Format account with 12 digits
     *
     * @return
     */
    public static String getTransactionAccount(String account) {
        return "000000000000".substring(account.length()) + account;
    }

    /**
     * Format mobile with 10 digits
     *
     * @return
     */
    public static String getTransactionMobile(String mobile) {
        if (!mobile.isEmpty())
            return "0000000000".substring(mobile.length()) + mobile;

        return "";
    }

    /**
     * Format with 12 digits
     *
     * @param account
     * @return
     */
    public static String getRegAccount(String account) {
        return "000000000000".substring(account.length()) + account;
    }

    /**
     * Get summary as a list of attributes
     *
     * @param context
     */
    public static Summary getSummary(Context context) {
        // user
        try {
            Summary summary = new BankzDbSource(context).getTransactionSummary();
            summary.setAgent(PreferenceUtils.getUser(context).getUsername());
            summary.setTime(getCurrentTime());

            return summary;
        } catch (NoUserException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String formatAmount(int amount) {
        DecimalFormat formatter = new DecimalFormat("#,###.00");

        return formatter.format(amount);
    }

}
