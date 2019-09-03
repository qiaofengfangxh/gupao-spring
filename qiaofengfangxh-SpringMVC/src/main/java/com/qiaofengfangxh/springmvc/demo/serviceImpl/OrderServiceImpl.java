package com.qiaofengfangxh.springmvc.demo.serviceImpl;

import com.qiaofengfangxh.springmvc.annotation.QFService;
import com.qiaofengfangxh.springmvc.demo.service.OrderService;

/**
 * 这里只是模拟springMVC的service层
 * @author qiaofengfangxh
 */

@QFService
public class OrderServiceImpl implements OrderService {

    @Override
    public String createOrder(String orderId, Integer amount) {
        return "订单号是"+orderId+"；订单金额是：" + amount + "元";
    }
}
