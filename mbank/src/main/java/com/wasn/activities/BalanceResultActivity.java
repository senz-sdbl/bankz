package com.wasn.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.wasn.R;
import com.wasn.application.MobileBankApplication;
import com.wasn.pojos.Attribute;

import java.util.ArrayList;

/**
 * Created by root on 11/18/15.
 */
public class BalanceResultActivity extends Activity implements View.OnClickListener {
    MobileBankApplication application;

    ListView balanceList;
    ArrayList<Attribute> attributesList;
    AttributeListAdapter adapter;
    RelativeLayout back;

    //to populate list


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance_query_result_layout);
        init();
    }

    public void init() {

        back = (RelativeLayout) findViewById(R.id.balance_query_result_layout_back);
        back.setOnClickListener(BalanceResultActivity.this);

        //Dummy data added
        attributesList = new ArrayList<>();
        attributesList.add(new Attribute("Acc No: ", "123"));
        attributesList.add(new Attribute("Customer: ", "FirstName_LastName"));
        attributesList.add(new Attribute("NIC: ", "000000000v"));
        attributesList.add(new Attribute("Balance: ", "15,000.00"));

        balanceList = (ListView) findViewById(R.id.balance_result_list);
        adapter = new AttributeListAdapter(BalanceResultActivity.this, attributesList);
        balanceList.setAdapter(adapter);

    }

    @Override
    public void onClick(View view) {
        if (view == back) {
            //go back to BalanceQueryActivity
            startActivity(new Intent(BalanceResultActivity.this, BalanceQueryActivity.class));
            BalanceResultActivity.this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        // back to ClientListActivity
        startActivity(new Intent(BalanceResultActivity.this, BalanceQueryActivity.class));
        BalanceResultActivity.this.finish();
    }

}
