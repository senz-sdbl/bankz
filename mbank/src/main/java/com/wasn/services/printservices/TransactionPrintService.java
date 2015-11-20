package com.wasn.services.printservices;

import android.os.AsyncTask;

import com.wasn.application.MobileBankApplication;
import com.wasn.exceptions.BluetoothNotAvailableException;
import com.wasn.exceptions.BluetoothNotEnableException;
import com.wasn.exceptions.CannotConnectToPrinterException;
import com.wasn.exceptions.CannotPrintException;
import com.wasn.pojos.Settings;
import com.wasn.pojos.Transaction;
import com.wasn.ui.TransactionDetailsActivity;
import com.wasn.utils.PreferenceUtils;
import com.wasn.utils.PrintUtils;

import java.io.IOException;

/**
 * Background task that handles printing
 *
 * @author eranga.herath@pagero.com (eranga herath)
 */
public class TransactionPrintService extends AsyncTask<String, String, String> {

    TransactionDetailsActivity activity;
    MobileBankApplication application;

    Transaction transaction;

    /**
     * Initialize class members
     *
     * @param activity
     */
    public TransactionPrintService(TransactionDetailsActivity activity, Transaction transaction) {
        this.activity = activity;
        application = (MobileBankApplication) activity.getApplication();

        this.transaction = transaction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doInBackground(String... strings) {
        // print type determine PRINT,RE_PRINT
        String printType = strings[0];
        String printState = "0";

        // send data to printer according to print state
        if (printType.equals("PRINT")) {
            printState = print();
        } else if (printType.equals("RE_PRINT")) {
            printState = print();
        }

        return printState;
    }

    /**
     * print receipt
     *
     * @return
     */
    public String print() {
        // printing attributes
        String printerAddress = PreferenceUtils.getPrinterAddress(activity);
        String telephoneNo = "Telephone";
        String branchName = "Branch";
        Settings settings = new Settings(printerAddress, telephoneNo, branchName);

        // send data to printer
        try {
            PrintUtils.printReceipt(transaction, settings);

            // after printing save transaction and receipt no
            // update client balance
            // update previous transaction amount
            application.getMobileBankData().insertTransaction(transaction);
            application.getMobileBankData().setReceiptNo(Integer.toString(transaction.getId()));
            application.getMobileBankData().updatePreviousTransactionAmount(transaction.getClientAccountNo(), Integer.toString(transaction.getTransactionAmount()));

            return "1";
        } catch (IOException e) {
            e.printStackTrace();
            return "-1";
        } catch (BluetoothNotEnableException e) {
            e.printStackTrace();
            return "-2";
        } catch (BluetoothNotAvailableException e) {
            e.printStackTrace();
            return "-3";
        } catch (CannotConnectToPrinterException e) {
            e.printStackTrace();
            return "-4";
        } catch (CannotPrintException e) {
            e.printStackTrace();
            return "0";
        } catch (IllegalArgumentException e) {
            // invalid bluetooth address
            e.printStackTrace();
            return "-5";
        }
    }

    /**
     * re print receipt
     *
     * @return
     */
    public String rePrint() {
        // printing attributes
        String printerAddress = application.getMobileBankData().getPrinterAddress();
        String telephoneNo = application.getMobileBankData().getTelephoneNo();
        String branchName = application.getMobileBankData().getBranchName();
        Settings settings = new Settings(printerAddress, telephoneNo, branchName);

        // send data to printer
        try {
            PrintUtils.rePrintReceipt(transaction, settings);

            return "1";
        } catch (IOException e) {
            e.printStackTrace();
            return "-1";
        } catch (BluetoothNotEnableException e) {
            e.printStackTrace();
            return "-2";
        } catch (BluetoothNotAvailableException e) {
            e.printStackTrace();
            return "-3";
        } catch (CannotConnectToPrinterException e) {
            e.printStackTrace();
            return "-4";
        } catch (CannotPrintException e) {
            e.printStackTrace();
            return "0";
        } catch (IllegalArgumentException e) {
            // invalid bluetooth address
            e.printStackTrace();
            return "-5";
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPostExecute(String status) {
        super.onPostExecute(status);

        activity.onPostPrint(status);
    }
}
