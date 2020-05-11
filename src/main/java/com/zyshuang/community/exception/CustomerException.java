package com.zyshuang.community.exception;

public class CustomerException extends RuntimeException {

    private String message;

    public CustomerException(CustomerErrorCode errorCode) {
        this.message = errorCode.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
