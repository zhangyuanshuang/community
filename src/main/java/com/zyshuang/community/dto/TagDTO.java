package com.zyshuang.community.dto;


import lombok.Data;

import java.util.List;

@Data
public class TagDTO {
    //分类名称
    private String categoryName;

    //所有tags
    private List<String> tags;
}
