package com.caipiao.modules.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.common.utils.Constant;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.common.utils.Query;
import com.caipiao.modules.app.dao.GradeInfoDao;
import com.caipiao.modules.app.entity.GradeInfo;
import com.caipiao.modules.app.service.GradeInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author xiaoyinandan
 * @date 2022/2/7 下午7:34
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GradeInfoServiceImpl extends ServiceImpl<GradeInfoDao, GradeInfo> implements GradeInfoService {


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String gradeName = (String) params.get("gradeName");

        IPage<GradeInfo> page = this.page(
                new Query<GradeInfo>().getPage(params),
                new QueryWrapper<GradeInfo>()
                        .like(StringUtils.isNotBlank(gradeName), "grade_name", gradeName)
                        .eq("del_flag", Constant.NO).ne("id", 1)
        );

        return new PageUtils(page);
    }
}
