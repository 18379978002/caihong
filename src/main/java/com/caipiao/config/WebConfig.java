package com.caipiao.config;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import com.caipiao.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configuration
public class WebConfig extends WebMvcConfigurationSupport {
//    @Autowired
//    AppLoginInterceptor appLoginInterceptor;
    @Value("${spring.jackson.date-format:yyyy-MM-dd HH:mm:ss}")
    private String pattern;
    static final String ORIGINS[] = new String[] { "GET", "POST", "PUT", "DELETE" };
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowCredentials(true).allowedMethods(ORIGINS)
                .maxAge(3600);
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(mappingJackson2HttpMessageConverter());
        StringHttpMessageConverter converter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        converters.add(converter);
    }

    @Bean
    public Snowflake snowflake(){
        return IdUtil.getSnowflake(1,1);
    }

    @Bean
    public LocalDateTimeSerializer localDateTimeSerializer() {
        return new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(pattern));
    }

    // localDateTime 反序列化器
    @Bean
    public LocalDateTimeDeserializer localDateTimeDeserializer() {
        return new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(pattern));
    }

   @Bean
   public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
       MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
       List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();
       supportedMediaTypes.add(MediaType.valueOf(MediaType.APPLICATION_JSON_UTF8_VALUE));
       //supportedMediaTypes.add(MediaType.valueOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8"));
       // 避免IE执行AJAX时，返回JSON出现下载文件
       supportedMediaTypes.add(MediaType.valueOf(MediaType.TEXT_XML_VALUE + ";charset=UTF-8"));
       supportedMediaTypes.add(MediaType.valueOf(MediaType.TEXT_XML_VALUE));
       supportedMediaTypes.add(MediaType.valueOf(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"));
       supportedMediaTypes.add(MediaType.valueOf(MediaType.TEXT_XML_VALUE + ";charset=UTF-8"));
       //converter.setSupportedMediaTypes(supportedMediaTypes);

       //   时间格式转换
       SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtils.DATE_TIME_PATTERN);
       ObjectMapper objectMapper = new ObjectMapper();
       objectMapper.setDateFormat(dateFormat);

       objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

       SimpleModule simpleModule = new SimpleModule();
       simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
       simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
//       simpleModule.addSerializer(LocalDateTime.class, localDateTimeSerializer());
//       simpleModule.addDeserializer(LocalDateTime.class,localDateTimeDeserializer());
       simpleModule.addDeserializer(Date.class, new DateDeserializer());

       objectMapper.registerModule(simpleModule);
       objectMapper.registerModule(new JavaTimeModule());
       converter.setObjectMapper(objectMapper);
       return converter;
   }


}
