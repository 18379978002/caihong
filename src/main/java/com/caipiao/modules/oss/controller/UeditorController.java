package com.caipiao.modules.oss.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caipiao.modules.oss.cloud.OSSFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * @author xiaoyinandan
 * @date 2021/7/27 4:15 下午
 */
@RestController
@RequestMapping("/ueditor")
public class UeditorController{

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UeditorController.class);

    @Autowired
    private OSSFactory ossFactory;

    @Autowired
    ResourceLoader resourceLoader;

    @RequestMapping("/getUeditorConfig")
    public void ueditorConfig(HttpServletRequest request, HttpServletResponse response, MultipartFile upfile) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        try {
            String exec = "";
            String actionType = request.getParameter("action");

            if ("uploadimage".equals(actionType) && !upfile.isEmpty()) {
                //上传图片,此处可自行选择配置到七牛云还是阿里oss
                exec = uploadImage(upfile);
            } else if (actionType.equals("config")) {
                exec = this.getConfigData("config.json");
            }else if (actionType.equals("catchimage")) {
                String[] sources = request.getParameterValues("source[]");
                exec = uploadImages(sources);
            } else {
                //此处请自行选择
                //exec = new ActionEnter(request, "").exec();
            }
            PrintWriter writer = response.getWriter();
            writer.write(exec);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            logger.error("ueditor exception:", e);
        }
    }

    private String uploadImages(String[] sources) throws IOException {

        Map<String, Object> mp = new HashMap<>();

        List<JSONObject> ls = new ArrayList<>();

        for(String source : sources){
            String ori = source;

            //url先去调？后面的内容
            int endIndex = source.lastIndexOf("?");
            if(-1 != endIndex){
                source = source.substring(0, endIndex);
            }


            URL url = new URL(source);
            // 打开连接
            URLConnection con = url.openConnection();
            //设置请求超时为5s
            con.setConnectTimeout(5*1000);
            // 输入流
            InputStream is = con.getInputStream();

            String fileName = source.substring(source.lastIndexOf("/")+1);
            String extraName = fileName.substring(fileName.lastIndexOf("."));

            String suffixName = "";
            if (fileName.indexOf(".") > 0) {
                suffixName = fileName.substring(fileName.lastIndexOf("."));
            }
            String fileKey = (UUID.randomUUID().toString().replace("-", "") + suffixName).toLowerCase();

            ossFactory.build().upload(is, fileKey);

            JSONObject jsonResult = new JSONObject(resultMap("SUCCESS", fileKey, 0, fileName, fileKey, extraName));
            jsonResult.put("source", ori);
            ls.add(jsonResult);
        }
        mp.put("list", ls);
        mp.put("state", "SUCCESS");
        return JSON.toJSONString(mp);


    }

    private String uploadImage(MultipartFile file) {
        JSONObject jsonResult = null;
        try {
            String fileName = file.getOriginalFilename();
            String extraName = fileName.substring(fileName.lastIndexOf("."));

            String suffixName = "";
            if (fileName.indexOf(".") > 0) {
                suffixName = fileName.substring(fileName.lastIndexOf("."));
            }
            String fileKey = (UUID.randomUUID().toString().replace("-", "") + suffixName).toLowerCase();
            ossFactory.build().upload(file.getBytes(), fileKey);
            jsonResult = new JSONObject(resultMap("SUCCESS", fileKey, file.getSize(), fileName, fileKey, extraName));
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult = new JSONObject(resultMap("文件上传失败", "", 0, "", "", ""));
        }
        return jsonResult.toString();
    }

    private Map<String, Object> resultMap(String state, String url, long size, String title, String original,
                                          String type) {
        Map<String, Object> result = new HashMap<>();
        result.put("state", state);
        result.put("original", original);
        result.put("size", size);
        result.put("title", title);
        result.put("type", type);
        result.put("url", url);
        return result;
    }

    private String getConfigData(String resouceName) throws Exception {
        try {
            Resource resource = resourceLoader.getResource("classpath:" + resouceName);
            InputStream is = resource.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String rs = "";
            String data = "";
            while ((data = br.readLine()) != null) {
                rs += data;
            }
            br.close();
            isr.close();
            is.close();
            rs = rs.replaceAll("/\\*{1,2}[\\s\\S]*?\\*/", "");// 解决去除多行注释
            return rs;
        } catch (Exception e) {
            throw e;
        }
    }
}
