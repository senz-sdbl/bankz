package com.wasn.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.wasn.R;
import com.wasn.utils.BitmapUtils;

import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

public class PrintService extends Service {

    private static final String TAG = PrintService.class.getName();

    private IWoyouService woyouService;
    private ICallback callback = null;

    private ServiceConnection connService = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            woyouService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            woyouService = IWoyouService.Stub.asInterface(service);
            printTest();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        callback = new ICallback.Stub() {
            @Override
            public void onRunResult(final boolean success) throws RemoteException {
            }

            @Override
            public void onReturnString(final String value) throws RemoteException {
            }

            @Override
            public void onRaiseException(int code, final String msg) throws RemoteException {
                Log.i(TAG, "onRaiseException: " + msg);
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // start service and bind
        Intent serviceIntent = new Intent();
        serviceIntent.setPackage("woyou.aidlservice.jiuiv5");
        serviceIntent.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
        startService(serviceIntent);
        bindService(serviceIntent, connService, Context.BIND_AUTO_CREATE);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // stop service
        Intent serviceIntent = new Intent();
        serviceIntent.setPackage("woyou.aidlservice.jiuiv5");
        serviceIntent.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
        stopService(serviceIntent);
    }

    private void printTest() {
        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap logo;
                    logo = BitmapFactory.decodeResource(getResources(), R.raw.logo_english);
                    logo = BitmapUtils.zoomBitmap(logo, 197, 163);

                    //alignment 0--align left , 1--align center, 2--align right
                    woyouService.setAlignment(1, null);
                    woyouService.printBitmap(logo, null);

                    woyouService.lineWrap(1, null);

                    woyouService.setAlignment(1, null);
                    woyouService.printTextWithFont("SANASA Development Bank\n", "", 28, null);
                    woyouService.printTextWithFont("Colombo - 02\n", "", 24, null);
                    woyouService.printTextWithFont("Tel: 011 2393759\n", "", 24, null);

                    woyouService.lineWrap(4, null);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        // stop service
        stopSelf();
    }

}
