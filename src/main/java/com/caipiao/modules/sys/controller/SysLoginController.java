package com.caipiao.modules.sys.controller;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caipiao.common.annotation.AnonymousAccess;
import com.caipiao.common.component.RedisComponent;
import com.caipiao.common.exception.RRException;
import com.caipiao.common.utils.*;
import com.caipiao.common.validator.ValidatorUtils;
import com.caipiao.config.JwtProperties;
import com.caipiao.modules.app.dto.UserInfoPhone;
import com.caipiao.modules.app.entity.UserInfo;
import com.caipiao.modules.app.service.InvitationCodeService;
import com.caipiao.modules.app.service.UserInfoService;
import com.caipiao.modules.company.entity.Shop;
import com.caipiao.modules.company.service.ShopService;
import com.caipiao.modules.order.entity.enmu.UserType;
import com.caipiao.modules.oss.entity.MinioEntity;
import com.caipiao.modules.oss.entity.VerifiCationTag;
import com.caipiao.modules.oss.service.MinioFileService;
import com.caipiao.modules.oss.service.SmsSendLogService;
import com.caipiao.modules.oss.service.SmsService;
import com.caipiao.modules.sys.dao.SysCompanyDao;
import com.caipiao.modules.sys.entity.SysCompStaffEntity;
import com.caipiao.modules.sys.entity.bo.UserInfoBO;
import com.caipiao.modules.sys.entity.dto.ShopDto;
import com.caipiao.modules.sys.form.SysLoginForm;
import com.caipiao.modules.sys.form.UserInfoDTO;
import com.caipiao.modules.sys.service.ISysCompStaffService;
import com.caipiao.modules.sys.service.SysCaptchaService;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

/**
 * 登录相关
 * @author xiaoyinandan
 */
@RestController
@EnableConfigurationProperties(value = {JwtProperties.class})
@Slf4j
@Api(tags = "登录管理")
@SuppressWarnings("all")
public class SysLoginController extends AbstractController {

    @Autowired
    private SysCaptchaService sysCaptchaService;

    @Autowired
    ISysCompStaffService sysCompStaffService;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    SysCompanyDao sysCompanyDao;

    @Autowired
    private ShopService shopService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    RedisComponent redisComponent;

    @Autowired
    MinioFileService minioFileService;

    @Autowired
    private SmsSendLogService smsSendLogService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private InvitationCodeService invitationCodeService;

    public static final String SMS_CODE_PREFIX = "JZCLOUD:SMS:CODE";
    public static final String SMS_TEMPLATE_CODE = "SMS_215145230";

    /**
     * 验证码
     */
    @GetMapping("captcha")
    @ApiOperation(value = "获取验证码",notes = "返回验证码图片")
    public void captcha(HttpServletResponse response, @ApiParam(value = "随意填，但每次不得重复", required = true)String uuid) throws IOException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");

        //获取图片验证码
        BufferedImage image = sysCaptchaService.getCaptcha(uuid);

        try(//try-with-resources 语法，自动关闭资源
            ServletOutputStream out = response.getOutputStream()
        ){
            ImageIO.write(image, "jpg", out);
        }
    }

    /**
     * showdoc
     *
     * @catalog 店主端/用户管理
     * @title 登录发送验证码
     * @description 登录发送验证码
     * @method get
     * @url https://{{domain}}/caipiao-api/loginsendVerificationCode
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
     * @catalog 店主端/用户管理
     * @title 手机验证码登录
     * @description 手机验证码登录
     * @method post
     * @url https://{{domain}}/caipiao-api/sys/loginVerificationCode
     * @param phoneNumber 必选 string 店主电话
     * @param code 必选 string 店主电话
     * @return {}
     * @remark 我也不知道返回了啥  自个拿
     * @number 0
     */
    @PostMapping("/sys/loginVerificationCode")
    public Map<String, Object> loginVerificationCode(@RequestBody UserInfoPhone userInfoPhone) {

        LambdaQueryWrapper<Shop> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Shop::getStaffId,userInfoPhone.getPhoneNumber());
        Shop shop = shopService.getOne(wrapper);
        if ("2".equals(shop.getShopStatus())) {
            return R.error("店铺涉及违规操作 请联系客服");
        }
        LambdaQueryWrapper<SysCompStaffEntity> Staffwrapper = new LambdaQueryWrapper<>();
        Staffwrapper.eq(SysCompStaffEntity::getStaffId,userInfoPhone.getPhoneNumber());
        SysCompStaffEntity staff = sysCompStaffService.getOne(Staffwrapper);

        if (!staff.getPosition().equals(Constant.SUPER_ADMINISTRATOR_ROLE_ST)){
            return R.error("请用管理员账号登录");
        }

        String code = smsService.queryVerificationCode(VerifiCationTag.LOGIN, userInfoPhone.getPhoneNumber());
        if (!code.equals(userInfoPhone.getCode())) {
            throw new RRException("验证码有误");
        }

        //账号锁定
        if (staff.getStaffStatus().equals(Constant.COMPANY_SUPER_STAFF_STATUS_VALID)) {
            return R.error("账号已被锁定,请联系管理员");
        }

        String token = JwtUtils.generateToken(staff, jwtProperties.getPrivateKey(), jwtProperties.getMaxAge());
        return Objects.requireNonNull(R.ok().put("token", token)).put("expire", jwtProperties.getMaxAge() * 3600);
    }

    /**
     * 登录
     */
    @PostMapping("/sys/login")
    public Map<String, Object> login(@RequestBody SysLoginForm form) {
        boolean captcha = sysCaptchaService.validate(form.getUuid(), form.getCaptcha());
        if (!captcha) {
            return R.error("验证码不正确");
        }
        LambdaQueryWrapper<Shop> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Shop::getStaffId,form.getUsername());
        Shop shop = shopService.getOne(wrapper);
        if ("2".equals(shop.getShopStatus())) {
            return R.error("店铺涉及违规操作 请联系客服");
        }
        LambdaQueryWrapper<SysCompStaffEntity> Staffwrapper = new LambdaQueryWrapper<>();
        Staffwrapper.eq(SysCompStaffEntity::getStaffId,form.getUsername());
        SysCompStaffEntity staff = sysCompStaffService.getOne(Staffwrapper);

        if (!staff.getPosition().equals(Constant.SUPER_ADMINISTRATOR_ROLE_ST)){
            return R.error("请用管理员账号登录");
        }

        //账号不存在、密码错误
        if (staff == null || !staff.getStaffPasswd().equalsIgnoreCase(MD5Util.getMD5AndSalt(form.getPassword()))) {
            return R.error("账号或密码不正确");
        }

        //账号锁定
        if (staff.getStaffStatus().equals(Constant.COMPANY_SUPER_STAFF_STATUS_VALID)) {
            return R.error("账号已被锁定,请联系管理员");
        }

        String token = JwtUtils.generateToken(staff, jwtProperties.getPrivateKey(), jwtProperties.getMaxAge());
        return Objects.requireNonNull(R.ok().put("token", token)).put("expire", jwtProperties.getMaxAge() * 3600);
    }

    /**
     * 修改密码
     */
    @PostMapping("/sys/user/password")
    public R resetPwd(@RequestBody SysLoginForm form) {
        SysCompStaffEntity staff = sysCompStaffService.getById(getUserId());
        //校验原密码是否和数据库一致
        if(!staff.getStaffPasswd().equalsIgnoreCase(MD5Util.getMD5AndSalt(form.getPassword()))){
            return error("原始密码不正确");
        }
        //更新密码
        staff.setStaffPasswd(MD5Util.getMD5AndSalt(form.getNewPassword()));
        sysCompStaffService.updateById(staff);
        return ok();
    }

    /**
     * 登出
     */
    @PostMapping("/sys/logout")
    public R logout(@RequestParam String refreshToken) {
        redisComponent.del(refreshToken);
        return ok();
    }

    /**
     * 登录
     */
    @GetMapping("/sys/user/info")
    public R getInfo() {
        return R.ok().put(getUser());
    }

    /**
     * showdoc
     *
     * @catalog 店主端/用户管理
     * @title 注册发送验证码
     * @description 注册发送验证码
     * @method get
     * @url https://{{domain}}/caipiao-api/regsendVerificationCode
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
     * @catalog 店主端/用户管理
     * @title 店铺注册接口
     * @description 店铺注册
     * @method post
     * @url https://{{domain}}/caipiao-api/sys/register
     * @param shopPhone 必选 string 店主电话
     * @param password 必选 string 密码
     * @param rePassword 必选 string 确认密码
     * @param wx 必选 string 微信
     * @param shopHost 必选 string 店铺名
     * @param shopName 必选 string 店主名
     * @param code 必选 string 验证码
     * @return {}
     * @return_param ok string 接口返回状态
     * @remark
     * @number 0
     */
    @PostMapping("/sys/register")
    @ApiOperation("注册")
    @AnonymousAccess
    @Transactional
    public R register(@RequestBody ShopDto dto){
        if (StringUtils.isBlank(dto.getShopPhone())) {
            throw new RRException("手机号不能为空");
        }
        if (StringUtils.isBlank(dto.getPassword())) {
            throw new RRException("密码不能为空");
        }
        if (StringUtils.isBlank(dto.getRePassword())) {
            throw new RRException("密码不能为空");
        }
        if (StringUtils.isBlank(dto.getWx())) {
            throw new RRException("微信不能为空");
        }
        if (StringUtils.isBlank(dto.getShopName())) {
            throw new RRException("店铺名不能为空");
        }
        if (StringUtils.isBlank(dto.getShopHost())) {
            throw new RRException("店主名不能为空");
        }
        if(!dto.getRePassword().equals(dto.getPassword())){
            throw new RRException("两次密码不一致");
        }

        LambdaQueryWrapper<Shop> wrapper = new LambdaQueryWrapper<>();
        //只要注册电话没注册过 直接注册
        wrapper.eq(Shop::getShopPhone, dto.getShopPhone());
        Shop one = shopService.getOne(wrapper);

        if(null != one){
            throw new RRException("手机号已被注册");
        }

        /*String verificationCode = smsService.queryVerificationCode(VerifiCationTag.REG, dto.getShopPhone());

        if (!verificationCode.equals(dto.getCode())){
            throw new RRException("验证码有误");
        }*/

        LambdaQueryWrapper<UserInfo> userInfowrapper = new LambdaQueryWrapper<>();
        //只要注册电话没注册过 直接注册
        userInfowrapper.eq(UserInfo::getPhone, dto.getShopPhone());
        UserInfo one1 = userInfoService.getOne(userInfowrapper);
        if (one1!=null) {
            throw new RRException("该手机号注册过用户端 在用户端注销该账号");
        }

        Shop shop = new Shop();
        BeanUtils.copyProperties(dto, shop);
        shop.setStaffId(dto.getShopPhone());
        shopService.save(shop);

        UserInfo userInfo=new UserInfo();
        userInfo.setUserType(UserType.ADMINISTRATOR);
        userInfo.setPassword(MD5Util.getMD5AndSalt(dto.getPassword()));
        userInfo.setPhone(dto.getShopPhone());
        String invitationCode = invitationCodeService.getInvitationCode();
        userInfo.setInvitationCode(invitationCode);
        userInfo.setSubordinateStore(String.valueOf(shop.getId()));
        userInfoService.save(userInfo);

        SysCompStaffEntity staff = new SysCompStaffEntity();
        staff.setStaffId(dto.getShopPhone());
        staff.setStaffName(dto.getShopHost());
        staff.setPosition(Constant.SUPER_ADMINISTRATOR_ROLE_ST);
        staff.setSubordinateStore(String.valueOf(shop.getId()));
        staff.setStaffPasswd(MD5Util.getMD5AndSalt(dto.getPassword()));
        staff.setUserId(userInfo.getId());
        sysCompStaffService.save(staff);

        return ok();
    }

    /**
     * showdoc
     *
     * @catalog 店主端/用户管理
     * @title 店铺实名接口
     * @description 上传实名信息
     * @method post
     * @url https://{{domain}}/caipiao-api/sys/Certification
     * @param businessLicense 必选 string 营业执照
     * @param id 必选 Long 店铺id
     * @param idcardZ 必选 string 法人身份证正面
     * @param idcardF 必选 string 法人身份证反面
     * @param dxz 必选 string 代销证
     * @param handDxz 必选 string 手持代销证
     * @param handIdcard 必选 string 手持身份证
     * @param inshopPicture 必选 string 店内照片
     * @param shopheadPicture 必选 string 店铺门头照
     * @param shopAddress 必选 string 店铺地址
     * @param idcard 必选 string 店长身份证号
     * @return {}
     * @number 0
     */
    @PostMapping("/sys/Certification")
    @ApiOperation("实名")
    @AnonymousAccess
    @Transactional
    public R certification(@RequestBody ShopDto dto){
        if (StringUtils.isBlank(dto.getBusinessLicense())) {
            throw new RRException("营业执照不能为空");
        }
        if (StringUtils.isBlank(dto.getDxz())) {
            throw new RRException("代销证不能为空");
        }
        if (StringUtils.isBlank(dto.getHandDxz())) {
            throw new RRException("手持代销证不能为空");
        }
        if (StringUtils.isBlank(dto.getIdcard())) {
            throw new RRException("身份证号不能为空");
        }
        if (StringUtils.isBlank(dto.getIdcardF())) {
            throw new RRException("身份证反面不能为空");
        }
        if (StringUtils.isBlank(dto.getIdcardZ())) {
            throw new RRException("身份证正面不能为空");
        }
        if (StringUtils.isBlank(dto.getInshopPicture())) {
            throw new RRException("店内照片不能为空");
        }
        if (StringUtils.isBlank(dto.getShopAddress())) {
            throw new RRException("店铺地址不能为空");
        }
        if (StringUtils.isBlank(dto.getShopheadPicture())) {
            throw new RRException("店铺门头照不能为空");
        }
        if (StringUtils.isBlank(dto.getHandIdcard())) {
            throw new RRException("手持身份证不能为空");
        }
        if (dto.getId()==null){
            return R.error("请传入店铺id");
        }
        ArrayList<String> list = Lists.newArrayList(dto.getHandIdcard(), dto.getInshopPicture()
                , dto.getShopheadPicture(), dto.getBusinessLicense(),
                dto.getIdcardZ(), dto.getIdcardF(), dto.getDxz(), dto.getHandDxz());

        list.forEach(s->{
            LambdaQueryWrapper<MinioEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MinioEntity::getFilePath,s);
            MinioEntity one = minioFileService.getOne(wrapper);
            if (one==null) {
               throw new RRException("照片上传错误");
            }
        });

        Shop shop = shopService.getById(dto.getId());
        if (shop==null) {
            return R.error("没有该店铺");
        }
        BeanUtils.copyProperties(dto, shop);
        shopService.updateById(shop);
        return ok();
    }

    /**
     * 获取自己所属店铺信息
     * @return
     */
    @GetMapping("sys/getmessage")
    @ApiOperation(value = "获取自己所属店铺信息",notes = "获取自己所属店铺信息")
    public R getShop() {
        String userId = getUserId();
        LambdaQueryWrapper<Shop> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Shop::getStaffId,userId);
        Shop shop = shopService.getOne(wrapper);
        if (shop==null) {
            return R.error("店铺不存在");
        }
        SysCompStaffEntity byId = sysCompStaffService.getById(userId);

        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("shop",shop);
        stringObjectHashMap.put("byId",byId);
        return R.ok().put(stringObjectHashMap);
    }

}
