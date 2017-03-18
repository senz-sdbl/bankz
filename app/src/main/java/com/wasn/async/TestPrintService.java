package com.wasn.async;

import android.os.AsyncTask;

import com.wasn.exceptions.BluetoothNotAvailableException;
import com.wasn.exceptions.BluetoothNotEnableException;
import com.wasn.exceptions.CannotConnectToPrinterException;
import com.wasn.exceptions.CannotPrintException;
import com.wasn.pojos.Setting;
import com.wasn.ui.SettingsActivity;
import com.wasn.utils.PreferenceUtils;
import com.wasn.utils.PrintUtils;

import java.io.IOException;

/**
 * Background task that handles test printing
 *
 * @author eranga.herath@pagero.com (eranga herath)
 */
public class TestPrintService extends AsyncTask<String, String, String> {

    SettingsActivity activity;
    Setting settings;

    /**
     * Initialize cass members
     *
     * @param activity
     */
    public TestPrintService(SettingsActivity activity, Setting settings) {
        this.activity = activity;
        this.settings = settings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doInBackground(String... strings) {
        return print(settings);
    }

    /**
     * print test receipt
     *
     * @param
     * @return
     */
    public String print(Setting settings) {
        // send data to printer
        try {
            PrintUtils.printTestPrint(settings);

            // save settings in SP
            PreferenceUtils.savePrinterAddress(activity, settings.getPrinterAddress());
            PreferenceUtils.saveBranch(activity, settings.getBranch());
            PreferenceUtils.savePhone(activity, settings.getTelephone());

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
