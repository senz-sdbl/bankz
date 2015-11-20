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
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
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
import com.wasn.application.MobileBankApplication;
import com.wasn.db.SenzorsDbSource;
import com.wasn.exceptions.EmptyFieldsException;
import com.wasn.exceptions.InvalidAccountException;
import com.wasn.exceptions.InvalidBalanceAmountException;
import com.wasn.pojos.BalanceQuery;
import com.wasn.pojos.Transaction;
import com.wasn.utils.ActivityUtils;
import com.wasn.utils.PreferenceUtils;
import com.wasn.utils.RSAUtils;
import com.wasn.utils.TransactionUtils;

import java.util.HashMap;

/**
 * Activity class to do new transaction
 *
 * @author erangaeb@gmail.com (eranga bandara)
 */
public class TransactionActivity extends Activity implements View.OnClickListener {

    MobileBankApplication application;
    private static final String TAG = TransactionActivity.class.getName();
    // form components
    EditText accountEditText;
    EditText amountEditText;
    EditText noteEditText;

    RelativeLayout back;
    RelativeLayout done;

    TextView headerText;

    private Typeface typeface;//*
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_layout);
        //Link to service
        //addDummyData();
        connectWithService();
        init();
        registerReceiver(senzMessageReceiver, new IntentFilter("DATA"));
    }

    private void connectWithService() {
        Intent intent = new Intent();
        intent.setClassName("com.wasn", "com.wasn.services.RemoteSenzService");
        bindService(intent, senzServiceConnection, Context.BIND_AUTO_CREATE);

        senzCountDownTimer = new SenzCountDownTimer(16000, 5000);
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
    public void init() {
        Intent i = getIntent();
        BalanceQuery balance = i.getExtras().getParcelable("balance");
        application = (MobileBankApplication) TransactionActivity.this.getApplication();

        accountEditText = (EditText) findViewById(R.id.transaction_layout_account_text);
        amountEditText = (EditText) findViewById(R.id.transaction_layout_amount_text);
        noteEditText = (EditText) findViewById(R.id.transaction_layout_note_text);

        back = (RelativeLayout) findViewById(R.id.transaction_layout_back);
        done = (RelativeLayout) findViewById(R.id.transaction_layout_help);
        //done = (RelativeLayout) findViewById(R.id.transaction_layout_done);

        // set done keyboard option with note text
        noteEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == keyEvent.ACTION_DOWN && i == keyEvent.KEYCODE_ENTER) {
                    // transaction done event
                    initTransaction();
                }

                return false;
            }
        });

        // set custom font to header text
        headerText = (TextView) findViewById(R.id.transaction_layout_header_text);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/vegur_2.otf");
        headerText.setTypeface(face);
        headerText.setTypeface(null, Typeface.BOLD);

        back.setOnClickListener(TransactionActivity.this);
        done.setOnClickListener(TransactionActivity.this);
        accountEditText.setText(balance.getClientAccount(), TextView.BufferType.NORMAL);
        accountEditText.setEnabled(false);
        accountEditText.setInputType(InputType.TYPE_NULL);

        // set custom font for transaction account no
        headerText = (TextView) findViewById(R.id.transaction_account_no);
        Typeface face1 = Typeface.createFromAsset(getAssets(), "fonts/vegur_2.otf");
        headerText.setTypeface(face1);
        headerText.setTypeface(null, Typeface.BOLD);

        // set custom font for transaction amount
        headerText = (TextView) findViewById(R.id.transaction_amount);
        Typeface face2 = Typeface.createFromAsset(getAssets(), "fonts/vegur_2.otf");
        headerText.setTypeface(face2);
        headerText.setTypeface(null, Typeface.BOLD);
    }
public void addDummyData(){
    SenzorsDbSource senzorsDbSource=new SenzorsDbSource(getApplicationContext());
    Transaction tr=new Transaction(5,"abc","159789456V","1255555","1000",100000,"120000000","deposit");
            /*
            * (int id,
                       String clientName,
                       String clientNic,
                       String clientAccountNo,
                       String previousBalance,
                       int transactionAmount,
                       String transactionTime,
                       String transactionType) {
                       */
    senzorsDbSource.createTransaction(tr);
}
    /**
     * Initialize new transaction
     */
    public void initTransaction() {
        String accountNo = accountEditText.getText().toString();
        String amount = amountEditText.getText().toString();

        try {
            // validate form fields and get corresponding client to the account
            TransactionUtils.validateFields(accountNo, amount);

            // get receipt no
            // database stored previous receipt no
            // receipt no equals to transaction id
            int transactionId = Integer.parseInt(application.getMobileBankData().getReceiptNo()) + 1;

            // get branch id
            // database stored branch id as well
            String branchId = application.getMobileBankData().getBranchId();

            // create transaction and share in application
            Transaction transaction = TransactionUtils.createTransaction(branchId, transactionId, amount);

            Intent intent = new Intent(this, TransactionDetailsActivity.class);
            intent.putExtra("transaction", transaction);
            startActivity(intent);
            TransactionActivity.this.finish();

            doTransactionoverNetwork();
        } catch (NumberFormatException e) {
            displayMessageDialog("Error", "Invalid amount, make sure amount is correct");
        } catch (EmptyFieldsException e) {
            displayMessageDialog("Error", "Empty fields, make sure not empty account and amount");
        } catch (InvalidAccountException e) {
            displayMessageDialog("Error", "Invalid account, make sure account is correct");
        } catch (InvalidBalanceAmountException e) {
            displayMessageDialog("Error", "Invalid balance amount, please recheck corresponding client details");
        }
    }


    /**
     * Display message dialog
     *
     * @param messageHeader message header
     * @param message       message to be display
     */
    public void displayMessageDialog(String messageHeader, String message) {
        final Dialog dialog = new Dialog(TransactionActivity.this);

        //set layout for dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.message_dialog_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        // set dialog texts
        TextView messageHeaderTextView = (TextView) dialog.findViewById(R.id.message_dialog_layout_message_header_text);
        TextView messageTextView = (TextView) dialog.findViewById(R.id.message_dialog_layout_message_text);
        messageHeaderTextView.setText(messageHeader);
        messageTextView.setText(message);

        // set custom font
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/vegur_2.otf");
        messageHeaderTextView.setTypeface(face);
        messageHeaderTextView.setTypeface(null, Typeface.BOLD);
        messageTextView.setTypeface(face);

        //set ok button
        Button okButton = (Button) dialog.findViewById(R.id.message_dialog_layout_yes_button);
        okButton.setTypeface(face);
        okButton.setTypeface(null, Typeface.BOLD);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }


    //Do transaction over network

    private void doTransactionoverNetwork() {
        try {
            // first create create senz
            HashMap<String, String> senzAttributes = new HashMap<>();
            senzAttributes.put("time", ((Long) (System.currentTimeMillis() / 1000)).toString());
            senzAttributes.put("pubkey", PreferenceUtils.getRsaKey(this, RSAUtils.PUBLIC_KEY));
            senzAttributes.put("testkey", "fgsfgdfg");

            // new senz
            String id = "_ID";
            String signature = "";
            SenzTypeEnum senzType = SenzTypeEnum.SHARE;
            //User sender = new User("", registeringUser.getUsername());
            User sender = new User("", "ge");
            User receiver = new User("", "mysensors");
            Senz senz = new Senz(id, signature, senzType, sender, receiver, senzAttributes);

            senzService.send(senz);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
            ActivityUtils.hideSoftKeyboard(TransactionActivity.this);
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


    /**
     * Call when click on view
     *
     * @param view
     */
    public void onClick(View view) {
        if (view == back) {
            // back to main activity
            startActivity(new Intent(TransactionActivity.this, MobileBankActivity.class));
            TransactionActivity.this.finish();
        } else if (view == done) {
            initTransaction();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        // back to main activity
        startActivity(new Intent(TransactionActivity.this, MobileBankActivity.class));
        TransactionActivity.this.finish();
    }
}
