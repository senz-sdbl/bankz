package com.wasn.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wasn.R;
import com.wasn.pojos.Account;

import java.util.ArrayList;

/**
 * Activity class to display transaction list
 *
 * @author erangaeb@gmail.com (eranga bandara)
 */
public class AccountListActivity extends Activity implements View.OnClickListener {

    // activity components
    Typeface typeface;
    RelativeLayout back;
    TextView headerText;

    // use to populate list
    ListView listView;
    AccountListAdapter adapter;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_list_layout);

        init();
        initList();
    }

    /**
     * Initialize layout components
     */
    public void init() {
        typeface = Typeface.createFromAsset(getAssets(), "fonts/GeosansLight.ttf");

        headerText = (TextView) findViewById(R.id.account_list_layout_header_text);
        headerText.setTypeface(typeface, Typeface.BOLD);

        back = (RelativeLayout) findViewById(R.id.account_list_layout_back);
        back.setOnClickListener(AccountListActivity.this);
    }

    private void initList() {
        // init list view
        listView = (ListView) findViewById(R.id.account_list);
        View headerView = View.inflate(this, R.layout.header, null);
        View footerView = View.inflate(this, R.layout.footer, null);
        listView.addHeaderView(headerView);
        listView.addFooterView(footerView);

        // set click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Account account = (Account) adapter.getItem(position - 1);

                // start transaction activity
                Intent intent = new Intent(AccountListActivity.this, TransactionActivity.class);
                intent.putExtra("ACCOUNT", account);
                startActivity(intent);

                AccountListActivity.this.finish();
            }
        });

        // display list
        ArrayList<Account> accounts = getIntent().getParcelableArrayListExtra("ACCOUNTS");
        adapter = new AccountListAdapter(AccountListActivity.this, accounts);
        listView.setAdapter(adapter);
    }

    /**
     * {@inheritDoc}
     */
    public void onClick(View view) {
        if (view == back) {
            // start transaction activity
            Intent intent = new Intent(AccountListActivity.this, TransactionActivity.class);
            startActivity(intent);
            this.finish();
        }
    }

}
