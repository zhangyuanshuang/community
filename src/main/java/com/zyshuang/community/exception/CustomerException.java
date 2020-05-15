package com.zyshuang.community.exception;

public class CustomerException extends RuntimeException {

    private String message;

    private Integer code;

    public CustomerException(ICustomerErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
