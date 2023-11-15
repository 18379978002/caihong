package com.caipiao.modules.sys.controller;

import com.caipiao.common.annotation.AnonymousAccess;
import com.caipiao.common.utils.R;
import com.caipiao.modules.sys.entity.dto.PutLotteryCateggorySettingsRequestDTO;
import com.caipiao.modules.sys.entity.enmu.LotteryCategory;
import com.caipiao.modules.sys.entity.util.SysCoverUtil;
import com.caipiao.modules.sys.service.TbLotteryTypeConfService;
import com.google.common.collect.Lists;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("manage/lotteryCategory")
public class LotteryCategoryController extends AbstractController{

    @Resource
    private TbLotteryTypeConfService tbLotteryTypeConfService;

    /**
     * showdoc
     *
     * @catalog 店主端/彩种管理
     * @title 获取所有彩种和彩种的设置信息
     * @description 获取所有彩种和彩种的设置信息.此设置信息指的是每个彩种能够设置的组件信息
     * @method get
     * @url https://{{domain}}/caipiao-api/manage/lotteryCategory/allLotteryCategory
     * @return [{"lotteryName":"大乐透","componentMetaDatas":[{"fieldName":"switch","fieldDesc":"关闭后用户无法查看彩种,请谨慎操作","fieldShowName":"彩种开关","unitName":null,"componentMetaDataFieldType":"BOOL"},{"fieldName":"leadTime","fieldDesc":"用户截止投注提前时间","fieldShowName":"用户截止投注提前时间","unitName":"分","componentMetaDataFieldType":"INT"}]},{"lotteryName":"排列三","componentMetaDatas":[{"fieldName":"switch","fieldDesc":"关闭后用户无法查看彩种,请谨慎操作","fieldShowName":"彩种开关","unitName":null,"componentMetaDataFieldType":"BOOL"},{"fieldName":"leadTime","fieldDesc":"用户截止投注提前时间","fieldShowName":"用户截止投注提前时间","unitName":"分","componentMetaDataFieldType":"INT"}]},{"lotteryName":"排列五","componentMetaDatas":[{"fieldName":"switch","fieldDesc":"关闭后用户无法查看彩种,请谨慎操作","fieldShowName":"彩种开关","unitName":null,"componentMetaDataFieldType":"BOOL"},{"fieldName":"leadTime","fieldDesc":"用户截止投注提前时间","fieldShowName":"用户截止投注提前时间","unitName":"分","componentMetaDataFieldType":"INT"}]},{"lotteryName":"竞猜足球","componentMetaDatas":[{"fieldName":"switch","fieldDesc":"关闭后用户无法查看彩种,请谨慎操作","fieldShowName":"彩种开关","unitName":null,"componentMetaDataFieldType":"BOOL"},{"fieldName":"leadTime","fieldDesc":"用户截止投注提前时间","fieldShowName":"用户截止投注提前时间","unitName":"分","componentMetaDataFieldType":"INT"}]},{"lotteryName":"竞猜篮球","componentMetaDatas":[{"fieldName":"switch","fieldDesc":"关闭后用户无法查看彩种,请谨慎操作","fieldShowName":"彩种开关","unitName":null,"componentMetaDataFieldType":"BOOL"},{"fieldName":"leadTime","fieldDesc":"用户截止投注提前时间","fieldShowName":"用户截止投注提前时间","unitName":"分","componentMetaDataFieldType":"INT"}]}]
     * @return_param fieldName string 组件在后端关联值
     * @return_param fieldDesc string 组件的描述
     * @return_param fieldShowName Integer 组件在后端展示名称
     * @return_param unitName String 组件的单位为INT这个值就生效
     * @return_param componentMetaDataFieldType String 组件类型目前两个 INT和BOOL
     * @return_param lotteryName String 彩票名称Code
     * @return_param lotteryNameCode String 彩票种类Code
     * @number 0
     */
    @GetMapping("/allLotteryCategory")
    @AnonymousAccess
    public R allLotteryCategoryData(){
        return ok().put(SysCoverUtil.INSTANCES.coverLotteryCategoryDTOs(LotteryCategory.values()));
    }

    /**
     * showdoc
     *
     * @catalog 店主端/彩种管理
     * @title 获取单个彩种的设置
     * @description 获取所有彩种和彩种的设置信息.此设置信息指的是每个彩种能够设置的组件信息
     * @method get
     * @param lotteryCategory 必选 string 从接口allLotteryCategoryData获取枚举值lotteryNameCode
     * @param fieldName 必选 string 彩种属性名称
     * @url https://{{domain}}/caipiao-api/manage/lotteryCategory/queryLotteryCateggorySettings
     * @return {}
     * @return_param fieldNames string[] 传入数组
     * @number 1
     */
    @GetMapping("/queryLotteryCateggorySettings")
    public R queryLotteryCateggorySettings(LotteryCategory lotteryCategory,String[] fieldName){
        return ok().put(tbLotteryTypeConfService.queryLotteryCateggorySettings(lotteryCategory, Lists.newArrayList(fieldName)));
    }


    /**
     * showdoc
     *
     * @catalog 店主端/彩种管理
     * @title 插入彩种设置
     * @description 设置彩种的设置属性
     * @method put
     * @param lotteryCategory 必选 string 从接口allLotteryCategoryData获取枚举值lotteryNameCode
     * @param fieldName 必选 string 彩种属性名称
     * @param fieldValue 必选 string 彩种属性值
     * @url https://{{domain}}/caipiao-api/manage/lotteryCategory/putLotteryCateggorySettings
     * @return {}
     * @return_param fieldName string 组件在后端关联值
     * @number 1
     */
    @PutMapping("/putLotteryCateggorySettings")
    public R putLotteryCateggorySettings(@RequestBody PutLotteryCateggorySettingsRequestDTO categgorySettingsRequest){
        tbLotteryTypeConfService.putLotteryCateggorySettings(categgorySettingsRequest.getLotteryCategory(),categgorySettingsRequest.getFieldName(),categgorySettingsRequest.getFieldValue());
        return ok();
    }

}
