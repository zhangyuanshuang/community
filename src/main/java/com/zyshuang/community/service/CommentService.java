package com.zyshuang.community.service;

import com.zyshuang.community.dto.CommentDTO;
import com.zyshuang.community.entities.*;
import com.zyshuang.community.enums.CommentTypeEnum;
import com.zyshuang.community.exception.CustomerErrorCode;
import com.zyshuang.community.exception.CustomerException;
import com.zyshuang.community.mapper.CommentMapper;
import com.zyshuang.community.mapper.QuestionExtMapper;
import com.zyshuang.community.mapper.QuestionMapper;
import com.zyshuang.community.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    /**
     * 事务回滚
     * @param comment
     */
    @Transactional
    public void insert(Comment comment) {
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            throw new CustomerException(CustomerErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
            throw new CustomerException(CustomerErrorCode.TYPE_PARAM_WRONG);
        }
        if (comment.getType() == CommentTypeEnum.COMMENT.getType()){
            //回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment == null){
                throw new CustomerException(CustomerErrorCode.COMMENT_NOT_FOUND);
            }
            commentMapper.insert(comment);
        }else{
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question == null) {
                throw new CustomerException(CustomerErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionExtMapper.incCommentCount(question);
        }
    }

    public List<CommentDTO> listByQuestionId(Long id) {
        //查评论问题的评论
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria()
                      .andParentIdEqualTo(id)
                      .andTypeEqualTo(CommentTypeEnum.QUESTION.getType());

        //按照评论时间排序
        List<Comment> comments = commentMapper.selectByExample(commentExample);

        if (comments.size() == 0){
            return new ArrayList<>();
        }
        //map 遍历一下返回结果集 算结果集变成set对象
        //获取去重的评论人 不拿重复的评论人的id
        //set不包含重复的值
        Set<Long> commentators = comments.stream().map(Comment::getCommentator).collect(Collectors.toSet());
        List<Long> userIds = new ArrayList<>(commentators);

        //根据id查询用户信息
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                   .andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, user -> user));

        // 转换 comment 为 commentDTO
        return comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment,commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());
    }
}
