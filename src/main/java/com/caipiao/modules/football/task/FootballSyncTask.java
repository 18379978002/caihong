package com.caipiao.modules.football.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.StopWatch;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caipiao.common.component.RedisComponent;
import com.caipiao.common.utils.ReflectUtils;
import com.caipiao.common.utils.StringUtils;
import com.caipiao.modules.common.dto.League;
import com.caipiao.modules.common.dto.MatchDate;
import com.caipiao.modules.common.dto.PoolList;
import com.caipiao.modules.common.entity.MatchResult;
import com.caipiao.modules.common.service.MatchResultService;
import com.caipiao.modules.common.util.MatchStopTimeUtil;
import com.caipiao.modules.football.dto.*;
import com.caipiao.modules.football.entity.FootballMatch;
import com.caipiao.modules.football.service.FootballMatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static com.caipiao.common.constants.ApiConstant.*;
import static com.caipiao.common.constants.CacheKeyConstant.*;
import static com.caipiao.modules.common.util.BugReportUtil.sendMsgToDingtalk;

/**
 * @author xiaoyinandan
 * @date 2022/2/26 下午12:57
 */
@Component
@EnableScheduling
@Slf4j
@EnableAsync
public class FootballSyncTask {
    @Autowired
    RestTemplate httpRestTemplate;
    @Autowired
    FootballMatchService matchService;
    @Autowired
    RedisComponent redisComponent;
    @Autowired
    MatchResultService matchResultService;

    public void syncResultDetailList(){
        log.info("++++++++++++++++++++开始同步足球比赛结果数据++++++++++++++++++++");
        StopWatch watch = new StopWatch();
        watch.start();

        LambdaQueryWrapper<FootballMatch> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FootballMatch::getMatchStatus, "Payout")
                .eq(FootballMatch::getResultSyncFlag, 0);
        List<FootballMatch> list = matchService.list(wrapper);

        if(CollUtil.isNotEmpty(list)){
            for (FootballMatch footballMatch : list) {
                try {
                    FootballResultDTO forObject = httpRestTemplate.getForObject(String.format(FOOTBALL_MATCH_RESULT_DETAIL_LIST_URL, footballMatch.getMatchId()), FootballResultDTO.class);
                    assert forObject != null;
                    List<MatchResult> matchResultList = forObject.getValue().getMatchResultList();

                    if(CollUtil.isNotEmpty(matchResultList)){
                        matchResultService.saveOrUpdateBatch(matchResultList);
                        footballMatch.setResultSyncFlag(1);
                        matchService.updateById(footballMatch);
                    }
                }catch (Exception e){
                    sendMsgToDingtalk(e);
                }
            }
        }


        watch.stop();
        log.info("++++++++++++++++++++结束同步足球比赛结果数据,共计用时:{}毫秒++++++++++++++++++++", watch.getTotalTimeMillis());
    }

    //1
    @Scheduled(cron = "0 0 */1 * * ?")
    public void syncResult(){
        log.info("++++++++++++++++++++开始同步足球比赛结果数据++++++++++++++++++++");
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            String endDate = DateUtil.format(new Date(), "yyyy-MM-dd");
            String startDate = DateUtil.format(DateUtil.offsetDay(new Date(), -2), "yyyy-MM-dd");

            FootballResultDTO forObject = httpRestTemplate.getForObject(String.format(FOOTBALL_RESULT_URL, startDate, endDate), FootballResultDTO.class);
            assert forObject != null;
            List<FootballMatchResult> matchResult = forObject.getValue().getMatchResult();

            List<FootballMatch> collect = matchResult.stream().map(r -> {
                FootballMatch footballMatch = new FootballMatch();
                footballMatch.setMatchId(r.getMatchId());

                if(StringUtils.isBlank(r.getPoolStatus())){

                    if(r.getSectionsNo999().equals("取消")){
                        footballMatch.setMatchStatus("cancel");
                    }else{
                        footballMatch.setMatchStatus(r.getMatchResultStatus().equals("2")?"Payout":"closed");
                    }
                }else {
                    footballMatch.setMatchStatus(r.getPoolStatus());
                }


                return footballMatch;
            }).collect(Collectors.toList());

            matchService.updateBatchById(collect);
        }catch (Exception e){
            sendMsgToDingtalk(e);
        }


        syncResultDetailList();

        watch.stop();
        log.info("++++++++++++++++++++结束同步足球比赛结果数据,共计用时:{}毫秒++++++++++++++++++++", watch.getTotalTimeMillis());
    }
    //1
    @Scheduled(cron = "0 0/5 * * * ?")
    public void syncCurrentMatchData(){
        log.info("++++++++++++++++++++开始同步足球比赛实时数据++++++++++++++++++++");
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            FootballResultDTO forObject = httpRestTemplate.getForObject(FOOTBALL_DATA_URL, FootballResultDTO.class);
            assert forObject != null;
            List<MatchInfoDTO<FootballMatchDTO>> matchInfoList = forObject.getValue().getMatchInfoList();

            if(CollUtil.isEmpty(matchInfoList)){
                FootballMatchData data = new FootballMatchData();
                data.setLeagueList(new ArrayList<>());
                data.setMatchDateList(new ArrayList<>());
                data.setTotalCount(0L);
                redisComponent.set(FOOTBALL_DATA_KEY, JSON.toJSONString(data));
                redisComponent.set(FOOTBALL_DATA_KEY_PART_MAIN, JSON.toJSONString(new ArrayList<>()));
                return;
            }

            List<MatchDate> matchDateList = forObject.getValue().getMatchDateList();
            List<League> leagueList = forObject.getValue().getLeagueList();
            Long totalCount = forObject.getValue().getTotalCount();
            FootballMatchData data = new FootballMatchData();

            data.setLeagueList(leagueList);
            data.setMatchDateList(matchDateList);
            data.setTotalCount(totalCount);

            redisComponent.set(FOOTBALL_DATA_KEY, JSON.toJSONString(data));

            List<FootballMatch> footballMatchList = new ArrayList<>();

            boolean isEdit = false;

            for (MatchInfoDTO<FootballMatchDTO> matchInfo : matchInfoList) {
                List<FootballMatchDTO> subMatchList = matchInfo.getSubMatchList();
                for (FootballMatchDTO footballMatchDTO : subMatchList) {
                    FootballMatch footballMatch = new FootballMatch();
                    BeanUtils.copyProperties(footballMatchDTO, footballMatch);
                    setExtraField(footballMatch, footballMatchDTO);
                    footballMatch.setStopPrintTime(MatchStopTimeUtil.getStopPrintTime(footballMatch.getMatchDate() + " " + footballMatch.getMatchTime()));

                    if(DateUtil.parse(footballMatch.getStopPrintTime()).getTime()<System.currentTimeMillis()){
                        isEdit = true;
                        continue;
                    }

                    footballMatchList.add(footballMatch);
                }
            }

            if(isEdit){
                if(CollUtil.isEmpty(footballMatchList)){
                    data.setLeagueList(new ArrayList<>());
                    data.setMatchDateList(new ArrayList<>());
                    data.setTotalCount(0L);
                    redisComponent.set(FOOTBALL_DATA_KEY, JSON.toJSONString(data));
                    redisComponent.set(FOOTBALL_DATA_KEY_PART_MAIN, JSON.toJSONString(new ArrayList<>()));
                    return;
                }

                Map<String, List<FootballMatch>> collect = footballMatchList.stream().collect(Collectors.groupingBy(FootballMatch::getBusinessDate));
                Map<Long, List<FootballMatch>> collect1 = footballMatchList.stream().collect(Collectors.groupingBy(FootballMatch::getLeagueId));

                Set<String> strings = collect.keySet();
                List<String> stringStream = collect1.keySet().stream().map(String::valueOf).collect(Collectors.toList());

                matchDateList.removeIf(next -> !strings.contains(next.getBusinessDate()));
                leagueList.removeIf(next -> !stringStream.contains(next.getLeagueId()));

                data.setLeagueList(leagueList);
                data.setMatchDateList(matchDateList);
                data.setTotalCount((long) footballMatchList.size());
                redisComponent.set(FOOTBALL_DATA_KEY, JSON.toJSONString(data));
            }

            matchService.saveOrUpdateBatch(footballMatchList);
            redisComponent.set(FOOTBALL_DATA_KEY_PART_MAIN, JSON.toJSONString(footballMatchList));
        }catch (Exception e){
            sendMsgToDingtalk(e);
        }

        watch.stop();
        log.info("++++++++++++++++++++结束同步足球比赛实时数据,共计用时:{}毫秒++++++++++++++++++++", watch.getTotalTimeMillis());
    }

    private void setExtraField(FootballMatch footballMatch, FootballMatchDTO footballMatchDTO){
        //胜平负
        if(!ReflectUtils.isAllFieldNull(footballMatchDTO.getHad(), Had.class)){
            footballMatch.setHad(getHadString(footballMatchDTO.getHad()));
        }else{
            footballMatch.setHad("");
        }

        footballMatch.setHhad(getHhadString(footballMatchDTO.getHhad()));
        footballMatch.setTtg(getTtgString(footballMatchDTO.getTtg()));
        footballMatch.setHafu(getHafuString(footballMatchDTO.getHafu()));
        footballMatch.setCrs(getCrsString(footballMatchDTO.getCrs()));

        //胜平负单关
        List<PoolList> poolList = footballMatchDTO.getPoolList();
        setSingle(footballMatch, poolList);
    }

    /**
     * 设置单关
     * @param footballMatch
     * @param poolList
     */
    private void setSingle(FootballMatch footballMatch, List<PoolList> poolList) {
        for (PoolList list : poolList) {
            switch(list.getPoolCode()){
                case "HAD":
                    footballMatch.setHadSingle(list.getSingle());
                    break;
                case "HHAD":
                    footballMatch.setHhadSingle(list.getSingle());
                    break;
                case "CRS":
                    footballMatch.setCrsSingle(list.getSingle());
                    break;
                case "TTG":
                    footballMatch.setTtgSingle(list.getSingle());
                    break;
                case "HAFU":
                    footballMatch.setHafuSingle(list.getSingle());
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 组装胜平负参数
     * @param had
     * @return
     */
    private String getHadString(Had had){
        return "0" + "," +
                had.getH() + "," +
                had.getD() + "," +
                had.getA();
    }

    /**
     * 组装让球胜平负参数
     * @param hhad
     * @return
     */
    private String getHhadString(Hhad hhad){
        return hhad.getGoalLine() +
                "," +
                hhad.getH() +
                "," +
                hhad.getD() +
                "," +
                hhad.getA();
    }

    /**
     * 组装进球数参数
     * @param ttg
     * @return
     */
    private String getTtgString(Ttg ttg){
        return ttg.getS0() + "," +
                ttg.getS1() + "," +
                ttg.getS2() + "," +
                ttg.getS3() + "," +
                ttg.getS4() + "," +
                ttg.getS5() + "," +
                ttg.getS6() + "," +
                ttg.getS7();
    }

    /**
     * 组装半全场参数
     * @param hafu
     * @return
     */
    private String getHafuString(Hafu hafu){
        return hafu.getHh() + "," +
                hafu.getHd() + "," +
                hafu.getHa() + "," +
                hafu.getDh() + "," +
                hafu.getDd() + "," +
                hafu.getDa() + "," +
                hafu.getAh() + "," +
                hafu.getAd() + "," +
                hafu.getAa();
    }

    /**
     * 组装比分参数
     * @param crs
     * @return
     */
    private String getCrsString(Crs crs){
        return crs.getS01s00() + "," +
                crs.getS02s00() + "," +
                crs.getS02s01() + "," +
                crs.getS03s00() + "," +
                crs.getS03s01() + "," +
                crs.getS03s02() + "," +
                crs.getS04s00() + "," +
                crs.getS04s01() + "," +
                crs.getS04s02() + "," +
                crs.getS05s00() + "," +
                crs.getS05s01() + "," +
                crs.getS05s02() + "," +
                crs.getS1sh() + "|" +
                crs.getS00s00() + "," +
                crs.getS01s01() + "," +
                crs.getS02s02() + "," +
                crs.getS03s03() + "," +
                crs.getS1sd() + "|" +
                crs.getS00s01() + "," +
                crs.getS00s02() + "," +
                crs.getS01s02() + "," +
                crs.getS00s03() + "," +
                crs.getS01s03() + "," +
                crs.getS02s03() + "," +
                crs.getS00s04() + "," +
                crs.getS01s04() + "," +
                crs.getS02s04() + "," +
                crs.getS00s05() + "," +
                crs.getS01s05() + "," +
                crs.getS02s05() + "," +
                crs.getS1sa();
    }


}
