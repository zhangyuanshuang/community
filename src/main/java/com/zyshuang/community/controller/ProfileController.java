package com.zyshuang.community.controller;

import com.zyshuang.community.dto.PaginationDTO;
import com.zyshuang.community.dto.QuestionDTO;
import com.zyshuang.community.entities.User;
import com.zyshuang.community.exception.CustomerErrorCode;
import com.zyshuang.community.exception.CustomerException;
import com.zyshuang.community.service.NotificationService;
import com.zyshuang.community.service.QuestionService;
import jdk.nashorn.internal.ir.IfNode;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 *  个人页
 */
@Controller
public class ProfileController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/profile/{action}")
    public String profile(HttpServletRequest request,
                          @PathVariable(name = "action") String action,
                          Model model,
                          @RequestParam(name = "page", defaultValue = "1") Integer page,
                          @RequestParam(name = "size", defaultValue = "5") Integer size) {

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }

        if ("question".equals(action)) {
            model.addAttribute("section", "question");
            model.addAttribute("sectionName", "我的提问");
            PaginationDTO paginationDTO = questionService.listByUserId(user.getId(), page, size);
            model.addAttribute("pagination", paginationDTO);
        } else if ("replies".equals(action)) {
            PaginationDTO paginationDTO = notificationService.listByUserId(user.getId(), page, size);
            model.addAttribute("section", "replies");
            model.addAttribute("pagination", paginationDTO);
            model.addAttribute("sectionName", "最新回复");
        }
        return "profile";
    }

    @GetMapping("/question/delete/{id}")
    public String doDelete(@PathVariable("id") Long id,
                           Model model,
                           @RequestParam(name = "page", defaultValue = "1") Integer page,
                           @RequestParam(name = "size", defaultValue = "5") Integer size,
                           HttpServletRequest request){

        //从session中获取
        User user = (User) request.getSession().getAttribute("user");

        //查询问题
        QuestionDTO questionDTO = questionService.getQuestionById(id);

        if (user == null) {
            throw new CustomerException(CustomerErrorCode.NOT_LOGIN);
        }
        if (questionDTO.getCreator() != user.getId()){
            throw new CustomerException(CustomerErrorCode.DELETE_QUESTION_FAIL);
        }
        if (id != questionDTO.getId()){
            throw new CustomerException(CustomerErrorCode.QUESTION_NOT_FOUND);
        }
        questionService.deleteQuestionById(id);

        return "redirect:/profile/question";
    }
}
