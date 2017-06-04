package com.wasn.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wasn.R;
import com.wasn.application.IntentProvider;
import com.wasn.enums.IntentType;
import com.wasn.exceptions.BluetoothNotAvailableException;
import com.wasn.exceptions.BluetoothNotEnableException;
import com.wasn.listeners.PrintListener;
import com.wasn.pojos.Attribute;
import com.wasn.pojos.Transaction;
import com.wasn.service.TransPrintService;
import com.wasn.utils.ActivityUtils;
import com.wasn.utils.PrintUtils;
import com.wasn.utils.TransactionUtils;

import java.util.ArrayList;

/**
 * Activity class to display transaction details
 *
 * @author erangaeb@gmail.com (eranga bandara)
 */
public class TransactionDetailsActivity extends Activity implements View.OnClickListener, PrintListener {

    // use to populate list
    ListView transactionDetailsListView;
    ArrayList<Attribute> attributesList;
    AttributeListAdapter adapter;

    // form components
    Typeface typeface;
    RelativeLayout back;
    RelativeLayout help;
    RelativeLayout print;
    TextView headerText;

    private Transaction transaction;

    private BroadcastReceiver printReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("PRINT_STATUS")) {
                String printStatus = intent.getExtras().getString("PRINT_STATUS");
                onPostPrint(printStatus);
            }
        }
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_details_list_layout);

        init();
        initList();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // bind to senz service
        registerReceiver(printReceiver, IntentProvider.getIntentFilter(IntentType.PRINT));
    }

    @Override
    public void onPause() {
        super.onPause();

        unregisterReceiver(printReceiver);
    }

    /**
     * Initialize activity components
     */
    public void init() {
        typeface = Typeface.createFromAsset(getAssets(), "fonts/GeosansLight.ttf");
        transactionDetailsListView = (ListView) findViewById(R.id.transaction_details_list);
        back = (RelativeLayout) findViewById(R.id.transaction_details_layout_back);
        help = (RelativeLayout) findViewById(R.id.transaction_details_layout_help);
        print = (RelativeLayout) findViewById(R.id.transaction_details_layout_print);

        // set custom font for header text
        headerText = (TextView) findViewById(R.id.transaction_details_list_layout_header_text);
        headerText.setTypeface(typeface, Typeface.BOLD);

        back.setOnClickListener(TransactionDetailsActivity.this);
        help.setOnClickListener(TransactionDetailsActivity.this);
        print.setOnClickListener(TransactionDetailsActivity.this);
    }

    private void initList() {
        this.transaction = getIntent().getParcelableExtra("transaction");

        // populate list only have transaction
        if (transaction != null) {
            attributesList = new ArrayList<>();
            attributesList.add(new Attribute("Account No", transaction.getClientAccountNo()));
            attributesList.add(new Attribute("Name", transaction.getClientName()));
            attributesList.add(new Attribute("Mobile", transaction.getClientMobile()));
            attributesList.add(new Attribute("Amount", TransactionUtils.formatAmount(transaction.getTransactionAmount())));
            attributesList.add(new Attribute("Time", transaction.getTransactionTime()));
            attributesList.add(new Attribute("Reference", transaction.getUid().substring(6, transaction.getUid().length() - 3)));

            // add header and footer
            View headerView = View.inflate(this, R.layout.header, null);
            View footerView = View.inflate(this, R.layout.footer, null);
            transactionDetailsListView.addHeaderView(headerView);
            transactionDetailsListView.addFooterView(footerView);

            adapter = new AttributeListAdapter(TransactionDetailsActivity.this, attributesList);
            transactionDetailsListView.setAdapter(adapter);
        }
    }

    /**
     * Display message dialog when user going to logout
     *
     * @param message
     */
    public void displayInformationMessageDialog(String message) {
        final Dialog dialog = new Dialog(TransactionDetailsActivity.this);

        //set layout for dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.confirm_message_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        // set dialog texts
        TextView messageHeaderTextView = (TextView) dialog.findViewById(R.id.information_message_dialog_layout_message_header_text);
        TextView messageTextView = (TextView) dialog.findViewById(R.id.information_message_dialog_layout_message_text);
        messageTextView.setText(message);

        // set custom font
        messageHeaderTextView.setTypeface(typeface, Typeface.BOLD);
        messageTextView.setTypeface(typeface, Typeface.BOLD);

        //set ok button
        Button okButton = (Button) dialog.findViewById(R.id.information_message_dialog_layout_ok_button);
        okButton.setTypeface(typeface, Typeface.BOLD);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // printing event need to handle according to previous activity
                dialog.cancel();
                // print and save transaction in database
                // print two receipts
                try {
                    if (PrintUtils.isEnableBluetooth()) {
                        ActivityUtils.showProgressDialog(TransactionDetailsActivity.this, "Printing...");

                        // start service to test print
                        Intent intent = new Intent(TransactionDetailsActivity.this, TransPrintService.class);
                        intent.putExtra("TRANSACTION", transaction);
                        startService(intent);
                    }
                } catch (BluetoothNotEnableException e) {
                    Toast.makeText(TransactionDetailsActivity.this, "Bluetooth not enabled", Toast.LENGTH_LONG).show();
                } catch (BluetoothNotAvailableException e) {
                    Toast.makeText(TransactionDetailsActivity.this, "Bluetooth not available", Toast.LENGTH_LONG).show();
                }
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
        messageTextView.setTypeface(typeface);

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
     * Execute after printing task
     *
     * @param status print status
     */
    public void onPostPrint(String status) {
        ActivityUtils.cancelProgressDialog();

        if (status.equals("DONE")) {
            // clear shared objects
            Toast.makeText(TransactionDetailsActivity.this, "Successfully printed receipt", Toast.LENGTH_LONG).show();

            // need to go back to transaction activity
            TransactionDetailsActivity.this.finish();
        } else if (status.equalsIgnoreCase("FAIL")) {
            Toast.makeText(TransactionDetailsActivity.this, "Fail to print receipt", Toast.LENGTH_LONG).show();
        } else if (status.equals("0")) {
            Toast.makeText(TransactionDetailsActivity.this, "Cannot print receipt", Toast.LENGTH_LONG).show();
        } else if (status.equals("-2")) {
            Toast.makeText(TransactionDetailsActivity.this, "Bluetooth not enabled", Toast.LENGTH_LONG).show();
        } else if (status.equals("-3")) {
            Toast.makeText(TransactionDetailsActivity.this, "Bluetooth not available", Toast.LENGTH_LONG).show();
        } else if (status.equals("-5")) {
            // invalid bluetooth address
            displayMessageDialog("Error", "Invalid printer address, Please make sure correct printer address in Settings");
        } else {
            // cannot print
            // may be invalid printer address
            displayMessageDialog("Cannot print", "Printer address might be incorrect, Please make sure correct printer address in Settings and printer switched ON");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void onClick(View view) {
        if (view == back) {
            TransactionDetailsActivity.this.finish();
        } else if (view == print) {
            displayInformationMessageDialog("Do you wnt to print the receipt? make sure bluetooth is ON");
        }
    }

}
