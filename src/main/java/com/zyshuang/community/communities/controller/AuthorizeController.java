package com.zyshuang.community.communities.controller;

import com.zyshuang.community.communities.entities.AccessToken;
import com.zyshuang.community.communities.entities.GithubUser;
import com.zyshuang.community.communities.provide.GithubProvide;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @RequestMapping("/callback")
    public String callback(@RequestParam(name = "code")String code,
                           @RequestParam(name = "state")String state){
        AccessToken accessToken = new AccessToken();
        accessToken.setClient_id(clientId);
        accessToken.setClient_secret(clientSecret);
        accessToken.setCode(code);
        accessToken.setRedirect_uri(redirectUri);
        accessToken.setState(state);
        String token = githubProvide.getAccessToken(accessToken);
        GithubUser user = githubProvide.getUser(token);
        System.out.println(user.getName());
        return "index";
    }
}
