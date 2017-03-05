package com.wasn.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Bundle;
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

import com.score.senz.ISenzService;
import com.score.senzc.enums.SenzTypeEnum;
import com.score.senzc.pojos.Senz;
import com.score.senzc.pojos.User;
import com.wasn.R;
import com.wasn.application.IntentProvider;
import com.wasn.enums.IntentType;
import com.wasn.utils.ActivityUtils;

import java.util.HashMap;

public class AccountInquiryActivity extends Activity implements View.OnClickListener {

    private static final String TAG = AccountInquiryActivity.class.getName();

    // ui
    private Typeface typeface;
    private TextView idLabel;
    private EditText idEditText;
    private RelativeLayout back;
    private RelativeLayout done;
    private TextView headerText;

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
        typeface = Typeface.createFromAsset(getAssets(), "fonts/GeosansLight.ttf");

        // init text/edit text fields
        idEditText = (EditText) findViewById(R.id.balance_query_layout_nic_text);
        idLabel = (TextView) findViewById(R.id.balance_query_nic_label);
        headerText = (TextView) findViewById(R.id.balance_query_layout_header_text);

        // set custom font
        idEditText.setTypeface(typeface, Typeface.BOLD);
        idLabel.setTypeface(typeface, Typeface.BOLD);
        headerText.setTypeface(typeface, Typeface.BOLD);

        back = (RelativeLayout) findViewById(R.id.balance_query_layout_back);
        done = (RelativeLayout) findViewById(R.id.balance_query_layout_done);

        back.setOnClickListener(AccountInquiryActivity.this);
        done.setOnClickListener(AccountInquiryActivity.this);
    }

    protected void bindToService() {
        Intent intent = new Intent("com.wasn.remote.SenzService");
        intent.setPackage(this.getPackageName());
        bindService(intent, senzServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(View view) {
        if (view == back) {
            startActivity(new Intent(AccountInquiryActivity.this, BankzActivity.class));
            AccountInquiryActivity.this.finish();
        } else if (view == done) {
            // TODO remote this[temporary solution]
//            Intent intent = new Intent(BalanceQueryActivity.this, BalanceResultActivity.class);
//            BalanceQuery balance = new BalanceQuery(accountEditText.getText().toString(), "Name", "0000000v", "15,000");
//            intent.putExtra("balance", balance);
//            startActivity(intent);
//            BalanceQueryActivity.this.finish();

            onClickGet();
        }
    }

    private void onClickGet() {
        String nic = idEditText.getText().toString().trim();

        if (!nic.isEmpty()) {
            doGet(nic);
        } else {

        }
    }

    private void doGet(String nic) {
        // send get
        try {
            HashMap<String, String> senzAttributes = new HashMap<>();
            senzAttributes.put("nic", nic);
            senzAttributes.put("acc", "");
            senzAttributes.put("time", ((Long) (System.currentTimeMillis() / 1000)).toString());

            // new senz
            String id = "_ID";
            String signature = "_SIGNATURE";
            SenzTypeEnum senzType = SenzTypeEnum.GET;
            User receiver = new User("", "sdblinq");
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
        if (senz.getAttributes().containsKey("acc")) {
            // msg response received
            ActivityUtils.cancelProgressDialog();

            // send acc with | separated
            String msg = senz.getAttributes().get("acc");
            String[] accs = msg.split("|");

            if (accs.length > 0) {
                // TODO display account list activity
            } else {
                displayMessageDialog("Error", "No accounts for given NIC");
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
        messageHeaderTextView.setTypeface(typeface, Typeface.BOLD);
        messageTextView.setTypeface(typeface, Typeface.BOLD);

        //set ok button
        Button okButton = (Button) dialog.findViewById(R.id.information_message_dialog_layout_ok_button);
        okButton.setTypeface(typeface, Typeface.BOLD);
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
        startActivity(new Intent(AccountInquiryActivity.this, BankzActivity.class));
        AccountInquiryActivity.this.finish();
    }
}
