package com.zyshuang.community.communities.entities;


import lombok.Data;

@Data
public class User {

    private Integer id;

    private String accountId;

    private String name;

    private String token;

    private String bio;

    private Long gmtCreate;

    private Long gmtModified;

    private String avatarUrl;

}
