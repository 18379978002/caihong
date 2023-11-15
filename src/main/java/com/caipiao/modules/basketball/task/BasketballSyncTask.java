package com.caipiao.modules.basketball.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.caipiao.common.component.RedisComponent;
import com.caipiao.common.utils.ReflectUtils;
import com.caipiao.modules.basketball.dto.*;
import com.caipiao.modules.basketball.entity.BasketballMatch;
import com.caipiao.modules.basketball.service.BasketballMatchService;
import com.caipiao.modules.common.dto.League;
import com.caipiao.modules.common.dto.MatchDate;
import com.caipiao.modules.common.dto.PoolList;
import com.caipiao.modules.common.entity.MatchResult;
import com.caipiao.modules.common.service.MatchResultService;
import com.caipiao.modules.common.util.MatchStopTimeUtil;
import com.caipiao.modules.football.dto.MatchInfoDTO;
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
import static com.caipiao.common.constants.CacheKeyConstant.BASKETBALL_DATA_KEY;
import static com.caipiao.common.constants.CacheKeyConstant.BASKETBALL_DATA_KEY_PART_MAIN;
import static com.caipiao.modules.common.util.BugReportUtil.sendMsgToDingtalk;

/**
 * @author xiaoyinandan
 * @date 2022/2/26 下午12:57
 */
@Component
@EnableScheduling
@Slf4j
@EnableAsync
public class BasketballSyncTask {
    @Autowired
    RestTemplate httpRestTemplate;
    @Autowired
    BasketballMatchService matchService;
    @Autowired
    RedisComponent redisComponent;
    @Autowired
    MatchResultService matchResultService;

    public void syncResultDetailList(){
        log.info("++++++++++++++++++++开始同步篮球比赛结果数据++++++++++++++++++++");
        StopWatch watch = new StopWatch();
        watch.start();

        LambdaQueryWrapper<BasketballMatch> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BasketballMatch::getMatchStatus, "Payout")
                .eq(BasketballMatch::getResultSyncFlag, 0);
        List<BasketballMatch> list = matchService.list(wrapper);

        if(CollUtil.isNotEmpty(list)){
            for (BasketballMatch baseketballMatch : list) {
                BasketballResultDetailDTO forObject = httpRestTemplate.getForObject(String.format(BASKETBALL_MATCH_RESULT_DETAIL_LIST_URL, baseketballMatch.getMatchId()), BasketballResultDetailDTO.class);
                assert forObject != null;

                List<MatchResult> results = new ArrayList<>();
                MatchResult matchResult = forObject.getValue().getMatchResult();
                //List<ResultDetailDTO>
                Object hiloResultList = matchResult.getHiloResultList();
                Object mnlResultList = matchResult.getMnlResultList();

                if(mnlResultList instanceof LinkedHashMap){
                    log.info("无此类似比赛结果");
                }else if(mnlResultList instanceof ArrayList){
                    MatchResult matchResult2 = new MatchResult();
                    BeanUtils.copyProperties(matchResult, matchResult2);
                    List<ResultDetailDTO> resultDetailDTOS = JSONArray.parseArray(JSON.toJSONString(mnlResultList), ResultDetailDTO.class);
                    matchResult2.setCode(resultDetailDTOS.get(0).getPoolCode());
                    matchResult2.setOdds(resultDetailDTOS.get(0).getOdds());
                    matchResult2.setOddsType(resultDetailDTOS.get(0).getOddsType());
                    matchResult2.setCombination(resultDetailDTOS.get(0).getCombination());
                    matchResult2.setPoolId(IdUtil.getSnowflake(1,1).nextId());
                    results.add(matchResult2);
                }

                //List<ResultDetailDTO>
                 Object wnmResultList = matchResult.getWnmResultList();
                //List<ResultDetailDTO>
                 Object hdcResultList = matchResult.getHdcResultList();

                 if(hdcResultList instanceof LinkedHashMap){
                 }else {
                     List<ResultDetailDTO> resultDetailDTOS = JSONArray.parseArray(JSON.toJSONString(hdcResultList), ResultDetailDTO.class);
                     matchResult.setCode(resultDetailDTOS.get(0).getPoolCode());
                     matchResult.setOdds(resultDetailDTOS.get(0).getOdds());
                     matchResult.setOddsType(resultDetailDTOS.get(0).getOddsType());
                     matchResult.setCombination(resultDetailDTOS.get(0).getCombination());
                 }

                matchResult.setPoolId(IdUtil.getSnowflake(1,1).nextId());
                results.add(matchResult);

                MatchResult matchResult1 = new MatchResult();
                BeanUtils.copyProperties(matchResult, matchResult1);

                if(hiloResultList instanceof LinkedHashMap){
                }else {
                    List<ResultDetailDTO> resultDetailDTOS = JSONArray.parseArray(JSON.toJSONString(hiloResultList), ResultDetailDTO.class);
                    matchResult1.setCode(resultDetailDTOS.get(0).getPoolCode());
                    matchResult1.setOdds(resultDetailDTOS.get(0).getOdds());
                    matchResult1.setOddsType(resultDetailDTOS.get(0).getOddsType());
                    matchResult1.setCombination(resultDetailDTOS.get(0).getCombination());
                }

                matchResult1.setPoolId(IdUtil.getSnowflake(1,1).nextId());
                results.add(matchResult1);



                MatchResult matchResult3 = new MatchResult();
                BeanUtils.copyProperties(matchResult, matchResult3);

                if(wnmResultList instanceof LinkedHashMap){
                }else {
                    List<ResultDetailDTO> resultDetailDTOS = JSONArray.parseArray(JSON.toJSONString(wnmResultList), ResultDetailDTO.class);
                    matchResult3.setCode(resultDetailDTOS.get(0).getPoolCode());
                    matchResult3.setOdds(resultDetailDTOS.get(0).getOdds());
                    matchResult3.setOddsType(resultDetailDTOS.get(0).getOddsType());
                    matchResult3.setCombination(resultDetailDTOS.get(0).getCombination());
                }


                matchResult3.setPoolId(IdUtil.getSnowflake(1,1).nextId());
                results.add(matchResult3);

                matchResultService.saveBatch(results);

                //更新为已同步
                baseketballMatch.setResultSyncFlag(1);
                matchService.updateById(baseketballMatch);
            }
        }


        watch.stop();
        log.info("++++++++++++++++++++结束同步篮球比赛结果数据,共计用时:{}毫秒++++++++++++++++++++", watch.getTotalTimeMillis());
    }

    //1
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void syncResult(){
        log.info("++++++++++++++++++++开始同步篮球比赛结果数据++++++++++++++++++++");
        StopWatch watch = new StopWatch();
        watch.start();
        try{
            String endDate = DateUtil.format(new Date(), "yyyy-MM-dd");
            String startDate = DateUtil.format(DateUtil.offsetDay(new Date(), -2), "yyyy-MM-dd");

            BasketballResultDTO forObject = httpRestTemplate.getForObject(String.format(BASKETBALL_RESULT_URL, startDate, endDate), BasketballResultDTO.class);
            assert forObject != null;
            List<BasketballMatchResult> matchResult = forObject.getValue().getMatchResult();

            List<BasketballMatch> collect = matchResult.stream().map(r -> {
                BasketballMatch basketballMatch = new BasketballMatch();
                basketballMatch.setMatchId(r.getMatchId());
                if(r.getFinalScore().equals("取消")){
                    basketballMatch.setMatchStatus("cancel");
                }else{
                    basketballMatch.setMatchStatus(r.getPoolStatus());
                }

                return basketballMatch;
            }).collect(Collectors.toList());

            matchService.updateBatchById(collect);


        }catch (Exception e){
            sendMsgToDingtalk(e);
        }

        syncResultDetailList();

        watch.stop();
        log.info("++++++++++++++++++++结束同步篮球比赛结果数据,共计用时:{}毫秒++++++++++++++++++++", watch.getTotalTimeMillis());
    }

    //1
    @Scheduled(cron = "0 0/5 * * * ?")
    public void syncCurrentMatchData(){
        log.info("++++++++++++++++++++开始同步篮球比赛实时数据++++++++++++++++++++");
        StopWatch watch = new StopWatch();
        watch.start();
        BasketballResultDTO forObject = null;
        try{
            forObject = httpRestTemplate.getForObject(BASKETBALL_DATA_URL, BasketballResultDTO.class);
        }catch (Exception e){
            sendMsgToDingtalk(e);
        }
        assert forObject != null;
        List<MatchInfoDTO<BasketballMatchDTO>> matchInfoList = forObject.getValue().getMatchInfoList();

        if(CollUtil.isEmpty(matchInfoList)){
            BasketballMatchData data = new BasketballMatchData();
            data.setLeagueList(new ArrayList<>());
            data.setMatchDateList(new ArrayList<>());
            data.setTotalCount(0L);
            redisComponent.set(BASKETBALL_DATA_KEY, JSON.toJSONString(data));
            redisComponent.set(BASKETBALL_DATA_KEY_PART_MAIN, JSON.toJSONString(new ArrayList<>()));
            return;
        }

        List<MatchDate> matchDateList = forObject.getValue().getMatchDateList();
        List<League> leagueList = forObject.getValue().getLeaguesList();
        BasketballMatchData data = new BasketballMatchData();

        data.setLeagueList(leagueList);
        data.setMatchDateList(matchDateList);
        data.setTotalCount(forObject.getValue().getTotalCount());
        redisComponent.set(BASKETBALL_DATA_KEY, JSON.toJSONString(data));

        List<BasketballMatch> basketballMatchList = new ArrayList<>();
        boolean isEdit = false;
        for (MatchInfoDTO<BasketballMatchDTO> matchInfo : matchInfoList) {
            List<BasketballMatchDTO> subMatchList = matchInfo.getSubMatchList();
            for (BasketballMatchDTO basketballMatchDTO : subMatchList) {
                BasketballMatch basketballMatch = new BasketballMatch();
                BeanUtils.copyProperties(basketballMatchDTO, basketballMatch);
                setExtraField(basketballMatch, basketballMatchDTO);
                basketballMatch.setStopPrintTime(MatchStopTimeUtil.getStopPrintTime(basketballMatch.getMatchDate() + " " + basketballMatch.getMatchTime()));
                if(DateUtil.parse(basketballMatch.getStopPrintTime()).getTime()<System.currentTimeMillis()){
                    isEdit = true;
                    continue;
                }
                basketballMatchList.add(basketballMatch);
            }
        }


        if(isEdit){
            if(CollUtil.isEmpty(basketballMatchList)){
                data.setLeagueList(new ArrayList<>());
                data.setMatchDateList(new ArrayList<>());
                data.setTotalCount(0L);
                redisComponent.set(BASKETBALL_DATA_KEY, JSON.toJSONString(data));
                redisComponent.set(BASKETBALL_DATA_KEY_PART_MAIN, JSON.toJSONString(new ArrayList<>()));
                return;
            }

            Map<String, List<BasketballMatch>> collect = basketballMatchList.stream().collect(Collectors.groupingBy(BasketballMatch::getBusinessDate));

            Set<String> strings = collect.keySet();
            Map<Long, List<BasketballMatch>> collect1 = basketballMatchList.stream().collect(Collectors.groupingBy(BasketballMatch::getLeagueId));

            List<String> stringStream = collect1.keySet().stream().map(String::valueOf).collect(Collectors.toList());

            matchDateList.removeIf(next -> !strings.contains(next.getBusinessDate()));
            leagueList.removeIf(next -> !stringStream.contains(next.getLeagueId()));

            data.setLeagueList(leagueList);
            data.setMatchDateList(matchDateList);
            data.setTotalCount((long) basketballMatchList.size());
            redisComponent.set(BASKETBALL_DATA_KEY, JSON.toJSONString(data));
        }



        matchService.saveOrUpdateBatch(basketballMatchList);
        redisComponent.set(BASKETBALL_DATA_KEY_PART_MAIN, JSON.toJSONString(basketballMatchList));
        watch.stop();
        log.info("++++++++++++++++++++结束同步篮球比赛实时数据,共计用时:{}毫秒++++++++++++++++++++", watch.getTotalTimeMillis());
    }



    private void setExtraField(BasketballMatch basketballMatch, BasketballMatchDTO basketballMatchDTO){
        basketballMatch.setHdc(getHdcString(basketballMatchDTO.getHdc()));
        basketballMatch.setHilo(getHiloString(basketballMatchDTO.getHilo()));
        if(!ReflectUtils.isAllFieldNull(basketballMatchDTO.getWnm(), Wnm.class)){
            basketballMatch.setWnm(getWnmString(basketballMatchDTO.getWnm()));
        }else{
            basketballMatch.setWnm("");
        }

        if(!ReflectUtils.isAllFieldNull(basketballMatchDTO.getMnl(), Mnl.class)){
            basketballMatch.setMnl(getMnlString(basketballMatchDTO.getMnl()));
        }else{
            basketballMatch.setMnl("");
        }


        List<PoolList> poolList = basketballMatchDTO.getPoolList();
        setSingle(basketballMatch, poolList);
    }

    /**
     * 设置单关
     * @param basketballMatch
     * @param poolList
     */
    private void setSingle(BasketballMatch basketballMatch, List<PoolList> poolList) {
        for (PoolList list : poolList) {
            switch(list.getPoolCode()){
                case "WNM":
                    basketballMatch.setWnmSingle(list.getSingle());
                    break;
                case "HDC":
                    basketballMatch.setHdcSingle(list.getSingle());
                    break;
                case "HILO":
                    basketballMatch.setHiloSingle(list.getSingle());
                    break;
                case "MNL":
                    basketballMatch.setMnlSingle(list.getSingle());
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 组装让分胜负参数
     * @param hdc
     * @return
     */
    private String getHdcString(Hdc hdc){
        return hdc.getA() + "," +
                hdc.getGoalLine() + "," +
                hdc.getH();
    }

    /**
     * 组装胜负参数
     * @param mnl
     * @return
     */
    private String getMnlString(Mnl mnl){
        return mnl.getA() + "," +
                mnl.getH();
    }

    /**
     * 组装胜分差参数
     * @param wnm
     * @return
     */
    private String getWnmString(Wnm wnm){
        return wnm.getL1() + "," +
                wnm.getL2() + "," +
                wnm.getL3() + "," +
                wnm.getL4() + "," +
                wnm.getL5() + "," +
                wnm.getL6() + "|" +
                wnm.getW1() + "," +
                wnm.getW2() + "," +
                wnm.getW3() + "," +
                wnm.getW4() + "," +
                wnm.getW5() + "," +
                wnm.getW6();
    }

    /**
     * 组装大小分参数
     * @param hilo
     * @return
     */
    private String getHiloString(Hilo hilo){
        return hilo.getH() + "," +
                hilo.getGoalLine() + "," +
                hilo.getL();
    }

}
