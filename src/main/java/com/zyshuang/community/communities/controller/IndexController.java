package com.zyshuang.community.communities.controller;

import com.zyshuang.community.communities.dto.PaginationDTO;
import com.zyshuang.community.communities.dto.QuestionDTO;
import com.zyshuang.community.communities.entities.User;
import com.zyshuang.community.communities.mapper.UserMapper;
import com.zyshuang.community.communities.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionService questionService;

    @RequestMapping("/")
    public String index(HttpServletRequest request,
                        Model model,
                        @RequestParam(name = "page",defaultValue = "1") Integer page,
                        @RequestParam(name = "size",defaultValue = "1") Integer size){
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length != 0)
        for (Cookie cookie :  cookies) {
            if ("token".equals(cookie.getName())){
                //获取cookie
                String token = cookie.getValue();
                User user = userMapper.findUserByToken(token);
                if (user != null){
                    request.getSession().setAttribute("user",user);
                }
                break;
            }
        }
        PaginationDTO pagination = questionService.list(page,size);
        model.addAttribute("pagination",pagination);
        return "index";
    }
}
