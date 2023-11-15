package com.caipiao.modules.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xiaoyinandan
 * @date 2022/2/26 下午9:45
 */
@Data
public class BaseResult implements Serializable {
    private String errorCode;
    private String errorMessage;
    private Boolean success;
}
