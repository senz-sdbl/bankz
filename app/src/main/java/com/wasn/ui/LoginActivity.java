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
import com.wasn.application.BankzApplication;
import com.wasn.exceptions.InvalidInputFieldsException;
import com.wasn.exceptions.MismatchingCredentialsException;
import com.wasn.exceptions.NoUserException;
import com.wasn.utils.ActivityUtils;
import com.wasn.utils.PreferenceUtils;
import com.wasn.utils.TransactionUtils;

/**
 * Activity class that handles login
 *
 * @author erangaeb@gmail.com (eranga herath)
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    private static final String TAG = ConfigureActivity.class.getName();

    // UI fields
    private Typeface typeface;
    private EditText editTextAccount;
    private EditText editTextPassword;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layoutt);

        initUi();
        initCredentials();
    }

    /**
     * Initialize UI components,
     * Set country code text
     * set custom font for UI fields
     */
    private void initUi() {
        typeface = Typeface.createFromAsset(getAssets(), "fonts/GeosansLight.ttf");

        editTextAccount = (EditText) findViewById(R.id.login_account_no);
        editTextPassword = (EditText) findViewById(R.id.login_password);
        loginButton = (Button) findViewById(R.id.login_btn);
        loginButton.setOnClickListener(this);

        editTextAccount.setTypeface(typeface, Typeface.BOLD);
        editTextPassword.setTypeface(typeface, Typeface.BOLD);
        loginButton.setTypeface(typeface, Typeface.BOLD);
    }

    private void initCredentials() {
        try {
            // saved credentials
            String acc = PreferenceUtils.getUser(this).getUsername();
            String pwd = PreferenceUtils.getPassword(this);

            // set credentials
            editTextAccount.setText(acc);
            editTextPassword.setText(pwd);
        } catch (NoUserException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View v) {
        if (v == loginButton) {
            onClickLogin();
        }
    }

    /**
     * login button action,
     */
    private void onClickLogin() {
        ActivityUtils.hideSoftKeyboard(this);

        try {
            // saved credentials
            String acc = PreferenceUtils.getUser(this).getUsername();
            String pwd = PreferenceUtils.getPassword(this);

            // given credentials
            String account = TransactionUtils.getRegAccount(editTextAccount.getText().toString().trim());
            String password = editTextPassword.getText().toString().trim();
            ActivityUtils.isValidLoginFields(account, password, acc, pwd);
            BankzApplication.setLogin(true);
            navigateToHome();
            Toast.makeText(this, "Login success", Toast.LENGTH_LONG).show();
        } catch (NoUserException e) {
            displayInformationMessageDialog("Error", "You have to register first");
        } catch (MismatchingCredentialsException e) {
            e.printStackTrace();
            displayInformationMessageDialog("Error", "Invalid account/password");
        } catch (InvalidInputFieldsException e) {
            e.printStackTrace();
            displayInformationMessageDialog("Error", "Empty account/password");
        }
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

