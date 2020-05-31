package com.zyshuang.community.controller;


import com.zyshuang.community.dto.CommentDTO;
import com.zyshuang.community.dto.QuestionDTO;
import com.zyshuang.community.enums.CommentTypeEnum;
import com.zyshuang.community.service.CommentService;
import com.zyshuang.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 问题详情页
 */
@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String toQuestion(@PathVariable("id") Long id,
                             Model model) {

        //查询问题
        QuestionDTO questionDTO = questionService.getQuestionById(id);

        //查询评论列表
        List<CommentDTO> comments = commentService.ListByTargetId(id, CommentTypeEnum.QUESTION);

        //查询标签
        List<QuestionDTO> relatedQuestions = questionService.selectRelated(questionDTO);

        //累加阅读数
        questionService.incView(id);
        model.addAttribute("question", questionDTO);

        model.addAttribute("comments",comments);

        model.addAttribute("relatedQuestions",relatedQuestions);

        return "question";
    }

}
