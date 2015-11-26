package com.wasn.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.wasn.R;
import com.wasn.exceptions.BluetoothNotAvailableException;
import com.wasn.exceptions.BluetoothNotEnableException;
import com.wasn.exceptions.EmptyPrinterAddressException;
import com.wasn.exceptions.UnTestedPrinterAddressException;
import com.wasn.pojos.Settings;
import com.wasn.services.printservices.TestPrintService;
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
    RelativeLayout back;
    RelativeLayout save;
    //RelativeLayout save;
    RelativeLayout testPrint;
    TextView headerText;
    TextView labelText;
    EditText printerAddressEditText;
    EditText telephoneNoEditText;
    EditText branchNameEditText;

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
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/vegur_2.otf");

        isTestedPrintAddress = false;

        back = (RelativeLayout) findViewById(R.id.settings_layout_back);
        //save = (RelativeLayout) findViewById(R.id.settings_layout_help);
        //save = (RelativeLayout) findViewById(R.id.settings_layout_save);
        testPrint = (RelativeLayout) findViewById(R.id.settings_layout_test_print);

        // set custom font to header text
        headerText = (TextView) findViewById(R.id.settings_layout_header_text);
        headerText.setTypeface(face);
        headerText.setTypeface(null, Typeface.BOLD);

        labelText = (TextView) findViewById(R.id.settings_printer_bluetooth_address);
        labelText.setTypeface(face);

        // set text to printer address
        // printer address stored in database
        printerAddressEditText = (EditText) findViewById(R.id.settings_layout_printer_address_text);
        printerAddressEditText.setText(PreferenceUtils.getPrinterAddress(this));
        printerAddressEditText.setTypeface(face);

        // set text to telephone no
        // stored in database
        //telephoneNoEditText = (EditText) findViewById(R.id.settings_layout_telephone_no_text);
        //telephoneNoEditText.setText(application.getMobileBankData().getTelephoneNo());

        // set text to branch name
        // stored in database
        //branchNameEditText = (EditText) findViewById(R.id.settings_layout_branch_name_text);
        //branchNameEditText.setText(application.getMobileBankData().getBranchName());

        back.setOnClickListener(SettingsActivity.this);
        //save.setOnClickListener(SettingsActivity.this);
        //save.setOnClickListener(SettingsActivity.this);
        testPrint.setOnClickListener(SettingsActivity.this);
    }

    /**
     * Action of save button
     */
    public void save() {
        // get form fields
        String printerAddress = printerAddressEditText.getText().toString();
        String telephoneNo = telephoneNoEditText.getText().toString();
        String branchName = branchNameEditText.getText().toString();
        Settings settings = new Settings(printerAddress, telephoneNo, branchName);

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
                    new TestPrintService(SettingsActivity.this).execute(printerAddress);
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
            }
        });

        dialog.show();
    }

//    /**
//     * Display admin login dialog
//     *
//     * @param settings settings attributes
//     */
//    public void displayAdminLoginDialog(final Settings settings) {
//        final Dialog dialog = new Dialog(SettingsActivity.this);
//
//        //set layout for dialog
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.admin_login_dialog_layout);
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        dialog.setCancelable(true);
//
//        // set custom font
//        TextView messageHeaderTextView = (TextView) dialog.findViewById(R.id.admin_login_layout_message_header_text);
//        TextView messageTextView = (TextView) dialog.findViewById(R.id.admin_login_layout_message_text);
//        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/vegur_2.otf");
//        messageHeaderTextView.setTypeface(face);
//        messageHeaderTextView.setTypeface(null, Typeface.BOLD);
//        messageTextView.setTypeface(face);
//
//        //set ok button
//        Button okButton = (Button) dialog.findViewById(R.id.admin_login_layout_ok_button);
//        okButton.setTypeface(face);
//        okButton.setTypeface(null, Typeface.BOLD);
//        okButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                try {
//                    // validate password
//                    EditText passwordEditText = (EditText) dialog.findViewById(R.id.admin_login_layout_password);
//                    String password = passwordEditText.getText().toString().trim();
//                    LoginUtils.validateAdminPassword(password);
//
//                    // save fields in database
//                    savePrinterAddress(settings.getPrinterAddress());
//                    saveTelephoneNo(settings.getBranchTelephoeNo());
//                    saveBranchName(settings.getBranchName());
//                    displayToast("Settings saved successfully");
//
//                    // back to main activity
//                    startActivity(new Intent(SettingsActivity.this, MobileBankActivity.class));
//                    SettingsActivity.this.finish();
//
//                    dialog.cancel();
//                } catch (UnAuthenticatedUserException e) {
//                    e.printStackTrace();
//                    displayToast("Invalid password");
//                } catch (EmptyFieldsException e) {
//                    e.printStackTrace();
//                    displayToast("Password empty");
//                }
//            }
//        });
//
//        // cancel button
//        Button cancelButton = (Button) dialog.findViewById(R.id.admin_login_layout_cancel_button);
//        cancelButton.setTypeface(face);
//        cancelButton.setTypeface(null, Typeface.BOLD);
//        cancelButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                dialog.cancel();
//            }
//        });
//
//        dialog.show();
//    }

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
            displayMessageDialog("Receipt printed", "Valid printer address, Now you can save settings");
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
            startActivity(new Intent(SettingsActivity.this, MobileBankActivity.class));
            SettingsActivity.this.finish();
        } else if (view == save) {
            save();
        } else if (view == testPrint) {
            print();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        // back to main activity
        startActivity(new Intent(SettingsActivity.this, MobileBankActivity.class));
        SettingsActivity.this.finish();
    }
}
