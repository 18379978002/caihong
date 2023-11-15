package com.caipiao.modules.company.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caipiao.modules.company.entity.enmu.AuthenticationStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("tb_shop")
@ApiModel("店铺")
public class Shop implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
     * id
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * 店铺名称
     */
    @ApiModelProperty("店铺名称")
    private String shopName;

    /**
     * 店铺电话
     */
    @ApiModelProperty("店铺电话")
    private String shopPhone;

    /**
     * 店主姓名
     */
    @ApiModelProperty("店主姓名")
    private String shopHost;

    /**
     * 店铺微信
     */
    @ApiModelProperty("店铺微信")
    private String wx;

    /**
     * 店铺地址
     */
    @ApiModelProperty("店铺地址")
    private String shopAddress;

    /**
     * 店铺公告
     */
    @ApiModelProperty("店铺公告")
    private String shopAffiche;

    /**
     * 逻辑删除
     */
    @ApiModelProperty("逻辑删除")
    private String delFlag;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 最后更新时间
     */
    @ApiModelProperty("最后更新时间")
    private Date updateTime;

    /**
     * 店长id
     */
    @ApiModelProperty("店长id")
    private String staffId;

    /**
     * 营业执照
     */
    @ApiModelProperty("营业执照")
    private String businessLicense;

    /**
     * 店长身份证号
     */
    @ApiModelProperty("店长身份证号")
    private String idcard;

    /**
     * 法人身份证正面
     */
    @ApiModelProperty("法人身份证正面")
    private String idcardZ;

    /**
     * 法人身份证反面
     */
    @ApiModelProperty("法人身份证反面")
    private String idcardF;

    /**
     * 代销证
     */
    @ApiModelProperty("代销证")
    private String dxz;

    /**
     * 手持代销证
     */
    @ApiModelProperty("手持代销证")
    private String handDxz;

    /**
     * 手持身份证
     */
    @ApiModelProperty("手持身份证")
    private String handIdcard;

    /**
     * 店内照片
     */
    @ApiModelProperty("店内照片")
    private String inshopPicture;

    /**
     * 店铺门头照
     */
    @ApiModelProperty("店铺门头照")
    private String shopheadPicture;

    /**
     * 店铺状态 1 运营中 2 违规封停
     */
    @ApiModelProperty("店铺状态")
    private String shopStatus;

    /**
     * 认证状态 1 已认证 2 未认证 3 未通过
     */
    @ApiModelProperty("认证状态")
    private AuthenticationStatus authenticationStatus;

    /**
     * 未通过原因
     */
    @ApiModelProperty("未通过原因")
    private String reason;

    /**
     *  店铺余额
     */
    @ApiModelProperty("店铺余额")
    private BigDecimal balance;
}
