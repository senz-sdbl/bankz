package com.wasn.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
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
import com.wasn.async.SummaryPrintService;
import com.wasn.exceptions.BluetoothNotAvailableException;
import com.wasn.exceptions.BluetoothNotEnableException;
import com.wasn.pojos.Attribute;
import com.wasn.pojos.Summary;
import com.wasn.utils.PrintUtils;
import com.wasn.utils.TransactionUtils;

import java.util.ArrayList;

/**
 * Activity class to display summary
 *
 * @author erangaeb@gmail.com (eranga bandara)
 */
public class SummaryDetailsActivity extends Activity implements View.OnClickListener {

    // use to populate list
    ListView summaryDetailsListView;
    ArrayList<Attribute> attributesList;
    AttributeListAdapter adapter;

    // form components
    Typeface typeface;
    RelativeLayout back;
    RelativeLayout help;
    RelativeLayout print;
    TextView headerText;

    // display when printing
    public ProgressDialog progressDialog;

    private Summary summary;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary_details_list_layout);

        initUi();
        intSummaryList();
    }

    /**
     * Initialize activity components
     */
    public void initUi() {
        typeface = Typeface.createFromAsset(getAssets(), "fonts/GeosansLight.ttf");

        back = (RelativeLayout) findViewById(R.id.summary_details_layout_back);
        help = (RelativeLayout) findViewById(R.id.summary_details_layout_help);
        print = (RelativeLayout) findViewById(R.id.summary_details_layout_print);

        // set custom font for header text
        headerText = (TextView) findViewById(R.id.summary_details_list_layout_header_text);
        headerText.setTypeface(typeface, Typeface.BOLD);

        back.setOnClickListener(SummaryDetailsActivity.this);
        help.setOnClickListener(SummaryDetailsActivity.this);
        print.setOnClickListener(SummaryDetailsActivity.this);

        summaryDetailsListView = (ListView) findViewById(R.id.summary_details_list);
    }

    private void intSummaryList() {
        summary = TransactionUtils.getSummary(this);

        if (summary != null) {
            attributesList = new ArrayList<>();
            attributesList.add(new Attribute("Date", summary.getTime()));
            attributesList.add(new Attribute("Agent", summary.getBranchId()));
            attributesList.add(new Attribute("Transaction Count", summary.getTransactionCount()));
            attributesList.add(new Attribute("Total Amount", summary.getTotalTransactionAmount()));

            // add header and footer
            View headerView = View.inflate(this, R.layout.header, null);
            View footerView = View.inflate(this, R.layout.footer, null);
            summaryDetailsListView.addHeaderView(headerView);
            summaryDetailsListView.addFooterView(footerView);

            adapter = new AttributeListAdapter(SummaryDetailsActivity.this, attributesList);
            summaryDetailsListView.setAdapter(adapter);
        }
    }

    /**
     * Display message dialog when user going to logout
     *
     * @param message
     */
    public void displayInformationMessageDialog(String message) {
        final Dialog dialog = new Dialog(SummaryDetailsActivity.this);

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
        messageHeaderTextView.setTypeface(typeface, Typeface.BOLD);
        messageTextView.setTypeface(typeface, Typeface.BOLD);

        //set ok button
        Button okButton = (Button) dialog.findViewById(R.id.information_message_dialog_layout_ok_button);
        okButton.setTypeface(typeface, Typeface.BOLD);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();

                // print summary
                try {
                    if (PrintUtils.isEnableBluetooth()) {
                        progressDialog = ProgressDialog.show(SummaryDetailsActivity.this, "", "Printing summary, Please wait ...");
                        SummaryPrintService service = new SummaryPrintService(SummaryDetailsActivity.this, summary);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            service.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "SUMMARY");
                        } else {
                            service.execute("SUMMARY");
                        }
                    }
                } catch (BluetoothNotEnableException e) {
                    Toast.makeText(SummaryDetailsActivity.this, "Bluetooth not enabled", Toast.LENGTH_LONG).show();
                } catch (BluetoothNotAvailableException e) {
                    Toast.makeText(SummaryDetailsActivity.this, "Bluetooth not available", Toast.LENGTH_LONG).show();
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
        final Dialog dialog = new Dialog(SummaryDetailsActivity.this);

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
     * Close progress dialog
     */
    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * Execute after printing task
     *
     * @param status print status
     */
    public void onPostPrint(String status) {
        // close progress dialog
        closeProgressDialog();

        if (status.equals("1")) {
            Toast.makeText(SummaryDetailsActivity.this, "Summary printed", Toast.LENGTH_LONG).show();

            // back to transaction list
            SummaryDetailsActivity.this.finish();
        } else if (status.equals("0")) {
            Toast.makeText(SummaryDetailsActivity.this, "Cannot print receipt", Toast.LENGTH_LONG).show();
        } else if (status.equals("-2")) {
            Toast.makeText(SummaryDetailsActivity.this, "Bluetooth not enabled", Toast.LENGTH_LONG).show();
        } else if (status.equals("-3")) {
            Toast.makeText(SummaryDetailsActivity.this, "Bluetooth not available", Toast.LENGTH_LONG).show();
        } else if (status.equals("-5")) {
            // invalid bluetooth address
            displayMessageDialog("Error", "Invalid printer address, Please make sure correct printer address in Settings");
        } else {
            // cannot print
            // may be invalid printer address
            displayMessageDialog("Error", "Printer address might be incorrect, Please make sure correct printer address in Settings and printer switched ON");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void onClick(View view) {
        if (view == back) {
            SummaryDetailsActivity.this.finish();
        } else if (view == help) {

        } else if (view == print) {
            displayInformationMessageDialog("Do you want to print the summary? After printing summary transaction history will be cleared. Make sure bluetooth is ON");
        }
    }

}
