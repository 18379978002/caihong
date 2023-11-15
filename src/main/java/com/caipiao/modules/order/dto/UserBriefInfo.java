package com.caipiao.modules.order.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserBriefInfo {

    private String userId;

    @ApiModelProperty("关注数")
    private Integer attentionNum;
    @ApiModelProperty("粉丝数")
    private Integer fansNum;
    @ApiModelProperty("带红人次")
    private Integer redFansNum;
    /**
     * 昵称
     */
    @ApiModelProperty("昵称")
    private String nickName;

    /**
     * 头像
     */
    @ApiModelProperty("头像")
    private String headimgUrl;
    @ApiModelProperty("是否是自己")
    private boolean isSelf;
    @ApiModelProperty("是否已关注")
    private boolean isAttention;
}
