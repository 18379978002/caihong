package com.caipiao.modules.app.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caipiao.modules.order.entity.enmu.UserType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author xiaoyinandan
 * @date 2022/2/7 下午7:16
 */
@Data
@ApiModel("app用户")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    public static String COL_InvitationCode="invitation_code";

    @TableId(type = IdType.ASSIGN_ID)
    /**
     * pk
     */
    @ApiModelProperty("pk")
    private String id;


    @TableField(exist = false)
    @ApiModelProperty("等级对象")
    private GradeInfo gradeInfo;

    @TableField(exist = false)
    @ApiModelProperty("用户等级")
    private String userGradeName;
    @ApiModelProperty("分管代理ID")
    private String manageStaffId;

    /**
     * 逻辑删除标记（0：显示；1：隐藏）
     */
    @ApiModelProperty("逻辑删除标记（0：显示；1：隐藏）")
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
     * 用户编码
     */
    @ApiModelProperty("用户编码")
    private Integer userCode;

    /**
     * 手机号码
     */
    @ApiModelProperty("手机号码")
    private String phone;

    /**
     * 密码
     */
    @ApiModelProperty("密码")
    private String password;

    /**
     * 用户等级
     */
    @ApiModelProperty("用户等级")
    private Long userGrade;

    /**
     * 总余额
     */
    @ApiModelProperty("总余额")
    private BigDecimal balance;

    /**
     * 充值余额
     */
    @ApiModelProperty("充值余额")
    private BigDecimal bonus;

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

    /**
     * 自己的邀请码
     */
    @ApiModelProperty("自己的邀请码")
    private String invitationCode;

    /**
     * 上级邀请码
     */
    @ApiModelProperty("上级邀请码")
    private String parentInvitationCode;

    /**
     * 真实姓名
     */
    @ApiModelProperty("真实姓名")
    private String realName;

    /**
     * 身份证号码
     */
    @ApiModelProperty("身份证号码")
    private String idcard;

    /**
     * 支付密码
     */
    @ApiModelProperty("支付密码")
    private String payPassword;
    @ApiModelProperty("累计中奖")
    private BigDecimal totalBonus;
    @ApiModelProperty("今日中奖")
    private BigDecimal todayBonus;
    @ApiModelProperty("关注数")
    private Integer attentionNum;
    @ApiModelProperty("粉丝数")
    private Integer fansNum;
    @ApiModelProperty("带红人次")
    private Integer redFansNum;

    @ApiModelProperty("用户类型")
    @TableField
    private UserType userType;

    @ApiModelProperty("从属店铺")
    private String subordinateStore;

    @ApiModelProperty("支付宝账号")
    private String alipayAccount;

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public UserInfo() {

    }
}
