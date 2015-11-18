package com.wasn.application;

import android.app.Application;

import com.wasn.models.MobileBankData;

/**
 * Application object class of mobile-bank
 * Keep shared objects
 *
 * @author erangaeb@gmail.com (eranga bandara)
 */
public class MobileBankApplication extends Application {

    // database class instance
    MobileBankData mobileBankData;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate() {
        super.onCreate();

        mobileBankData = new MobileBankData(MobileBankApplication.this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onTerminate() {
        super.onTerminate();

        // close database connections
        mobileBankData.close();
    }

    public MobileBankData getMobileBankData() {
        return mobileBankData;
    }

}
