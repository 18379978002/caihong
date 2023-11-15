package com.caipiao.modules.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caipiao.common.annotation.AnonymousAccess;
import com.caipiao.common.exception.RRException;
import com.caipiao.common.utils.*;
import com.caipiao.modules.app.entity.UserInfo;
import com.caipiao.modules.app.service.InvitationCodeService;
import com.caipiao.modules.app.service.UserInfoService;
import com.caipiao.modules.company.entity.Shop;
import com.caipiao.modules.company.entity.enmu.AuthenticationStatus;
import com.caipiao.modules.company.service.ShopService;
import com.caipiao.modules.order.entity.enmu.UserType;
import com.caipiao.modules.sys.entity.SysCompStaffEntity;
import com.caipiao.modules.sys.entity.bo.UserInfoBO;
import com.caipiao.modules.sys.entity.dto.StaffManageDTO;
import com.caipiao.modules.sys.entity.vo.SysCompStaffVO;
import com.caipiao.modules.sys.service.ISysCompStaffService;
import com.caipiao.modules.sys.service.SysUserRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("sys/staff")
@Api(tags = "员工管理")
public class SysCompStaffController extends AbstractController {

    @Autowired
    private ISysCompStaffService sysCompStaffService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private InvitationCodeService invitationCodeService;

    /**
     * showdoc
     *
     * @catalog 店主端/用户管理
     * @title 查询用户信息
     * @description 查询用户信息
     * @method get
     * @url https://{{domain}}/caipiao-api/sys/staff/info"
     * @return {}
     * @return_param staff SysCompStaffEntity 返回的员工信息 它里面有很多属性自个拿
     * @remark
     * @number 0
     */
    @ApiOperation("查询用户信息")
    @GetMapping("/info")
    public R info() {
        SysCompStaffEntity staff = getUser();
        Shop shop = shopService.getById(staff.getSubordinateStore());
        staff.setAuthenticationStatus(shop.getAuthenticationStatus());

        return R.ok().put("user", staff);
    }

    @ApiOperation("添加员工分管")
    @PostMapping("addStaffManage")
    public R setStaffManage(@RequestBody StaffManageDTO dto){
        if(!isShopManager()){
            return error("暂无权限");
        }

        for (String userId : dto.getUserIds()) {
            UserInfo userInfo = userInfoService.getById(userId);

            if(StringUtils.isNotBlank(userInfo.getManageStaffId())){
                continue;
            }

            userInfo.setManageStaffId(dto.getStaffId());

            userInfoService.updateById(userInfo);
        }

        return ok();
    }

    @ApiOperation("删除员工分管")
    @PostMapping("deleteStaffManage")
    public R deleteStaffManage(@RequestBody StaffManageDTO dto){
        if(!isShopManager()){
            return error("暂无权限");
        }

        for (String userId : dto.getUserIds()) {
            UserInfo userInfo = userInfoService.getById(userId);

            if(StringUtils.isBlank(userInfo.getManageStaffId())){
                continue;
            }

            LambdaUpdateWrapper<UserInfo> wrapper = new LambdaUpdateWrapper<>();
            wrapper.set(UserInfo::getManageStaffId, null);
            wrapper.eq(UserInfo::getId, userId);
            userInfoService.update(wrapper);
        }

        return ok();
    }
    //此接口废弃
    @ApiOperation("添加员工")
    @PostMapping("addStaff")
    @Transactional
    public R addStaff(@RequestBody SysCompStaffEntity staff){
        if(!isShopManager()){
            return error("暂无权限");
        }

        String staffId = staff.getStaffId();
        SysCompStaffEntity entity = sysCompStaffService.getById(staffId);

        if(null != entity){
            return error("用户名已存在");
        }
        if(StringUtils.isEmpty(staff.getStaffPasswd())){
            return error("密码不能为空");
        }
        if(StringUtils.isEmpty(staff.getSubordinateStore())){
            return error("必须设置从属店铺");
        }
        if(StringUtils.isEmpty(staff.getStaffName())){
            return error("姓名不能为空");
        }

        if(StringUtils.isEmpty(staff.getMobile())){
            return error("电话不能为空");
        }
        staff.setStaffPasswd(MD5Util.getMD5AndSalt(staff.getStaffPasswd()));
        staff.setMobile("");
        staff.setCompanyId("10000");
        staff.setPosition("1");
        SysCompStaffEntity staffEntity = ((SysCompStaffEntity) SecurityUtils.getSubject().getPrincipal());
        UserInfoBO userInfoBO = UserInfoBO.coverUserInfoBO(staff);
        userInfoBO.setManageStaffId(staffEntity.getStaffId());
        UserInfo userInfo = userInfoService.registerUserInfo(userInfoBO);
        staff.setUserId(userInfo.getId());
        sysCompStaffService.save(staff);

        return ok();
    }

    @ApiOperation("删除员工")
    @DeleteMapping("delStaff/{staffId}")
    public R delStaff(@PathVariable String staffId){
        if(!isShopManager()){
            return error("暂无权限");
        }
        SysCompStaffVO sysCompStaffVO = sysCompStaffService.getByStaffId(staffId);
        if (null == sysCompStaffVO) {
            throw new RRException("无此员工", 401);
        }
        SysCompStaffEntity entity = new SysCompStaffEntity();
        entity.setStaffId(sysCompStaffVO.getStaffId());
        entity.setStaffStatus("S0X");

        sysCompStaffService.updateById(entity);
        userInfoService.removeById(sysCompStaffVO.getUserId());
        return ok();
    }

    @ApiOperation("查看员工分管")
    @GetMapping("queryStaffManage")
    public R queryStaffManage(@RequestParam Map<String, Object> params){

        if(!isShopManager()){
            return error("暂无权限");
        }
        SysCompStaffEntity staff = getUser();
        params.put("subordinateStore",staff.getSubordinateStore());
        return ok().put("page", new PageUtils<>(userInfoService.queryStaffManage(params)));
    }


    /**
     * 用户信息
     */
    @ApiOperation("查询用户详情")
    @GetMapping("/info/{userId}")
    public R info(@PathVariable("userId") String userId) {
        SysCompStaffEntity user = sysCompStaffService.getById(userId);

        if(!user.getCompanyId().equals(getCompanyId())){
            return error("暂无权限");
        }

        //获取用户所属的角色列表
        List<Long> roleIdList = sysUserRoleService.queryRoleIdList(userId);
        user.setRoleIdList(roleIdList);

        return R.ok().put("user", user);
    }

    @ApiOperation("员工列表")
    @GetMapping("list")
    public R getDeptStaffs(@RequestParam(value = "page", required = false, defaultValue = "1")Integer page,
                           @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit){

        if(!isShopManager()){
            IPage<SysCompStaffEntity> page1 = new Page<>();
            return ok().put("page", new PageUtils<>(page1));
        }
        SysCompStaffEntity staff = getUser();
        IPage<SysCompStaffEntity> pg = new Page<>(page, limit);
        LambdaQueryWrapper<SysCompStaffEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(SysCompStaffEntity::getPosition, Constant.SUPER_ADMINISTRATOR_ROLE_ST)
                .eq(SysCompStaffEntity::getStaffStatus,"S0A")

                //手动加店铺id
                .eq(SysCompStaffEntity::getSubordinateStore,staff.getSubordinateStore());
        IPage<SysCompStaffEntity> page1 = sysCompStaffService.page(pg, wrapper);
        return R.ok().put("page", new PageUtils<>(page1));

    }

    @PutMapping("putUserToAgent")
    @ApiOperation("将用户设置为代理")
    public R putUserToAgent(String userId){
        if(!isShopManager()){
            throw new RRException("只有超级管理员才能添加代理", 401);
        }
        // 修改用户角色,往员工表插入信息
        UserInfo agentUser = userInfoService.getById(userId);
        if (agentUser==null){
            throw new RRException("查无此人");
        }
        SysCompStaffEntity managerstaff = getUser();

        agentUser.setUserType(UserType.AGENT);
        agentUser.setManageStaffId(managerstaff.getStaffId());
        userInfoService.updateById(agentUser);
        SysCompStaffEntity staffEntity = sysCompStaffService.getByUserId(userId);
        if (staffEntity==null) {
            SysCompStaffEntity staff = new SysCompStaffEntity();
            staff.setStaffId(agentUser.getPhone());
            staff.setStaffName(agentUser.getNickName());
            staff.setStaffPasswd(agentUser.getPassword());
            staff.setUserId(agentUser.getId());
            staff.setSubordinateStore(agentUser.getSubordinateStore());
            sysCompStaffService.save(staff);
            return R.ok();
        }
        staffEntity.setStaffStatus(Constant.SUPER_STAFF_STATUS);
        sysCompStaffService.updateById(staffEntity);
        return R.ok();
    }

    /**
     * showdoc
     *
     * @catalog 店主端/用户管理
     * @title 取消代理
     * @description 查询用户信息
     * @method put
     * @url https://{{domain}}/caipiao-api/sys/staff/putAgentToUser"
     * @param userId 必选 string 用户id
     * @return {}
     * @remark
     * @number 0
     */
    @PutMapping("putAgentToUser")
    @ApiOperation("取消代理")
    public R putAgentToUser(String userId){
        if(!isShopManager()){
            throw new RRException("只有超级管理员才能取消代理", 401);
        }
        // 修改用户角色,往员工表插入信息
        UserInfo agentUser = userInfoService.getById(userId);
        if (agentUser==null){
            throw new RRException("查无此人");
        }
        agentUser.setUserType(UserType.USER);
        String parentInvitationCode = agentUser.getParentInvitationCode();
        UserInfo userInfo = invitationCodeService.linkAndfreshInvitaAndtionCode(parentInvitationCode);

        agentUser.setManageStaffId(userInfo.getPhone());
        userInfoService.updateById(agentUser);

        SysCompStaffEntity staffEntity = sysCompStaffService.getByUserId(userId);
        staffEntity.setStaffStatus(Constant.COMPANY_SUPER_STAFF_STATUS_VALID);
        sysCompStaffService.updateById(staffEntity);
        return R.ok();
    }

    /**
     * showdoc
     *
     * @catalog 店主端/用户管理
     * @title 换头像
     * @description 换头像
     * @method put
     * @url https://{{domain}}/caipiao-api/sys/staff/avatar
     * @param filepath 必选 string 图片路径
     * @return {}
     * @number 0
     */
    @PutMapping("/staff/avatar")
    @ApiOperation("换头像")
    @AnonymousAccess
    @Transactional
    public R certification(String filepath){
        String userId = getUserId();
        SysCompStaffEntity byId = sysCompStaffService.getById(userId);
        byId.setAvatar(filepath);
        sysCompStaffService.updateById(byId);
        return R.ok();
    }

}
