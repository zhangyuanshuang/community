package com.zyshuang.community.dto;

import com.zyshuang.community.exception.CustomerErrorCode;
import com.zyshuang.community.exception.CustomerException;
import lombok.Data;

/**
 *  不确定传入的都是同一个类型 用泛型
 */
@Data
public class ResultDTO<T> {

    private Integer code;

    private String message;

    private T data;

    private Boolean success;

    public static ResultDTO errorOf(Integer code , String message){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(code);
        resultDTO.setMessage(message);
        return resultDTO;
    }

    public static ResultDTO errorOf(CustomerErrorCode errorCode) {
        return errorOf(errorCode.getCode(),errorCode.getMessage());
    }

    public static ResultDTO errorOf(CustomerException e) {
        return errorOf(e.getCode(),e.getMessage());
    }

    public static ResultDTO okOf(){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(200);
        resultDTO.setMessage("请求成功");
        return resultDTO;
    }

    public static <T> ResultDTO okOf(T t){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(200);
        resultDTO.setMessage("请求成功");
        resultDTO.setData(t);
        return resultDTO;
    }

}
