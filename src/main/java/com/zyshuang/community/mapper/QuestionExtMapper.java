package com.zyshuang.community.mapper;

import com.zyshuang.community.entities.Question;


import java.util.List;

public interface QuestionExtMapper {
    int incView(Question record);

    int incCommentCount(Question record);

    List<Question> selectRelated(Question question);

}