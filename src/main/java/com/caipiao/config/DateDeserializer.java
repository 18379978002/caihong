package com.caipiao.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.caipiao.common.utils.DatePatternUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Date;

/**
 * @author: create by xiaoyinandan
 * @version: v1.0
 * @description:
 *
 *  java.util.Date类型反序列化配置
 *
 * @date: 2020/9/23 14:02
 */
@Slf4j
public class DateDeserializer extends StdDeserializer<Date> {

    /**
     * 构造方法
     *
     */
    public DateDeserializer() {
        this(null);
    }

    /**
     * 构造方法
     *
     * @param vc Class
     */
    public DateDeserializer(Class<?> vc) {
        super(vc);
    }

    /**
     * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser,
     *      com.fasterxml.jackson.databind.DeserializationContext)
     */
    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        log.debug("反序列化前端时间: {}", p.getValueAsString());
        return DatePatternUtil.getPatternDate(p.getValueAsString());
    }

}
