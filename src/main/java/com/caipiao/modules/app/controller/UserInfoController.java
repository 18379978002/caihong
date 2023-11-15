package com.caipiao.modules.app.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdcardUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caipiao.common.annotation.AnonymousAccess;
import com.caipiao.common.component.RedisComponent;
import com.caipiao.common.exception.RRException;
import com.caipiao.common.utils.*;
import com.caipiao.common.validator.ValidatorUtils;
import com.caipiao.modules.app.dto.PayPasswordDTO;
import com.caipiao.modules.app.dto.UserInfoPhone;
import com.caipiao.modules.app.entity.GradeInfo;
import com.caipiao.modules.app.entity.UserInfo;
import com.caipiao.modules.app.service.GradeInfoService;
import com.caipiao.modules.app.service.InvitationCodeService;
import com.caipiao.modules.app.service.UserInfoService;
import com.caipiao.modules.common.dao.CountDataDao;
import com.caipiao.modules.company.entity.Shop;
import com.caipiao.modules.company.service.ShopService;
import com.caipiao.modules.oss.entity.VerifiCationTag;
import com.caipiao.modules.oss.service.SmsService;
import com.caipiao.modules.sys.entity.SysCompStaffEntity;
import com.caipiao.modules.sys.entity.bo.UserInfoBO;
import com.caipiao.modules.sys.form.LinkInvitationReqDTO;
import com.caipiao.modules.sys.form.SysLoginForm;
import com.caipiao.modules.sys.form.UserInfoDTO;
import com.caipiao.modules.sys.service.ISysCompStaffService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaoyinandan
 * @date 2022/2/7 下午7:23
 */
@RestController
@RequestMapping("/app/userinfo")
@Api(tags = "《app端》app用户管理")
@RequiredArgsConstructor
@Slf4j
public class UserInfoController extends AppAbstractController{

    private final SmsService smsService;

    private final UserInfoService userInfoService;

    private final GradeInfoService gradeInfoService;

    private final RedisComponent redisComponent;

    private final HttpServletRequest request;
    private final ISysCompStaffService sysCompStaffService;

    private final InvitationCodeService invitationCodeService;

    private final ShopService shopService;

    /**
     * 修改密码
     */
    @ApiOperation("修改密码")
    @PostMapping("/resetpassword")
    public R resetPwd(@RequestBody SysLoginForm form) {
        UserInfo userInfo = userInfoService.getById(getUser().getId());
        //校验原密码是否和数据库一致
        if(!userInfo.getPassword().equalsIgnoreCase(MD5Util.getMD5AndSalt(form.getPassword()))){
            return error("原始密码不正确");
        }
        //更新密码
        userInfo.setPassword(MD5Util.getMD5AndSalt(form.getNewPassword()));
        userInfoService.updateById(userInfo);
        return ok();
    }

    /**
     * 修改密码
     */
    @ApiOperation("检测是否认证、支付密码设置、余额是否足够")
    @PostMapping("/checkBalanceAndPass")
    public R checkBalanceAndPass(@RequestParam BigDecimal orderAmount) {
        UserInfo userInfo = userInfoService.getById(getUser().getId());
        //检测是否已认证
        Map<String, Object> mp = new HashMap<>();

        if(StringUtils.isBlank(userInfo.getIdcard()) || StringUtils.isBlank(userInfo.getRealName())){
            mp.put("isAuth", false);
        }else{
            mp.put("isAuth", true);
        }

        //检测余额是否足够
        if(orderAmount.compareTo(userInfo.getBalance())>0){
            mp.put("balance", false);
        }else{
            mp.put("balance", true);
        }

        //检测是否设置支付密码
        if(StringUtils.isBlank(userInfo.getPayPassword())){
            mp.put("payPassword", false);
        }else{
            mp.put("payPassword", true);
        }


        return ok().put(mp);
    }

    /**
     * 设置支付密码
     */
    @ApiOperation("设置支付密码")
    @PostMapping("/setPayPassword")
    public R setPayPassword(@RequestBody PayPasswordDTO form) {
        UserInfo userInfo = userInfoService.getById(getUser().getId());
        //校验原密码是否和数据库一致
        if(!form.getPayPassword().equalsIgnoreCase(form.getRePayPassword())){
            return error("两次密码不一致");
        }
        //更新密码
        userInfo.setPayPassword(MD5Util.getMD5AndSalt(form.getPayPassword()));
        userInfoService.updateById(userInfo);
        return ok();
    }

    /**
     * 重新设置支付密码
     */
    @ApiOperation("重新设置支付密码")
    @PostMapping("/resetPayPassword")
    public R resetPayPassword(@RequestBody PayPasswordDTO form) {
        UserInfo userInfo = userInfoService.getById(getUser().getId());
        //校验原密码是否和数据库一致
        if(!userInfo.getPayPassword().equalsIgnoreCase(form.getOriPayPassword())){
            return error("原始支付密码不正确");
        }
        if(!form.getPayPassword().equalsIgnoreCase(form.getRePayPassword())){
            return error("两次密码不一致");
        }
        //更新密码
        userInfo.setPayPassword(MD5Util.getMD5AndSalt(form.getPayPassword()));
        userInfoService.updateById(userInfo);
        return ok();
    }

    /**
     * showdoc
     *
     * @catalog app端/用户管理
     * @title 注册发送验证码
     * @description 注册发送验证码
     * @method get
     * @url https://{{domain}}/caipiao-api/app/userinfo/regsendVerificationCode
     * @param phone 必选 string 店主电话
     * @return {}
     * @return_param ok string 接口返回状态
     * @remark
     * @number 0
     */
    @GetMapping ("regsendVerificationCode")
    @ApiOperation("注册发送验证码")
    @AnonymousAccess
    @Transactional
    public R regsendVerificationCode(String phone){
        if (StringUtils.isBlank(phone)) {
            throw new RRException("手机号不能为空");
        }
        smsService.sendVerificationCode(VerifiCationTag.REG,phone);
        return ok();
    }

    /**
     * showdoc
     *
     * @catalog app端/用户管理
     * @title 注册
     * @description 注册
     * @method post
     * @url https://{{domain}}/caipiao-api/app/userinfo/register
     * @param invitationCode 必选 string 邀请码
     * @param phoneNumber 必选 string 电话
     * @param password 必选 string 密码
     * @param rePassword 必选 string 重复密码
     * @param code 必选 string 验证码
     * @return {}
     * @remark
     * @number 0
     */
    @PostMapping("register")
    @ApiOperation("注册")
    @AnonymousAccess
    @Transactional
    public R register(@RequestBody UserInfoDTO dto){
        ValidatorUtils.validateEntity(dto);
        if (StringUtils.isBlank(dto.getInvitationCode())) {
            return R.error("邀请码不能为空");
        }
        if(!dto.getRePassword().equals(dto.getPassword())){
            throw new RRException("两次密码不一致");
        }

        String code = smsService.queryVerificationCode(VerifiCationTag.REG, dto.getPhoneNumber());

        if (!code.equals(dto.getCode())){
            throw new RRException("验证码有误");
        }

        UserInfoBO userInfoBO = UserInfoBO.coverUserInfoBO(dto);

        UserInfo registerUserInfo = userInfoService.registerUserInfo(userInfoBO);
        //通过邀请码回填分管用户
        invitationCodeService.linkAndfreshInvitaAndtionCode(registerUserInfo.getId(),dto.getInvitationCode());
        return ok();
    }


    /*@PostMapping("linkInvitationCode")
    @ApiOperation("链接邀请码")
    @AnonymousAccess
    @Transactional
    public R linkInvitationCode(@RequestBody LinkInvitationReqDTO dto){
        if(StringUtils.isBlank(dto.getInvitationCode())){
            throw new RRException("邀请码不能为空!");
        }
        if(StringUtils.isBlank(dto.getUserId())){
            throw new RRException("身份信息为空");
        }
        invitationCodeService.linkAndfreshInvitaAndtionCode(dto.getUserId(),dto.getInvitationCode());
        return R.ok();
    }*/


    @PostMapping("editUserInfo")
    @ApiOperation("编辑个人信息")
    public R editUserInfo(@RequestBody UserInfoDTO dto){
        //保存用户信息，注册成功
        UserInfo info = new UserInfo();
        info.setHeadimgUrl(dto.getAvatar());
        info.setId(getUser().getId());
        //设置邀请码
        info.setNickName(dto.getNickName());
        info.setRealName(dto.getRealName());
        if(!IdcardUtil.isValidCard18(dto.getIdCard())){
            throw new RRException("无效的身份证信息!");
        }
        int age = IdcardUtil.getAgeByIdCard(dto.getIdCard(), new Date());
        if(age<18){
            throw new RRException("注册身份必须已成年!");
        }
        info.setIdcard(dto.getIdCard());
        userInfoService.updateById(info);

        //更新redis用户信息
        String appToken = request.getHeader("appToken");

        redisComponent.set(appToken, userInfoService.getById(info.getId()), 15, TimeUnit.DAYS);

        return getUserInfo();
    }

    /**
     * showdoc
     *
     * @catalog app端/用户管理
     * @title 登录发送验证码
     * @description 登录发送验证码
     * @method get
     * @url https://{{domain}}/caipiao-api/app/userinfo/loginsendVerificationCode
     * @param phone 必选 string 店主电话
     * @return {}
     * @return_param ok string 接口返回状态
     * @remark
     * @number 0
     */
    @GetMapping ("loginsendVerificationCode")
    @ApiOperation("登录发送验证码")
    @AnonymousAccess
    @Transactional
    public R loginsendVerificationCode(String phone){
        if (StringUtils.isBlank(phone)) {
            throw new RRException("手机号不能为空");
        }
        smsService.sendVerificationCode(VerifiCationTag.LOGIN,phone);
        return ok();
    }

    /**
     * showdoc
     *
     * @catalog app端/用户管理
     * @title 手机验证码登录
     * @description 手机验证码登录
     * @method post
     * @url https://{{domain}}/caipiao-api/app/userinfo/loginVerificationCode
     * @param phoneNumber 必选 string 店主电话
     * @param code 必选 string 店主电话
     * @return {}
     * @return_param stringObjectMap map 里面有东西自个去拿
     * @remark
     * @number 0
     */
    @AnonymousAccess
    @PostMapping("loginVerificationCode")
    @ApiOperation(value = "手机验证码登录",notes = "手机验证码登录")
    public R loginVerificationCode(@RequestBody UserInfoPhone userInfoPhone) {
        //用户信息
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getPhone, userInfoPhone.getPhoneNumber());
        UserInfo one = userInfoService.getOne(wrapper);
        //账号不存在、密码错误
        if (one == null) {
            return R.error("请先注册");
        }
        String code = smsService.queryVerificationCode(VerifiCationTag.LOGIN, userInfoPhone.getPhoneNumber());
        if (!code.equals(userInfoPhone.getCode())) {
            throw new RRException("验证码有误");
        }

        //直接登录
        Map<String, Object> stringObjectMap = generateTokenByUserInfo(one);

        return Objects.requireNonNull(ok().put("flag", "directLogin")).put(stringObjectMap);

    }

    /**
     * 手机号和密码登录
     */
    @AnonymousAccess
    @PostMapping("loginByPass")
    @ApiOperation(value = "手机号和密码登录",notes = "手机号和密码登录")
    public R loginByPass(@RequestBody SysLoginForm form) {
        //用户信息
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getPhone, form.getPhoneNumber());
        UserInfo one = userInfoService.getOne(wrapper);

        //账号不存在、密码错误
        if (one == null || !one.getPassword().equalsIgnoreCase(MD5Util.getMD5AndSalt(form.getPassword()))) {
            return R.error("账号或密码不正确");
        }

        //直接登录
        Map<String, Object> stringObjectMap = generateTokenByUserInfo(one);

        return Objects.requireNonNull(ok().put("flag", "directLogin")).put(stringObjectMap);

    }

    /**
     * 刷新token登录
     */
    @AnonymousAccess
    @PostMapping("loginByRefreshToken")
    @ApiOperation(value = "刷新token登录",notes = "刷新token登录")
    public R loginByRefreshToken(@RequestParam("refreshToken") String refreshToken) {
        String staff = (String)redisComponent.get(refreshToken);
        if(StringUtils.isBlank(staff)){
            return error("refreshToken不存在或者已过期");
        }

        UserInfo userInfo = userInfoService.getById(staff);

        //存在直接登录即可
        Map<String, Object> stringObjectMap = generateTokenByUserInfo(userInfo);

        //删除原refreshToken
        redisComponent.del(refreshToken);

        return Objects.requireNonNull(ok().put("flag", "directLogin")).put(stringObjectMap);

    }

    private Map<String, Object> generateTokenByUserInfo(UserInfo staff){
        //获得token
        String token = UUID.randomUUID().toString(true);

        //创建refreshToken
        String refreshToken = UUID.randomUUID().toString(true);

        //refreshToken 100天过期
        redisComponent.set(refreshToken, staff.getId(), 100, TimeUnit.DAYS);
        //token 15天过期
        redisComponent.set(token, staff, 15, TimeUnit.DAYS);

        Map<String, Object> map = new HashMap<>();
        DateTime dateTime = DateUtil.offsetDay(new Date(), 15);
        map.put("token", token);
        map.put("expire", dateTime.getTime() + "");
        map.put("refreshToken", refreshToken);
        map.put("refreshExpire", DateUtil.offsetDay(new Date(), 100).getTime() + "");
        return map;
    }

    /**
     * showdoc
     *
     * @catalog app端/用户管理
     * @title 查询用户信息
     * @description 查询用户信息
     * @method get
     * @url https://{{domain}}/caipiao-api/app/userinfo/getUserInfo
     * @return {"msg":"success","code":200,"data":{"id":"1630449087612514305","gradeInfo":{"id":"1","gradeName":"普通会员","gradeIcon":null,"delFlag":"0","createTime":"2022-02-08 12:32:19","monthFee":0,"yearFee":0,"partYearFee":0,"seasonFee":0},"userGradeName":null,"manageStaffId":"admin","delFlag":"0","createTime":"2023-02-28 14:05:29","updateTime":"2023-03-31 09:31:58","userCode":49,"phone":"18379978002","password":"e085d797739d883b004d4217037b6558","userGrade":"1","balance":79780.00,"bonus":21680.00,"nickName":"北梦","headimgUrl":null,"invitationCode":"roAXfxTI","parentInvitationCode":"xdbPTDE6","realName":"王昱恒","idcard":"360124200102066014","payPassword":"3ca0a063cd9b4b9ecec27f8185bcdd35","totalBonus":0.00,"todayBonus":0.00,"attentionNum":0,"fansNum":0,"redFansNum":0,"userType":"AGENT","subordinateStore":"1"}}
     * @return_param nickName string 昵称
     * @return_param phone string 手机号码
     * @return_param userCode Integer 用户编码
     * @return_param idcard String 实名认证 有就是实名 无就是没有实名
     * @number 0
     */
    @GetMapping("getUserInfo")
    @ApiOperation(value = "查询用户信息",notes = "查询用户信息")
    public R getUserInfo() {
        UserInfo userInfo = userInfoService.getById(getUser().getId());

        //查询等级
        GradeInfo gradeInfo = gradeInfoService.getById(userInfo.getUserGrade());

        userInfo.setGradeInfo(gradeInfo);

        return ok().put(userInfo);

    }

    @ApiOperation("获取自己的邀请码")
    @GetMapping ("getInvitationCode")
    public R getInvitationCode(){
        String userId = getUserId();
        UserInfo byId = userInfoService.getById(userId);

        //TODO 这块移动到注册里
        if (StringUtils.isBlank(byId.getInvitationCode())) {
            String code = invitationCodeService.getInvitationCode();
            byId.setInvitationCode(code);
            userInfoService.updateById(byId);
            return R.ok().put(code);
        }

        String invitationCode = byId.getInvitationCode();
        return R.ok().put(invitationCode);
    }

    @ApiOperation("查看用户")
    @GetMapping("queryStaffManage")
    public R queryStaffManage(@RequestParam Map<String, Object> params){
        String userId = getUserId();
        SysCompStaffEntity staff = sysCompStaffService.getByUserId(userId);
        if (staff==null){
            throw new RRException("参数错误");
        }
        params.put("staffId",staff.getStaffId());

        //手动加店铺id
        params.put("subordinateStore",staff.getSubordinateStore());

        return ok().put("page", new PageUtils<>(userInfoService.queryStaffManage(params)));
    }

    /**
     * 获取自己所属店铺信息
     * @return
     */
    @GetMapping("getShop")
    @ApiOperation(value = "获取自己所属店铺信息",notes = "获取自己所属店铺信息")
    public R getShop() {
        String userId = getUserId();
        UserInfo userInfo = userInfoService.getById(userId);
        Shop shop = shopService.getById(userInfo.getSubordinateStore());
        if (shop==null) {
            return R.error("店铺不存在");
        }
        return R.ok().put(shop);
    }
    /**
     * showdoc
     *
     * @catalog app端/用户管理
     * @title 查询支付宝账号
     * @description 查询支付宝账号
     * @method get
     * @url https://{{domain}}/caipiao-api/app/userinfo/getAlipayAccount
     * @return {"msg":"success","code":200,"data":"2171202228@qq.com"}
     * @return_param alipayAccount string 支付宝账号 有就是有 没有就是没有
     * @number 0
     */
    @GetMapping("getAlipayAccount")
    @ApiOperation(value = "查询支付宝账号",notes = "查询支付宝账号")
    public R getAlipayAccount() {
        String userId = getUserId();
        UserInfo userInfo = userInfoService.getById(userId);
        String alipayAccount = userInfo.getAlipayAccount();
        if (StringUtils.isBlank(alipayAccount)) {
            return R.ok();
        }
        return R.ok().put(alipayAccount);
    }
    /**
     * showdoc
     *
     * @catalog app端/用户管理
     * @title 更新支付宝账号
     * @description 更新支付宝账号
     * @method put
     * @url https://{{domain}}/caipiao-api/app/userinfo/updateAlipayAccount
     * @param alipayAccount 必选 string 支付宝账号
     * @return {"msg":"success","code":200}
     * @number 0
     */
    @PutMapping ("updateAlipayAccount")
    @ApiOperation(value = "更新支付宝账号",notes = "更新支付宝账号")
    public R updateAlipayAccount(String alipayAccount) {
        String userId = getUserId();
        UserInfo userInfo = userInfoService.getById(userId);
        userInfo.setAlipayAccount(alipayAccount);
        userInfoService.updateById(userInfo);
        return R.ok();
    }
    /**
     * showdoc
     *
     * @catalog app端/用户管理
     * @title 换头像
     * @description 换头像
     * @method put
     * @url https://{{domain}}/caipiao-api/app/userinfo/avatar
     * @param filepath 必选 string 图片路径
     * @return {}
     * @number 0
     */
    @PutMapping("/avatar")
    @ApiOperation("换头像")
    @AnonymousAccess
    @Transactional
    public R certification(String filepath){
        String userId = getUserId();
        UserInfo byId = userInfoService.getById(userId);
        byId.setHeadimgUrl(filepath);
        userInfoService.updateById(byId);
        return R.ok();
    }

}
