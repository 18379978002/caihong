package com.caipiao.modules.basketball.dto;

import com.caipiao.modules.common.dto.BaseResult;
import lombok.Data;

import java.io.Serializable;

/**
 * @author xiaoyinandan
 * @date 2022/2/26 下午9:46
 */
@Data
public class BasketballResultDTO extends BaseResult implements Serializable {
    private BasketballValue value;
}
