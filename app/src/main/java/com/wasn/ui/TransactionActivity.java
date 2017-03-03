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
import android.widget.Toast;

import com.score.senz.ISenzService;
import com.score.senzc.enums.SenzTypeEnum;
import com.score.senzc.pojos.Senz;
import com.score.senzc.pojos.User;
import com.wasn.R;
import com.wasn.application.IntentProvider;
import com.wasn.db.SenzorsDbSource;
import com.wasn.enums.IntentType;
import com.wasn.exceptions.InvalidAccountException;
import com.wasn.exceptions.InvalidInputFieldsException;
import com.wasn.pojos.BalanceQuery;
import com.wasn.pojos.Transaction;
import com.wasn.utils.ActivityUtils;
import com.wasn.utils.NetworkUtil;
import com.wasn.utils.SenzUtils;
import com.wasn.utils.TransactionUtils;

import java.util.HashMap;

/**
 * Activity class to do new transaction
 *
 * @author erangaeb@gmail.com (eranga bandara)
 */
public class TransactionActivity extends Activity implements View.OnClickListener {

    private static final String TAG = TransactionActivity.class.getName();

    // custom font
    private Typeface typeface;

    // form components
    private EditText accountEditText;
    private EditText amountEditText;

    // header
    private TextView headerText;
    private RelativeLayout back;
    private RelativeLayout done;
    private RelativeLayout search;

    // current transaction
    private Transaction transaction;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_layout);
        typeface = Typeface.createFromAsset(getAssets(), "fonts/vegur_2.otf");

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
        search = (RelativeLayout) findViewById(R.id.transaction_layout_search);

        back.setOnClickListener(TransactionActivity.this);
        done.setOnClickListener(TransactionActivity.this);
        search.setOnClickListener(TransactionActivity.this);

        // balance query receives from previous activity
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            BalanceQuery balance = intent.getExtras().getParcelable("balance");
            accountEditText.setText(balance.getClientAccount(), TextView.BufferType.NORMAL);
        }
    }

    protected void bindToService() {
        Intent intent = new Intent("com.wasn.remote.SenzService");
        intent.setPackage(this.getPackageName());
        bindService(intent, senzServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View view) {
        if (view == back) {
            // back to main activity
            TransactionActivity.this.finish();
        } else if (view == done) {
            onClickPut();
        } else if (view == search) {
            navigateBalanceQuery();
        }
    }

    private void onClickPut() {
        ActivityUtils.hideSoftKeyboard(this);

        try {
            String account = accountEditText.getText().toString().trim();
            int amount = Integer.parseInt(amountEditText.getText().toString().trim());
            ActivityUtils.isValidTransactionFields(account, amount);

            // initialize transaction
            transaction = new Transaction(1, "", "", account, "", amount, TransactionUtils.getCurrentTime(), "");

            if (NetworkUtil.isAvailableNetwork(this)) {
                displayInformationMessageDialog("Are you sure you want to do the transaction #Account " + transaction.getClientAccountNo() + " #Amount " + transaction.getTransactionAmount());
            } else {
                displayMessageDialog("#ERROR", "No network connection");
            }
        } catch (InvalidInputFieldsException | NumberFormatException e) {
            e.printStackTrace();

            displayMessageDialog("#ERROR", "Invalid account no/amount");
        } catch (InvalidAccountException e) {
            e.printStackTrace();

            displayMessageDialog("#ERROR", "Account no should be 12 character length");
        }
    }

    private void doPut() {
        // create put senz
        HashMap<String, String> senzAttributes = new HashMap<>();
        senzAttributes.put("acc", accountEditText.getText().toString().trim());
        senzAttributes.put("amnt", amountEditText.getText().toString().trim());

        Long timestamp = System.currentTimeMillis() / 1000;
        senzAttributes.put("time", timestamp.toString());
        senzAttributes.put("uid", SenzUtils.getUid(this, timestamp.toString()));

        // new senz
        String id = "_ID";
        String signature = "_SIGNATURE";
        SenzTypeEnum senzType = SenzTypeEnum.PUT;
        User receiver = new User("", "sdbltrans");

        send(new Senz(id, signature, senzType, null, receiver, senzAttributes));
    }

    public void send(Senz senz) {
        if (NetworkUtil.isAvailableNetwork(this)) {
            try {
                if (isServiceBound) {
                    senzService.send(senz);
                } else {
                    Toast.makeText(this, "Failed to connect", Toast.LENGTH_LONG).show();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "No internet", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Handle broadcast message receives
     * Need to handle registration success failure here
     *
     * @param senz senz
     */
    private void handleSenz(Senz senz) {
        if (senz.getAttributes().containsKey("status")) {
            String msg = senz.getAttributes().get("status");
            if (msg != null && msg.equalsIgnoreCase("PENDING")) {
                // pending trans
                // TODO create transaction with PENDING state
                new SenzorsDbSource(TransactionActivity.this).createTransaction(transaction);
            } else if (msg != null && msg.equalsIgnoreCase("DONE")) {
                // DONE response received
                ActivityUtils.cancelProgressDialog();
                Toast.makeText(this, "Transaction successful", Toast.LENGTH_LONG).show();

                // save transaction in db
                if (transaction != null) {
                    // TODO update transaction
                    //new SenzorsDbSource(TransactionActivity.this).createTransaction(transaction);
                }

                // navigate
                navigateTransactionDetails(transaction);
            } else {
                ActivityUtils.cancelProgressDialog();

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

    /**
     * Display message dialog when user going to logout
     *
     * @param message
     */
    public void displayInformationMessageDialog(String message) {
        final Dialog dialog = new Dialog(this);

        //set layout for dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.share_confirm_message_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        // set dialog texts
        TextView messageHeaderTextView = (TextView) dialog.findViewById(R.id.information_message_dialog_layout_message_header_text);
        TextView messageTextView = (TextView) dialog.findViewById(R.id.information_message_dialog_layout_message_text);
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
                // do transaction
                dialog.cancel();
                ActivityUtils.showProgressDialog(TransactionActivity.this, "Please wait...");

                // send data
                doPut();
            }
        });

        // cancel button
        Button cancelButton = (Button) dialog.findViewById(R.id.information_message_dialog_layout_cancel_button);
        cancelButton.setTypeface(face);
        cancelButton.setTypeface(null, Typeface.BOLD);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void navigateTransactionDetails(Transaction transaction) {
        // navigate to transaction details
        Intent intent = new Intent(TransactionActivity.this, TransactionDetailsActivity.class);
        intent.putExtra("transaction", transaction);
        intent.putExtra("ACTIVITY_NAME", TransactionActivity.class.getName());
        startActivity(intent);

        TransactionActivity.this.finish();
    }

    private void navigateBalanceQuery() {
        // navigate to transaction details
        Intent intent = new Intent(TransactionActivity.this, BalanceQueryActivity.class);
        startActivity(intent);

        TransactionActivity.this.finish();
    }

}
