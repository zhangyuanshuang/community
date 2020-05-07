package com.zyshuang.community.communities;

import com.zyshuang.community.communities.dto.QuestionDTO;
import com.zyshuang.community.communities.service.QuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class CommunitiesApplicationTests {

    @Autowired
    private QuestionService questionService;

    @Test
    void contextLoads() {
    }

}
