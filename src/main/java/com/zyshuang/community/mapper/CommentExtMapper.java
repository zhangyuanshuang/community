package com.zyshuang.community.mapper;

import com.zyshuang.community.entities.Comment;
import com.zyshuang.community.entities.CommentExample;
import com.zyshuang.community.entities.Question;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface CommentExtMapper {
    int incCommentCount(Comment comment);
}