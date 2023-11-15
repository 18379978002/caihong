package com.caipiao.modules.sys.service;

import java.net.MalformedURLException;

/**
 * @author xiaoyinandan
 * @date 2021/12/26 下午1:04
 */
public interface RichTextService {
    /**
     * 转换富文本
     * @param richText
     * @return
     */
    String transferRichText(String richText) throws MalformedURLException, Exception;
}
