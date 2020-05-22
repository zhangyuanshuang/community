package com.zyshuang.community.service;

import com.zyshuang.community.dto.CommentDTO;
import com.zyshuang.community.entities.*;
import com.zyshuang.community.enums.CommentTypeEnum;
import com.zyshuang.community.enums.NotificationStatusEnum;
import com.zyshuang.community.enums.NotificationTypeEnum;
import com.zyshuang.community.exception.CustomerErrorCode;
import com.zyshuang.community.exception.CustomerException;
import com.zyshuang.community.mapper.*;
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

    @Autowired
    private CommentExtMapper commentExtMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    /**
     * 事务回滚
     *
     * @param comment
     * @param commentator
     */
    @Transactional
    public void insert(Comment comment, User commentator) {
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            throw new CustomerException(CustomerErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
            throw new CustomerException(CustomerErrorCode.TYPE_PARAM_WRONG);
        }
        if (comment.getType().equals(CommentTypeEnum.COMMENT.getType())) {
            //回复谁的评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment == null) {
                throw new CustomerException(CustomerErrorCode.COMMENT_NOT_FOUND);
            }
            //回复谁的问题
            Question question = questionMapper.selectByPrimaryKey(dbComment.getParentId());
            if (question == null) {
                throw new CustomerException(CustomerErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);

            //增加二级评论数
            Comment parentComment = new Comment();
            parentComment.setId(comment.getParentId());
            parentComment.setCommentCount(1);
            commentExtMapper.incCommentCount(parentComment);

            //调用方法 创建通知
            createNotify(comment, dbComment.getCommentator(), commentator.getName(), question.getTitle(), NotificationTypeEnum.REPLY_COMMENT, question.getId());
        } else {
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question == null) {
                throw new CustomerException(CustomerErrorCode.QUESTION_NOT_FOUND);
            }
            comment.setCommentCount(0);
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionExtMapper.incCommentCount(question);

            //调用方法 创建通知
            createNotify(comment, question.getCreator(), commentator.getName(), question.getTitle(), NotificationTypeEnum.REPLY_QUESTION, question.getId());
        }
    }


    /**
     * 通知
     *
     * @param comment
     * @param receiver
     * @param notifierName
     * @param outerTitle
     * @param notificationTypeEnum
     * @param outerId
     */
    private void createNotify(Comment comment, Long receiver, String notifierName, String outerTitle, NotificationTypeEnum notificationTypeEnum, Long outerId) {
        //当时回复别人的时候 不用通知自己
        if(receiver == comment.getCommentator()){
            return;
        }
        //通知
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(notificationTypeEnum.getType());
        notification.setOuterid(outerId);
        notification.setNotifier(comment.getCommentator());  //通知者
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus()); //默认为未读状态
        notification.setReceiver(receiver); //接收
        notification.setNotifierName(notifierName);
        notification.setOuterTitle(outerTitle);
        notificationMapper.insert(notification);
    }

    /**
     * 通过id和类型查询评论
     *
     * @param id
     * @param type
     * @return
     */
    public List<CommentDTO> ListByTargetId(Long id, CommentTypeEnum type) {
        //查评论问题的评论
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria()
                .andParentIdEqualTo(id)
                .andTypeEqualTo(type.getType());
        //按照评论时间排序
        commentExample.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(commentExample);

        if (comments.size() == 0) {
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
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());
    }
}
