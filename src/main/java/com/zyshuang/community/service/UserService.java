package com.zyshuang.community.service;

import com.zyshuang.community.entities.User;
import com.zyshuang.community.entities.UserExample;
import com.zyshuang.community.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @GetMapping
    public void createOrUpdate(User user) {
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andAccountIdEqualTo(user.getAccountId());
        List<User> users = userMapper.selectByExample(userExample);
        //查询数据库是否存在该用户 没有就插入  有就更新
        if (users.size() == 0) {
            //插入
            userMapper.insert(user);
        } else {
            User dbUser = users.get(0);
            dbUser.setGmtModified(System.currentTimeMillis());
            dbUser.setAvatarUrl(user.getAvatarUrl());
            dbUser.setToken(user.getToken());
            dbUser.setName(user.getName());

            User upDateUser = new User();
            upDateUser.setGmtModified(System.currentTimeMillis());
            upDateUser.setAvatarUrl(user.getAvatarUrl());
            upDateUser.setToken(user.getToken());
            upDateUser.setName(user.getName());

            UserExample example = new UserExample();
            example.createCriteria()
                    .andIdEqualTo(dbUser.getId());
            userMapper.updateByExampleSelective(upDateUser, example);
        }
    }
}
