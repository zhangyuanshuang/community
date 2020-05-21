package com.zyshuang.community.controller;

import com.zyshuang.community.dto.CommentCreateDTO;
import com.zyshuang.community.dto.CommentDTO;
import com.zyshuang.community.dto.ResultDTO;
import com.zyshuang.community.entities.Comment;
import com.zyshuang.community.entities.User;
import com.zyshuang.community.enums.CommentTypeEnum;
import com.zyshuang.community.exception.CustomerErrorCode;
import com.zyshuang.community.mapper.CommentExtMapper;
import com.zyshuang.community.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
        commentService.insert(comment,user);

        return ResultDTO.okOf();
    }

    /**
     *  查询二级评论
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/comment/{id}",method = RequestMethod.GET)
    public ResultDTO<List<CommentDTO>> comments(@PathVariable(name = "id") Long id){
        //根据目标id查询二级评论（CommentTypeEnum.COMMENT为二级评论）
        List<CommentDTO> commentDTOS = commentService.ListByTargetId(id, CommentTypeEnum.COMMENT);
        return ResultDTO.okOf(commentDTOS);
    }
}
