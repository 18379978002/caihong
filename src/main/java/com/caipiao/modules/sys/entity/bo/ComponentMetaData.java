package com.caipiao.modules.sys.entity.bo;

import com.caipiao.modules.sys.entity.enmu.ComponentFieldType;
import lombok.Data;

@Data
public class ComponentMetaData {
    private String fieldName;
    private String fieldDesc;
    private String fieldShowName;
    private String unitName;

    private String defaultValue;
    private ComponentFieldType componentMetaDataFieldType;
}
