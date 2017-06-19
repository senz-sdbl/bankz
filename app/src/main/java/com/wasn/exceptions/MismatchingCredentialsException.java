package com.wasn.exceptions;

public class MismatchingCredentialsException extends Exception {

    @Override
    public String toString() {
        return "invalid credentials";
    }

}
