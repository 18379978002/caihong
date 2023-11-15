package com.caipiao.common.constants;

/**
 * @author xiaoyinandan
 * @date 2022/2/25 下午8:56
 */
public class ApiConstant {
    /** 足球数据请求地址 **/
    public static final String FOOTBALL_DATA_URL = "https://webapi.sporttery.cn/gateway/jc/football/getMatchCalculatorV1.qry?poolCode=&channel=c";
    /** 足球比赛结果请求地址 **/
    public static final String FOOTBALL_RESULT_URL = "https://webapi.sporttery.cn/gateway/jc/football/getMatchResultV1.qry?matchPage=1&matchBeginDate=%s&matchEndDate=%s&leagueId=&pageSize=120&pageNo=1&isFix=0&pcOrWap=1";

    /** 足球比赛结果详情列表请求地址 **/
    public static final String FOOTBALL_MATCH_RESULT_DETAIL_LIST_URL = "https://webapi.sporttery.cn/gateway/jc/football/getFixedBonusV1.qry?clientCode=3001&matchId=%s";

    /** 篮球球比赛结果详情列表请求地址 **/
    public static final String BASKETBALL_MATCH_RESULT_DETAIL_LIST_URL = "https://webapi.sporttery.cn/gateway/jc/basketball/getFixedBonusV2.qry?clientCode=3001&matchId=%s";

    /** 篮球数据请求地址 **/
    public static final String BASKETBALL_DATA_URL = "https://webapi.sporttery.cn/gateway/jc/basketball/getMatchCalculatorV1.qry?poolCode=&channel=c";
    /** 篮球比赛结果请求地址 **/
    public static final String BASKETBALL_RESULT_URL = "https://webapi.sporttery.cn/gateway/jc/basketball/getMatchResultV1.qry?matchPage=1&matchBeginDate=%s&matchEndDate=%s&leagueId=&pageSize=120&pageNo=1&isFix=0&pcOrWap=1";


    /** 排列三结果数据 **/
    public static final String PERMUTATION_RESULT = "https://webapi.sporttery.cn/gateway/lottery/getHistoryPageListV1.qry?gameNo=35&provinceId=0&pageSize=1&isVerify=1&pageNo=1";

    /** 大乐透结果数据 **/
    public static final String LOTTERY_RESULT = "https://webapi.sporttery.cn/gateway/lottery/getHistoryPageListV1.qry?gameNo=85&provinceId=0&pageSize=1&isVerify=1&pageNo=1";


}
