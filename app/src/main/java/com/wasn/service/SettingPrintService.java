package com.wasn.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.wasn.R;
import com.wasn.application.IntentProvider;
import com.wasn.pojos.Setting;
import com.wasn.utils.BitmapUtils;
import com.wasn.utils.PreferenceUtils;

import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

public class SettingPrintService extends Service {

    private static final String TAG = SettingPrintService.class.getName();

    private Setting setting;

    private IWoyouService woyoService;
    private ICallback callback;
    private Intent serviceIntent;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            woyoService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            woyoService = IWoyouService.Stub.asInterface(service);

            // print setting via async task
            PrintTask printTask = new PrintTask();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                printTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "PARAM");
            } else {
                printTask.execute("PARAM");
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initWoyo();

        callback = new ICallback.Stub() {
            @Override
            public void onRunResult(final boolean success) throws RemoteException {
            }

            @Override
            public void onReturnString(final String value) throws RemoteException {
            }

            @Override
            public void onRaiseException(int code, final String msg) throws RemoteException {
                Log.i(TAG, "print error " + msg);
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        initPrefs(intent);

        // bind to woyo service
        if (woyoService == null)
            bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // unbind and stop woyo service
        unbindService(serviceConnection);
        stopService(serviceIntent);
    }

    private void initPrefs(Intent intent) {
        if (intent.hasExtra("SETTING"))
            setting = intent.getParcelableExtra("SETTING");
        else
            setting = new Setting("kirulapana", "kirulapana", "0775432015", "3432322323");
    }

    private void initWoyo() {
        // start woyo service
        serviceIntent = new Intent();
        serviceIntent.setPackage("woyou.aidlservice.jiuiv5");
        serviceIntent.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
        startService(serviceIntent);
    }

    private class PrintTask extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                printSetting(setting);
                saveSetting(setting);

                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);

            // stop service
            stopSelf();

            // broadcast status according to s
            Intent intent = new Intent(IntentProvider.ACTION_PRINT);
            intent.putExtra("PRINT_STATUS", s ? "DONE" : "FAIL");
            sendBroadcast(intent);
        }

        private void printSetting(Setting setting) throws RemoteException {
            Bitmap logo = BitmapFactory.decodeResource(getResources(), R.raw.logo_english);
            logo = BitmapUtils.zoomBitmap(logo, 197, 163);

            String address      = "SANASA Development Bank\n";
            String branch       = "Branch       : " + setting.getBranch() + "\n";
            String telephoneNo  = "Telephone No : " + setting.getTelephone() + "\n";

            // print logo
            woyoService.setAlignment(1, callback);
            woyoService.printBitmap(logo, callback);

            woyoService.lineWrap(2, callback);

            // print header
            woyoService.setAlignment(1, callback);
            woyoService.printTextWithFont(address, "", 28, callback);
            woyoService.printTextWithFont(branch, "", 24, callback);
            woyoService.printTextWithFont(telephoneNo, "", 24, callback);

            woyoService.lineWrap(4, callback);
        }

        private void saveSetting(Setting setting) {
            // save settings in SP
            PreferenceUtils.savePrinterAddress(SettingPrintService.this, setting.getPrinterAddress());
            PreferenceUtils.saveBranch(SettingPrintService.this, setting.getBranch());
            PreferenceUtils.savePhone(SettingPrintService.this, setting.getTelephone());
        }
    }
}
