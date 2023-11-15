package com.caipiao.modules.oss.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.RandomUtil;
import com.caipiao.common.utils.CloudFileUtil;
import com.caipiao.modules.oss.entity.MinioEntity;
import com.caipiao.modules.oss.service.IMinioService;
import com.caipiao.modules.oss.service.MinioFileService;
import com.google.common.io.Files;
import io.minio.MinioClient;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Component
@ConfigurationProperties(prefix = "custom.attach.minio")
@ConditionalOnProperty(name = "custom.attach.active", havingValue = "minio")
public class MinioServiceImpl implements IMinioService {
    @Setter
    @Getter
    private String bucket;
    @Setter
    @Getter
    private String host;
    @Setter
    @Getter
    private String accessKey;
    @Setter
    @Getter
    private String secretKey;

    @Autowired
    MinioFileService minioFileService;

    @Override
    public MinioEntity uploadFileToMinio(MultipartFile multipartFile, String uploadTempPath, String bucket) {
        String uuid = UUID.randomUUID().toString();
        String  fileName=uuid +"." + Files.getFileExtension(multipartFile.getOriginalFilename());
        //上传图片的路径名称
        MinioEntity entity = new MinioEntity();
        entity.setId(uuid);
        entity.setFileName(multipartFile.getOriginalFilename());
        entity.setFileSize(multipartFile.getSize()+"");
        String filepath = null;
        try {
            filepath=this.saveUploadFile(multipartFile.getInputStream(), uploadTempPath, fileName, bucket, entity);
        }catch (Exception e){

        }
        // 插入ru_wf_upload表

        entity.setFilePath(filepath);
        minioFileService.save(entity);
        return entity;
    }

    @Override
    public MinioEntity uploadFileToMinio(File is, String originalFileName,String size) {
        String uuid = UUID.randomUUID().toString();
        String  fileName=uuid +"." + Files.getFileExtension(is.getName());
        //上传图片的路径名称
        MinioEntity entity = new MinioEntity();
        entity.setId(uuid);
        entity.setFileName(originalFileName);
        entity.setFileSize(size);
        String filepath = this.saveUploadFile(IoUtil.toStream(is), RandomUtil.randomNumbers(3) + "/" + RandomUtil.randomNumbers(2), fileName, bucket, entity);
        // 插入ru_wf_upload表
        entity.setFilePath(filepath);
        minioFileService.save(entity);
        return entity;
    }

    @Override
    public MinioEntity uploadToMinioFile(MultipartFile multipartFile) {
        String uploadTempPath = CloudFileUtil.makePath(multipartFile.getOriginalFilename());
        return uploadFileToMinio(multipartFile, uploadTempPath.substring(0, uploadTempPath.length() - 1), null);
    }

    public MinioEntity uploadToMinioFile(File file) {
        String uploadTempPath = CloudFileUtil.makePath(file.getName());
        return uploadFileToMinio(file, uploadTempPath.substring(0, uploadTempPath.length() - 1), null);
    }


    /**
     * 保存上传附件,如果同名会直接覆盖
     *
     * @param multipartFile  file
     * @param uploadTempPath enterprise-auth/   参考TenantController上传企业审核附件用法，注意前面不用加/, 可多级目录
     *                       说明：根目录的下一级目录,用于区分不同类型（企业认证，个人认证等） 配置文件里面配置的根目录 + /enterprise-auth/
     * @return
     */
    public String saveUploadFile(MultipartFile multipartFile, String uploadTempPath, String fileName, String bucketParam, MinioEntity entity) {
        try {
            return this.saveUploadFile(multipartFile.getInputStream(), uploadTempPath, fileName, bucketParam, entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String saveUploadFile(InputStream is, String uploadTempPath, String fileName, String bucketParam, MinioEntity entity) {
        String path = null;
        try {
            String filePath = uploadTempPath + "/"+fileName;
            MinioClient minioClient = new MinioClient(host, accessKey, secretKey);
            // Check if the bucket already exists.
            String bucketResult = StringUtils.isBlank(bucketParam) ? bucket : bucketParam;
            boolean isExist = minioClient.bucketExists(bucketResult);
            if (!isExist) {
                minioClient.makeBucket(bucketResult);
            }
            minioClient.putObject(bucketResult, filePath, is, null,null, null, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            path = filePath;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("文件上传失败:" + e.getMessage());
        }
        return path;
    }




    /**
     * 下载文件，返回流
     *
     * @param filePath
     * @return
     */
    public InputStream downloadFile(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            throw new RuntimeException("路径参数错误");
        }
        try {
            MinioClient minioClient = new MinioClient(host, accessKey, secretKey);
            return minioClient.getObject(bucket, filePath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("下载文件失败:" + e.getMessage());
        }
    }


    /**
     * 删除文件（递归删除）
     *
     * @param filePath
     */
    public boolean deleteFile(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            throw new RuntimeException("路径参数错误");
        }
        try {
            MinioClient minioClient = new MinioClient(host, accessKey, secretKey);
            minioClient.removeObject(bucket, filePath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("删除文件失败:" + e.getMessage());
        }
        return true;
    }
}
