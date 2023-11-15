package com.caipiao.modules.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caipiao.modules.app.dto.UserInfoReq;
import com.caipiao.modules.app.entity.UserInfo;
import org.springframework.stereotype.Repository;

/**
 * @author xiaoyinandan
 * @date 2022/2/7 下午7:21
 */
@Repository
public interface UserInfoDao extends BaseMapper<UserInfo> {
    IPage<UserInfo> queryPage(IPage<UserInfo> page, UserInfoReq req);
}
