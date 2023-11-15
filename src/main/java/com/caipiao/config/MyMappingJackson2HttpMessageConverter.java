package com.caipiao.config;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaoyinandan
 * @date 2022/2/27 下午10:12
 */
public class MyMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
    public MyMappingJackson2HttpMessageConverter(){
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_PLAIN);
        mediaTypes.add(MediaType.valueOf("text/plain;charset=UTF-8"));
        mediaTypes.add(MediaType.TEXT_HTML);
        mediaTypes.add(MediaType.valueOf("text/html;charset=UTF-8"));
        setSupportedMediaTypes(mediaTypes);// tag6
    }

}
