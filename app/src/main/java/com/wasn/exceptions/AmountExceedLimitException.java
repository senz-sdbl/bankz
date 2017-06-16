package com.wasn.exceptions;

/**
 * Exception when throw invalid balance
 *
 * @author erangaeb@gmail.com (eranga bandara)
 */
public class AmountExceedLimitException extends Exception {

    @Override
    public String toString() {
        return "amount exceed the limit";
    }

}
