package com.wasn.remote;

import android.content.Intent;
import android.util.Log;

import com.score.senzc.pojos.Senz;
import com.wasn.application.IntentProvider;
import com.wasn.utils.SenzParser;

class SenHandler {
    private static final String TAG = SenHandler.class.getName();

    private static SenHandler instance;

    static SenHandler getInstance() {
        if (instance == null) {
            instance = new SenHandler();
        }

        return instance;
    }

    void handle(String senzMsg, SenzService senzService) {
        Senz senz = SenzParser.parse(senzMsg);
        switch (senz.getSenzType()) {
            case SHARE:
                Log.d(TAG, "SHARE received");
                handleShare(senz, senzService);
                break;
            case GET:
                Log.d(TAG, "GET received");
                handleGet(senz, senzService);
                break;
            case DATA:
                Log.d(TAG, "DATA received");
                handleData(senz, senzService);
                break;
        }
    }

    private void handleShare(Senz senz, SenzService senzService) {
        // broadcast

    }

    private void handleGet(Senz senz, SenzService senzService) {
    }

    private void handleData(Senz senz, SenzService senzService) {
        // broadcast
        Intent intent = new Intent(IntentProvider.ACTION_SENZ);
        intent.putExtra("SENZ", senz);
        senzService.getApplicationContext().sendBroadcast(intent);
    }
}
