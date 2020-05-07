package com.zyshuang.community.communities.dto;

import com.zyshuang.community.communities.entities.User;
import lombok.Data;

@Data
public class QuestionDTO {
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

    //共同关注
    private int commentCount;

    //阅读数
    private int viewCount;

    private int likeCount;

    //标签
    private String tag;

    private User user;
}
