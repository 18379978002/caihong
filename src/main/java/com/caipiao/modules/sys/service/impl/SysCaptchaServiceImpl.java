package com.caipiao.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.code.kaptcha.Producer;
import com.caipiao.common.component.RedisComponent;
import com.caipiao.common.exception.RRException;
import com.caipiao.modules.sys.dao.SysCaptchaDao;
import com.caipiao.modules.sys.entity.SysCaptchaEntity;
import com.caipiao.modules.sys.service.SysCaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

/**
 * jzcloud
 *
 * @author : xiaoyinandan
 * @date : 2021/4/19 13:18
 */
@Service("sysCaptchaService")
public class SysCaptchaServiceImpl extends ServiceImpl<SysCaptchaDao, SysCaptchaEntity> implements SysCaptchaService {
    @Autowired
    private Producer producer;
    @Autowired
    RedisComponent redisComponent;

    @Override
    public BufferedImage getCaptcha(String uuid) {
        if (StringUtils.isBlank(uuid)) {
            throw new RRException("uuid不能为空");
        }
        //生成文字验证码
        String code = producer.createText();

        //5分钟失效
        redisComponent.set(uuid, code, 5, TimeUnit.MINUTES);

        return producer.createImage(code);
    }

    @Override
    public boolean validate(String uuid, String code) {
        Object o = redisComponent.get(uuid);
        redisComponent.del(uuid);
        if(null == o){
            throw new RRException("验证码不存在或者已失效");
        }
        return ((String)o).equalsIgnoreCase(code);
    }
}

