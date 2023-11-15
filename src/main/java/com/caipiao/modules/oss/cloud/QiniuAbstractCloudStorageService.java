package com.caipiao.modules.oss.cloud;

import com.caipiao.common.exception.RRException;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.processing.OperationManager;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * jzcloud
 *
 * @author : xiaoyinandan
 * @date : 2021/4/26 11:46
 */
@Slf4j
public class QiniuAbstractCloudStorageService extends AbstractCloudStorageService {
    private UploadManager uploadManager;
    private OperationManager operationManager;
    private BucketManager bucketManager;
    private String token;
    private String videoToken;

    public QiniuAbstractCloudStorageService(CloudStorageConfig config) {
        this.config = config;

        //初始化
        init();
    }



    private void init() {
        uploadManager = new UploadManager(new Configuration(Region.autoRegion()));
        Auth auth = Auth.create(config.getAccessKey(), config.getSecretKey());
        operationManager = new OperationManager(auth, new Configuration(Region.autoRegion()));
        bucketManager = new BucketManager(auth, new Configuration(Region.autoRegion()));
        this.token = auth.uploadToken(config.getBucketName());
        this.videoToken = auth.uploadToken(config.getVideoBucket());
    }

    public String thumbnail400_300(String orgKey, String key) throws QiniuException {
        String fops = "imageView2/1/w/400/h/300/q/75";
        String saveasKey = config.getBucketName() + ":" + key;
        fops = fops + "|saveas/" + UrlSafeBase64.encodeToString(saveasKey);
        StringMap stringMap = new StringMap();
        fops = UrlSafeBase64.encodeToString(fops);
        String pfop = operationManager.pfop(config.getBucketName(), orgKey, fops, stringMap);

        return pfop;
    }

    @Override
    public CloudStorageConfig getConfig() {
        return this.config;
    }

    @Override
    public String upload(byte[] data, String path) {
        try {
            Response res = uploadManager.put(data, path, token);
            if (!res.isOK()) {
                throw new RuntimeException("上传七牛出错：" + res.toString());
            }
        } catch (Exception e) {
            throw new RRException("上传文件失败，请核对七牛配置信息", e);
        }

        return path;
    }

    @Override
    public String uploadCategory(byte[] data, String path) {
        try {
            Response res = uploadManager.put(data, path, token);
            if (!res.isOK()) {
                throw new RuntimeException("上传七牛出错：" + res.toString());
            }
        } catch (Exception e) {
            throw new RRException("上传文件失败，请核对七牛配置信息", e);
        }

        return path;
    }



    @Override
    public String upload(InputStream inputStream, String path) {
        try {
            byte[] data = IOUtils.toByteArray(inputStream);
            return this.upload(data, path);
        } catch (IOException e) {
            throw new RRException("上传文件失败", e);
        }
    }

    @Override
    public String uploadSuffix(byte[] data, String suffix) {
        return upload(data, getPath(config.getPrefix(), suffix));
    }

    @Override
    public String uploadSuffix(InputStream inputStream, String suffix) {
        return upload(inputStream, getPath(config.getPrefix(), suffix));
    }

    @Override
    public String getUploadToken() {
        return this.videoToken;
    }

    @Override
    public String getToken() {
        return this.token;
    }

    @Override
    public String getUpToken() {
        return this.token;
    }

    @Override
    public int delete(String fileName, String bucketName) {

        try {
            Response delete = bucketManager.delete(bucketName, fileName);
            return delete.statusCode;
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            ex.printStackTrace();
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }
        return -1;
    }
}
