package com.wasn.utils;

import com.wasn.exceptions.EmptyBranchNameException;
import com.wasn.exceptions.EmptyPrinterAddressException;
import com.wasn.exceptions.InvalidTelephoneNoException;

/**
 * Utility class for settings activity
 *
 * @author erangaeb@gmail.com (eranga bandara)
 */
public class SettingsUtils {

    /**
     * Validate printer address
     *
     * @param printerAddress printer address
     */
    public static boolean validatePrinterAddress(String printerAddress) throws EmptyPrinterAddressException {
        if (printerAddress.equals("")) {
            // check empty printer address since it doesn't have default value
            throw new EmptyPrinterAddressException();
        }

        return true;
    }

    /**
     * Validate telephone no
     *
     * @param telephoneNo telephone no
     */
    public static void validateTelephoneNo(String telephoneNo) throws InvalidTelephoneNoException {
        if (telephoneNo.equals("") || telephoneNo.length() < 9) {
            // empty telephone
            throw new InvalidTelephoneNoException();
        }
    }

    /**
     * Validate branch name
     *
     * @param branchName branch name
     */
    public static void validateBranchName(String branchName) throws EmptyBranchNameException {
        if (branchName.equals("")) {
            // empty branch name
            throw new EmptyBranchNameException();
        }
    }
}
