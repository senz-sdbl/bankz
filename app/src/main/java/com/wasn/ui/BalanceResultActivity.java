package com.wasn.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.wasn.R;
import com.wasn.pojos.Attribute;
import com.wasn.pojos.BalanceQuery;

import java.util.ArrayList;

/**
 * Created by root on 11/18/15.
 */
public class BalanceResultActivity extends Activity implements View.OnClickListener {
    ListView balanceList;
    ArrayList<Attribute> attributesList;
    AttributeListAdapter adapter;
    RelativeLayout back;
    RelativeLayout done;
    BalanceQuery balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance_query_result_layout);

        balance = getIntent().getExtras().getParcelable("balance");
        init();
    }

    public void init() {

        back = (RelativeLayout) findViewById(R.id.balance_query_result_layout_back);
        done = (RelativeLayout) findViewById(R.id.balance_query_result_layout_done);
        back.setOnClickListener(BalanceResultActivity.this);
        done.setOnClickListener(BalanceResultActivity.this);

        //Dummy data added
        attributesList = new ArrayList<>();
        attributesList.add(new Attribute("Acc No: ", balance.getClientAccount()));
        attributesList.add(new Attribute("Customer: ", balance.getClientName()));
        attributesList.add(new Attribute("NIC: ", balance.getClientNic()));
        attributesList.add(new Attribute("Balance: ", balance.getBalance()));

        // list
        balanceList = (ListView) findViewById(R.id.balance_result_list);
        View headerView = View.inflate(this, R.layout.header, null);
        View footerView = View.inflate(this, R.layout.footer, null);

        balanceList.addHeaderView(headerView);
        balanceList.addFooterView(footerView);
        adapter = new AttributeListAdapter(BalanceResultActivity.this, attributesList);
        balanceList.setAdapter(adapter);

    }

    @Override
    public void onClick(View view) {
        if (view == back) {
            //go back to BalanceQueryActivity
            startActivity(new Intent(BalanceResultActivity.this, AccountInquiryActivity.class));
            BalanceResultActivity.this.finish();
        }

        if (view == done) {
            //go to TransactionActivity
            Intent j = new Intent(BalanceResultActivity.this, TransactionActivity.class);
            j.putExtra("balance", balance);
            startActivity(j);
            BalanceResultActivity.this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        // back to ClientListActivity
        startActivity(new Intent(BalanceResultActivity.this, AccountInquiryActivity.class));
        BalanceResultActivity.this.finish();
    }

}
