package com.caipiao.modules.oss.service;

import com.caipiao.common.utils.CloudFileUtil;
import com.caipiao.modules.oss.entity.MinioEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

public interface IMinioService {
    String saveUploadFile(MultipartFile multipartFile, String uploadTempPath, String fileName, String bucket, MinioEntity entity);
    InputStream downloadFile(String filePath);
    boolean deleteFile(String filePath);
    MinioEntity uploadFileToMinio(MultipartFile multipartFile, String uploadTempPath, String bucket);
    MinioEntity uploadFileToMinio(File is, String originalFileName, String size);
    public MinioEntity uploadToMinioFile(File file);
    MinioEntity uploadToMinioFile(MultipartFile multipartFile);
}
