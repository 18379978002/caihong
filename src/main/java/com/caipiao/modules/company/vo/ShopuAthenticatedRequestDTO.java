package com.caipiao.modules.company.vo;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import java.io.Serializable;


@Data
@ApiModel("接收更新店铺参数")
public class ShopuAthenticatedRequestDTO implements Serializable {

    private Integer shopId;

    private String reason;

    private String shopStatus;
}
