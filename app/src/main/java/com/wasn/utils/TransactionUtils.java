package com.wasn.utils;

import android.content.Context;

import com.wasn.db.BankzDbSource;
import com.wasn.exceptions.EmptyFieldsException;
import com.wasn.exceptions.InvalidBalanceAmountException;
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
     * Validate transaction form fields
     *
     * @param accountNo
     * @param amount
     */
    public static void validateFields(String accountNo, String amount) throws EmptyFieldsException, NumberFormatException {
        // check empty of fields
        if (accountNo.equals("") || amount.equals("")) {
            throw new EmptyFieldsException();
        }

        // validate amount
        try {
            Double.parseDouble(amount);
        } catch (NumberFormatException e) {
            throw e;
        }
    }

    /**
     * Calculate current balance
     *
     * @param previousBalance
     * @param transactionAmount
     * @return currentBalance
     */
    private static String getBalanceAmount(String previousBalance, String transactionAmount) throws InvalidBalanceAmountException {
        // calculate and format balance into #.## format
        // cna raise number format exception when parsing client balance
        try {
            double balance = (Double.parseDouble(previousBalance)) + (Double.parseDouble(transactionAmount));
            DecimalFormat decimalFormat = new DecimalFormat("0.00");

            return decimalFormat.format(balance);
        } catch (NumberFormatException e) {
            throw new InvalidBalanceAmountException();
        }
    }

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
     * @param username
     * @return
     */
    public static String getRegUsername(String username) {
        return "000000000000".substring(username.length()) + username;
    }

    /**
     * Generate receipt id according to receipt no and branch id
     *
     * @param branchId  users branch id
     * @param receiptNo receipt no
     * @return receiptId
     */
    public static String getReceiptId(String branchId, int receiptNo) {
        String receiptId;

        if (branchId.length() == 1) {
            receiptId = "0" + branchId + receiptNo;
        } else {
            receiptId = branchId + receiptNo;
        }

        return receiptId;
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
            summary.setBranchId(PreferenceUtils.getUser(context).getUsername());
            summary.setTime(getCurrentTime());

            return summary;
        } catch (NoUserException e) {
            e.printStackTrace();
        }

        return null;
    }

}
