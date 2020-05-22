package com.zyshuang.community.provide;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import com.zyshuang.community.dto.AliyunOssDTO;
import com.zyshuang.community.exception.CustomerErrorCode;
import com.zyshuang.community.exception.CustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Component
public class AliyunOssProvide {

//    @Autowired
//    private final Logger LOG = LoggerFactory.getLogger(AliyunOssProvide.class);

    @Autowired
    AliyunOssDTO aliyunOssDTO;
    /**
     * 获取阿里云OSS客户端对象
     * */
    public OSS getOSSClient(){
        return new OSSClientBuilder().build(aliyunOssDTO.getEndpoint(),aliyunOssDTO.getAccessKeyId(), aliyunOssDTO.getAccessKeySecret());
    }
    /**
     * 新建Bucket  --Bucket权限:私有
     * @param bucketName bucket名称
     * @return true 新建Bucket成功
     * */
    public boolean createBucket(OSSClient client, String bucketName){
        Bucket bucket = client.createBucket(bucketName);
        return bucketName.equals(bucket.getName());
    }
    /**
     * 删除Bucket
     * @param bucketName bucket名称
     * */
    public void deleteBucket(OSSClient client, String bucketName){
        client.deleteBucket(bucketName);
    }

    /**
     * 向阿里云的OSS存储中存储文件  --file也可以用InputStream替代
     * @param client OSS客户端
     * @param contentType
     * @param is 上传文件
     * @param fileName 文件名
     * @param fileSize 文件大小
     * @return String 唯一MD5数字签名
     * */
    public String uploadObject2OSS(OSS client, String contentType, InputStream is, String fileName, Long fileSize) {
        String genarateStr = "";
        String[] filePaths = fileName.split("\\.");
        if (filePaths.length >1){
            genarateStr = UUID.randomUUID().toString() + "." + filePaths[filePaths.length - 1];
        }else {
            return null;
        }

        try {
            //创建上传Object的Metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(is.available());
            metadata.setCacheControl("no-cache");
            metadata.setHeader("Pragma", "no-cache");
            metadata.setContentEncoding("utf-8");
            metadata.setContentType(getContentType(contentType));
            metadata.setContentDisposition("filename/filesize=" + fileName + "/" + fileSize + "Byte.");
            //上传文件
            PutObjectResult putResult = client.putObject(aliyunOssDTO.getBucketName(), aliyunOssDTO.getFilePath() + genarateStr, is, metadata);
            is.close();
        } catch (Exception e) {
//            LOG.error("上传阿里云OSS服务器异常." + e.getMessage(), e);
        }

        // 生成URL
        URL url = client.generatePresignedUrl(aliyunOssDTO.getBucketName(),
                aliyunOssDTO.getFilePath()+genarateStr,
                new Date((new Date().getTime() + 24*60*60*365*10)));

        if (url != null) {
            return url.toString();
        }else {
            throw  new CustomerException(CustomerErrorCode.FILE_UPLOAD_FAIL);
        }
    }

    /**
     * 根据key获取OSS服务器上的文件输入流
     * @param client OSS客户端
     * @param bucketName bucket名称
     * @param filePath 文件路径
     * @param key Bucket下的文件的路径名+文件名
     */
    public InputStream getOSS2InputStream(OSSClient client, String bucketName, String filePath, String key){
        OSSObject ossObj = client.getObject(bucketName, filePath + key);
        return ossObj.getObjectContent();
    }

    /**
     * 根据key删除OSS服务器上的文件
     * @param client OSS客户端
     * @param bucketName bucket名称
     * @param filePath 文件路径
     * @param key Bucket下的文件的路径名+文件名
     */
    public void deleteFile(OSSClient client, String bucketName, String filePath, String key){
        client.deleteObject(bucketName, filePath+ key);
    //  LOG.info("删除" + bucketName + "下的文件" + filePath + key + "成功");
    }

    /**
     * 通过文件名判断并获取OSS服务文件上传时文件的contentType
     * @param fileName 文件名
     * @return 文件的contentType
     */
    public final String getContentType(String fileName){
        String fileExtension = fileName.substring(fileName.lastIndexOf(".")+1);
        if("bmp".equalsIgnoreCase(fileExtension)) return "image/bmp";
        if("gif".equalsIgnoreCase(fileExtension)) return "image/gif";
        if("jpeg".equalsIgnoreCase(fileExtension) || "jpg".equalsIgnoreCase(fileExtension)  ) return "image/jpeg";
        if("png".equalsIgnoreCase(fileExtension)) return "image/png";
        return "text/html";
    }

    /**
     * 销毁
     */
    public void destory(OSSClient client) {
        client.shutdown();
    }
}

