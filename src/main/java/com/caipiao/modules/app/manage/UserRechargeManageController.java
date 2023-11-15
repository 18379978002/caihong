package com.caipiao.modules.app.manage;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caipiao.common.utils.PageUtils;
import com.caipiao.common.utils.Query;
import com.caipiao.common.utils.R;
import com.caipiao.modules.app.dto.RechargeCountDTO;
import com.caipiao.modules.app.entity.UserRechargeRecord;
import com.caipiao.modules.app.service.UserRechargeRecordService;
import com.caipiao.modules.common.dto.CommonLotteryDTO;
import com.caipiao.modules.sys.controller.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xiaoyinandan
 * @date 2022/2/8 下午8:23
 */
@RestController
@RequestMapping("/manage/userrecharge")
@Api(tags = "《管理端》用户充值管理")
@Slf4j
@RequiredArgsConstructor
public class UserRechargeManageController extends AbstractController {

    private final UserRechargeRecordService userRechargeRecordService;


    @GetMapping("list")
    @ApiOperation("查看客户消费明细")
    public R getList(@RequestParam Map<String, Object> params){
        IPage<UserRechargeRecord> page = new Query<UserRechargeRecord>().getPage(params);

        Object paymentType = params.get("paymentType");
        String userId = (String) params.get("userId");
        String staffId = (String) params.get("staffId");

        if(!isShopManager()){
            userId = getUserId();
        }

        List<String> userIds = new ArrayList<>();

        if(isShopManager() && StringUtils.isNotBlank(staffId)){
            userIds = queryStaffManageList(staffId);
        }

        QueryWrapper<UserRechargeRecord> ne = new QueryWrapper<UserRechargeRecord>()
                .eq(StringUtils.isNotBlank(userId),"user_id", userId)
                .eq(paymentType != null && StringUtils.isNotBlank((CharSequence) paymentType), "payment_type", paymentType);

        if(CollUtil.isNotEmpty(userIds) && StringUtils.isEmpty(userId)){
            ne.in("user_id", userIds);
        }

        IPage<UserRechargeRecord> page1 = userRechargeRecordService.page(page, ne);

        return ok().put("page", new PageUtils<>(page1));
    }

    @PostMapping("queryCountData")
    @ApiOperation("查询充值业绩")
    public R queryCountData(@RequestBody CommonLotteryDTO dto){

        if(isShopManager() && StringUtils.isBlank(dto.getStaffId())){
            dto.setUserIds(null);
        } else{
            String staffId = getUserId();
            if(isShopManager()){
                staffId = dto.getStaffId();
            }

            List<String> collection = queryStaffManageList(staffId);
            if(CollUtil.isEmpty(collection)){
                //0代表没有权限
                return ok().put(new RechargeCountDTO());
            }else{
                dto.setUserIds(collection);
            }
        }
        List<RechargeCountDTO> rechargeCountDTO = userRechargeRecordService.queryCountData(dto);
        return ok().put(rechargeCountDTO);
    }


}
