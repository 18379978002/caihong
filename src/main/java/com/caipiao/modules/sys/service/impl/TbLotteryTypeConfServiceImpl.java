package com.caipiao.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.common.exception.RRException;
import com.caipiao.common.utils.CollectionUtils;
import com.caipiao.common.utils.StringUtils;
import com.caipiao.modules.sys.entity.bo.ComponentMetaData;
import com.caipiao.modules.sys.entity.enmu.LotteryCategory;
import com.google.api.client.util.Maps;
import org.springframework.stereotype.Service;
import com.caipiao.modules.sys.dao.TbLotteryTypeConfMapper;
import com.caipiao.modules.sys.entity.TbLotteryTypeConf;
import com.caipiao.modules.sys.service.TbLotteryTypeConfService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TbLotteryTypeConfServiceImpl extends ServiceImpl<TbLotteryTypeConfMapper, TbLotteryTypeConf> implements TbLotteryTypeConfService {


    @Override
    public Map<String, String> queryLotteryCateggorySettings(LotteryCategory lotteryCategory, List<String> fieldNames) {
        Map<String, String> lotteryCateggorySettings = Maps.newHashMap();
        Map<String, ComponentMetaData> metaDataMap = lotteryCategory.getComponentMetaDatas().stream()
                .collect(Collectors.toMap(ComponentMetaData::getFieldName, Function.identity()));
        if(CollectionUtils.isEmpty(fieldNames)){
            fieldNames = lotteryCategory.getComponentMetaDatas().stream().map(ComponentMetaData::getFieldName).collect(Collectors.toList());
        }
        if (hasKeys(lotteryCategory, fieldNames)) {
            for (String fieldName : fieldNames) {
                TbLotteryTypeConf tbLotteryTypeConf = queryFieldValue(lotteryCategory, fieldName);
                if (null != tbLotteryTypeConf) {
                    lotteryCateggorySettings.put(tbLotteryTypeConf.getFieldName(), tbLotteryTypeConf.getFieldValue());
                }else {
                    lotteryCateggorySettings.put(fieldName, metaDataMap.get(fieldName).getDefaultValue());
                }
            }
        } else {
            throw new RRException("传入的fieldName不是可以设置的彩种");
        }

        return lotteryCateggorySettings;
    }

    private static Boolean hasKeys(LotteryCategory lotteryCategory, List<String> fieldNames) {
        for (String fieldName : fieldNames) {
            if (!LotteryCategory.hasKey(lotteryCategory, fieldName)) {
                return false;
            }
        }
        return true;
    }

    protected TbLotteryTypeConf queryFieldValue(LotteryCategory lotteryCategory, String fieldName) {
        QueryWrapper<TbLotteryTypeConf> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TbLotteryTypeConf.COL_LOTTERY_TYPE, lotteryCategory.getValue());
        if (StringUtils.isNotEmpty(fieldName)) {
            queryWrapper.in(TbLotteryTypeConf.COL_FIELD_NAME, fieldName);
        }
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public void putLotteryCateggorySettings(LotteryCategory lotteryCategory, String fieldName, String fieldValue) {
        if (LotteryCategory.hasKey(lotteryCategory, fieldName)) {
            TbLotteryTypeConf tbLotteryTypeConf = queryFieldValue(lotteryCategory, fieldName);
            if (null == tbLotteryTypeConf) {
                tbLotteryTypeConf = new TbLotteryTypeConf();
                tbLotteryTypeConf.setLotteryType(lotteryCategory);
                tbLotteryTypeConf.setFieldName(fieldName);
            }
            tbLotteryTypeConf.setFieldValue(fieldValue);
            saveOrUpdate(tbLotteryTypeConf);
        } else {
            throw new RRException("传入的fieldName不是可以设置的彩种");
        }
    }

}

