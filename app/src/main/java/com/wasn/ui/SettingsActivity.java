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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wasn.R;
import com.wasn.async.TestPrintService;
import com.wasn.exceptions.BluetoothNotAvailableException;
import com.wasn.exceptions.BluetoothNotEnableException;
import com.wasn.exceptions.EmptyPrinterAddressException;
import com.wasn.exceptions.NoUserException;
import com.wasn.exceptions.UnTestedPrinterAddressException;
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

    // keep track with weather tested printer address
    // testing via printing a test print
    boolean isTestedPrintAddress;

    // activity components
    Typeface typeface;
    RelativeLayout back;
    RelativeLayout save;
    RelativeLayout testPrint;
    TextView headerText;
    TextView agentLabelText;
    TextView printerLabelText;
    EditText agentNameEditText;
    EditText printerAddressEditText;
    EditText telephoneNoEditText;

    // display when printing
    public ProgressDialog progressDialog;

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

        isTestedPrintAddress = false;

        back = (RelativeLayout) findViewById(R.id.settings_layout_back);
        //save = (RelativeLayout) findViewById(R.id.settings_layout_help);
        //save = (RelativeLayout) findViewById(R.id.settings_layout_save);
        testPrint = (RelativeLayout) findViewById(R.id.settings_layout_test_print);

        // set custom font to header text
        headerText = (TextView) findViewById(R.id.settings_layout_header_text);
        headerText.setTypeface(typeface, Typeface.BOLD);

        agentLabelText = (TextView) findViewById(R.id.settings_agent_username);
        agentLabelText.setTypeface(typeface);

        printerLabelText = (TextView) findViewById(R.id.settings_printer_bluetooth_address);
        printerLabelText.setTypeface(typeface);

        // set text to username
        // stored in database
        agentNameEditText = (EditText) findViewById(R.id.settings_layout_username_text);
        agentNameEditText.setTypeface(typeface, Typeface.BOLD);
        try {
            agentNameEditText.setText(PreferenceUtils.getUser(this).getUsername());
        } catch (NoUserException e) {
            e.printStackTrace();
        }

        // set text to printer address
        // printer address stored in database
        printerAddressEditText = (EditText) findViewById(R.id.settings_layout_printer_address_text);
        printerAddressEditText.setText(PreferenceUtils.getPrinterAddress(this));
        printerAddressEditText.setTypeface(typeface, Typeface.BOLD);

        back.setOnClickListener(SettingsActivity.this);
        testPrint.setOnClickListener(SettingsActivity.this);
    }

    /**
     * Action of save button
     */
    public void save() {
        // get form fields
        String printerAddress = printerAddressEditText.getText().toString();
        try {
            // validate settings fields
            SettingsUtils.validatePrinterAddress(printerAddress, PreferenceUtils.getPrinterAddress(this), isTestedPrintAddress);

            // save settings attributes in database
            // need admin privileges
            //displayAdminLoginDialog(settings);
        } catch (UnTestedPrinterAddressException e) {
            // untested printer address
            // need to print test print and validate the printer address
            displayMessageDialog("Warning", "Print a test print and make sure valid printer address");
            e.printStackTrace();
        } catch (EmptyPrinterAddressException e) {
            displayMessageDialog("Error", "Empty printer address, make sure not empty printer address");
            e.printStackTrace();
        }
    }

    /**
     * Print test print
     */
    public void print() {
        String printerAddress = printerAddressEditText.getText().toString();

        try {
            SettingsUtils.validatePrinterAddress(printerAddress, PreferenceUtils.getPrinterAddress(this), isTestedPrintAddress);

            // its already tested address
            // but need to print test print
            if (PrintUtils.isEnableBluetooth()) {
                // start background thread to print test print
                progressDialog = ProgressDialog.show(SettingsActivity.this, "", "Printing test print, Please wait ...");
                new TestPrintService(SettingsActivity.this).execute(printerAddress);
            }
        } catch (BluetoothNotEnableException e) {
            displayToast("Bluetooth not enabled");
            e.printStackTrace();
        } catch (BluetoothNotAvailableException e) {
            displayToast("Bluetooth not available");
            e.printStackTrace();
        } catch (UnTestedPrinterAddressException e) {
            // need to print here
            // since its untested address
            try {
                if (PrintUtils.isEnableBluetooth()) {
                    // start background thread to print test print
                    progressDialog = ProgressDialog.show(SettingsActivity.this, "", "Printing test print, Please wait ...");
                    TestPrintService testPrintService = new TestPrintService(SettingsActivity.this);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        testPrintService.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, printerAddress);
                    } else {
                        testPrintService.execute(printerAddress);
                    }
                }
            } catch (BluetoothNotEnableException e1) {
                displayToast("Bluetooth not enabled");
                e.printStackTrace();
            } catch (BluetoothNotAvailableException e1) {
                displayToast("Bluetooth not available");
                e.printStackTrace();
            }
            e.printStackTrace();
        } catch (EmptyPrinterAddressException e) {
            displayToast("Empty printer address");
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

    // password popup....
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
     * Display toast message
     *
     * @param message message tobe display
     */
    public void displayToast(String message) {
        Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_LONG).show();
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
     * Call after printing test print
     *
     * @param status print status
     */
    public void onPostPrint(String status) {
        closeProgressDialog();

        if (status.equals("1")) {
            // valid print address
            displayMessageDialog("Done", "Printer details successfully saved");
            isTestedPrintAddress = true;

            // save printer address
            String printerAddress = printerAddressEditText.getText().toString().trim();
            PreferenceUtils.savePrinterAddress(this, printerAddress);
        } else if (status.equals("-2")) {
            // bluetooth not enable
            displayToast("Bluetooth not enabled");
        } else if (status.equals("-3")) {
            // bluetooth not available
            displayToast("Bluetooth not available");
        } else if (status.equals("-5")) {
            // invalid bluetooth address
            displayMessageDialog("Error", "Invalid printer address, Please make sure printer address is correct");
        } else {
            // cannot print
            // may be invalid printer address
            displayMessageDialog("Cannot print", "Printer address might be incorrect, Please make sure printer address is correct and printer switched ON");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void onClick(View view) {
        if (view == back) {
            // back to main activity
            SettingsActivity.this.finish();
        } else if (view == save) {
            save();
        } else if (view == testPrint) {
            showPasswordInputDialog();
        }
    }

}
