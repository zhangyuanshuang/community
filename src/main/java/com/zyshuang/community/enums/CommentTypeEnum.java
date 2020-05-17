package com.zyshuang.community.enums;

public enum CommentTypeEnum {
    //评论问题
    QUESTION(1),
    //评论回复
    COMMENT(2);

    private Integer type;

    public Integer getType(){
        return type;
    }
    CommentTypeEnum(Integer type) {
        this.type = type;
    }
    public static boolean isExist(Integer type) {
        for (CommentTypeEnum commentTypeEnum : CommentTypeEnum.values()) {
            if (commentTypeEnum.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }
}
