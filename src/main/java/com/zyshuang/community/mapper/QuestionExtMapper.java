package com.zyshuang.community.mapper;

import com.zyshuang.community.dto.QuestionQueryDTO;
import com.zyshuang.community.entities.Question;
import com.zyshuang.community.entities.QuestionExample;


import java.util.List;

public interface QuestionExtMapper {
    int incView(Question record);

    int incCommentCount(Question record);

    List<Question> selectRelated(Question question);

    int countBySearch(QuestionQueryDTO questionQueryDTO);

    List<Question> selectBySearch(QuestionQueryDTO questionQueryDTO);
}