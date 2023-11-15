package com.caipiao.modules.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caipiao.modules.common.dto.CommonLotteryDTO;
import com.caipiao.modules.order.dto.AmountCountDTO;
import com.caipiao.modules.order.dto.MyDataDTO;
import com.caipiao.modules.order.entity.Order;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDao extends BaseMapper<Order> {
    MyDataDTO queryStatusNumCount(@Param("userId")String userId);
    List<AmountCountDTO> queryAmountCount(@Param("req")CommonLotteryDTO req);
}
