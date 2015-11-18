package com.wasn.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wasn.R;
import com.wasn.application.MobileBankApplication;
import com.wasn.pojos.Transaction;
import com.wasn.services.backgroundservices.TransactionSyncService;
import com.wasn.utils.NetworkUtil;

import java.util.ArrayList;

/**
 * Activity class to display transaction list
 *
 * @author erangaeb@gmail.com (eranga bandara)
 */
public class TransactionListActivity extends Activity implements View.OnClickListener {

    MobileBankApplication application;

    // activity components
    RelativeLayout back;
    RelativeLayout help;
    LinearLayout bottomPannel;
    RelativeLayout done;
    TextView headerText;
    TextView doneText;

    // use to populate list
    ListView transactionListView;
    TransactionListAdapter adapter;
    ViewStub emptyView;

    ArrayList<Transaction> allTransactionList;

    // display when syncing
    public ProgressDialog progressDialog;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_list_layout);

        init();
    }

    /**
     * Initialize layout components
     */
    public void init() {
        application = (MobileBankApplication) TransactionListActivity.this.getApplication();

        // initialize
        back = (RelativeLayout) findViewById(R.id.transaction_list_layout_back);
        help = (RelativeLayout) findViewById(R.id.transaction_list_layout_help);
        bottomPannel = (LinearLayout) findViewById(R.id.transaction_list_layout_bottom_pannel);
        done = (RelativeLayout) findViewById(R.id.transaction_list_layout_done);
        headerText = (TextView) findViewById(R.id.transaction_list_layout_header_text);
        doneText = (TextView) findViewById(R.id.transaction_list_layout_done_text);

        // set custom font to header text
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/vegur_2.otf");
        headerText.setTypeface(face);
        headerText.setTypeface(null, Typeface.BOLD);

        // set click listeners
        back.setOnClickListener(TransactionListActivity.this);
        help.setOnClickListener(TransactionListActivity.this);
        done.setOnClickListener(TransactionListActivity.this);

        // populate list view
        transactionListView = (ListView) findViewById(R.id.transaction_list);
        emptyView = (ViewStub) findViewById(R.id.transaction_list_layout_empty_view);

        allTransactionList = application.getTransactionList();

        // add header and footer
        View headerView = View.inflate(this, R.layout.header, null);
        View footerView = View.inflate(this, R.layout.footer, null);
        transactionListView.addHeaderView(headerView);
        transactionListView.addFooterView(footerView);

        //set long press listener
        transactionListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                // get corresponding transaction and share in application
                Transaction transaction = (Transaction) adapter.getItem(i - 1);
                application.setTransaction(transaction);

                // start new activity
                Intent intent = new Intent(TransactionListActivity.this, TransactionDetailsActivity.class);
                intent.putExtra("ACTIVITY_NAME", TransactionListActivity.class.getName());
                startActivity(intent);

                return true;
            }
        });

        displayAllTransactionList();
    }

    /**
     * Display all transaction list
     */
    public void displayAllTransactionList() {
        allTransactionList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            allTransactionList.add(new Transaction(1, "test", "eranga", "34534", "sfsd", "334", "345", "345", "345", "4345", "34", "wer", "3453", "test", "werew"));
        }

        if (allTransactionList.size() > 0) {
            // have transaction
            adapter = new TransactionListAdapter(TransactionListActivity.this, allTransactionList);
            transactionListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            enableBottomPannel();
        } else {
            disableBottomPannel();
            displayEmptyView();
        }
    }

    /**
     * Display empty view when no clients
     */
    public void displayEmptyView() {
        adapter = new TransactionListAdapter(TransactionListActivity.this, new ArrayList<Transaction>());
        transactionListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        transactionListView.setEmptyView(emptyView);
    }

    /**
     * disable bottom pannel
     */
    public void disableBottomPannel() {
        bottomPannel.setVisibility(View.GONE);
    }

    /**
     * display bottom pannel
     */
    public void enableBottomPannel() {
        bottomPannel.setVisibility(View.VISIBLE);
    }

    /**
     * Change text of done button
     *
     * @param text
     */
    public void changeDoneButtonText(String text) {
        doneText.setText(text);
    }

    /**
     * Sync transactions to bank server
     */
    public void syncTransaction() {
        // sync, if only available network connection
        if (NetworkUtil.isAvailableNetwork(TransactionListActivity.this)) {
            // start background thread to sync
            progressDialog = ProgressDialog.show(TransactionListActivity.this, "", "Syncing transactions, please wait...");
            new TransactionSyncService(TransactionListActivity.this).execute("SYNC");
        } else {
            displayToast("No network connection");
        }
    }

    /**
     * Execute after sync transactions
     *
     * @param status sync status
     */
    public void onPostSync(int status) {
        closeProgressDialog();

        // display toast according to sync status
        if (status > 0) {
            // sync success
            // no un synced transaction now
            allTransactionList = application.getMobileBankData().getAllTransactions(application.getMobileBankData().getBranchId());
            application.setTransactionList(allTransactionList);
            displayEmptyView();

            displayToast("Synced " + status + "transactions ");
        } else if (status == -1) {
            displayToast("Sync fail, error in synced record");
        } else if (status == -2) {
            displayToast("Sync fail, server response error");
        } else if (status == -3) {
            displayToast("Server response error");
        } else {
            displayToast("Sync fail");
        }
    }

    /**
     * Close progress dialog
     */
    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * Display message dialog when user going to logout
     *
     * @param message
     */
    public void displayInformationMessageDialog(String message) {
        final Dialog dialog = new Dialog(TransactionListActivity.this);

        //set layout for dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.information_message_dialog_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
                dialog.cancel();
                syncTransaction();
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

    /**
     * Display toast message
     *
     * @param message message tobe display
     */
    public void displayToast(String message) {
        Toast.makeText(TransactionListActivity.this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * {@inheritDoc}
     */
    public void onClick(View view) {
        if (view == back) {
            // back to main activity
            startActivity(new Intent(TransactionListActivity.this, MobileBankActivity.class));
            TransactionListActivity.this.finish();
            application.resetFields();
        } else if (view == done) {
            // display summary activity
            startActivity(new Intent(TransactionListActivity.this, SummaryDetailsActivity.class));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        // back to main activity
        startActivity(new Intent(TransactionListActivity.this, MobileBankActivity.class));
        TransactionListActivity.this.finish();
        application.resetFields();
    }
}
