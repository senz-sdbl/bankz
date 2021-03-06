package com.wasn.application;

import android.content.IntentFilter;
import android.util.Log;

import com.wasn.enums.IntentType;
import com.wasn.exceptions.InvalidIntentTypeException;

/**
 * This class is responsible to distribute specific or general itents.
 * Please use this wrapper to send out intents inside the app
 */
public class IntentProvider {

    private static final String TAG = IntentProvider.class.getName();

    // intent actions
    public static final String ACTION_SENZ = "com.score.bankz.SENZ";
    public static final String ACTION_TIMEOUT = "com.score.bankz.TIMEOUT";
    public static final String ACTION_RESTART = "com.score.bankz.RESTART";
    public static final String ACTION_CONNECTED = "com.score.bankz.CONNECTED";
    public static final String ACTION_PRINT = "com.score.bankz.PRINT";
    public static final String ACTION_PRINT_FAIL = "com.score.bankz.PRINT_FAIL";
    private static final String ACTION_PHONE_STATE = "android.intent.action.PHONE_STATE";

    /**
     * Return the intent filter for the intent_type.
     *
     * @param type intent type
     * @return
     */
    public static IntentFilter getIntentFilter(IntentType type) {
        try {
            return new IntentFilter(getIntentAction(type));
        } catch (InvalidIntentTypeException ex) {
            Log.e(TAG, "No such intent, " + ex);
        }

        return null;
    }

    /**
     * Intent string generator
     * Get intents from this method, to centralize where intents are generated from for easier customization in the future.
     *
     * @param intentType intent type
     * @return
     */
    private static String getIntentAction(IntentType intentType) throws InvalidIntentTypeException {
        switch (intentType) {
            case SENZ:
                return ACTION_SENZ;
            case TIMEOUT:
                return ACTION_TIMEOUT;
            case CONNECTED:
                return ACTION_CONNECTED;
            case PRINT:
                return ACTION_PRINT;
            case PHONE_STATE:
                return ACTION_PHONE_STATE;
            default:
                throw new InvalidIntentTypeException();
        }
    }

}
