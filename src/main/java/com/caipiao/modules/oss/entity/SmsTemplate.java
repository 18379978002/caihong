package com.caipiao.modules.oss.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caipiao.common.validator.annotation.FlagValidator;
import com.caipiao.common.validator.group.AddGroup;
import com.caipiao.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * Description: jz-cloud
 * Created by yj198 on 2021/4/10 20:19
 */
@Data
@TableName("sms_template")
public class SmsTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.INPUT)
    /**
     * template_code
     */
    @NotBlank(message = "模板代码不能为空", groups = UpdateGroup.class)
    private String templateCode;

    /**
     * 短信类型。其中：0、验证码 1：短信通知。2：推广短信。
     */
    @FlagValidator(value = {"0","1","2"}, groups = AddGroup.class)
    private Integer templateType;

    /**
     * 模板名称
     */
    @NotBlank(message = "模板名称不能为空", groups = AddGroup.class)
    private String templateName;

    /**
     * template_content
     */
    @NotBlank(message = "模板内容不能为空", groups = AddGroup.class)
    private String templateContent;

    /**
     * remark
     */
    @NotBlank(message = "备注不能为空", groups = AddGroup.class)
    private String remark;

    /**
     * 0：审核中。1：审核通过。2：审核失败，请在返回参数reason中查看审核失败原因。
     */
    private Integer templateStatus;

    /**
     * create_date
     */
    private Date createDate;

    /**
     * message
     */
    private String message;

    /**
     * reason
     */
    private String reason;

    private String companyId;

    public SmsTemplate() {}
}
