package com.caipiao.modules.common.controller;

import com.caipiao.common.utils.Constant;
import com.caipiao.common.utils.DateUtils;
import com.caipiao.common.utils.ExtendMath;
import com.caipiao.common.utils.R;
import com.caipiao.modules.app.controller.AppAbstractController;
import com.caipiao.modules.common.dto.AgentDto;
import com.caipiao.modules.common.dto.LotteryBonusDTO;
import com.caipiao.modules.common.dto.PermutationDTO;
import com.caipiao.modules.common.dto.enmu.AllSaleListReqTag;
import com.caipiao.modules.common.entity.CountData;
import com.caipiao.modules.common.entity.CountDataDTO;
import com.caipiao.modules.common.entity.LotteryDTO;
import com.caipiao.modules.common.service.CountDataService;
import com.caipiao.modules.common.util.LotteryCalcUtils;
import com.caipiao.modules.sys.entity.SysCompStaffEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * @author xiaoyinandan
 * @date 2022/2/27 下午9:41
 */
@RestController
@RequestMapping("/app/calc")
@Api(tags = "《app端》彩票计算")
public class LotteryCalcController extends AppAbstractController {

    @Autowired
    private CountDataService countDataService;

    @PostMapping("getlotterybonus")
    @ApiOperation("获取彩票柱数、预计奖金")
    public R getLotteryBonus(@RequestBody LotteryBonusDTO dto){
        List<String> passType = dto.getPassType();//3 2  3串1的3 2串1的2
        Map<String, Object> result = new HashMap<>();
        int tzNumber = 0;
        double maxBonus = 0.0d;
        double minBonus = 0.0d;
        for (String pass : passType) {
            Map<String, Object> calc = LotteryCalcUtils.calc(dto.getMatches(), Integer.parseInt(pass));
            tzNumber += ((int)calc.get("tzNumber"));
            maxBonus += ((double)calc.get("maxBonus"));
            minBonus += ((double)calc.get("minBonus"));
        }

        result.put("maxBonus", maxBonus * dto.getMultiple());
        result.put("minBonus", minBonus * dto.getMultiple());
        result.put("tzNumber", tzNumber * dto.getMultiple());
        result.put("tzMoney", tzNumber * dto.getMultiple() * 2);

        return ok().put(result);
    }

    @PostMapping("getpermutationnumber")
    @ApiOperation("获取排列三柱数")
    public R getLotteryNumber(@RequestBody PermutationDTO dto){

        if(dto.getType() == 1){
            //直选
            List<String> all = ExtendMath.findAll(dto.getDirectSelectData());

            return ok().put(all.size());
        }

        if(dto.getType() == 2){
            //组三
            return ok().put(ExtendMath.nchoosek(dto.getComb3Data(), 2).length * 2);
        }

        if(dto.getType() == 3){
            //组六
            return ok().put(ExtendMath.nchoosek(dto.getComb6Data(), 3).length);
        }


        return ok();
    }

    @PostMapping("getlotterynumber")
    @ApiOperation("获取大乐透柱数")
    public R getLotteryNumber(@RequestBody LotteryDTO dto){
        long total = ExtendMath.nchoosek(dto.getFrontSectionList().size(), 5) * ExtendMath.nchoosek(dto.getBackSectionList().size(), 2);
        return ok().put(total);
    }

    @GetMapping("allSaleList")
    @ApiOperation(value = "龙虎榜",notes = "龙虎榜")
    public R allSaleList(AllSaleListReqTag reqTag){
        Calendar calendar = Calendar.getInstance(); // 获取 Calendar 实例
        calendar.setTime(new Date()); // 将 Calendar 实例设定为当前时间
        calendar.set(Calendar.HOUR_OF_DAY, 0); // 将 Calendar 实例的小时设定为 0
        calendar.set(Calendar.MINUTE, 0); // 将 Calendar 实例的分钟设定为 0
        calendar.set(Calendar.SECOND, 0); // 将 Calendar 实例的秒数设定为 0
        calendar.set(Calendar.MILLISECOND, 0); // 将 Calendar 实例的毫秒数设定为 0

        if(null==reqTag){
            reqTag=AllSaleListReqTag.M;
        }
        if(AllSaleListReqTag.D.equals(reqTag)){
            calendar.add(Calendar.DATE,-1);

        }
        if(AllSaleListReqTag.M.equals(reqTag)){
            calendar.add(Calendar.MONTH,-1);
        }
        Date date = calendar.getTime();
        return R.ok().put(countDataService.getCountSaleList(date,10));
    }

}
