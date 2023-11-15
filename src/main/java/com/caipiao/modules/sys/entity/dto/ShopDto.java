package com.caipiao.modules.sys.entity.dto;

import com.caipiao.modules.company.entity.Shop;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class ShopDto extends Shop implements Serializable {


    private String password;

    private String rePassword;

    private String code;

}
