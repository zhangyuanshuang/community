package com.zyshuang.community.advice;


import com.alibaba.fastjson.JSON;
import com.zyshuang.community.dto.ResultDTO;
import com.zyshuang.community.exception.CustomerErrorCode;
import com.zyshuang.community.exception.CustomerException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice
public class CustomerExceptionHandler {

    @ExceptionHandler(Exception.class)
    ModelAndView handle(Throwable e, Model model,
                  HttpServletRequest request,
                  HttpServletResponse response) {

        String contentType = request.getContentType();
        if ("application/json".equals(contentType)) {
            ResultDTO resultDTO;
            // 返回 JSON
            if (e instanceof CustomerException) {
                resultDTO = ResultDTO.errorOf((CustomerException) e);
            } else {
                resultDTO = ResultDTO.errorOf(CustomerErrorCode.SYS_ERROR);
            }
            try {
                response.setContentType("application/json");
                response.setStatus(200);
                response.setCharacterEncoding("utf-8");
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            } catch (IOException ioe) {
            }

            return null;
        } else {
            // 错误页面跳转
            if (e instanceof CustomerException) {
                model.addAttribute("message", e.getMessage());
            } else {
                model.addAttribute("message", CustomerErrorCode.SYS_ERROR.getMessage());
            }
            System.out.println("执行了..........................................");
            return new ModelAndView("error");
        }

    }

}