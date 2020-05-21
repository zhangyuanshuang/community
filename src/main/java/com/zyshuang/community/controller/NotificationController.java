package com.zyshuang.community.controller;

import com.zyshuang.community.dto.NotificationDTO;
import com.zyshuang.community.entities.User;
import com.zyshuang.community.enums.NotificationTypeEnum;
import com.zyshuang.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    @GetMapping("/notification/{id}")
    public String profile(HttpServletRequest request,
                          @PathVariable(name = "id") Long id) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null){
            return "redirect:/";
        }
        NotificationDTO notificationDTO = notificationService.read(id,user);

        //查看回复通知的问题内容
        if (NotificationTypeEnum.REPLY_COMMENT.getType() == notificationDTO.getType()
                ||NotificationTypeEnum.REPLY_QUESTION.getType() == notificationDTO.getType() ){
            return "redirect:/question/" + notificationDTO.getOuterid();  //当回复评论的评论的时候此处会跳转到评论者的问题
        }else {
            return "redirect:/";
        }
    }
}
