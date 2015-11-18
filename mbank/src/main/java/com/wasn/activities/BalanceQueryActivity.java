package com.wasn.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.wasn.R;
import com.wasn.application.MobileBankApplication;

/**
 * Created by root on 11/18/15.
 */
public class BalanceQueryActivity extends Activity implements View.OnClickListener {
    MobileBankApplication application;
    RelativeLayout back;
    RelativeLayout done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance_query_layout);
        init();
    }

    public void init() {
        application = (MobileBankApplication) BalanceQueryActivity.this.getApplication();
        back = (RelativeLayout) findViewById(R.id.balance_query_layout_back);
        done = (RelativeLayout) findViewById(R.id.balance_query_layout_get_balance);

        back.setOnClickListener(BalanceQueryActivity.this);
        done.setOnClickListener(BalanceQueryActivity.this);

    }

    @Override
    public void onClick(View view) {
        if (view == back) {
            startActivity(new Intent(BalanceQueryActivity.this, MobileBankActivity.class));
            BalanceQueryActivity.this.finish();
        } else if (view == done) {
            startActivity(new Intent(BalanceQueryActivity.this, BalanceResultActivity.class));
            BalanceQueryActivity.this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        // back to main activity
        startActivity(new Intent(BalanceQueryActivity.this, MobileBankActivity.class));
        BalanceQueryActivity.this.finish();
    }
}
