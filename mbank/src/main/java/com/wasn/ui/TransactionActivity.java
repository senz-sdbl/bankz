package com.wasn.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.score.senz.ISenzService;
import com.score.senzc.enums.SenzTypeEnum;
import com.score.senzc.pojos.Senz;
import com.score.senzc.pojos.User;
import com.wasn.R;
import com.wasn.exceptions.InvalidInputFieldsException;
import com.wasn.pojos.BalanceQuery;
import com.wasn.utils.ActivityUtils;
import com.wasn.utils.NetworkUtil;

import java.util.HashMap;

/**
 * Activity class to do new transaction
 *
 * @author erangaeb@gmail.com (eranga bandara)
 */
public class TransactionActivity extends Activity implements View.OnClickListener {

    private static final String TAG = TransactionActivity.class.getName();

    // form components
    private EditText accountEditText;
    private EditText amountEditText;

    // header
    private RelativeLayout back;
    private RelativeLayout done;
    private TextView headerText;

    // custom font
    private Typeface typeface;

    // use to track registration timeout
    private SenzCountDownTimer senzCountDownTimer;
    private boolean isResponseReceived;

    // service interface
    private ISenzService senzService;
    private boolean isServiceBound;

    // service connection
    private ServiceConnection senzServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d("TAG", "Connected with senz service");
            isServiceBound = true;
            senzService = ISenzService.Stub.asInterface(service);
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.d("TAG", "Disconnected from senz service");

            senzService = null;
            isServiceBound = false;
        }
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_layout);
        typeface = Typeface.createFromAsset(getAssets(), "fonts/vegur_2.otf");

        initUi();

        // service
        senzService = null;
        isServiceBound = false;

        // register broadcast receiver
        registerReceiver(senzMessageReceiver, new IntentFilter("com.wasn.bankz.DATA_SENZ"));

        // bind with senz service
        // bind to service from here as well
        if (!isServiceBound) {
            Intent intent = new Intent();
            intent.setClassName("com.wasn", "com.wasn.services.RemoteSenzService");
            bindService(intent, senzServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(senzServiceConnection);
        unregisterReceiver(senzMessageReceiver);
    }

    /**
     * Initialize form components and values
     */
    public void initUi() {
        // init text/edit text fields
        accountEditText = (EditText) findViewById(R.id.transaction_layout_account_text);
        amountEditText = (EditText) findViewById(R.id.transaction_layout_amount_text);
        headerText = (TextView) findViewById(R.id.transaction_layout_header_text);

        // set custom font
        accountEditText.setTypeface(typeface, Typeface.BOLD);
        amountEditText.setTypeface(typeface, Typeface.BOLD);
        headerText.setTypeface(typeface, Typeface.BOLD);

        back = (RelativeLayout) findViewById(R.id.transaction_layout_back);
        done = (RelativeLayout) findViewById(R.id.transaction_layout_done);

        back.setOnClickListener(TransactionActivity.this);
        done.setOnClickListener(TransactionActivity.this);

        // balance query receives from previous activity
        Intent intent = getIntent();
        BalanceQuery balance = intent.getExtras().getParcelable("balance");
        accountEditText.setText(balance.getClientAccount(), TextView.BufferType.NORMAL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View view) {
        if (view == back) {
            // back to main activity
            startActivity(new Intent(TransactionActivity.this, BankzActivity.class));
            TransactionActivity.this.finish();
        } else if (view == done) {
            onClickPut();
        }
    }

    private void onClickPut() {
        ActivityUtils.hideSoftKeyboard(this);

        try {
            String account = accountEditText.getText().toString().trim();
            int amount = Integer.parseInt(amountEditText.getText().toString().trim());
            ActivityUtils.isValidTransactionFields(account, amount);

            if (NetworkUtil.isAvailableNetwork(this)) {
                ActivityUtils.showProgressDialog(TransactionActivity.this, "Please wait...");

                // start new timer
                isResponseReceived = false;
                senzCountDownTimer = new SenzCountDownTimer(16000, 5000, getPutSenz());
                senzCountDownTimer.start();
            } else {
                Toast.makeText(this, "No network connection available", Toast.LENGTH_LONG).show();
            }
        } catch (InvalidInputFieldsException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private Senz getPutSenz() {
        HashMap<String, String> senzAttributes = new HashMap<>();
        senzAttributes.put("acc", accountEditText.getText().toString().trim());
        senzAttributes.put("amnt", amountEditText.getText().toString().trim());
        senzAttributes.put("time", ((Long) (System.currentTimeMillis() / 1000)).toString());

        // new senz
        String id = "_ID";
        String signature = "_SIGNATURE";
        SenzTypeEnum senzType = SenzTypeEnum.PUT;
        User receiver = new User("", "sdbltrans");

        return new Senz(id, signature, senzType, null, receiver, senzAttributes);
    }

    private void doPut(Senz senz) {
        try {
            senzService.send(senz);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Keep track with share response timeout
     */
    private class SenzCountDownTimer extends CountDownTimer {

        // timer deals with only one senz
        private Senz senz;

        public SenzCountDownTimer(long millisInFuture, long countDownInterval, final Senz senz) {
            super(millisInFuture, countDownInterval);

            this.senz = senz;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // if response not received yet, resend share
            if (!isResponseReceived) {
                doPut(senz);
                Log.d(TAG, "Response not received yet");
            }
        }

        @Override
        public void onFinish() {
            ActivityUtils.hideSoftKeyboard(TransactionActivity.this);
            ActivityUtils.cancelProgressDialog();

            // display message dialog that we couldn't reach the user
            if (!isResponseReceived) {
                String message = "Seems we couldn't complete the transaction at this moment";
                displayMessageDialog("#PUT Fail", message);
            }
        }
    }

    private BroadcastReceiver senzMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Got message from Senz service");
            handleMessage(intent);
        }
    };

    /**
     * Handle broadcast message receives
     * Need to handle registration success failure here
     *
     * @param intent intent
     */
    private void handleMessage(Intent intent) {
        String action = intent.getAction();

        if (action.equals("com.wasn.bankz.DATA_SENZ")) {
            Senz senz = intent.getExtras().getParcelable("SENZ");

            if (senz.getAttributes().containsKey("msg")) {
                // msg response received
                ActivityUtils.cancelProgressDialog();
                isResponseReceived = true;
                senzCountDownTimer.cancel();

                String msg = senz.getAttributes().get("msg");
                if (msg != null && msg.equalsIgnoreCase("PUTDONE")) {
                    Toast.makeText(this, "Transaction successful", Toast.LENGTH_LONG).show();

                    // TODO save transaction in db
                    // TODO navigate

                } else {
                    String informationMessage = "Failed to complete the transaction";
                    displayMessageDialog("PUT fail", informationMessage);
                }
            }
        }
    }

    /**
     * Display message dialog
     *
     * @param messageHeader message header
     * @param message       message to be display
     */
    public void displayMessageDialog(String messageHeader, String message) {
        final Dialog dialog = new Dialog(this);

        //set layout for dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.information_message_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        // set dialog texts
        TextView messageHeaderTextView = (TextView) dialog.findViewById(R.id.information_message_dialog_layout_message_header_text);
        TextView messageTextView = (TextView) dialog.findViewById(R.id.information_message_dialog_layout_message_text);
        messageHeaderTextView.setText(messageHeader);
        messageTextView.setText(message);

        // set custom font
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/vegur_2.otf");
        messageHeaderTextView.setTypeface(face);
        messageHeaderTextView.setTypeface(null, Typeface.BOLD);
        messageTextView.setTypeface(face);

        //set ok button
        Button okButton = (Button) dialog.findViewById(R.id.information_message_dialog_layout_ok_button);
        okButton.setTypeface(face);
        okButton.setTypeface(null, Typeface.BOLD);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        // back to main activity
        startActivity(new Intent(TransactionActivity.this, BankzActivity.class));
        TransactionActivity.this.finish();
    }
}
