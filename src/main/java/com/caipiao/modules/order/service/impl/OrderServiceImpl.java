package com.caipiao.modules.order.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caipiao.common.exception.RRException;
import com.caipiao.common.utils.*;
import com.caipiao.modules.app.dto.UserRechargeDTO;
import com.caipiao.modules.app.entity.UserInfo;
import com.caipiao.modules.app.entity.UserRechargeRecord;
import com.caipiao.modules.app.service.UserInfoService;
import com.caipiao.modules.app.service.UserRechargeRecordService;
import com.caipiao.modules.basketball.dao.BasketballMatchDao;
import com.caipiao.modules.basketball.entity.BasketballMatch;
import com.caipiao.modules.common.dto.CommonLotteryDTO;
import com.caipiao.modules.common.enums.AppEnums;
import com.caipiao.modules.common.util.MatchStopTimeUtil;
import com.caipiao.modules.football.dao.FootballMatchDao;
import com.caipiao.modules.football.entity.FootballMatch;
import com.caipiao.modules.order.dao.BillingDao;
import com.caipiao.modules.order.dao.OrderDao;
import com.caipiao.modules.order.dto.AmountCountDTO;
import com.caipiao.modules.order.dto.BillingConfigDTO;
import com.caipiao.modules.order.entity.*;
import com.caipiao.modules.order.service.BillingTicketService;
import com.caipiao.modules.order.service.OrderMatchService;
import com.caipiao.modules.order.service.OrderService;
import com.caipiao.modules.order.service.OrderTicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderDao, Order> implements OrderService {

    @Autowired
    UserInfoService userInfoService;
    @Autowired
    OrderTicketService orderTicketService;
    @Autowired
    OrderMatchService orderMatchService;
    @Autowired
    UserRechargeRecordService userRechargeRecordService;
    @Autowired
    BasketballMatchDao basketballMatchDao;
    @Autowired
    FootballMatchDao footballMatchDao;

    @Autowired
    BillingDao billingDao;

    @Autowired
    BillingTicketService billingTicketService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public String submitOrder(Order order, UserInfo user) {

//        log.info("前端传递参数:{}", JSON.toJSONString(order));

        if(order.getStopPrintTime() == null){
            throw new RRException("截止时间未传递");
        }

        order.setUserId(user.getId());
        order.setNickName(user.getNickName());

        //检测用户是否已认证
        UserInfo userInfo = userInfoService.getById(user.getId());
        if(StringUtils.isBlank(userInfo.getIdcard())){
            throw new RRException("用户未实名");
        }

        //校验金额
        BigDecimal amount = order.getAmount();

        //状态设置
        order.setPlanStatus(AppEnums.PlanStatus.NOT_PAY.getValue());
        order.setWinStatus(AppEnums.WinStatus.LOTTERY_NOT_OUT.getValue());
        order.setTaskStatus(1);
        order.setPlanNo("P"+ IdUtil.getSnowflake(1,1).nextIdStr());
        //超级大乐透追期配置
        if(order.getMatchType() == AppEnums.LotteryType.SUPER_LOTTERY.getValue()){
            order.setPeriodConfig(JSON.toJSONString(order.getPeriodConfigDTO()));
        }

        this.save(order);

        //保存彩票
        List<OrderTicket> orderTickets = order.getOrderTickets();

        AtomicReference<BigDecimal> subAmountSum = new AtomicReference<>();
        subAmountSum.set(BigDecimal.ZERO);

        List<OrderTicket> collect = orderTickets.stream().peek(s -> {
            s.setOrderId(order.getId());
            BigDecimal amount1 = s.getAmount();

            if(order.getMatchType()==AppEnums.LotteryType.FOOTBALL.getValue()
                    || order.getMatchType()==AppEnums.LotteryType.BASKETBALL.getValue()){
                //是否单场决胜
                if(s.getPlayType().equals("单场决胜")){
                    s.setContent(s.getContent().split("\\|")[0]);
                }

                if(s.getPassType().equals("1")){
                    s.setPlayType("单关投注");
                }
            }

            subAmountSum.set(subAmountSum.get().add(amount1));
        }).collect(Collectors.toList());

        if(CollUtil.isEmpty(collect)){
            throw new RRException("参数错误！请重新提交订单");
        }

        if(order.getMatchType()==AppEnums.LotteryType.SUPER_LOTTERY.getValue() && order.getPeriod() == 1){
            log.info("不校验");
        }else {
            if(amount.intValue() != subAmountSum.get().intValue()){
                throw new RRException("参数错误！请重新提交订单");
            }
        }

        orderTicketService.saveBatch(collect);

       // 保存场次
        List<OrderMatch> orderMatches = order.getOrderMatches();
        saveOrderMatches(orderMatches, order, order.getMatchType());

        //发单配置
        saveBilling(order, userInfo.getHeadimgUrl());

        //加入redis，30分钟自动取消
        String keyRedis = String.valueOf(StrUtil.format("{}{}:{}", AppConstant.REDIS_ORDER_KEY_IS_PAY_0, "10000", order.getId()));
        redisTemplate.opsForValue().set(keyRedis, order.getId()+"", AppConstant.ORDER_TIME_OUT_0, TimeUnit.MINUTES);//设置过期时间

        return order.getId() + "";
    }

    /**
     * 发单配置
     */
    private void saveBilling(Order order, String headImgUrl){
        //需要发单
        if(order.getIsBilling() == AppEnums.YesOrNo.YES.getValue()){
            order.setIsDocumentary(0);
            if(order.getMatchType() != AppEnums.LotteryType.FOOTBALL.getValue()
                    && order.getMatchType() != AppEnums.LotteryType.BASKETBALL.getValue()){
                throw new RRException("只有竞彩足球和竞彩篮球才能发单");
            }

            BillingConfigDTO dto = order.getBillingConfigDTO();

            //计算起投金额
            int start = order.getOrderTickets().size() * 2;

            //发单金额必须大于100
            if(order.getAmount().intValue()<100){
                throw new RRException("发单金额必须大于100");
            }

            //检测是否是start的整数倍
            if(!Validator.canExactDivisor(dto.getStartOrderAmount().intValue(), start)){
                throw new RRException("发单起始金额必须为"+start +"的倍数");
            }


            if(dto.getStartOrderAmount().intValue()<(order.getOrderTickets().size() * 2)){
                throw new RRException("起投金额最低为" + (order.getOrderTickets().size() * 2) + "元");
            }

            if(order.getAmount().intValue() < dto.getStartOrderAmount().intValue()){
                throw new RRException("起投金额不能大于下单金额");
            }

            if(dto.getCommissionRate().doubleValue()>0.1d){
                throw new RRException("佣金比例不得大于10%");
            }

            Billing billing = new Billing();
            BeanUtils.copyProperties(order, billing);
            billing.setId(null);
            //选号金额
            billing.setAmount(order.getAmount());
            billing.setStartOrderAmount(dto.getStartOrderAmount());
            billing.setMultiple(1);
            billing.setBillingContent(dto.getBillingContent());
            billing.setBillingPic(dto.getBillingPic());
            billing.setCommissionRate(dto.getCommissionRate());
            billing.setOrderId(order.getId());
            billing.setAvatar(headImgUrl);
            billingDao.insert(billing);

            order.setBillingId(billing.getId());
            this.updateById(order);

            //计算倍数
            int multiple = order.getAmount().intValue()/dto.getStartOrderAmount().intValue();

            //保存彩票
            List<OrderTicket> orderTickets = order.getOrderTickets();

            List<BillingTicket> collect = orderTickets.stream().map(ticket -> {
                BillingTicket billingTicket = new BillingTicket();
                BeanUtils.copyProperties(ticket, billingTicket);
                billingTicket.setId(null);
                billingTicket.setBillingId(billing.getId());
                billingTicket.setAmount(ticket.getAmount().divide(BigDecimal.valueOf(multiple), 1, RoundingMode.HALF_UP));
                billingTicket.setMultiple(1);
                return billingTicket;
            }).collect(Collectors.toList());

            billingTicketService.saveBatch(collect);
        }
    }




    /**
     * 保存并校验比赛场次
     * @param orderMatches
     * @param order
     * @param matchType
     */
    private void saveOrderMatches(List<OrderMatch> orderMatches, Order order, Integer matchType){

        //计算截止时间
        AtomicReference<Date> stopPrintTime = new AtomicReference<>();
        stopPrintTime.set(DateUtil.offsetDay(new Date(), 10));
        if(CollUtil.isNotEmpty(orderMatches)){
            List<OrderMatch> collect1 = orderMatches.stream().peek(s -> {
                s.setOrderId(order.getId());
                //校验场次是否过期
                //足球
                if(matchType == AppEnums.LotteryType.FOOTBALL.getValue()){
                    LambdaQueryWrapper<FootballMatch> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(FootballMatch::getMatchId, s.getMatchId())
                            .select(FootballMatch::getMatchDate,FootballMatch::getMatchTime);
                    FootballMatch footballMatch = footballMatchDao.selectOne(wrapper);

                    if(null == footballMatch){
                        throw new RRException("参数错误！请重新提交订单");
                    }

                    String matchTime = footballMatch.getMatchDate() + " " + footballMatch.getMatchTime();

                    DateTime parse = DateUtil.parse(matchTime, "yyyy-MM-dd HH:mm:ss");
                    //提前五分钟截止
                    if((System.currentTimeMillis()-(5 * 60 * 1000))>parse.getTime()){
                        throw new RRException("订单含有比赛已开始的场次，请检查并重新选择比赛场次！");
                    }

                    if(stopPrintTime.get().getTime()>parse.getTime()){
                        stopPrintTime.set(parse);
                    }
                }

                if(matchType == AppEnums.LotteryType.BASKETBALL.getValue()){
                    LambdaQueryWrapper<BasketballMatch> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(BasketballMatch::getMatchId, s.getMatchId())
                            .select(BasketballMatch::getMatchDate,BasketballMatch::getMatchTime);
                    BasketballMatch basketballMatch = basketballMatchDao.selectOne(wrapper);

                    if(null == basketballMatch){
                        throw new RRException("参数错误！请重新提交订单");
                    }

                    String matchTime = basketballMatch.getMatchDate() + " " + basketballMatch.getMatchTime();
                    DateTime parse = DateUtil.parse(matchTime, "yyyy-MM-dd HH:mm:ss");
                    //提前五分钟截止
                    if((System.currentTimeMillis()-(5 * 60 * 1000))>parse.getTime()){
                        throw new RRException("订单含有比赛已开始的场次，请检查并重新选择比赛场次！");
                    }
                    if(stopPrintTime.get().getTime()>parse.getTime()){
                        stopPrintTime.set(parse);
                    }
                }


            }).collect(Collectors.toList());

            orderMatchService.saveBatch(collect1);

            //更新stopPrintTime
            order.setStopPrintTime(MatchStopTimeUtil.getStopPrintTime(stopPrintTime.get()));
            updateById(order);
        }

    }


    @Override
    @Transactional
    public R pay(String planNo, Integer payType, UserInfo user, String payPassword) {
        Order order = getById(Long.parseLong(planNo));

        if(null == order){
            return R.error("订单不存在");
        }

        if(!order.getUserId().equals(user.getId())){
            return R.error("订单不存在");
        }

        if(order.getPlanStatus()!=AppEnums.PlanStatus.NOT_PAY.getValue()){
            return R.error("当前订单状态不可支付");
        }

        //校验时间是否截止
        if(order.getStopPrintTime().getTime() < System.currentTimeMillis()){
            order.setPlanStatus(AppEnums.PlanStatus.ORDER_TIMEOUT.getValue());
            updateById(order);
            return R.error(10086,"订单已超时，自动撤销");
        }



        if(payType == AppEnums.PayType.BALANCE.getValue()){
            //余额支付，检测用户余额是否足够
            UserInfo userInfo = userInfoService.getById(user.getId());
            if(userInfo.getBalance().compareTo(order.getAmount())<0){
                return R.error("余额不足");
            }

            if(!userInfo.getPayPassword().equals(MD5Util.getMD5AndSalt(payPassword))){
                return R.error("支付密码不正确");
            }

            //扣款
            userInfo.setBalance(userInfo.getBalance().subtract(order.getAmount()));
            if(userInfo.getBonus().compareTo(order.getAmount())<0){
                userInfo.setBonus(BigDecimal.ZERO);
            }else{
                userInfo.setBonus(userInfo.getBonus().subtract(order.getAmount()));
            }

            userInfoService.updateById(userInfo);

            //更新订单状态
            order.setPlanStatus(1);
            updateById(order);
            Billing billing = billingDao.selectById(order.getBillingId());
            boolean update = false;
            if(order.getIsBilling() == 1 && order.getBillingId() != null){
                update = true;
                billing.setPlanStatus(1);
            }
            //跟单，热度加1
            if(order.getIsDocumentary() == AppEnums.YesOrNo.YES.getValue()){
                update = true;
                billing.setBillingHeat(billing.getBillingHeat() + 1);
            }

            if(update){
                billingDao.updateById(billing);
            }

            //加入消费记录
            UserRechargeRecord record = new UserRechargeRecord();
            record.setRechargeAmount(order.getAmount().negate());
            record.setSubject("消费");
            record.setPayStatus(1);
            //投注
            record.setPaymentType(2);
            record.setTransactionId(planNo);
            record.setUserId(order.getUserId());
            record.setCreateTime(new Date());
            record.setPaymentTime(new Date());
            userRechargeRecordService.save(record);

            return R.ok();
        }

        if(payType == AppEnums.PayType.ALIPAY.getValue()){
            //支付宝支付
            UserRechargeDTO dto = new UserRechargeDTO();
            dto.setPaymentWay(Constant.PaymentWay.ALIPAY.getValue());
            dto.setPlanNo(planNo);
            dto.setRechargeMoney(order.getAmount());
            UserRechargeRecord recharge = userRechargeRecordService.recharge(dto, user);

            return R.ok().put(recharge);
        }


        return null;
    }

    @Override
    @Transactional
    public void cancelOrder(String planNo, String reason) {
        Order order = getById(planNo);
        if(null == order){
            throw new RRException("订单不存在");
        }

        if(order.getPlanStatus() != 1){
            throw new RRException("订单无法撤单");
        }
        //已撤单
        order.setPlanStatus(3);
        //撤单原因
        order.setIssueResult(reason);
        updateById(order);

        UserInfo userInfo = userInfoService.getById(order.getUserId());
        UserRechargeRecord record = new UserRechargeRecord();
        record.setRechargeAmount(order.getAmount());
        record.setSubject("撤单回退金额");
        record.setPayStatus(1);
        //其他
        record.setPaymentType(5);
        record.setTransactionId(planNo);
        record.setUserId(order.getUserId());
        record.setCreateTime(new Date());
        record.setPaymentTime(new Date());
        userRechargeRecordService.save(record);

        userInfo.setBalance(userInfo.getBalance().add(order.getAmount()));
        userInfo.setBonus(userInfo.getBonus().add(order.getAmount()));
        userInfoService.updateById(userInfo);


    }

    @Override
    @Transactional
    public void dispatchBonusBatch(List<Long> orderIds) {
        for (Long orderId : orderIds) {
            this.dispatchBonus(orderId);
        }
    }

    @Override
    public List<AmountCountDTO> queryAmountCount(CommonLotteryDTO req) {
        return baseMapper.queryAmountCount(req);
    }

    @Override
    @Transactional
    public void dispatchBonus(Long orderId) {
        Order order = getById(orderId);

        if(null == order){
            throw new RRException("订单不存在");
        }

        if(order.getWinStatus() != 3){
            throw new RRException("订单无法派奖");
        }

        //中奖金额
        BigDecimal bonusAmount = order.getBonusAmount();
        UserInfo userInfo = userInfoService.getById(order.getUserId());

        userInfo.setTodayBonus(userInfo.getTodayBonus().add(bonusAmount));
        userInfo.setTotalBonus(userInfo.getTotalBonus().add(bonusAmount));
        userInfo.setBalance(userInfo.getBalance().add(bonusAmount));
        userInfoService.updateById(userInfo);

        //已派奖
        order.setWinStatus(4);

        //加入消费记录
        UserRechargeRecord record = new UserRechargeRecord();
        record.setRechargeAmount(order.getAmount());
        record.setSubject("派奖,派奖人ID："+ ShiroUtils.getUserId() + ",派奖人姓名："+ ShiroUtils.getUserEntity().getStaffName());
        record.setPayStatus(1);
        //派奖
        record.setPaymentType(3);
        record.setPaymentWay("4");
        record.setTransactionId(order.getId()+"");
        record.setUserId(order.getUserId());
        record.setCreateTime(new Date());
        record.setPaymentTime(new Date());
        userRechargeRecordService.save(record);

        updateById(order);

    }
}
