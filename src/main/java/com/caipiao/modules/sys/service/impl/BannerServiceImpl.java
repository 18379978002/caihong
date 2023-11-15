package com.caipiao.modules.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.common.exception.RRException;
import com.caipiao.common.utils.ShiroUtils;
import com.caipiao.modules.sys.dao.BannerDao;
import com.caipiao.modules.sys.entity.Banner;
import com.caipiao.modules.sys.entity.SysCompStaffEntity;
import com.caipiao.modules.sys.service.BannerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author xiaoyinandan
 * @date 2021/8/24 下午3:17
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerDao, Banner> implements BannerService {

    @Override
    @Transactional
    public void batchDelete(List<Long> ids) {
        SysCompStaffEntity userEntity = ShiroUtils.getUserEntity();
        for (Long id : ids) {
            Banner banner = getById(id);

            if(null == banner || !banner.getCompanyId().equals(userEntity.getCompanyId())){
                throw new RRException("记录不存在");
            }
        }

        removeByIds(ids);
    }
}
