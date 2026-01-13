package com.sky.Task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    // 处理超时订单
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrder() {
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        List<Orders> ordersList = orderMapper.getOrderTL(Orders.PENDING_PAYMENT, time);
        for (Orders order : ordersList) {
            order.setStatus(Orders.CANCELLED);
            order.setCancelTime(LocalDateTime.now());
            order.setCancelReason("订单超时，系统自动取消订单");
            orderMapper.update(order);
        }
    }

    // 处理一直处于派送中的订单
    @Scheduled(cron = "0/10 * * * * ?")
    public void processDeliveryOrder() {
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        List<Orders> ordersList = orderMapper.getOrderTL(Orders.DELIVERY_IN_PROGRESS, time);
        for (Orders order : ordersList) {
            order.setStatus(Orders.COMPLETED);
            orderMapper.update(order);
        }
    }


}
