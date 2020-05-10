package com.zyshuang.community.communities.controller;

import com.zyshuang.community.communities.dto.AccessTokenDTO;
import com.zyshuang.community.communities.dto.GithubUser;
import com.zyshuang.community.communities.entities.User;
import com.zyshuang.community.communities.mapper.UserMapper;
import com.zyshuang.community.communities.provide.GithubProvide;
import com.zyshuang.community.communities.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * 认证类
 */
@Controller
public class AuthorizeController {

    @Autowired
    private GithubProvide githubProvide;

    /**
     *          点击页面的连接直接访问github认证 返回callback请求
     *          通过callback（accessToken.setRedirect_uri("http://localhost:8887/callback");）请求获取访问令牌
     *
     * @param code
     * @param state
     * @return
     */
    @Value("${github.Client.id}")
    private String clientId;

    @Value("${github.Client.secret}")
    private String clientSecret;

    @Value("${github.Redirect.uri}")
    private String redirectUri;

    @Autowired
    private UserService userService;

    @RequestMapping("/callback")
    public String callback(@RequestParam(name = "code")String code,
                           @RequestParam(name = "state")String state,
                           HttpServletRequest request,
                           HttpServletResponse response){
        AccessTokenDTO accessToken = new AccessTokenDTO();
        accessToken.setClient_id(clientId);
        accessToken.setClient_secret(clientSecret);
        accessToken.setCode(code);
        accessToken.setRedirect_uri(redirectUri);
        accessToken.setState(state);
        String token = githubProvide.getAccessToken(accessToken);
        GithubUser githubUser = githubProvide.getUser(token);
        if (githubUser != null && githubUser.getId() != null){
            User user = new User();
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setName(githubUser.getName());
            user.setToken(UUID.randomUUID().toString());
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            user.setBio(githubUser.getBio());
            user.setAvatarUrl(githubUser.getAvatarUrl());
            userService.createOrUpdate(user);
            request.getSession().setAttribute("user",user);
            //添加cookies 持久化
            response.addCookie(new Cookie("token",user.getToken()));
            return "redirect:/";
        }else {
            return "redirect:/";
        }
    }

    @GetMapping("/logout")
    public String logOut(HttpServletRequest request,
                         HttpServletResponse response){
        //退出清除session
        request.getSession().removeAttribute("user");
        //退出删除cooki  覆盖cookie
        Cookie cookie = new Cookie("token",null);
        //立即过时
        cookie.setMaxAge(0);
        //覆盖
        response.addCookie(cookie);
        return "redirect:/";
    }
}
