package com.zyshuang.community.communities.entities;

import lombok.Data;

@Data
public class Question {

    private Integer id;

    //标题
    private String title;

    //描述补充
    private String description;

    //创建时间戳
    private Long gmtCreate;

    //修改时间戳
    private Long gmtModified;

    //创建话题的发布者
    private Integer creator;

    //评论数
    private int commentCount;

    //浏览数
    private int viewCount;

    private int likeCount;

    //标签
    private String tag;

}
