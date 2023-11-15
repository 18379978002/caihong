package com.caipiao.modules.oss.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.caipiao.common.annotation.AnonymousAccess;
import com.caipiao.common.exception.RRException;
import com.caipiao.common.utils.CloudFileUtil;
import com.caipiao.common.utils.ImageUtil;
import com.caipiao.common.utils.R;
import com.caipiao.modules.oss.entity.MinioEntity;
import com.caipiao.modules.oss.service.IMinioService;
import com.caipiao.modules.oss.service.MinioFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Base64Util;
import org.hibernate.validator.internal.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;

import static com.caipiao.common.utils.R.error;
import static com.caipiao.common.utils.R.ok;


@Slf4j
@RestController
@RequestMapping("app/minio")
@Api(tags = "文件存储管理")
public class MinioFileController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IMinioService attachComponent;

    @Resource
    MinioFileService minioFileService;

    @Autowired
    HttpServletResponse response;

    /**
     * showdoc
     *
     * @catalog 系统/文件
     * @title 图片查看
     * @description 查看图片
     * @method get
     * @url https://{{domain}}/caipiao-api/app/minio/showImage
     * @param filePath 必选 string 文件于minio路径
     * @return 图片二进制
     * @return_param data byte[] 这是一个图片二进制资源返回
     * @remark 查看图片二进制资源,此接口作为前端展示图片使用
     * @number 0
     */
    @ApiOperation(value = "展示图片或文件", notes = "展示图片或文件")
    @GetMapping(value = "/showImage")
    @AnonymousAccess
    public void showImage(@RequestParam("filePath") String filePath) {
        response.setContentType("image/jpeg");
        try (InputStream inputStream = attachComponent.downloadFile(filePath);
             ServletOutputStream outputStream = response.getOutputStream()){
            int len;
            byte[] buffer = new byte[4096];
            while ((len = inputStream.read(buffer)) != -1){
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        } catch (IOException e) {
            logger.error("", e);
        }
    }


    @ApiOperation(value = "展示图片或文件", notes = "展示图片或文件")
    @GetMapping(value = "/showImageBase64")
    @AnonymousAccess
    public R showImageBase64(@RequestParam("filePath") String filePath) {
        byte[] imgBytes = IoUtil.readBytes(attachComponent.downloadFile(filePath));
        return new R().put("data:image/png;base64,"+Base64.encode(imgBytes));
    }


    @ApiOperation(value = "删除图片或文件", notes = "删除图片或文件")
    @DeleteMapping(value = "/delete")
    @AnonymousAccess
    public R deleteImage(@RequestParam("fileId") String fileId) {
        try {
            MinioEntity ruWfUpload = this.validRequestParam(fileId);
            attachComponent.deleteFile(ruWfUpload.getFilePath());
            minioFileService.removeById(fileId);
            return ok();
        } catch (Exception e) {
            logger.error("", e);
            return error(e.getMessage());
        }
    }

    @ApiOperation(value = "下载图片或文件", notes = "下载图片或文件")
    @GetMapping(value = "/downloadFile")
    @AnonymousAccess
    public void downloadFile(@RequestParam("fileId") String fileId) {
        OutputStream out = null;
        BufferedInputStream is = null;
        try {
            response.setCharacterEncoding("UTF-8");
            MinioEntity ruWfUpload = this.validRequestParam(fileId);
            response.reset();
            response.setContentType("application/x-download");
            response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(ruWfUpload.getFileName(),"UTF-8"));
            response.setHeader("Content-Length", String.valueOf(ruWfUpload.getFileSize()));

            out = response.getOutputStream();
            is = new BufferedInputStream(this.attachComponent.downloadFile(ruWfUpload.getFilePath()));

            byte[] b = new  byte[4096];
            int len;
            while((len= is.read(b)) != -1) {
                out.write(b,0,len);
            }

        } catch (Exception e) {
            log.info("{}", e);

        } finally {
            try {

                out.flush();

                if(is!=null)

                    is.close();

                if(out!=null)

                    out.close();

            } catch (IOException e) {

                e.printStackTrace();

            }
        }

    }

    /**
     * showdoc
     *
     * @catalog 系统/文件
     * @title 查看文件信息
     * @description 查看文件信息
     * @method get
     * @url https://{{domain}}/caipiao-api/app/minio/fileinfo
     * @param fileId 必选 string 文件Id
     * @return {"msg": "success","code": 200,"data": {"id": "5337321f-cb5f-4215-9ea7-37eb5e422470","fileName": "图片 1.png","fileSize": "60450","filePath": "5/1/5337321f-cb5f-4215-9ea7-37eb5e422470.png","createTime": "2023-03-30 21:28:14","companyId": "10000","fileId": null}}
     * @return_param filePath string 图片路径
     * @return_param id string 图片id
     * @return_param fileName string 图片名称
     * @remark 上传图片或文件,此系统部分接口需要文件根据需要来取文件id 或者 filePath
     * @number 3
     */
    @ApiOperation(value = "查看文件信息", notes = "查看文件信息")
    @GetMapping(value = "/fileinfo")
    @AnonymousAccess
    public R fileInfo(@RequestParam("fileId") String fileId) {
        return new R().put(this.validRequestParam(fileId));
    }

    /**
     * showdoc
     *
     * @catalog 系统/文件
     * @title 上传图片或文件
     * @description 上传图片或文件
     * @method Post
     * @url https://{{domain}}/caipiao-api/app/minio/uploadFile
     * @param file 必选 string 需要上传的图片二进制
     * @return {"msg": "success","code": 200,"data": {"id": "5337321f-cb5f-4215-9ea7-37eb5e422470","fileName": "图片 1.png","fileSize": "60450","filePath": "5/1/5337321f-cb5f-4215-9ea7-37eb5e422470.png","createTime": null,"companyId": null,"fileId": null}}
     * @return_param filePath string 图片路径
     * @return_param id string 图片id
     * @return_param fileName string 图片名称
     * @remark 上传图片或文件,此系统部分接口需要文件根据需要来取文件id 或者 filePath
     * @number 2
     */
    @ApiOperation(value = "上传图片或文件", notes = "上传图片或文件")
    @PostMapping(value = "/uploadFile")
    @AnonymousAccess
    public R uploadFile(@RequestParam(value = "file") MultipartFile multipartFile) {

        if (multipartFile == null) {
            return error(-1, "参数缺失");
        }

        try {
            MinioEntity entity = attachComponent.uploadToMinioFile(multipartFile);
            JSONObject object = new JSONObject();
            object.put("filePath", entity.getFilePath());
            object.put("entity", entity);
            return ok().put(object);
        } catch (Exception e) {
            return error(-1, e.getMessage());
        }
    }
    /**
     * showdoc
     *
     * @catalog 系统/文件
     * @title 上传头像
     * @description 上传头像
     * @method Post
     * @url https://{{domain}}/caipiao-api/app/minio/uploadAvatar
     * @param file 必选 string 需要上传的图片二进制
     * @return {"msg": "success","code": 200,"data": {"id": "5337321f-cb5f-4215-9ea7-37eb5e422470","fileName": "图片 1.png","fileSize": "60450","filePath": "5/1/5337321f-cb5f-4215-9ea7-37eb5e422470.png","createTime": null,"companyId": null,"fileId": null}}
     * @return_param filePath string 图片路径
     * @return_param id string 图片id
     * @return_param fileName string 图片名称
     * @remark 上传图片或文件,此系统部分接口需要文件根据需要来取文件id 或者 filePath
     * @number 2
     */
    @ApiOperation(value = "上传头像", notes = "上传头像")
    @PostMapping(value = "/uploadAvatar")
    @AnonymousAccess
    public R uploadAvatar(@RequestParam(value = "file") MultipartFile multipartFile) {

        if (multipartFile == null) {
            return error(-1, "参数缺失");
        }

        //将图片裁剪成400*400
        try (InputStream inputStream = multipartFile.getInputStream()){
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            BufferedImage sque = ImageUtil.getSque(bufferedImage);
            BufferedImage bufferedImage1 = ImageUtil.reSize(sque, 450, 450, true);
            String fileExtension = FileUtil.extName(multipartFile.getOriginalFilename());
            String newImage = IdUtil.getSnowflake(1,1).nextIdStr() + "." + fileExtension;
            if(!new File("./imgCache/").exists()){
                new File("./imgCache/").mkdirs();
            }
            ImageUtil.writeImageLocal("./imgCache/" + newImage, bufferedImage1);
            return ok().put(attachComponent.uploadToMinioFile(new File("./imgCache/" + newImage)));
        } catch (Exception e) {
            return error(-1, e.getMessage());
        }
    }

    private MinioEntity validRequestParam(String fileId) {
        if (StringHelper.isNullOrEmptyString(fileId)) {
            throw new RRException("查询参数不能为空");
        }

        MinioEntity entity = minioFileService.getById(fileId);
        if (null == entity || StringHelper.isNullOrEmptyString(entity.getFilePath())) {
            throw new RRException("文件不存在");
        }

        return entity;
    }

}
