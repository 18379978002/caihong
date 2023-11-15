package com.caipiao.modules.sys.service.impl;

import com.caipiao.modules.oss.cloud.OSSFactory;
import com.caipiao.modules.sys.service.RichTextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

/**
 * @author xiaoyinandan
 * @date 2021/12/26 下午1:04
 */
@Service
@Slf4j
public class RichTextServiceImpl implements RichTextService {
    @Autowired
    private OSSFactory ossFactory;

    @Value("${qiniu.domain}")
    private String domain;




    @Override
    public String transferRichText(String richText) throws Exception {

        if(richText.contains("url(&quot;https://img.xiumi.us")){
            String queryString = richText.substring(richText.indexOf("url(&quot;https://img.xiumi.us") + 10, richText.indexOf("&quot;);"));

            if(queryString.startsWith("https://img.xiumi.us")){

                //处理图片问题
                URL url = new URL(queryString);
                // 打开连接
                URLConnection con = url.openConnection();
                //设置请求超时为5s
                con.setConnectTimeout(5*1000);
                // 输入流
                InputStream is = con.getInputStream();

                String fileName = UUID.randomUUID().toString();

                String suffix = queryString.substring(queryString.lastIndexOf("."));

                ossFactory.build().upload(is, fileName + suffix);

                richText = richText.replaceAll(queryString, domain + fileName + suffix);

                log.debug("旧图片地址={}, 新图片地址={}",queryString,domain+fileName+suffix);

                transferRichText(richText);
            }


            return richText;
        }

        return richText;

    }
}
