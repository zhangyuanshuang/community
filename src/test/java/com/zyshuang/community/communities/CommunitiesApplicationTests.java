package com.zyshuang.community.communities;

import com.zyshuang.community.mapper.QuestionMapper;
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



    @Test
    void contextLoads() {
    }

}
