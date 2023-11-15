package com.caipiao.modules.oss.entity.req;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author xiaoyinandan
 * @date 2021/9/13 上午8:41
 */
@Data
public class SendSmsDTO implements Serializable {
    private String templateCode;
    private List<Long> groupIds;
}
