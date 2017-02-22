package com.wasn.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wasn.remote.SenzService;

/**
 * This receiver will be call
 * 1. on SenzService destroy
 * 2. on device boot
 * We have to start SenzService again from here
 */
public class BootCompleteReceiver extends BroadcastReceiver {

    private static final String TAG = BootCompleteReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Starting senz service");
        context.startService(new Intent(context, SenzService.class));
    }
}
