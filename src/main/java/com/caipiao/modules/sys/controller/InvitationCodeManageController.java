package com.caipiao.modules.sys.controller;

import com.caipiao.common.utils.PageUtils;
import com.caipiao.common.utils.R;
import com.caipiao.common.validator.ValidatorUtils;
import com.caipiao.common.validator.group.UpdateGroup;
import com.caipiao.modules.app.service.InvitationCodeService;
import com.caipiao.modules.sys.entity.InvitationCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author xiaoyinandan
 * @date 2022/2/10 下午4:05
 */
@RestController
@RequestMapping("manage/invitationcode")
@Api(tags = "邀请码管理《后台》")
@RequiredArgsConstructor
public class InvitationCodeManageController extends AbstractController {

    private final InvitationCodeService invitationCodeService;
//
//    @ApiOperation("批量新增")
//    @PostMapping("batchAdd")
//    public R batchAdd(@RequestParam("num")Integer num){
//
//        if(num<=0){
//            return error("数量必须大于0");
//        }
//
//        invitationCodeService.batchAdd(num);
//        return ok();
//    }
//
//    @ApiOperation("修改")
//    @PutMapping
//    public R update(@RequestBody InvitationCode gradeInfo){
//        ValidatorUtils.validateEntity(gradeInfo, UpdateGroup.class);
//        invitationCodeService.updateById(gradeInfo);
//        return ok();
//    }
//
//    @ApiOperation("删除")
//    @DeleteMapping("batchDelete")
//    public R del(@RequestBody List<Long> ids){
//       invitationCodeService.removeByIds(ids);
//       return ok();
//    }
//
//    @ApiOperation("查询")
//    @GetMapping("{id}")
//    public R get(@PathVariable Long id){
//        InvitationCode info = invitationCodeService.getById(id);
//        return ok().put(info);
//    }
//
//    @ApiOperation("分页查询")
//    @GetMapping("list")
//    public R list(@RequestParam Map<String, Object> params){
//        PageUtils pageUtils = invitationCodeService.queryPage(params);
//        return ok().put("page", pageUtils);
//    }
}
