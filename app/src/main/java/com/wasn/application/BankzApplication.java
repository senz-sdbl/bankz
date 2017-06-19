package com.wasn.application;

import android.app.Application;

/**
 * Application object class of mobile-bank
 * Keep shared objects
 *
 * @author erangaeb@gmail.com (eranga bandara)
 */
public class BankzApplication extends Application {

    private static boolean isLogin;

    public static boolean isLogin() {
        return isLogin;
    }

    public static void setLogin(boolean login) {
        isLogin = login;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
