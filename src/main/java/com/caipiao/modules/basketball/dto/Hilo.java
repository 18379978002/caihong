package com.caipiao.modules.basketball.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xiaoyinandan
 * @date 2022/2/26 下午8:56
 */
@Data
public class Hilo implements Serializable {
    private String l;
    private String h;
    private String goalLine;
}
