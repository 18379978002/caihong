package com.caipiao.modules.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.modules.app.dao.FansDao;
import com.caipiao.modules.app.entity.Fans;
import com.caipiao.modules.app.entity.UserInfo;
import com.caipiao.modules.app.service.FansService;
import com.caipiao.modules.app.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FansServiceImpl extends ServiceImpl<FansDao, Fans> implements FansService {

    private final UserInfoService userInfoService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void attention(String userId, UserInfo user) {

        LambdaQueryWrapper<Fans> fansLambdaQueryWrapper = new LambdaQueryWrapper<>();
        fansLambdaQueryWrapper.eq(Fans::getUserId, userId)
                .eq(Fans::getFansId, user.getId());
        Fans one = this.getOne(fansLambdaQueryWrapper);
        UserInfo userInfo = userInfoService.getById(userId);
        UserInfo userInfo1 = userInfoService.getById(user.getId());
        if(null != one){
            //取消关注
            removeById(one.getId());
            //粉丝数减去1
            userInfo.setFansNum(userInfo.getFansNum() - 1);

            //自己的关注数减去1
            userInfo1.setAttentionNum(userInfo1.getAttentionNum()-1);

        }else {
            Fans fans = new Fans();
            fans.setUserId(userId);
            fans.setFansId(user.getId());
            this.save(fans);
            //粉丝数+1
            userInfo.setFansNum(userInfo.getFansNum() + 1);

            //自己的关注数+1
            userInfo1.setAttentionNum(userInfo1.getAttentionNum()+1);
        }

        userInfoService.updateById(userInfo);
        userInfoService.updateById(userInfo1);

    }
}
