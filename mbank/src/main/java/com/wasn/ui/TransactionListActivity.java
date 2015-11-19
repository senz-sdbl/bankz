package com.wasn.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wasn.R;
import com.wasn.application.MobileBankApplication;
import com.wasn.pojos.Transaction;

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
            allTransactionList.add(new Transaction(1, "test", "eranga", "34", "wer", "3453", "test", "werew"));
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
     * Close progress dialog
     */
    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void onClick(View view) {
        if (view == back) {
            // back to main activity
            startActivity(new Intent(TransactionListActivity.this, MobileBankActivity.class));
            TransactionListActivity.this.finish();
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
    }
}
