package com.caipiao.modules.app.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caipiao.common.utils.R;
import com.caipiao.modules.app.dao.UpdateLogDao;
import com.caipiao.modules.app.entity.UpdateLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UpdateController {
    @Autowired
    UpdateLogDao updateLogDao;

    @GetMapping("getLatestVersion")
    public R getLatestVersion(){
        LambdaQueryWrapper<UpdateLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(UpdateLog::getVersionNumber).last("limit 1");
        UpdateLog updateLog = updateLogDao.selectOne(wrapper);
        return R.ok().put(updateLog);
    }

}
