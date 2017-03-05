package com.wasn.ui;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wasn.R;
import com.wasn.async.TestPrintService;
import com.wasn.exceptions.BluetoothNotAvailableException;
import com.wasn.exceptions.BluetoothNotEnableException;
import com.wasn.exceptions.EmptyBranchNameException;
import com.wasn.exceptions.EmptyPrinterAddressException;
import com.wasn.exceptions.InvalidTelephoneNoException;
import com.wasn.exceptions.NoUserException;
import com.wasn.pojos.Settings;
import com.wasn.utils.ActivityUtils;
import com.wasn.utils.PreferenceUtils;
import com.wasn.utils.PrintUtils;
import com.wasn.utils.SettingsUtils;


/**
 * Activity class correspond to settings
 *
 * @author erangaeb@gmail.com (eranga bandara)
 */
public class SettingsActivity extends Activity implements View.OnClickListener {

    // activity components
    Typeface typeface;
    RelativeLayout back;
    RelativeLayout done;
    TextView headerText;
    TextView agentLabelText;
    TextView branchLabelText;
    TextView telephoneLabelText;
    TextView printerLabelText;
    EditText agentNameEditText;
    EditText branchEditText;
    EditText telephoneEditText;
    EditText printerAddressEditText;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        init();
    }

    /**
     * Initialize activity components and valuesdi
     */
    public void init() {
        typeface = Typeface.createFromAsset(getAssets(), "fonts/GeosansLight.ttf");

        back = (RelativeLayout) findViewById(R.id.settings_layout_back);
        done = (RelativeLayout) findViewById(R.id.settings_layout_test_print);
        back.setOnClickListener(SettingsActivity.this);
        done.setOnClickListener(SettingsActivity.this);

        headerText = (TextView) findViewById(R.id.settings_layout_header_text);
        agentLabelText = (TextView) findViewById(R.id.settings_agent_username);
        branchLabelText = (TextView) findViewById(R.id.settings_branch_label);
        telephoneLabelText = (TextView) findViewById(R.id.settings_phone_label);
        printerLabelText = (TextView) findViewById(R.id.settings_printer_bluetooth_address);

        agentNameEditText = (EditText) findViewById(R.id.settings_layout_username_text);
        branchEditText = (EditText) findViewById(R.id.settings_branch_text);
        telephoneEditText = (EditText) findViewById(R.id.settings_phone_text);
        printerAddressEditText = (EditText) findViewById(R.id.settings_layout_printer_address_text);

        headerText.setTypeface(typeface, Typeface.BOLD);
        agentLabelText.setTypeface(typeface);
        branchLabelText.setTypeface(typeface);
        telephoneLabelText.setTypeface(typeface);
        printerLabelText.setTypeface(typeface);

        agentNameEditText.setTypeface(typeface, Typeface.BOLD);
        branchEditText.setTypeface(typeface, Typeface.BOLD);
        telephoneEditText.setTypeface(typeface, Typeface.BOLD);
        printerAddressEditText.setTypeface(typeface, Typeface.BOLD);

        // set text to username
        // stored in database
        try {
            agentNameEditText.setText(PreferenceUtils.getUser(this).getUsername());
        } catch (NoUserException e) {
            e.printStackTrace();
        }

        // set text to inputs
        branchEditText.setText(PreferenceUtils.getBranch(this));
        telephoneEditText.setText(PreferenceUtils.getPhone(this));
        printerAddressEditText.setText(PreferenceUtils.getPrinterAddress(this));
    }

    private void onClickDone() {
        String printerAddress = printerAddressEditText.getText().toString().trim();
        String branch = branchEditText.getText().toString().trim();
        String telephone = telephoneEditText.getText().toString().trim();

        try {
            SettingsUtils.validatePrinterAddress(printerAddress);
            SettingsUtils.validateBranchName(branch);
            SettingsUtils.validateTelephoneNo(telephone);

            showPasswordInputDialog();
        } catch (EmptyPrinterAddressException e) {
            Toast.makeText(SettingsActivity.this, "Empty printer address", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (EmptyBranchNameException e) {
            Toast.makeText(SettingsActivity.this, "Empty branch name", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (InvalidTelephoneNoException e) {
            Toast.makeText(SettingsActivity.this, "Invalid telephone no", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    /**
     * Print test print
     */
    private void print() {
        String branch = branchEditText.getText().toString().trim();
        String telephone = telephoneEditText.getText().toString().trim();
        String printerAddress = printerAddressEditText.getText().toString().trim();
        Settings settings = new Settings("", branch, telephone, printerAddress);
        try {
            if (PrintUtils.isEnableBluetooth()) {
                ActivityUtils.showProgressDialog(this, "Printing...");
                TestPrintService testPrintService = new TestPrintService(SettingsActivity.this, settings);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    testPrintService.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, printerAddress);
                } else {
                    testPrintService.execute(printerAddress);
                }
            }
        } catch (BluetoothNotEnableException e) {
            displayMessageDialog("ERROR", "Bluetooth not enabled");
            e.printStackTrace();
        } catch (BluetoothNotAvailableException e) {
            displayMessageDialog("ERROR", "Bluetooth not available");
            e.printStackTrace();
        }
    }

    /**
     * Display message dialog
     *
     * @param messageHeader message header
     * @param message       message to be display
     */
    public void displayMessageDialog(String messageHeader, String message) {
        final Dialog dialog = new Dialog(SettingsActivity.this);

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
        okButton.setTypeface(typeface);
        okButton.setTypeface(null, Typeface.BOLD);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    protected void showPasswordInputDialog() {
        final Dialog dialog = new Dialog(this);

        //set layout for dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.input_password_dialog_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        TextView messageHeaderTextView = (TextView) dialog.findViewById(R.id.information_message_dialog_layout_message_header_text);
        final EditText passwordText = (EditText) dialog.findViewById(R.id.settings_password);

        final Button btnOk = (Button) dialog.findViewById(R.id.information_message_dialog_layout_ok_button);
        btnOk.setTypeface(typeface, Typeface.BOLD);

        btnOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String password = passwordText.getText().toString();
                if (password.equals("admin")) {
                    ActivityUtils.hideSoftKeyboard(SettingsActivity.this);
                    dialog.cancel();

                    // start to print
                    print();
                }
            }
        });

        final Button btnCancel = (Button) dialog.findViewById(R.id.information_message_dialog_layout_cancel_button);
        btnCancel.setTypeface(typeface, Typeface.BOLD);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        messageHeaderTextView.setTypeface(typeface, Typeface.BOLD);
        passwordText.setTypeface(typeface, Typeface.BOLD);

        dialog.show();
    }

    /**
     * Call after printing test print
     *
     * @param status print status
     */
    public void onPostPrint(String status) {
        ActivityUtils.cancelProgressDialog();

        if (status.equals("1")) {
            // valid print address
            displayMessageDialog("Done", "Settings successfully saved");
        } else if (status.equals("-2")) {
            // bluetooth not enable
            displayMessageDialog("ERROR", "Bluetooth not enabled");
        } else if (status.equals("-3")) {
            // bluetooth not available
            displayMessageDialog("ERROR", "Bluetooth not available");
        } else if (status.equals("-5")) {
            // invalid bluetooth address
            displayMessageDialog("ERROR", "Invalid printer address, Please make sure printer address is correct");
        } else {
            // cannot print
            // may be invalid printer address
            displayMessageDialog("ERROR", "Printer address might be incorrect, Please make sure printer address is correct and printer switched ON");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void onClick(View view) {
        if (view == back) {
            // back to main activity
            SettingsActivity.this.finish();
        } else if (view == done) {
            onClickDone();
        }
    }

}
