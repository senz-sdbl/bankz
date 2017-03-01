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
import com.wasn.application.IntentProvider;
import com.wasn.enums.IntentType;
import com.wasn.pojos.BalanceQuery;
import com.wasn.utils.ActivityUtils;
import com.wasn.utils.NetworkUtil;

import java.util.HashMap;

public class BalanceQueryActivity extends Activity implements View.OnClickListener {

    private static final String TAG = BalanceQueryActivity.class.getName();

    // form components
    private EditText accountEditText;

    // header
    private RelativeLayout back;
    private RelativeLayout done;
    private TextView headerText;

    // custom font
    private Typeface typeface;

    // service interface
    private ISenzService senzService;
    private boolean isServiceBound;

    // service connection
    protected ServiceConnection senzServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG, "Connected with senz service");
            senzService = ISenzService.Stub.asInterface(service);
            isServiceBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.d(TAG, "Disconnected from senz service");
            senzService = null;
            isServiceBound = false;
        }
    };

    // senz received
    private BroadcastReceiver senzReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("SENZ")) {
                Senz senz = intent.getExtras().getParcelable("SENZ");
                switch (senz.getSenzType()) {
                    case DATA:
                        handleSenz(senz);
                        break;
                    case SHARE:
                        break;
                    default:
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance_query_layout);

        initUi();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "Bind to senz service");
        bindToService();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // unbind from service
        if (isServiceBound) {
            Log.d(TAG, "Unbind to senz service");
            unbindService(senzServiceConnection);

            isServiceBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // bind to senz service
        registerReceiver(senzReceiver, IntentProvider.getIntentFilter(IntentType.SENZ));
    }

    @Override
    public void onPause() {
        super.onPause();

        unregisterReceiver(senzReceiver);
    }

    public void initUi() {
        typeface = Typeface.createFromAsset(getAssets(), "fonts/vegur_2.otf");

        // init text/edit text fields
        accountEditText = (EditText) findViewById(R.id.balance_query_layout_account_text);
        headerText = (TextView) findViewById(R.id.balance_query_layout_header_text);

        // set custom font
        accountEditText.setTypeface(typeface, Typeface.BOLD);
        headerText.setTypeface(typeface, Typeface.BOLD);

        back = (RelativeLayout) findViewById(R.id.balance_query_layout_back);
        done = (RelativeLayout) findViewById(R.id.balance_query_layout_done);

        back.setOnClickListener(BalanceQueryActivity.this);
        done.setOnClickListener(BalanceQueryActivity.this);
    }

    protected void bindToService() {
        Intent intent = new Intent("com.wasn.remote.SenzService");
        intent.setPackage(this.getPackageName());
        bindService(intent, senzServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(View view) {
        if (view == back) {
            startActivity(new Intent(BalanceQueryActivity.this, BankzActivity.class));
            BalanceQueryActivity.this.finish();
        } else if (view == done) {
            // TODO remote this[temporary solution]
            Intent intent = new Intent(BalanceQueryActivity.this, BalanceResultActivity.class);
            BalanceQuery balance = new BalanceQuery(accountEditText.getText().toString(), "Name", "0000000v", "15,000");
            intent.putExtra("balance", balance);
            startActivity(intent);
            BalanceQueryActivity.this.finish();

            // TODO use this
            //onClickGet();
        }
    }

    private void onClickGet() {
        // send get
        try {
            HashMap<String, String> senzAttributes = new HashMap<>();
            senzAttributes.put("acc", "3444");
            senzAttributes.put("time", ((Long) (System.currentTimeMillis() / 1000)).toString());

            // new senz
            String id = "_ID";
            String signature = "_SIGNATURE";
            SenzTypeEnum senzType = SenzTypeEnum.PUT;
            User receiver = new User("", "sdblbal");
            Senz senz = new Senz(id, signature, senzType, null, receiver, senzAttributes);

            senzService.send(senz);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle broadcast message receives
     * Need to handle registration success failure here
     *
     * @param senz senz
     */
    private void handleSenz(Senz senz) {
        if (senz.getAttributes().containsKey("msg")) {
            // msg response received
            ActivityUtils.cancelProgressDialog();

            String msg = senz.getAttributes().get("msg");
            if (msg != null && msg.equalsIgnoreCase("GETDONE")) {
                Toast.makeText(this, "Balance query successful", Toast.LENGTH_LONG).show();

                // TODO navigate
            } else {
                String informationMessage = "Failed to complete the transaction";
                displayMessageDialog("PUT fail", informationMessage);
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

    @Override
    public void onBackPressed() {
        // back to main activity
        startActivity(new Intent(BalanceQueryActivity.this, BankzActivity.class));
        BalanceQueryActivity.this.finish();
    }
}
