package com.caipiao.modules.basketball.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caipiao.common.component.RedisComponent;
import com.caipiao.common.utils.R;
import com.caipiao.modules.app.controller.AppAbstractController;
import com.caipiao.modules.basketball.dto.BasketballMatchData;
import com.caipiao.modules.basketball.entity.BasketballMatch;
import com.caipiao.modules.basketball.service.BasketballMatchService;
import com.caipiao.modules.common.util.MatchStopTimeUtil;
import com.caipiao.modules.football.entity.FootballMatch;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.caipiao.common.constants.CacheKeyConstant.BASKETBALL_DATA_KEY;
import static com.caipiao.common.constants.CacheKeyConstant.BASKETBALL_DATA_KEY_PART_MAIN;

/**
 * @author xiaoyinandan
 * @date 2022/2/25 下午9:41
 */
@RestController
@RequestMapping("/app/basketball/match")
@Api(tags = "《app端》篮球比赛管理")
@Slf4j
@RequiredArgsConstructor
public class BasketballMatchController extends AppAbstractController {
    private final BasketballMatchService matchService;
    private final RedisComponent redisComponent;

    @GetMapping("getbasketballmatchlist")
    @ApiOperation("获取篮球比赛数据")
    public R getFootballMatchList(){
        log.info("获取篮球比赛数据^_^");
        String basketballData = (String) redisComponent.get(BASKETBALL_DATA_KEY);

        String basketballDataString = (String) redisComponent.get(BASKETBALL_DATA_KEY_PART_MAIN);
        List<BasketballMatch> list;
        if(StringUtils.isNotBlank(basketballDataString)){
            list = JSONArray.parseArray(basketballDataString, BasketballMatch.class);
        }else{
            LambdaQueryWrapper<BasketballMatch> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BasketballMatch::getMatchStatus, "Selling");
            list = matchService.list(wrapper);

        }

//        LambdaQueryWrapper<BasketballMatch> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(BasketballMatch::getMatchStatus, "Selling");
//        List<BasketballMatch> list = matchService.list(wrapper);
//        Map<String, List<BasketballMatch>> collect = list.stream().collect(Collectors.groupingBy(BasketballMatch::getBusinessDate));
//        BasketballMatchData data = new BasketballMatchData();
//
//        data.setMatchList(collect);

        if(CollUtil.isNotEmpty(list)){
            redisComponent.set(BASKETBALL_DATA_KEY_PART_MAIN, JSON.toJSONString(list));
            Map<String, List<BasketballMatch>> collect = list.stream().collect(Collectors.groupingBy(BasketballMatch::getBusinessDate));
            BasketballMatchData data = JSONObject.parseObject(basketballData, BasketballMatchData.class);
            data.setMatchList(collect);
            return ok().put(data);
        }
        return ok();
    }




}
