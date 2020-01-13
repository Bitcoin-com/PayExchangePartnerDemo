package com.bitcoin.examples.exchange.exceptions;

public class InvalidPaymentException extends Exception {

    public InvalidPaymentException(String reason) {
        super(reason);
    }
}
