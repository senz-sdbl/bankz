package com.wasn.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import com.wasn.exceptions.AmountExceedLimitException;
import com.wasn.exceptions.EmptyBranchNameException;
import com.wasn.exceptions.InvalidAccountException;
import com.wasn.exceptions.InvalidAmountException;
import com.wasn.exceptions.InvalidInputFieldsException;
import com.wasn.exceptions.InvalidPasswordException;
import com.wasn.exceptions.InvalidTelephoneNoException;
import com.wasn.exceptions.MismatchingCredentialsException;
import com.wasn.exceptions.PasswordMisMatchException;

/**
 * Utility class to handle activity related common functions
 *
 * @author erangaeb@gmail.com (eranga herath)
 */
public class ActivityUtils {

    private static ProgressDialog progressDialog;

    /**
     * Hide keyboard
     * Need to hide soft keyboard in following scenarios
     * 1. When starting background task
     * 2. When exit from activity
     * 3. On button submit
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getApplicationContext().getSystemService(activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * Create and show custom progress dialog
     * Progress dialogs displaying on background tasks
     * <p/>
     * So in here
     * 1. Create custom layout for message dialog
     * 2, Set messages to dialog
     *
     * @param context activity context
     * @param message message to be display
     */
    public static void showProgressDialog(Context context, String message) {
        progressDialog = ProgressDialog.show(context, null, message, true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);

        progressDialog.show();
    }

    /**
     * Cancel progress dialog when background task finish
     */
    public static void cancelProgressDialog() {
        if (progressDialog != null) {
            progressDialog.cancel();
        }
    }

    /**
     * Validate input fields of registration form,
     * Need to have
     * 1. non empty valid phone no
     * 2. non empty username
     * 3. non empty passwords
     * 4. two passwords should be match
     *
     * @param username object
     * @return valid or not
     */
    public static boolean isValidUsername(String username) throws InvalidInputFieldsException {
        if (username.isEmpty() || (username.length() < 5 || username.length() > 12)) {
            throw new InvalidInputFieldsException();
        }

        return true;
    }

    public static boolean isValidRegistrationFields(String account, String password, String confirmPassword) throws InvalidAccountException, InvalidPasswordException, PasswordMisMatchException {
        if (account.isEmpty() || account.length() < 5 || account.length() > 12) {
            throw new InvalidAccountException();
        }

        if (password.isEmpty() || password.length() < 4)
            throw new InvalidPasswordException();

        if (!password.equals(confirmPassword))
            throw new PasswordMisMatchException();

        return true;
    }

    public static boolean isValidConfigurationFields(String phone, String branch, String region) throws InvalidTelephoneNoException, EmptyBranchNameException {
        if (phone.isEmpty() || phone.length() < 9 || phone.length() > 10)
            throw new InvalidTelephoneNoException();

        if (branch.isEmpty())
            throw new EmptyBranchNameException();

        return true;
    }

    public static boolean isValidLoginFields(String givenAccount, String givenPassword, String account, String password) throws InvalidInputFieldsException, MismatchingCredentialsException {
        if (givenAccount.isEmpty() || givenPassword.isEmpty())
            // empty fields
            throw new InvalidInputFieldsException();

        if (!givenAccount.equalsIgnoreCase(account) || !givenPassword.equalsIgnoreCase(password))
            // invalid username/password
            throw new MismatchingCredentialsException();

        return true;
    }

    public static boolean isValidTransactionFields(String account, String mobile, String amount) throws InvalidAccountException, InvalidTelephoneNoException, NumberFormatException, InvalidAmountException, AmountExceedLimitException {
        if (account.isEmpty() || account.length() < 5 || account.length() > 12) {
            throw new InvalidAccountException();
        }

        if (!mobile.isEmpty()) {
            if (mobile.length() < 9 || mobile.length() > 10)
                throw new InvalidTelephoneNoException();
        }

        if (amount.isEmpty() || Integer.parseInt(amount) == 0) {
            throw new InvalidAmountException();
        }

        if (Integer.parseInt(amount) > 50000) {
            throw new AmountExceedLimitException();
        }

        return true;
    }

    public static String formatNic(String nic) throws InvalidInputFieldsException {
        if (!nic.isEmpty()) {
            if (nic.length() == 10 || nic.length() == 13) {
                String c = nic.substring(nic.length() - 1);
                if (c.toUpperCase().equalsIgnoreCase("X") || c.toUpperCase().equalsIgnoreCase("V")) {
                    // valid nic
                    return nic.toUpperCase();
                } else {
                    throw new InvalidInputFieldsException();
                }
            } else {
                throw new InvalidInputFieldsException();
            }
        } else {
            throw new InvalidInputFieldsException();
        }
    }

}
