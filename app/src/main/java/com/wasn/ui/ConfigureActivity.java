package com.wasn.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wasn.R;
import com.wasn.exceptions.EmptyBranchNameException;
import com.wasn.exceptions.InvalidTelephoneNoException;
import com.wasn.utils.ActivityUtils;
import com.wasn.utils.PreferenceUtils;

/**
 * Activity class that handles configurations
 *
 * @author erangaeb@gmail.com (eranga herath)
 */
public class ConfigureActivity extends Activity implements View.OnClickListener {

    private static final String TAG = ConfigureActivity.class.getName();

    // UI fields
    private Typeface typeface;
    private EditText editTextPhone;
    private EditText editTextBranch;
    private EditText editRegion;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configure_layout);

        initUi();
    }

    /**
     * Initialize UI components,
     * Set country code text
     * set custom font for UI fields
     */
    private void initUi() {
        typeface = Typeface.createFromAsset(getAssets(), "fonts/GeosansLight.ttf");

        editTextPhone = (EditText) findViewById(R.id.configure_telephone);
        editTextBranch = (EditText) findViewById(R.id.configure_branch);
        editRegion = (EditText) findViewById(R.id.configure_region);
        saveButton = (Button) findViewById(R.id.save_btn);
        saveButton.setOnClickListener(this);

        editRegion.setTypeface(typeface, Typeface.BOLD);
        editTextPhone.setTypeface(typeface, Typeface.BOLD);
        editTextBranch.setTypeface(typeface, Typeface.BOLD);
        saveButton.setTypeface(typeface, Typeface.BOLD);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View v) {
        if (v == saveButton) {
            onClickSave();
        }
    }

    /**
     * Save button action,
     * create user and validate fields from here
     */
    private void onClickSave() {
        ActivityUtils.hideSoftKeyboard(this);

        // crate user
        String phone = editTextPhone.getText().toString().trim();
        String branch = editTextBranch.getText().toString().trim();
        String region = editRegion.getText().toString().trim();
        try {
            ActivityUtils.isValidConfigurationFields(phone, branch, region);
            saveConfiguration();
            navigateToHome();
        } catch (InvalidTelephoneNoException e) {
            e.printStackTrace();
            displayInformationMessageDialog("Error", "Invalid telephone no");
        } catch (EmptyBranchNameException e) {
            e.printStackTrace();
            displayInformationMessageDialog("Error", "Invalid branch name");
        }
    }

    private void saveConfiguration() {
        PreferenceUtils.savePhone(this, editTextPhone.getText().toString().trim());
        PreferenceUtils.saveBranch(this, editTextBranch.getText().toString().trim());

        Toast.makeText(ConfigureActivity.this, "Configuration saved", Toast.LENGTH_LONG).show();
    }

    /**
     * Switch to home activity
     * This method will be call after successful login
     */
    private void navigateToHome() {
        Intent intent = new Intent(this, BankzActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        this.finish();
    }

    /**
     * Display message dialog when user request(click) to register
     *
     * @param message message to be display
     */
    public void displayConfirmationMessageDialog(String message) {
        final Dialog dialog = new Dialog(this);

        //set layout for dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.confirm_message_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);

        // set dialog texts
        TextView messageHeaderTextView = (TextView) dialog.findViewById(R.id.information_message_dialog_layout_message_header_text);
        TextView messageTextView = (TextView) dialog.findViewById(R.id.information_message_dialog_layout_message_text);
        messageHeaderTextView.setText("Confirm");
        messageTextView.setText(Html.fromHtml(message));

        // set custom font
        messageHeaderTextView.setTypeface(typeface, Typeface.BOLD);
        messageTextView.setTypeface(typeface, Typeface.BOLD);

        //set ok button
        Button okButton = (Button) dialog.findViewById(R.id.information_message_dialog_layout_ok_button);
        okButton.setTypeface(typeface, Typeface.BOLD);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();
                Toast.makeText(ConfigureActivity.this, "Configuration saved", Toast.LENGTH_LONG).show();
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
     * Display message dialog with registration status
     *
     * @param message message to be display
     */
    public void displayInformationMessageDialog(String title, String message) {
        final Dialog dialog = new Dialog(this);

        //set layout for dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.information_message_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);

        // set dialog texts
        TextView messageHeaderTextView = (TextView) dialog.findViewById(R.id.information_message_dialog_layout_message_header_text);
        TextView messageTextView = (TextView) dialog.findViewById(R.id.information_message_dialog_layout_message_text);
        messageHeaderTextView.setText(title);
        messageTextView.setText(Html.fromHtml(message));

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

}

