package com.caipiao.common.utils;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AesUtils {

    // 密钥
    @Value("${mybatis-plus.aes.key}")
    public String key;

    // 偏移量
    private static int offset = 16;
    private static String transformation = "AES/CBC/PKCS5Padding";
    private static String algorithm = "AES";
    private static String charset = "utf-8";

    private static AesUtils aesUtils;

    @PostConstruct
    public void init(){
        aesUtils = this;
    }

    /**
     * 加密
     *
     * @param content
     * @return
     */
    public static String encrypt(String content) {
        return encrypt(content, aesUtils.key);
    }

    /**
     * 解密
     *
     * @param content
     * @return
     */
    public static String decrypt(String content) {
        return decrypt(content, aesUtils.key);
    }

    /**
     * 加密
     *
     * @param content 需要加密的内容
     * @param key     加密密码
     * @return
     */
    public static String encrypt(String content, String key) {
        try {
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), algorithm);
            IvParameterSpec iv = new IvParameterSpec(key.getBytes(), 0, offset);
            Cipher cipher = Cipher.getInstance(transformation);
            byte[] byteContent = content.getBytes(charset);
            cipher.init(Cipher.ENCRYPT_MODE, skey, iv);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return new Base64().encodeToString(result); // 加密
        } catch (Exception e) {
            // LogUtil.exception(e);
        }
        return null;
    }

    /**
     * AES（256）解密
     *
     * @param content 待解密内容
     * @param key     解密密钥
     * @return 解密之后
     * @throws Exception
     */
    public static String decrypt(String content, String key) {
        try {

            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), algorithm);
            IvParameterSpec iv = new IvParameterSpec(key.getBytes(), 0, offset);
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, skey, iv);// 初始化
            byte[] result = cipher.doFinal(new Base64().decode(content));
            return new String(result); // 解密
        } catch (Exception e) {
            //LogUtil.exception(e);
        }
        return null;
    }
}
