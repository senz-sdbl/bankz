package com.wasn.services.printservices;

import android.content.Context;
import android.os.AsyncTask;

import com.wasn.enums.PrintType;
import com.wasn.exceptions.BluetoothNotAvailableException;
import com.wasn.exceptions.BluetoothNotEnableException;
import com.wasn.exceptions.CannotConnectToPrinterException;
import com.wasn.exceptions.CannotPrintException;
import com.wasn.listeners.PrintListener;
import com.wasn.pojos.Settings;
import com.wasn.pojos.Transaction;
import com.wasn.utils.PreferenceUtils;
import com.wasn.utils.PrintUtils;

import java.io.IOException;

/**
 * Background task that handles printing
 *
 * @author eranga.herath@pagero.com (eranga herath)
 */
public class TransactionPrintService extends AsyncTask<String, String, String> {

    Context context;
    PrintListener printListener;
    Transaction transaction;
    PrintType printType;

    /**
     * Initialize class members
     */
    public TransactionPrintService(Context context, PrintListener printListener, Transaction transaction, PrintType printType) {
        this.context = context;
        this.printListener = printListener;
        this.transaction = transaction;
        this.printType = printType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doInBackground(String... strings) {
        // print type determine PRINT,REPRINT
        String printState = "0";

        // send data to printer according to print state
        if (printType == PrintType.PRINT) {
            printState = print();
        } else if (printType == PrintType.REPRINT) {
            printState = rePrint();
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
        String printerAddress = PreferenceUtils.getPrinterAddress(context);
        String telephoneNo = "Telephone";
        String branchName = "Branch";
        Settings settings = new Settings(printerAddress, telephoneNo, branchName);

        // send data to printer
        try {
            PrintUtils.printReceipt(transaction, settings);

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
        String printerAddress = PreferenceUtils.getPrinterAddress(context);
        String telephoneNo = "0775432015";
        String branchName = "Kirulapona";
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

        printListener.onPostPrint(status);
    }
}
