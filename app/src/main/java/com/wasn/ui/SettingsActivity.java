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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wasn.R;
import com.wasn.application.IntentProvider;
import com.wasn.enums.IntentType;
import com.wasn.exceptions.BluetoothNotAvailableException;
import com.wasn.exceptions.BluetoothNotEnableException;
import com.wasn.exceptions.EmptyBranchNameException;
import com.wasn.exceptions.InvalidTelephoneNoException;
import com.wasn.exceptions.NoUserException;
import com.wasn.pojos.Setting;
import com.wasn.service.SettingPrintService;
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
        setContentView(R.layout.settings_layout);

        init();
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
        String branch = branchEditText.getText().toString().trim();
        String telephone = telephoneEditText.getText().toString().trim();

        try {
            SettingsUtils.validateBranchName(branch);
            SettingsUtils.validateTelephoneNo(telephone);

            showPasswordInputDialog();
        } catch (EmptyBranchNameException e) {
            e.printStackTrace();
            displayMessageDialog("ERROR", "Invalid branch name");
        } catch (InvalidTelephoneNoException e) {
            e.printStackTrace();
            displayMessageDialog("ERROR", "Invalid telephone no");
        }
    }

    /**
     * Print test print
     */
    private void print() {
        String branch = branchEditText.getText().toString().trim();
        String telephone = telephoneEditText.getText().toString().trim();
        String printerAddress = printerAddressEditText.getText().toString().trim();
        Setting setting = new Setting("", branch, telephone, printerAddress);
        try {
            if (PrintUtils.isEnableBluetooth()) {
                ActivityUtils.showProgressDialog(this, "Printing...");

                // start service to test print
                Intent intent = new Intent(this, SettingPrintService.class);
                intent.putExtra("SETTING", setting);
                startService(intent);
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

        if (status.equalsIgnoreCase("DONE")) {
            displayMessageDialog("Done", "Settings successfully saved");
        } else {
            displayMessageDialog("Error", "Error while printing...");
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
