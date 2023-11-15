package com.caipiao.modules.app.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.common.exception.RRException;
import com.caipiao.common.utils.*;
import com.caipiao.modules.app.dao.UserInfoDao;
import com.caipiao.modules.app.dao.UserRechargeRecordDao;
import com.caipiao.modules.app.dto.UserInfoReq;
import com.caipiao.modules.app.entity.UserInfo;
import com.caipiao.modules.app.entity.UserRechargeRecord;
import com.caipiao.modules.app.service.InvitationCodeService;
import com.caipiao.modules.app.service.UserInfoService;
import com.caipiao.modules.order.entity.enmu.UserType;
import com.caipiao.modules.sys.entity.SysCompStaffEntity;
import com.caipiao.modules.sys.entity.bo.UserInfoBO;
import com.caipiao.modules.sys.service.ISysCompStaffService;
import com.caipiao.modules.sys.service.StaffManageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xiaoyinandan
 * @date 2022/2/7 下午7:22
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserInfoServiceImpl extends ServiceImpl<UserInfoDao, UserInfo> implements UserInfoService {
    private final StaffManageService staffManageService;
    private final UserRechargeRecordDao userRechargeRecordDao;
    @Autowired
    private ISysCompStaffService sysCompStaffService;

    @Autowired
    private InvitationCodeService invitationCodeService;

    @Autowired
    private UserInfoService userInfoService;

    @Override
    public R queryPage(UserInfoReq req)     {
        Map<String, Object> mp = new HashMap<>();
        mp.put("page", req.getPage()+"");
        mp.put("limit", req.getLimit()+"");
        mp.put("sidx", req.getSidx());
        mp.put("order", req.getOrder());
        mp.put("phone", req.getPhone());
        mp.put("staffId", req.getStaffId());
        mp.put("realName", req.getRealName());
        mp.put("nickName", req.getNickName());
        mp.put("subordinateStore", req.getShop());

        if(StringUtils.isBlank(req.getStaffId())){
//            List<String> userIds = new ArrayList<>();
//            if(StringUtils.isNotBlank(req.getManageStaffId())){
//                userIds = getStaffManageUsers(req.getManageStaffId());
//            }

            IPage<UserInfo> page = new Query<UserInfo>().getPage(mp);
            LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.likeLeft(StringUtils.isNotBlank(req.getPhone()),UserInfo::getPhone, req.getPhone())
                    .like(StringUtils.isNotBlank(req.getNickName()),UserInfo::getNickName, req.getNickName())
                    .eq(UserInfo::getDelFlag, Constant.NO)
                    .eq(UserInfo::getUserType,UserType.USER)
                    //手动加店铺id
                    .eq(UserInfo::getSubordinateStore,req.getShop())
                    .like(StringUtils.isNotBlank(req.getRealName()),UserInfo::getRealName, req.getRealName())
                    .orderByDesc(UserInfo::getBalance);

            if(StringUtils.isNotBlank(req.getManageStaffId())){
//                wrapper.notIn(UserInfo::getManageStaffId, userIds);
                wrapper.isNull(UserInfo::getManageStaffId);
            }
            return R.ok().put("page", new PageUtils<>(this.page(page, wrapper)));
        }else{
            return R.ok().put("page", new PageUtils<>(queryStaffManage(mp)));
        }
    }

    @Override
    public IPage<UserInfo> queryStaffManage(Map<String, Object> params) {
        String subordinateStore = (String) params.get("subordinateStore");
        String staffId = (String) params.get("staffId");
        String phone = (String) params.get("phone");
        String nickName = (String) params.get("nickName");
        String realName = (String) params.get("realName");

        if(StringUtils.isBlank(staffId)){
            throw new RRException("员工ID不能为空");
        }

        IPage<UserInfo> page = new Query<UserInfo>().getPage(params);

        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeLeft(StringUtils.isNotBlank(phone),UserInfo::getPhone, phone)
                .eq(UserInfo::getManageStaffId, staffId)
                //查询手动加店铺
                .eq(UserInfo::getSubordinateStore,subordinateStore)
                .eq(UserInfo::getDelFlag, Constant.NO)
                .like(StringUtils.isNotBlank(nickName),UserInfo::getNickName, nickName)
                .like(StringUtils.isNotBlank(realName),UserInfo::getRealName, realName)
                .orderByDesc(UserInfo::getBalance);

        return this.page(page, wrapper);
    }

    private List<String> getStaffManageUsers(String staffId) {
        LambdaQueryWrapper<UserInfo> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(UserInfo::getManageStaffId, staffId);

        List<UserInfo> list = this.list(wrapper1);

        List<String> userIds = new ArrayList<>();

        if(CollUtil.isNotEmpty(list)){
            userIds = list.stream().map(UserInfo::getManageStaffId).collect(Collectors.toList());
        }
        return userIds;
    }

    @Override
    @Transactional
    public void recharge(String userId, BigDecimal amount) {
        UserRechargeRecord rechargeRecord = new UserRechargeRecord();
        rechargeRecord.setCreateTime(new Date());
        rechargeRecord.setPaymentTime(new Date());
        rechargeRecord.setPaymentWay("3");
        rechargeRecord.setPaymentType(1);
        rechargeRecord.setTransactionId(null);
        rechargeRecord.setSubject("客服代充值，客服编号："+ ShiroUtils.getUserId());
        rechargeRecord.setPayStatus(1);
        rechargeRecord.setRechargeAmount(amount);
        rechargeRecord.setUserId(userId);
        userRechargeRecordDao.insert(rechargeRecord);

        //更新用户金额
        UserInfo userInfo = this.getById(userId);

        userInfo.setBalance(userInfo.getBalance().add(amount));
        userInfo.setBonus(userInfo.getBonus().add(amount));
        updateById(userInfo);
    }

    @Override
    @Transactional
    public void subtract(String userId, BigDecimal amount) {
        UserRechargeRecord rechargeRecord = new UserRechargeRecord();
        rechargeRecord.setCreateTime(new Date());
        rechargeRecord.setPaymentTime(new Date());
        rechargeRecord.setPaymentWay("3");
        rechargeRecord.setPaymentType(1);
        rechargeRecord.setTransactionId(null);
        rechargeRecord.setSubject("客服减款操作，客服编号："+ ShiroUtils.getUserId());
        rechargeRecord.setPayStatus(1);
        rechargeRecord.setRechargeAmount(amount.negate());
        rechargeRecord.setUserId(userId);
        userRechargeRecordDao.insert(rechargeRecord);

        //更新用户金额
        UserInfo userInfo = this.getById(userId);

        userInfo.setBalance(userInfo.getBalance().subtract(amount));
        userInfo.setBonus(userInfo.getBonus().subtract(amount));
        updateById(userInfo);
    }

    @Override
    public UserInfo registerUserInfo(UserInfoBO userInfoBO) {
        String manageStaffId = userInfoBO.getManageStaffId();
        if(StringUtils.isNotBlank(userInfoBO.getManageStaffId())){
            SysCompStaffEntity compStaffEntity = sysCompStaffService.getById(manageStaffId);
            if(null == compStaffEntity){
                throw new RRException("分享人不存在");
            }
        }
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        //只要注册电话没注册过 直接注册
        wrapper.eq(UserInfo::getPhone, userInfoBO.getPhoneNumber());
        UserInfo one = getOne(wrapper);
        if(null != one){
            throw new RRException("手机号已被注册");
        }
        UserInfo userByInvitationCode = userInfoService.getUserByInvitationCode(userInfoBO.getInvitationCode());
        //保存用户信息，注册成功
        String invitationCode = invitationCodeService.getInvitationCode();
        UserInfo info = new UserInfo();
        info.setHeadimgUrl(userInfoBO.getAvatar());
        info.setInvitationCode(invitationCode);
        info.setNickName(userInfoBO.getNickName());
        info.setUserType(UserType.USER);
        info.setPhone(userInfoBO.getPhoneNumber());
        info.setParentInvitationCode(userInfoBO.getInvitationCode());
        info.setPassword(MD5Util.getMD5AndSalt(userInfoBO.getPassword()));
        info.setManageStaffId(manageStaffId);
        info.setSubordinateStore(userByInvitationCode.getSubordinateStore());
        baseMapper.insert(info);
        return info;
    }


    public UserInfo getUserByInvitationCode(String invitationCode) {
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq(UserInfo.COL_InvitationCode,invitationCode);
        //如果查询到有两个,赶紧做数据订正
        UserInfo userInfo = getOne(userInfoQueryWrapper);
        return userInfo;
    }
}
