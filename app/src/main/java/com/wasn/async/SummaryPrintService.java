package com.wasn.async;

import android.os.AsyncTask;

import com.wasn.application.BankzApplication;
import com.wasn.db.BankzDbSource;
import com.wasn.exceptions.BluetoothNotAvailableException;
import com.wasn.exceptions.BluetoothNotEnableException;
import com.wasn.exceptions.CannotConnectToPrinterException;
import com.wasn.exceptions.CannotPrintException;
import com.wasn.pojos.Setting;
import com.wasn.pojos.Summary;
import com.wasn.ui.SummaryDetailsActivity;
import com.wasn.utils.PreferenceUtils;
import com.wasn.utils.PrintUtils;

import java.io.IOException;

/**
 * Background task that handles summary printing
 *
 * @author eranga.herath@pagero.com (eranga herath)
 */
public class SummaryPrintService extends AsyncTask<String, String, String> {

    SummaryDetailsActivity activity;
    BankzApplication application;
    Summary summary;

    /**
     * Initialize cass members
     *
     * @param activity
     */
    public SummaryPrintService(SummaryDetailsActivity activity, Summary summary) {
        this.activity = activity;
        this.summary = summary;
        application = (BankzApplication) activity.getApplication();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doInBackground(String... strings) {
        // send data to printer
        return print();
    }

    /**
     * print summary receipt
     *
     * @return print status
     */
    private String print() {
        // printing attributes
        String printerAddress = PreferenceUtils.getPrinterAddress(application);
        String branchName = PreferenceUtils.getBranch(application);
        String telephoneNo = PreferenceUtils.getPhone(application);
        Setting settings = new Setting("", branchName, telephoneNo, printerAddress);

        // send ate to printer
        try {
            PrintUtils.printSummary(summary, settings);

            // print summary means day end(delete all transaction)
            new BankzDbSource(activity).deleteAllTransactions();

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
