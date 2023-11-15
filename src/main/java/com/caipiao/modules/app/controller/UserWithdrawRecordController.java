package com.caipiao.modules.app.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caipiao.common.exception.RRException;
import com.caipiao.common.utils.MD5Util;
import com.caipiao.common.utils.R;
import com.caipiao.common.validator.ValidatorUtils;
import com.caipiao.modules.app.dto.UserWithDrawRecordDTO;
import com.caipiao.modules.app.enmu.WithdrawalRecord;
import com.caipiao.modules.app.entity.UserInfo;
import com.caipiao.modules.app.entity.UserWithdrawRecord;
import com.caipiao.modules.app.service.UserInfoService;
import com.caipiao.modules.app.service.UserWithdrawRecordService;
import com.caipiao.modules.common.entity.CountData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaoyinandan
 * @date 2022/2/21 下午3:48
 */
@RestController
@RequestMapping("/app/userwithdrawrecord")
@Api(tags = "《app端》用户提现管理")
@Slf4j
@RequiredArgsConstructor
public class UserWithdrawRecordController extends AppAbstractController {
    private final UserWithdrawRecordService userWithdrawRecordService;
    private final UserInfoService userInfoService;

    /**
     * 提现申请
     * @param record
     * @return
     */
    @PostMapping("apply")
    @ApiOperation("提现申请")
    public R apply(@RequestBody UserWithdrawRecord record){
        ValidatorUtils.validateEntity(record);
        //1分销佣金提现
        record.setWithdrawType("1");
        //1为银行卡
        record.setPaymentMethod("1");
        //0为审核中
        record.setStatus("0");
        record.setUserId(getUser().getId());
        //校验提现金额是否合格
        UserInfo userInfo = userInfoService.getById(getUser().getId());

        if(!userInfo.getPayPassword().equals(MD5Util.getMD5AndSalt(record.getPayPassword()))){
            return error("支付密码错误");
        }

        //校验金额是否足够
        if(record.getApplyAmount().compareTo(userInfo.getBalance().subtract(userInfo.getBonus()))>0){
            return error("余额不足");
        }


        userWithdrawRecordService.save(record);

        return ok();
    }

    /**
     * 查看提现记录
     * @return
     */
    @GetMapping("tobewithdrawn")
    @ApiOperation("查看提现记录")
    public R toBeWithDrawn(WithdrawalRecord withdrawalRecord){

        LambdaQueryWrapper<UserWithdrawRecord> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(UserWithdrawRecord::getUserId,getUserId())
                .eq(WithdrawalRecord.S.equals(withdrawalRecord),UserWithdrawRecord::getStatus,"0")
                .in(WithdrawalRecord.J.equals(withdrawalRecord),UserWithdrawRecord::getStatus,"1","2")
                .eq(WithdrawalRecord.C.equals(withdrawalRecord),UserWithdrawRecord::getStatus,"3");

        List<UserWithdrawRecord> userWithdrawRecords = userWithdrawRecordService.list(wrapper);
        if (CollectionUtils.isEmpty(userWithdrawRecords)) {
            return ok();
        }
        List<UserWithDrawRecordDTO> userWithDrawRecordDTOS = UserWithDrawRecordDTO.coverUserWithDrawRecordDTOS(userWithdrawRecords);

        return ok().put(userWithDrawRecordDTOS);
    }

    /**
     * 撤销提现
     * @return
     */
    @GetMapping("withdrawal")
    @ApiOperation("撤销提现")
    public R withdrawal(@RequestBody List<String> ids){
        if (CollectionUtils.isEmpty(ids)) {
            throw new RRException("请选择您要撤回的提现记录");
        }
        List<UserWithdrawRecord> userWithdrawRecords = userWithdrawRecordService.listByIds(ids);
        if (CollectionUtils.isEmpty(userWithdrawRecords)) {
            throw new RRException("错误的撤回邀请");
        }

        List<UserWithdrawRecord> collect = userWithdrawRecords.stream().map(s -> {
            s.setStatus("3");
            return s;
        }).collect(Collectors.toList());
        userWithdrawRecordService.updateBatchById(collect);
        return ok();
    }
}
