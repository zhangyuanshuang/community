package com.zyshuang.community.controller;

import com.zyshuang.community.dto.CommentCreateDTO;
import com.zyshuang.community.dto.CommentDTO;
import com.zyshuang.community.dto.ResultDTO;
import com.zyshuang.community.entities.Comment;
import com.zyshuang.community.entities.User;
import com.zyshuang.community.exception.CustomerErrorCode;
import com.zyshuang.community.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 *  评论
 */
@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @ResponseBody
    @RequestMapping(value = "/comment",method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO,
                       HttpServletRequest request){
        //获取登录状态 判断是否已经登录
        User user = (User) request.getSession().getAttribute("user");
        if (user == null){
            return ResultDTO.errorOf(CustomerErrorCode.NOT_LOGIN);
        }

        //判断内容是否为空
        if (commentCreateDTO == null || StringUtils.isBlank(commentCreateDTO.getContent())/*commentCreateDTO.getContent() == null || commentCreateDTO.getContent().equals("")*/){
            return ResultDTO.errorOf(CustomerErrorCode.CONTENT_IS_EMPTY);
        }

        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setContent(commentCreateDTO.getContent());
        comment.setType(commentCreateDTO.getType());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setCommentator(user.getId());
        comment.setLikeCount(0L);
        commentService.insert(comment);
        return ResultDTO.okOf();
    }
}
