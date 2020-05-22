package com.zyshuang.community.controller;

import com.zyshuang.community.dto.FileDTO;
import com.zyshuang.community.provide.AliyunOssProvide;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class FileController {

    @Autowired
    private AliyunOssProvide aliyunOssProvide;

    @RequestMapping("/file/upload")
    @ResponseBody
    public FileDTO upload(HttpServletRequest request) {
        MultipartHttpServletRequest multiparRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multiparRequest.getFile("editormd-image-file");
        try {
            String fileName = aliyunOssProvide.uploadObject2OSS(aliyunOssProvide.getOSSClient(),
                    file.getContentType(),
                    file.getInputStream(),
                    file.getOriginalFilename(),
                    file.getSize());
            FileDTO fileDTO = new FileDTO();
            fileDTO.setSuccess(1);
            fileDTO.setUrl(fileName);
            return fileDTO;
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileDTO fileDTO = new FileDTO();
        fileDTO.setSuccess(1);
        fileDTO.setUrl("/images/weixin.jpg");
        return fileDTO;
    }
}
