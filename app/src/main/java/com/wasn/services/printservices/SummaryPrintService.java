package com.wasn.services.printservices;

import android.os.AsyncTask;

import com.wasn.ui.SummaryDetailsActivity;
import com.wasn.application.MobileBankApplication;
import com.wasn.exceptions.BluetoothNotAvailableException;
import com.wasn.exceptions.BluetoothNotEnableException;
import com.wasn.exceptions.CannotConnectToPrinterException;
import com.wasn.exceptions.CannotPrintException;
import com.wasn.pojos.Settings;
import com.wasn.pojos.Summary;
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
    MobileBankApplication application;
    Summary summary;

    /**
     * Initialize cass members
     *
     * @param activity
     */
    public SummaryPrintService(SummaryDetailsActivity activity, Summary summary) {
        this.activity = activity;
        this.summary = summary;
        application = (MobileBankApplication) activity.getApplication();
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
        String telephoneNo = "0775432015";
        String branchName = "Kirulapana";
        Settings settings = new Settings(printerAddress, telephoneNo, branchName);

        // send ate to printer
        try {
            PrintUtils.printSummary(summary, settings);

            // TODO print summary means day end(delete all transaction)

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
