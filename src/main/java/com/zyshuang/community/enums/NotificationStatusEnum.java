package com.zyshuang.community.enums;

public enum NotificationStatusEnum {
    UNREAD(0),
    READ(1);

    //状态：已读或者未读
    private int status;

    public int getStatus() {
        return status;
    }

    NotificationStatusEnum(int status) {
        this.status = status;
    }
}
