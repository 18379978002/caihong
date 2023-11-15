package com.caipiao.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caipiao.modules.sys.entity.enmu.LotteryCategory;
import lombok.Data;

/**
 * 彩种管理
 */
@TableName(value = "tb_lottery_type_conf")
@Data
public class TbLotteryTypeConf {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Integer id;

    /**
     * 属性名称
     */
    @TableField(value = "field_name")
    private String fieldName;

    /**
     * 属性值
     */
    @TableField(value = "field_value")
    private String fieldValue;

    /**
     * 彩票种类
     */
    @TableField(value = "lottery_type")
    private LotteryCategory lotteryType;

    public static final String COL_ID = "id";

    public static final String COL_FIELD_NAME = "field_name";

    public static final String COL_FIELD_VALUE = "field_value";

    public static final String COL_LOTTERY_TYPE = "lottery_type";

}