package com.zyshuang.community.communities;

import com.zyshuang.community.mapper.QuestionMapper;
import com.zyshuang.community.dto.AliyunOssDTO;
import com.zyshuang.community.service.QuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CommunitiesApplicationTests {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private AliyunOssDTO aliyunOssProvide;

    @Test
    void contextLoads() {

        System.out.println(aliyunOssProvide.getAccessKeyId());
        System.out.println(aliyunOssProvide.getAccessKeySecret());
        System.out.println(aliyunOssProvide.getBucketName());
        System.out.println(aliyunOssProvide.getEndpoint());

    }

}
