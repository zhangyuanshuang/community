package com.zyshuang.community.exception;

public enum CustomerErrorEnum implements CustomerErrorCode{
    QUESTION_NOT_FOUND("你找的问题不在了，要不换个试试？");
    private String message;

    public String getMessage() {
        return message;
    }

    CustomerErrorEnum(String message) {
        this.message = message;
    }
}
