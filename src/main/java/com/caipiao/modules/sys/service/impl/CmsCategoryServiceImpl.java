package com.caipiao.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.common.utils.Query;
import com.caipiao.common.utils.ShiroUtils;
import com.caipiao.modules.sys.dao.CmsCategoryDao;
import com.caipiao.modules.sys.entity.CmsCategory;
import com.caipiao.modules.sys.service.CmsCategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author xiaoyinandan
 * @date 2021/11/29 下午2:35
 */
@Service
public class CmsCategoryServiceImpl extends ServiceImpl<CmsCategoryDao, CmsCategory> implements CmsCategoryService {
    /**
     * 查询文章列表时返回的字段（过滤掉详情字段以加快速度）
     */
    private static final String LIST_FILEDS = "id,category_name,category_code,parent_id,top_level,company_id,type,out_target_link,icon,belong_app";
    @Autowired
    CmsCategoryDao categoryMapper;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String title = (String) params.get("categoryName");
        String type = (String) params.get("type");
        String belongApp = (String) params.get("belongApp");
        IPage<CmsCategory> page = this.page(
                new Query<CmsCategory>().getPage(params),
                new QueryWrapper<CmsCategory>()
                        .select(LIST_FILEDS)
                        .eq("company_id", ShiroUtils.getUserEntity().getCompanyId())
                        .eq(!StringUtils.isEmpty(belongApp),"belong_app", belongApp)
                        .eq(!StringUtils.isEmpty(type), "type", type)
                        .like(!StringUtils.isEmpty(title), "category_name", title)
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage1(Map<String, Object> params) {
        String title = (String) params.get("categoryName");
        String type = (String) params.get("type");
        String companyId = (String) params.get("companyId");
        String belongApp = (String) params.get("belongApp");
        IPage<CmsCategory> page = this.page(
                new Query<CmsCategory>().getPage(params),
                new QueryWrapper<CmsCategory>()
                        .select(LIST_FILEDS)
                        .eq("company_id", companyId)
                        .eq(!StringUtils.isEmpty(belongApp),"belong_app", belongApp)
                        .eq(!StringUtils.isEmpty(type), "type", type)
                        .like(!StringUtils.isEmpty(title), "category_name", title)
                .orderByAsc("top_level")
        );

        return new PageUtils(page);
    }
}
