package com.zyshuang.community.interceptor;

import com.zyshuang.community.entities.User;
import com.zyshuang.community.entities.UserExample;
import com.zyshuang.community.mapper.UserMapper;
import com.zyshuang.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;

    @Value("${github.Redirect.uri}")
    private String redirectUri;

    @Autowired
    private NotificationService notificationService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //设置 context 级别的属性
        request.getServletContext().setAttribute("redirectUri", redirectUri);
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length != 0)
            for (Cookie cookie :  cookies) {
                if ("token".equals(cookie.getName())){
                    //获取cookie
                    String token = cookie.getValue();
                    UserExample userExample = new UserExample();
                    userExample.createCriteria()
                               .andTokenEqualTo(token);
                    List<User> users = userMapper.selectByExample(userExample);
                    if (users.size() != 0){
                        request.getSession().setAttribute("user",users.get(0));
                        Long unreadCount = notificationService.unreadCount(users.get(0).getId());
                        //通知数字
                        request.getSession().setAttribute("unreadCount",unreadCount);
                    }
                    break;
                }
            }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
