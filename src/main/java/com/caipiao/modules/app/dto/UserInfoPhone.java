package com.caipiao.modules.app.dto;


import com.caipiao.common.validator.annotation.MobileValidator;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;


@Data
public class UserInfoPhone implements Serializable {

    @MobileValidator
    private String phoneNumber;


    private String code;
}
