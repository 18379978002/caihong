package com.caipiao.modules.basketball.dto;

import com.caipiao.modules.common.entity.MatchResult;
import lombok.Data;

import java.io.Serializable;

/**
 * @author xiaoyinandan
 * @date 2022/2/27 上午10:51
 */
@Data
public class BasketballDetailValue implements Serializable {

    private MatchResult matchResult;
}
