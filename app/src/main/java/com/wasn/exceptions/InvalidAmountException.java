package com.wasn.exceptions;

/**
 * Exception when throw invalid balance
 *
 * @author erangaeb@gmail.com (eranga bandara)
 */
public class InvalidAmountException extends Exception {

    @Override
    public String toString() {
        return "invalid amount";
    }

}
