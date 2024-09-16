package com.ing_hub_case.exception;

public class NoSuchCustomerExistsException extends RuntimeException {
    private String message;

    public NoSuchCustomerExistsException() {}

    public NoSuchCustomerExistsException(String msg) {
        super(msg);
        this.message = msg;
    }
}
