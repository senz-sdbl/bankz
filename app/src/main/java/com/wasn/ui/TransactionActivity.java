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
import android.text.Html;
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
import com.wasn.db.BankzDbSource;
import com.wasn.enums.IntentType;
import com.wasn.exceptions.AmountExceedLimitException;
import com.wasn.exceptions.InvalidAccountException;
import com.wasn.exceptions.InvalidAmountException;
import com.wasn.exceptions.InvalidTelephoneNoException;
import com.wasn.pojos.Account;
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
    private TextView accountLabel;
    private TextView amountLabel;
    private TextView mobileLabel;
    private EditText accountEditText;
    private EditText amountEditText;
    private EditText mobileEditText;

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
        typeface = Typeface.createFromAsset(getAssets(), "fonts/GeosansLight.ttf");

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
        headerText = (TextView) findViewById(R.id.transaction_layout_header_text);
        accountLabel = (TextView) findViewById(R.id.transaction_account_no_label);
        amountLabel = (TextView) findViewById(R.id.transaction_amount_label);
        mobileLabel = (TextView) findViewById(R.id.transaction_mobile_label);
        accountEditText = (EditText) findViewById(R.id.transaction_layout_account_text);
        amountEditText = (EditText) findViewById(R.id.transaction_layout_amount_text);
        mobileEditText = (EditText) findViewById(R.id.transaction_layout_mobile_text);

        // set custom font
        headerText.setTypeface(typeface, Typeface.BOLD);
        accountLabel.setTypeface(typeface);
        amountLabel.setTypeface(typeface);
        mobileLabel.setTypeface(typeface);
        accountEditText.setTypeface(typeface, Typeface.BOLD);
        amountEditText.setTypeface(typeface, Typeface.BOLD);
        mobileEditText.setTypeface(typeface, Typeface.BOLD);

        back = (RelativeLayout) findViewById(R.id.transaction_layout_back);
        done = (RelativeLayout) findViewById(R.id.transaction_layout_done);
        search = (RelativeLayout) findViewById(R.id.transaction_layout_search);

        back.setOnClickListener(TransactionActivity.this);
        done.setOnClickListener(TransactionActivity.this);
        search.setOnClickListener(TransactionActivity.this);

        // account receives from previous activity
        Intent intent = getIntent();
        if (intent.hasExtra("ACCOUNT")) {
            Account account = intent.getExtras().getParcelable("ACCOUNT");
            accountEditText.setText(account.getAccNo(), TextView.BufferType.NORMAL);
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

        String account = accountEditText.getText().toString().trim();
        String mobile = mobileEditText.getText().toString().trim();
        String amount = amountEditText.getText().toString().trim();

        if (transaction == null || !TransactionUtils.isNewTransaction(transaction.getClientAccountNo(), account)) {
            // new transaction
            try {
                ActivityUtils.isValidTransactionFields(account, mobile, amount);

                // initialize transaction
                Long timestamp = (System.currentTimeMillis() / 1000);
                String uid = SenzUtils.getUid(this, timestamp.toString());
                transaction = new Transaction(1,
                        uid,
                        (getIntent().hasExtra("ACCOUNT") ? ((Account) getIntent().getExtras().getParcelable("ACCOUNT")).getName() : ""),
                        TransactionUtils.getTransactionAccount(account),
                        "",
                        TransactionUtils.getTransactionMobile(mobile),
                        Integer.parseInt(amount),
                        "",
                        TransactionUtils.getTransactionTime(timestamp),
                        "");

                String message = "<font size=10 color=#636363>Are you sure you want to do the deposit for account</font> <font size=12 color=#00a1e4>" + "<b>" + transaction.getClientAccountNo() + "</b>" + "</font> <font> with amount</font> <font size=12 color=#00a1e4>" + "<b>" + TransactionUtils.formatAmount(transaction.getTransactionAmount()) + "</b>" + "</font> ";
                showConfirmMessage("CONFIRM", message);
            } catch (InvalidAccountException e) {
                e.printStackTrace();

                showStatusMessage("ERROR", "Account no should be 5-12 character length");
            } catch (InvalidTelephoneNoException e) {
                e.printStackTrace();

                showStatusMessage("ERROR", "Invalid mobile no");
            } catch (InvalidAmountException e) {
                e.printStackTrace();

                showStatusMessage("ERROR", "Invalid amount");
            } catch (AmountExceedLimitException e) {
                e.printStackTrace();

                showStatusMessage("ERROR", "Amount exceed the limit 50000");
            }
        } else {
            // ask to resubmit deposit
            String message = "<font size=10 color=#636363>Are you sure you want to resubmit the deposit</font>";
            showConfirmMessage("RETRY?", message);
        }
    }

    private void doPut() {
        // create put senz
        HashMap<String, String> senzAttributes = new HashMap<>();
        senzAttributes.put("acc", transaction.getClientAccountNo());
        senzAttributes.put("amnt", Integer.toString(transaction.getTransactionAmount()));
        if (!transaction.getClientMobile().isEmpty())
            senzAttributes.put("mob", transaction.getClientMobile());
        senzAttributes.put("uid", transaction.getUid());

        // new senz
        String id = "_ID";
        String signature = "_SIGNATURE";
        SenzTypeEnum senzType = SenzTypeEnum.PUT;
        User receiver = new User("", "sdbltrans");

        if (NetworkUtil.isAvailableNetwork(this)) {
            ActivityUtils.showProgressDialog(TransactionActivity.this, "Please wait...");
            send(new Senz(id, signature, senzType, null, receiver, senzAttributes));
        } else {
            showStatusMessage("ERROR", "No network connection");
        }
    }

    public void send(Senz senz) {
        try {
            if (isServiceBound) {
                senzService.send(senz);
            } else {
                showStatusMessage("ERROR", "Fail to connect");
            }
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
        if (senz.getAttributes().containsKey("status")) {
            String msg = senz.getAttributes().get("status");
            if (msg != null && msg.equalsIgnoreCase("INIT")) {
                // init trans
                // TODO create transaction with INIT state
                // new BankzDbSource(TransactionActivity.this).createTransaction(transaction);
            } else if (msg != null && msg.equalsIgnoreCase("PENDING")) {
                // pending trans
                ActivityUtils.cancelProgressDialog();
                showStatusMessage("WAITING", "Waiting to complete the deposit");
            } else if (msg != null && msg.equalsIgnoreCase("DONE")) {
                // DONE response received
                ActivityUtils.cancelProgressDialog();
                Toast.makeText(this, "Transaction successful", Toast.LENGTH_LONG).show();

                // save transaction in db
                new BankzDbSource(TransactionActivity.this).createTransaction(transaction);

                navigateTransactionDetails(transaction);
            } else {
                // ERROR response received
                ActivityUtils.cancelProgressDialog();
                showStatusMessage("ERROR", "Fail to complete the deposit, please make sure account no is correct");
            }
        }
    }

    /**
     * Display message dialog
     *
     * @param messageHeader message header
     * @param message       message to be display
     */
    public void showStatusMessage(String messageHeader, String message) {
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

    /**
     * Display confirm message dialog
     *
     * @param message
     */
    public void showConfirmMessage(String title, String message) {
        final Dialog dialog = new Dialog(this);

        //set layout for dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.confirm_message_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        TextView messageHeaderTextView = (TextView) dialog.findViewById(R.id.information_message_dialog_layout_message_header_text);
        TextView messageTextView = (TextView) dialog.findViewById(R.id.information_message_dialog_layout_message_text);

        // set texts
        messageHeaderTextView.setText(title);
        messageTextView.setText(Html.fromHtml(message));

        // set custom font
        messageHeaderTextView.setTypeface(typeface, Typeface.BOLD);
        messageTextView.setTypeface(typeface, Typeface.BOLD);

        //set ok button
        Button okButton = (Button) dialog.findViewById(R.id.information_message_dialog_layout_ok_button);
        okButton.setTypeface(typeface, Typeface.BOLD);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // do transaction
                dialog.cancel();

                // send data
                doPut();
                // save transaction in db
                //new BankzDbSource(TransactionActivity.this).createTransaction(transaction);

                //navigateTransactionDetails(transaction);
            }
        });

        // cancel button
        Button cancelButton = (Button) dialog.findViewById(R.id.information_message_dialog_layout_cancel_button);
        cancelButton.setTypeface(typeface, Typeface.BOLD);
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
        Intent intent = new Intent(TransactionActivity.this, AccountInquiryActivity.class);
        startActivity(intent);

        TransactionActivity.this.finish();
    }

}
