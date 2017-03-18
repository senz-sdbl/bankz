package com.wasn.exceptions;

/**
 * Exception that need to throw when telephone no is empty
 *
 * eranga.herath@pagero.com (eranga herath)
 */
public class InvalidTelephoneNoException extends Exception {
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return "Invalid telephone no";
    }
}
