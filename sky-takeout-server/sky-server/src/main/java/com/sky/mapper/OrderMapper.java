package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    void insert(Orders orders);

    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    void update(Orders orders);

    @Select("select * from orders where status = #{status} and order_time < #{time}")
    List<Orders> getOrderTL(int status, LocalDateTime time);

    /**
     * 根据订单号查询订单
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    Double turnoverStatistics(LocalDateTime beginTime, LocalDateTime endTime, Integer status);

    int ordersStatistics(LocalDateTime beginTime, LocalDateTime endTime, Integer... status);

    List<GoodsSalesDTO> top10(LocalDateTime beginTime, LocalDateTime endTime, Integer status);

    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer toBeConfirmed);

    int countByMap(Map map);
}
