package com.zyshuang.community.communities.controller;


import com.zyshuang.community.communities.dto.QuestionDTO;
import com.zyshuang.community.communities.mapper.QuestionMapper;
import com.zyshuang.community.communities.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 问题详情页
 */
@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/question/{id}")
        public String toQuestion(@PathVariable("id") Integer id,
                Model model){
            QuestionDTO questionDTO = questionService.getQuestionById(id);
            model.addAttribute("question",questionDTO);
            return "question";
    }
}
