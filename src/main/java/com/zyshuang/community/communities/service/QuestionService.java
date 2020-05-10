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

    //查询所有用户发布的话题数
    public PaginationDTO list(Integer page,Integer size){

        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalPage;
        //总题数
        Integer totalCount = questionMapper.count();

        if (totalCount % size == 0){
            totalPage = totalCount / size;
        }else {
            totalPage = totalCount / size + 1;
        }
        //所有用户话题分页
        paginationDTO.setPagination(totalPage,page);


        if (page < 1) {
            page = 1;
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

    //查询当前用户发布的话题数
    public PaginationDTO listByUserId(Integer userId, Integer page, Integer size) {

        PaginationDTO paginationDTO = new PaginationDTO();
        int totalPage;
        //总题数
        Integer totalCount = questionMapper.countByUserId(userId);

        if (totalCount % size == 0){
            totalPage = totalCount / size;
        }else {
            totalPage = totalCount / size + 1;
        }

        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }

        //当前用户发布话题分页
        paginationDTO.setPagination(totalPage,page);


        //查询数据索引值
        Integer offset = size*(page-1);
        List<Question> questions = questionMapper.listByUserId(userId,offset,size);
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

    public QuestionDTO getQuestionById(Integer id) {
        //根据Id查询问题
        Question question = questionMapper.getQuestionById(id);

        User user = userMapper.findById(question.getCreator());

        //创建对象
        QuestionDTO questionDTO = new QuestionDTO();

        //存储User
        questionDTO.setUser(user);

        //将question所有属性复制到questionDTO
        BeanUtils.copyProperties(question,questionDTO);

        return questionDTO;
    }

    public void insertOrUpdate(Question question) {
        if (question.getId() == null){
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.insert(question);
        }else{
            question.setGmtModified(System.currentTimeMillis());
            questionMapper.update(question);
        }
    }
}
