package org.csu.mypetstorespring.service;

import org.csu.mypetstorespring.domain.Order;

import java.util.List;

public interface OrderService {

    // 插入订单，生成新订单
    public void insertOrder(Order order);

    // 取得订单
    public Order getOrder(int orderId);

    // 通过用户名查看订单
    public List<Order> getOrdersByUsername(String username);

    // 生成订单序列
    public int getNextId(String name) ;
}
