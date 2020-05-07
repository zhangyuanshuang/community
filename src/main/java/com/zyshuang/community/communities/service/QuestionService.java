package com.zyshuang.community.communities.service;

import com.zyshuang.community.communities.dto.PaginationDTO;
import com.zyshuang.community.communities.dto.QuestionDTO;
import com.zyshuang.community.communities.entities.Question;
import com.zyshuang.community.communities.entities.User;
import com.zyshuang.community.communities.mapper.QuestionMapper;
import com.zyshuang.community.communities.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    public PaginationDTO list(Integer page,Integer size){

        PaginationDTO paginationDTO = new PaginationDTO();
        //总题数
        Integer totalCount = questionMapper.count();
        //
        paginationDTO.setPagination(totalCount,size,page);

        if (page < 1) {
            page = 1;
        }
        if (page > paginationDTO.getTotalPage()) {
            page = paginationDTO.getTotalPage();
        }

        //查询数据索引值
        Integer offset = size*(page-1);
        List<Question> questions = questionMapper.list(offset,size);
        //放置查询出来的数据
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : questions) {
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            //将question所有属性复制到questionDTO
            BeanUtils.copyProperties(question,questionDTO);
            //将查出来的user的数据添加到传输层（DTO）
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestionDTOS(questionDTOList);
        return paginationDTO;
    }
}
