package com.zyshuang.community.communities.service;

import com.zyshuang.community.communities.entities.User;
import com.zyshuang.community.communities.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @GetMapping
    public void createOrUpdate(User user) {
       User dbUser =  userMapper.findByAccountId(user.getAccountId());
       //查询数据库是否存在该用户 没有就插入  有就更新
       if (dbUser == null){
           //插入
           userMapper.insert(user);
       }else {
           //更新
           dbUser.setGmtModified(System.currentTimeMillis());
           dbUser.setAvatarUrl(user.getAvatarUrl());
           dbUser.setToken(user.getToken());
           dbUser.setName(user.getName());
           userMapper.update(dbUser);
       }
    }
}
