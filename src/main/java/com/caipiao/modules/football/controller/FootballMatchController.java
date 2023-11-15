package com.caipiao.modules.football.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caipiao.common.component.RedisComponent;
import com.caipiao.common.utils.R;
import com.caipiao.common.utils.StringUtils;
import com.caipiao.modules.app.controller.AppAbstractController;
import com.caipiao.modules.common.util.MatchStopTimeUtil;
import com.caipiao.modules.football.dto.FootballMatchData;
import com.caipiao.modules.football.entity.FootballMatch;
import com.caipiao.modules.football.service.FootballMatchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.caipiao.common.constants.CacheKeyConstant.FOOTBALL_DATA_KEY;
import static com.caipiao.common.constants.CacheKeyConstant.FOOTBALL_DATA_KEY_PART_MAIN;

/**
 * @author xiaoyinandan
 * @date 2022/2/25 下午9:41
 */
@RestController
@RequestMapping("/app/football/match")
@Api(tags = "《app端》足球比赛管理")
@Slf4j
@RequiredArgsConstructor
public class FootballMatchController extends AppAbstractController {
    private final FootballMatchService matchService;
    private final RedisComponent redisComponent;

    @GetMapping("getfootballmatchlist")
    @ApiOperation("获取足球比赛数据")
    public R getFootballMatchList(){
        log.info("获取比赛数据^_^");
        String footballData = (String) redisComponent.get(FOOTBALL_DATA_KEY);

        String footballDataString = (String) redisComponent.get(FOOTBALL_DATA_KEY_PART_MAIN);
        List<FootballMatch> list;
        if(StringUtils.isNotBlank(footballDataString)){
            list = JSONArray.parseArray(footballDataString, FootballMatch.class);
        }else{
            LambdaQueryWrapper<FootballMatch> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FootballMatch::getMatchStatus, "Selling");
            list = matchService.list(wrapper);
        }

        if(CollUtil.isNotEmpty(list)){
            redisComponent.set(FOOTBALL_DATA_KEY_PART_MAIN, JSON.toJSONString(list));
            Map<String, List<FootballMatch>> collect = list.stream().collect(Collectors.groupingBy(FootballMatch::getBusinessDate));
            FootballMatchData data = JSONObject.parseObject(footballData, FootballMatchData.class);
            data.setMatchList(collect);
            return ok().put(data);
        }
        return ok();
    }




}
