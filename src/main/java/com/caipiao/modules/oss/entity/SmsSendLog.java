package com.caipiao.modules.oss.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Description: jz-cloud
 * Created by yj198 on 2021/4/12 20:12
 */
@Data
@TableName("sms_send_log")
public class SmsSendLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
     * id
     */
    private Long id;

    /**
     * send_time
     */
    private Date sendTime;

    /**
     * send_template_code
     */
    private String sendTemplateCode;

    /**
     * send_content
     */
    private String sendContent;

    /**
     * send_template_name
     */
    private String sendTemplateName;

    /**
     * phone_number
     */
    private String phoneNumber;

    /**
     * biz_id
     */
    private String bizId;

    /**
     * 短信发送状态，包括：1：等待回执。2：发送失败。3：发送成功。
     */
    private Integer sendState;

    /**
     * receive_date
     */
    private String receiveDate;

    /**
     * err_code
     */
    private String errCode;

    private String companyId;

    public SmsSendLog() {}
}
