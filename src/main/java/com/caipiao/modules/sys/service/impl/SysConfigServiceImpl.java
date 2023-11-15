package com.caipiao.modules.sys.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.common.utils.ShiroUtils;
import com.caipiao.modules.sys.entity.SysConfigEntity;
import com.caipiao.modules.sys.service.SysConfigService;
import com.caipiao.common.exception.RRException;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.common.utils.Query;
import com.caipiao.modules.sys.dao.SysConfigDao;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Map;

@Service("sysConfigService")
public class SysConfigServiceImpl extends ServiceImpl<SysConfigDao, SysConfigEntity> implements SysConfigService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String paramKey = (String) params.get("paramKey");

        IPage<SysConfigEntity> page = this.page(
            new Query<SysConfigEntity>().getPage(params),
            new QueryWrapper<SysConfigEntity>()
                .like(StringUtils.isNotBlank(paramKey), "param_key", paramKey)
                .eq("status", 1)
                .eq("company_id", params.get("companyId"))
        );

        return new PageUtils(page);
    }

    @Override
    public void saveConfig(SysConfigEntity config) {
        this.save(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysConfigEntity config) {
        if(config.getId() < 100L){
            throw new RRException("系统参数，不能修改");
        }

        SysConfigEntity byId = this.getById(config.getId());

        if(!byId.getCompanyId().equals(config.getCompanyId())){
            throw new RRException("暂无权限");
        }

        this.updateById(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateValueByKey(String key, String value) {
        baseMapper.updateValueByKey(key, value);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(Long[] ids, String companyId) {
        for (Long aLong : Arrays.asList(ids)) {
            SysConfigEntity byId = this.getById(aLong);
            if(!byId.getCompanyId().equals(companyId)){
                throw new RRException("暂无权限");
            }

            if(aLong < 100L){
                throw new RRException("系统参数，不能删除");
            }
        }
        this.removeByIds(Arrays.asList(ids));
    }

    @Override
    public String getValue(String key) {
        SysConfigEntity config = baseMapper.queryByKey(key, ShiroUtils.getUserEntity().getCompanyId());

        return config == null ? null : config.getParamValue();
    }

    @Override
	public SysConfigEntity getSysConfig(String key) {
		return baseMapper.queryByKey(key, ShiroUtils.getUserEntity().getCompanyId());
	}

    @Override
    public <T> T getConfigObject(String key, Class<T> clazz) {
        String value = getValue(key);
        if (StringUtils.isNotBlank(value)) {
            return JSON.parseObject(value, clazz);
        }

        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RRException("获取参数失败");
        }
    }
}
