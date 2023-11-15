package com.caipiao.modules.common.util;

import cn.hutool.core.date.StopWatch;
import com.caipiao.modules.order.dto.LotteryParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author xiaoyinandan
 * @date 2022/2/15 下午2:40
 */
@Slf4j
public class LotteryCalcUtils {

    public static void main(String[] args) {
//        List<String> l1 = new ArrayList<>();
//        l1.add("周四002(平@2.03)");
//        l1.add("周四002(胜@3.15)");
//
//
//        List<String> l2 = new ArrayList<>();
//        l2.add("周四006(胜@2.38)");
//
//        List<String> l3 = new ArrayList<>();
//        l3.add("周四005(胜@3.20)");
//
//
//        List<String> l4 = new ArrayList<>();
//        l4.add("周五004(胜@1.49)");
//
//        List<String> l5 = new ArrayList<>();
//        l5.add("周四001(胜@3.80)");
//
//        List<List<String>> ls = new ArrayList<>();
//        ls.add(l4);
//        ls.add(l3);
//        ls.add(l2);
//        ls.add(l1);
//        ls.add(l5);

//        System.out.println(ls);
//        Map<String, Object> calc = calc(ls, 3);
//        System.out.println(JSON.toJSONString(calc));

        List<List<LotteryParam>> ls = new ArrayList<>();
        List<LotteryParam> l1 = new ArrayList<>();
        LotteryParam p1 = new LotteryParam();
        p1.setAwayTeamName("大阪樱花");
        p1.setBetItem("H");
        p1.setHomeTeamName("清水鼓动");
        p1.setMatchId(1011521L);
        p1.setMatchNumStr("周六001");
        p1.setSp("1.84");
        l1.add(p1);

        LotteryParam p2 = new LotteryParam();
        p2.setAwayTeamName("大阪樱花");
        p2.setBetItem("D");
        p2.setHomeTeamName("清水鼓动");
        p2.setMatchId(1011521L);
        p2.setMatchNumStr("周六001");
        p2.setSp("3.30");
        l1.add(p2);

        List<LotteryParam> l2 = new ArrayList<>();

        LotteryParam p3 = new LotteryParam();
        p3.setAwayTeamName("横滨水手");
        p3.setBetItem("H");
        p3.setHomeTeamName("札幌冈萨");
        p3.setMatchId(1011522L);
        p3.setMatchNumStr("周六002");
        p3.setSp("2.18");
        l2.add(p3);

        List<LotteryParam> l3 = new ArrayList<>();
        LotteryParam p4 = new LotteryParam();
        p4.setAwayTeamName("大分三神");
        p4.setBetItem("D");
        p4.setHomeTeamName("长崎航海");
        p4.setMatchId(1011523L);
        p4.setMatchNumStr("周六003");
        p4.setSp("2.70");
        l3.add(p4);
        List<LotteryParam> l4 = new ArrayList<>();
        LotteryParam p5 = new LotteryParam();
        p5.setAwayTeamName("熊本深红");
        p5.setBetItem("D");
        p5.setHomeTeamName("德岛漩涡");
        p5.setMatchId(1011524L);
        p5.setMatchNumStr("周六004");
        p5.setSp("2.72");
        l4.add(p5);


        ls.add(l1);
        ls.add(l2);
        ls.add(l3);
        ls.add(l4);

        StopWatch watch = new StopWatch();
        watch.start();

        List<List<String>> collect = ls.stream()
                .map(s1 -> s1.stream().map(s2 -> s2.getMatchNumStr() + "(" + s2.getBetItem() + "@" + s2.getSp() + ")")
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        Map<String, Object> calc = calc(collect, 2);
//        System.out.println(JSON.toJSONString(calc));


        Set<List<String>> lists = bonusOptimize(ls, 2, 20);
        double ttz = 0d;
        for (List<String> list : lists) {
            ttz += Double.parseDouble(list.get(list.size()-2));
//            log.info("奖金：{}", list.get(list.size()-1));
//            log.info("柱数：{}", list.get(list.size()-2));
        }
        log.info("总柱数：{}", ttz);

        watch.stop();
        log.info("总计用时:{}", watch.getTotalTimeMillis());
    }

    public static Set<List<String>> orderSplit(List<List<LotteryParam>> ls, int length, int multiple){
        List<String> collect = ls.stream()
                .flatMap(Collection::stream)
                .map(item -> item.getMatchId()+"|"
                        +item.getMatchNumStr() + "|"
                        + item.getHomeTeamName() + "|"
                        + item.getAwayTeamName() + "|"
                        + item.getBetItem() + "|"
                        + item.getSp().concat("flag"))
                .collect(Collectors.toList());

        Set<List<String>> lists = permutationNoRepeat(collect, length);

        Set<List<String>> newsets = new HashSet<>();

        for (List<String> set : lists) {
            Set<String> collect1 = set.stream()
                    .map(s -> s.substring(0, 7))
                    .collect(Collectors.toSet());

            if (collect1.size() == length) {
                newsets.add(set);
            }
        }

        return newsets;

    }

    public static Set<List<String>> bonusOptimize(List<List<LotteryParam>> ls, int length, int multiple){
        List<String> collect = ls.stream()
                .flatMap(Collection::stream)
                .map(item -> item.getMatchId()+"|"
                        +item.getMatchNumStr() + "|"
                        + item.getHomeTeamName() + "|"
                        + item.getAwayTeamName() + "|"
                        + item.getBetItem() + "|"
                        + item.getSp().concat("flag"))
                .collect(Collectors.toList());

        Set<List<String>> lists = permutationNoRepeat(collect, length);

        Set<List<String>> newsets = new HashSet<>();

        for (List<String> set : lists) {
            Set<String> collect1 = set.stream()
                    .map(s -> s.substring(0, 7))
                    .collect(Collectors.toSet());

            if (collect1.size() == length) {
                newsets.add(set);
            }

        }

        //计算出总奖金
        double d = 0d;
        for (List<String> list : newsets) {
            double bonus = list.stream().map(l->l.split("\\|")[5]).mapToDouble(Double::parseDouble)
                    .reduce(1,(a,b)->a*b)*2;
            list.add(bonus+"");
            d += bonus;
        }
        double ttz = 0d;
        for (List<String> list : newsets) {
            //柱数
            double bonus = Double.parseDouble(list.get(list.size()-1));
            double v = d*multiple/bonus/newsets.size();
            double zs = formatDouble0(v);
            ttz += zs;
            list.add(zs + "");
            list.add(formatDouble0(zs*bonus) + "");
        }

        return refactor(newsets, ttz, multiple * newsets.size(), newsets.size());
    }

    public static Set<List<String>> refactor(Set<List<String>> newsets, double ttz, double limitTtz, double single){
        if(ttz>limitTtz){
                //排序，递减
                List<String> collect = newsets.stream()
                        .map(a -> a.stream().reduce("", (x, y) -> x + "flag" + y))
                        .sorted(Comparator.comparing(String::toString, (ss1, ss2) -> {
                            if (Double.parseDouble(ss1.split("flag")[ss1.split("flag").length - 1])
                                    > Double.parseDouble(ss2.split("flag")[ss2.split("flag").length - 1])) {
                                return 1;
                            } else {
                                return -1;
                            }
                        }).reversed()).collect(Collectors.toList());
            if(single>= (ttz-limitTtz)){
                for(int i = 0; i < ttz-limitTtz; i++){
                    String[] flags = collect.get(i).split("flag");
                    int length = flags.length;
                    flags[length-2] = Double.parseDouble(flags[length-2]) -1 + "";
                    flags[length-1] = formatDouble0(Double.parseDouble(flags[length-2]) * Double.parseDouble(flags[length-3])) + "";

                    StringBuilder res = new StringBuilder();
                    for (String flag : flags) {
                        res.append(flag).append("flag");
                    }

                    collect.set(i, res.toString());
                }
                return collect.stream()
                        .map(cc -> new ArrayList<>(Arrays.stream(cc.split("flag"))
                                .filter(StringUtils::isNotBlank)
                                .collect(Collectors.toList())))
                        .collect(Collectors.toSet());
            }else{

                for(int i = 0; i < single; i++){
                    String[] flags = collect.get(i).split("flag");
                    int length = flags.length;
                    flags[length-2] = Double.parseDouble(flags[length-2]) -1 + "";
                    flags[length-1] = Double.parseDouble(flags[length-2]) * Double.parseDouble(flags[length-3]) + "";

                    StringBuilder res = new StringBuilder();
                    for (String flag : flags) {
                        res.append(flag).append("flag");
                    }

                    collect.set(i, res.toString());
                }

                ttz = (ttz-limitTtz-single) + limitTtz;

                Set<List<String>> st  = collect.stream()
                        .map(cc -> new ArrayList<>(Arrays.stream(cc.split("flag"))
                                .filter(StringUtils::isNotBlank)
                                .collect(Collectors.toList())))
                        .collect(Collectors.toSet());

                return refactor(st, ttz, limitTtz, single);
            }
        }else if(ttz == limitTtz){
            return newsets;
        }else{
            //排序，递增
            List<String> collect = newsets.stream()
                    .map(a -> a.stream().reduce("", (x, y) -> x + "flag" + y))
                    .sorted(Comparator.comparing(String::toString, (ss1, ss2) -> {
                        if (Double.parseDouble(ss1.split("flag")[ss1.split("flag").length - 1])
                                > Double.parseDouble(ss2.split("flag")[ss2.split("flag").length - 1])) {
                            return 1;
                        } else {
                            return -1;
                        }
                    })).collect(Collectors.toList());

            if(single>=(limitTtz-ttz)){
                for(int i = 0; i < limitTtz-ttz; i++){
                    String[] flags = collect.get(i).split("flag");
                    int length = flags.length;
                    flags[length-2] = Double.parseDouble(flags[length-2]) + 1 + "";
                    flags[length-1] = formatDouble0(Double.parseDouble(flags[length-2]) * Double.parseDouble(flags[length-3])) + "";

                    StringBuilder res = new StringBuilder();
                    for (String flag : flags) {
                        res.append(flag).append("flag");
                    }

                    collect.set(i, res.toString());
                }

                return collect.stream()
                        .map(cc -> new ArrayList<>(Arrays.stream(cc.split("flag"))
                                .filter(StringUtils::isNotBlank)
                                .collect(Collectors.toList())))
                        .collect(Collectors.toSet());
            }else{
                ttz = limitTtz - (limitTtz-ttz-single);

                Set<List<String>> st  = collect.stream()
                        .map(cc -> new ArrayList<>(Arrays.stream(cc.split("flag"))
                                .filter(StringUtils::isNotBlank)
                                .collect(Collectors.toList())))
                        .collect(Collectors.toSet());

                return refactor(st, ttz, limitTtz, single);
            }
        }

    }
    //ls选了几场比赛  l选了比赛的几个胜平负
    public static Map<String, Object> calc(List<List<String>> ls, int length) {
        Map<String, Object> result = new HashMap<>();
        List<String> maxs = new ArrayList<>();
        List<String> mins = new ArrayList<>();
        for (List<String> l : ls) {
            if (l.size() == 1) {//单选
                maxs.add((l.get(0).substring(0, 5) + l.get(0).split("@")[1]).replace(")", "") + "flag");
                mins.add((l.get(0).split("@")[1]).replace(")", ""));
            } else {
                List<String> collect = l.stream()
                        .map(x1 -> x1.substring(0, 5) + (x1.split("@")[1]).replace(")", ""))
                        .sorted(Comparator.comparing(String::toString, (ss1, ss2) -> {

                            if (Double.parseDouble(ss1.substring(5)) > Double.parseDouble(ss2.substring(5))) {
                                return 1;
                            } else {
                                return -1;
                            }
                        })).collect(Collectors.toList());
                maxs.add(collect.get(collect.size() - 1) + "flag");
                mins.add(collect.get(0).substring(5));
            }
        }

        //计算5选3组合，并计算出金额,最小金额为5选3最小
        Set<List<String>> maxsets = permutationNoRepeat(maxs, length);

        double maxval = 0.00d;
        //计算最大奖金
        for (List<String> maxset : maxsets) {
            maxval += (maxset.stream().map(ma -> ma.substring(5))
                    .mapToDouble(Double::parseDouble)
                    .reduce(1, (a, b) -> a * b) * 2);
        }

        log.info("最大奖金：{}", formatDouble1(maxval));

        //计算最小奖金
        List<Double> collect2 = mins.stream()
                .map(Double::parseDouble)
                .sorted()
                .collect(Collectors.toList());
        //取前length个数
        List<Double> doubles = collect2.subList(0, length);

        double min = doubles.stream()
                .mapToDouble(a -> a)
                .reduce(1, (af, ah) -> af * ah) * 2;
        log.info("最小奖金：{}", formatDouble1(min));

        List<String> collect = ls.stream()
                .flatMap(Collection::stream)
                .map(s -> s.concat("flag"))
                .collect(Collectors.toList());

        Set<List<String>> sets = permutationNoRepeat(collect, length);

        Set<List<String>> newsets = new HashSet<>();

        for (List<String> set : sets) {
            Set<String> collect1 = set.stream()
                    .map(s -> s.substring(0, 5))
                    .collect(Collectors.toSet());

            if (collect1.size() == length) {
                newsets.add(set);
            }

        }

        result.put("maxBonus", formatDouble1(maxval));
        result.put("minBonus", formatDouble1(min));
        result.put("tzNumber", newsets.size());

//        System.out.println(JSON.toJSONString(newsets));

        return result;

    }

    /**
     * 保留两位小数，四舍五入的一个老土的方法
     *
     * @param d
     * @return
     */
    public static double formatDouble1(double d) {
        return (double) Math.round(d * 100) / 100;
    }

    public static double formatDouble0(double d) {
        return (double) Math.round(d) / 1;
    }


    /**
     * 排列组合(字符不重复排列)<br>
     * 内存占用：需注意结果集大小对内存的占用（list:10位，length:8，结果集:[10! / (10-8)! = 1814400]）
     *
     * @param list   待排列组合字符集合(忽略重复字符)
     * @param length 排列组合生成长度
     * @return 指定长度的排列组合后的字符串集合
     * @author xiaoyinandan
     */
    public static Set<List<String>> permutationNoRepeat(List<String> list, int length) {//3
        Stream<String> stream = list.stream().distinct();
        for (int n = 1; n < length; n++) {
            stream = stream.flatMap(str -> list.stream()
                    .filter(temp -> !str.contains(temp))
                    .map(str::concat));
        }
        return stream.map(s ->
                Arrays.stream(s.split("flag"))
                        .sorted()
                        .collect(Collectors.toList()))
                .collect(Collectors.toSet());
    }




}
