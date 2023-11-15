package com.caipiao.modules.sys.entity.enmu;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.caipiao.modules.sys.entity.bo.ComponentMetaData;
import lombok.Getter;

import java.util.List;

@Getter
public enum LotteryCategory implements IEnum<String> {
    GrandLotto("大乐透","[{\"componentMetaDataFieldType\":\"BOOL\",\"fieldDesc\":\"关闭后用户无法查看彩种,请谨慎操作\",\"fieldName\":\"switch\",\"fieldShowName\":\"彩种开关\",\"defaultValue\":\"true\"},{\"componentMetaDataFieldType\":\"INT\",\"fieldDesc\":\"用户截止投注提前时间\",\"fieldName\":\"leadTime\",\"fieldShowName\":\"用户截止投注提前时间\",\"unitName\":\"分钟\",\"defaultValue\":\"10\"}]"),
    ArrangeThree("排列三","[{\"componentMetaDataFieldType\":\"BOOL\",\"fieldDesc\":\"关闭后用户无法查看彩种,请谨慎操作\",\"fieldName\":\"switch\",\"fieldShowName\":\"彩种开关\",\"defaultValue\":\"true\"},{\"componentMetaDataFieldType\":\"INT\",\"fieldDesc\":\"用户截止投注提前时间\",\"fieldName\":\"leadTime\",\"fieldShowName\":\"用户截止投注提前时间\",\"unitName\":\"分钟\",\"defaultValue\":\"10\"}]"),
    ArrangeFive("排列五","[{\"componentMetaDataFieldType\":\"BOOL\",\"fieldDesc\":\"关闭后用户无法查看彩种,请谨慎操作\",\"fieldName\":\"switch\",\"fieldShowName\":\"彩种开关\",\"defaultValue\":\"true\"},{\"componentMetaDataFieldType\":\"INT\",\"fieldDesc\":\"用户截止投注提前时间\",\"fieldName\":\"leadTime\",\"fieldShowName\":\"用户截止投注提前时间\",\"unitName\":\"分钟\",\"defaultValue\":\"10\"}]"),
    GuessFootball("竞猜足球","[{\"componentMetaDataFieldType\":\"BOOL\",\"fieldDesc\":\"关闭后用户无法查看彩种,请谨慎操作\",\"fieldName\":\"switch\",\"fieldShowName\":\"彩种开关\",\"defaultValue\":\"true\"},{\"componentMetaDataFieldType\":\"INT\",\"fieldDesc\":\"用户截止投注提前时间\",\"fieldName\":\"leadTime\",\"fieldShowName\":\"用户截止投注提前时间\",\"unitName\":\"分钟\",\"defaultValue\":\"10\"}]"),
    GuessBasketball("竞猜篮球","[{\"componentMetaDataFieldType\":\"BOOL\",\"fieldDesc\":\"关闭后用户无法查看彩种,请谨慎操作\",\"fieldName\":\"switch\",\"fieldShowName\":\"彩种开关\",\"defaultValue\":\"true\"},{\"componentMetaDataFieldType\":\"INT\",\"fieldDesc\":\"用户截止投注提前时间\",\"fieldName\":\"leadTime\",\"fieldShowName\":\"用户截止投注提前时间\",\"unitName\":\"分钟\",\"defaultValue\":\"10\"}]"),

    ;
    LotteryCategory(String lotteryName,String componentMetaData){
        this.componentMetaDatas = JSON.parseArray(componentMetaData, ComponentMetaData.class);
        this.lotteryName = lotteryName;
    }
    private String lotteryName;
    private List<ComponentMetaData> componentMetaDatas;


    public String getLotteryNameCode(){
        return this.name();
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }


    public static Boolean hasKey(LotteryCategory lotteryCategory,String filedName){
        for (ComponentMetaData componentMetaData : lotteryCategory.getComponentMetaDatas()) {
            if(componentMetaData.getFieldName().equals(filedName)){
                return true;
            }
        }
        return false;
    }
    @Override
    public String getValue() {
        return name();
    }
}
