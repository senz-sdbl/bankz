package com.wasn.ui;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.score.senz.ISenzService;
import com.score.senzc.enums.SenzTypeEnum;
import com.score.senzc.pojos.Senz;
import com.score.senzc.pojos.User;
import com.wasn.R;
import com.wasn.application.MobileBankApplication;
import com.wasn.utils.ActivityUtils;
import com.wasn.utils.PreferenceUtils;
import com.wasn.utils.RSAUtils;
import com.wasn.pojos.BalanceQuery;

import java.util.HashMap;

/**
 * Created by root on 11/18/15.
 */
public class BalanceQueryActivity extends Activity implements View.OnClickListener {
    MobileBankApplication application;
    RelativeLayout back;
    RelativeLayout done;
    TextView LineText;

    private static final String TAG = BalanceQueryActivity.class.getName();

    private EditText accountEditText;
    // use to track registration timeout
    private SenzCountDownTimer senzCountDownTimer;
    private boolean isResponseReceived;

    // service interface
    private ISenzService senzService = null;
    private boolean isServiceBound = false;

    // service connection
    private ServiceConnection senzServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d("TAG", "Connected with senz service");
            isServiceBound = true;
            senzService = ISenzService.Stub.asInterface(service);

            isResponseReceived = false;
            senzCountDownTimer.start();
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.d("TAG", "Disconnected from senz service");

            senzService = null;
            isServiceBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance_query_layout);
        connectWithService();
        init();
        registerReceiver(senzMessageReceiver, new IntentFilter("DATA"));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(senzServiceConnection);
        unregisterReceiver(senzMessageReceiver);
    }

    private void connectWithService() {
        Intent intent = new Intent();
        intent.setClassName("com.wasn", "com.wasn.services.RemoteSenzService");
        bindService(intent, senzServiceConnection, Context.BIND_AUTO_CREATE);

        senzCountDownTimer = new SenzCountDownTimer(16000, 5000);
    }


    public void init() {
        application = (MobileBankApplication) BalanceQueryActivity.this.getApplication();
        back = (RelativeLayout) findViewById(R.id.balance_query_layout_back);
        done = (RelativeLayout) findViewById(R.id.balance_query_layout_get_balance);

        back.setOnClickListener(BalanceQueryActivity.this);
        done.setOnClickListener(BalanceQueryActivity.this);
        accountEditText = (EditText) findViewById(R.id.balance_query_layout_account_text);

        // set custom font for header text
        LineText = (TextView) findViewById(R.id.balance_query_account_no);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/vegur_2.otf");
        LineText.setTypeface(face);
        LineText.setTypeface(null, Typeface.BOLD);

    }

    private void doQueryOverNetwork() {
        try {

            // first create create senz
            HashMap<String, String> senzAttributes = new HashMap<>();
            senzAttributes.put("time", ((Long) (System.currentTimeMillis() / 1000)).toString());
            senzAttributes.put("pubkey", PreferenceUtils.getRsaKey(this, RSAUtils.PUBLIC_KEY));
            senzAttributes.put("testkey", "fgsfgdfg");

            // new senz
            String id = "_ID";
            String signature = "";
            SenzTypeEnum senzType = SenzTypeEnum.GET;
            User sender = new User("", "registeringUser.getUsername()");
            User receiver = new User("", "mysensors");
            Senz senz = new Senz(id, signature, senzType, sender, receiver, senzAttributes);

            senzService.send(senz);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == back) {
            startActivity(new Intent(BalanceQueryActivity.this, MobileBankActivity.class));
            BalanceQueryActivity.this.finish();
        } else if (view == done) {
            //preSendToNetwork(); ToDo have to remove this
            Intent i = new Intent(BalanceQueryActivity.this, BalanceResultActivity.class);
            BalanceQuery balance = new BalanceQuery(accountEditText.getText().toString(), "Name", "0000000v", "15,000");
            i.putExtra("balance", balance);
            startActivity(i);
            preSendToNetwork();

            
            
        }
    }

    public void preSendToNetwork() {//input validations are done here
        String accno = accountEditText.getText().toString();
        doQueryOverNetwork();
    }

    @Override
    public void onBackPressed() {
        // back to main activity
        startActivity(new Intent(BalanceQueryActivity.this, MobileBankActivity.class));
        BalanceQueryActivity.this.finish();
    }

    private class SenzCountDownTimer extends CountDownTimer {

        public SenzCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // if response not received yet, resend share
            if (!isResponseReceived) {
                //doRegistration();
                Log.d(TAG, "Response not received yet");
            }
        }

        @Override
        public void onFinish() {
            ActivityUtils.hideSoftKeyboard(BalanceQueryActivity.this);
            ActivityUtils.cancelProgressDialog();

            // display message dialog that we couldn't reach the user
            if (!isResponseReceived) {
                String message = "<font color=#000000>Seems we couldn't reach the senz service at this moment</font>";
                // displayInformationMessageDialog("#Registration Fail", message);
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

    private void handleMessage(Intent intent) {
        String action = intent.getAction();

        if (action.equals("DATA")) {

            //startActivity(new Intent(TransactionActivity.this, TransactionDetailsActivity.class));//ToDo Fix to appropriate place
            //TransactionActivity.this.finish();

            Senz senz = intent.getExtras().getParcelable("SENZ");
           /* startActivity(new Intent(BalanceQueryActivity.this, BalanceResultActivity.class));
            BalanceQueryActivity.this.finish();
            */
           /* if (senz.getAttributes().containsKey("msg")) {
                // msg response received
                ActivityUtils.cancelProgressDialog();
                isResponseReceived = true;
                senzCountDownTimer.cancel();

                String msg = senz.getAttributes().get("msg");
                if (msg != null && msg.equalsIgnoreCase("UserCreated")) {
                    Toast.makeText(this, "Successfully registered", Toast.LENGTH_LONG).show();

                    // save user
                    // navigate home
                    PreferenceUtils.saveUser(getApplicationContext(), registeringUser);
                    //navigateToHome();
                } else {
                    String informationMessage = "<font color=#4a4a4a>Seems username </font> <font color=#eada00>" + "<b>" + registeringUser.getUsername() + "</b>" + "</font> <font color=#4a4a4a> already obtained by some other user, try SenZ with different username</font>";
                    //displayInformationMessageDialog("Registration fail", informationMessage);
                }
            }*/
        }
    }



}
