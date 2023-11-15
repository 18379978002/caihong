package com.caipiao.common.utils;

import cn.hutool.core.util.RandomUtil;
import com.caipiao.modules.app.entity.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class AES {
    private static final Logger logger = LoggerFactory.getLogger(AES.class);

    //偏移量
//    public static final String iv = "Rainbow902884$%#";

    public static String encrypt(String content, String key, String encryptIv) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] raw = key.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            IvParameterSpec iv = new IvParameterSpec(encryptIv.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return new BASE64Encoder().encode(encrypted);
        }catch (Exception e){

            return null;
        }
    }

    public static String decrypt(String content, String key, String encryptIv)  {
        if (content.length() < 1) {
            return null;
        }
        try {
            byte[] raw = key.getBytes(StandardCharsets.US_ASCII);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(encryptIv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(content);
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception e) {

            return null;
        }
    }

    /**
     * 从用户信息里面获取aes加密key
     * @param userInfo
     * @return
     */
    public static String getAesKey(UserInfo userInfo){
        String key = userInfo.getId().substring(0, 4) +
                "*" + userInfo.getPhone().substring(4) +
                "&" + userInfo.getId().substring(4,7);

        //判断key的长度是否为16，超过16则截取前16位
        if(key.length()>16){
            return key.substring(0,16);
        }

        //判断key的长度是否为16，小于16则补1
        if(key.length() < 16){
            int sub = 16 - key.length();
            return key + RandomUtil.randomString("1", sub);
        }

        return key;
    }


    public static void main(String[] args) {

        UserInfo info = new UserInfo();
        info.setId("125456222332123321254");
        info.setPhone("18601754726");

        System.out.println(RandomUtil.randomString(16));

        System.out.println(getAesKey(info).length());
        System.out.println(getAesKey(info));

        String encrypt = encrypt("{\"key\":123}", "dss0uxmagg6drrda", "h6zue1kx70qc0d5h");
        System.out.println(encrypt);
        System.out.println(decrypt("nUsKTpL35QPYvr5UTg9ylw==", "dss0uxmagg6drrda", "h6zue1kx70qc0d5h"));
    }


}
