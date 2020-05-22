package com.zyshuang.community.service;

import com.zyshuang.community.dto.PaginationDTO;
import com.zyshuang.community.dto.QuestionDTO;
import com.zyshuang.community.dto.QuestionQueryDTO;
import com.zyshuang.community.entities.Question;
import com.zyshuang.community.entities.QuestionExample;
import com.zyshuang.community.entities.User;
import com.zyshuang.community.exception.CustomerErrorCode;
import com.zyshuang.community.exception.CustomerException;
import com.zyshuang.community.mapper.QuestionExtMapper;
import com.zyshuang.community.mapper.QuestionMapper;
import com.zyshuang.community.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private UserMapper userMapper;

    //查询所有用户发布的话题数
    public PaginationDTO list(String search, Integer page, Integer size){


        if (StringUtils.isNotBlank(search)){
            //按空格切割
            String[] searchSplit = StringUtils.split(search, " ");
            //以|拼接起来
            Arrays.stream(searchSplit).collect(Collectors.joining("|"));
        }

        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO();

        int totalPage;

        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);
        //总题数
        Integer totalCount = questionExtMapper.countBySearch(questionQueryDTO);

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
        int offset = size*(page-1);
        questionQueryDTO.setSize(size);
        questionQueryDTO.setPage(offset);
        List<Question> questions = questionExtMapper.selectBySearch(questionQueryDTO);
        //放置查询出来的数据
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            //将question所有属性复制到questionDTO
            BeanUtils.copyProperties(question,questionDTO);
            //将查出来的user的数据添加到传输层（DTO）
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);
        return paginationDTO;
    }

    //查询当前用户发布的话题数
    public PaginationDTO listByUserId(Long userId, Integer page, Integer size) {

        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();
        int totalPage;
        //总题数
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria()
                       .andCreatorEqualTo(userId);
        Integer totalCount = (int)questionMapper.countByExample(questionExample);
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
        int offset = size*(page-1);

        QuestionExample example = new QuestionExample();
        example.createCriteria()
                .andCreatorEqualTo(userId);
        example.setOrderByClause("gmt_create desc");
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(example, new RowBounds(offset, size));
        //放置查询出来的数据
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            //将question所有属性复制到questionDTO
            BeanUtils.copyProperties(question,questionDTO);
            //将查出来的user的数据添加到传输层（DTO）
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);
        return paginationDTO;
    }

    /**
     * 根据id查询话题
     * @param id
     * @return
     */
        public QuestionDTO getQuestionById(Long id) {
        //根据Id查询问题
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question == null){
            throw new CustomerException(CustomerErrorCode.QUESTION_NOT_FOUND);
        }
        User user = userMapper.selectByPrimaryKey(question.getCreator());

        //创建对象
        QuestionDTO questionDTO = new QuestionDTO();

        //存储User
        questionDTO.setUser(user);

        //将question所有属性复制到questionDTO
        BeanUtils.copyProperties(question,questionDTO);

        return questionDTO;
    }

    /**
     * 修改和发布话题
     * @param question
     */
    public void insertOrUpdate(Question question) {
        if (question.getId() == null){
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setViewCount(0);
            question.setCommentCount(0);
            question.setLikeCount(0);
            questionMapper.insert(question);
        }else{

            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            QuestionExample example = new QuestionExample();
            example.createCriteria()
                   .andIdEqualTo(question.getId());
            int i = questionMapper.updateByExampleSelective(updateQuestion, example);
            if (i != 1){
                throw new CustomerException(CustomerErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    public void incView(Long id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }

    public List<QuestionDTO> selectRelated(QuestionDTO questionDTO) {
        if (StringUtils.isBlank(questionDTO.getTag())){
            return new ArrayList<>();
        }
        //替换
        String replace = StringUtils.replace(questionDTO.getTag(), "，", ",");
        //切割
        String[] tags = StringUtils.split(replace, ",");
        //拼接
        String regexpTag = String.join("|", tags);
        Question question = new Question();
        question.setId(questionDTO.getId());
        question.setTag(regexpTag);
        List<Question> questions = questionExtMapper.selectRelated(question);
        return questions.stream().map(q -> {
            QuestionDTO quesDTO = new QuestionDTO();
            BeanUtils.copyProperties(q,quesDTO);
            return quesDTO;
        }).collect(Collectors.toList());
    }
}
