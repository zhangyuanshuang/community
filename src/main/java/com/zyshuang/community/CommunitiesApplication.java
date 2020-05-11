package com.zyshuang.community;

import com.zyshuang.community.advice.CustomerExceptionHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com/zyshuang/community/mapper")
public class CommunitiesApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunitiesApplication.class, args);
    }

}
