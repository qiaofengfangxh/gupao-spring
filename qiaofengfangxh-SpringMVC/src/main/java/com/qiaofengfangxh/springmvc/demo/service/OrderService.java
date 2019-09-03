package com.qiaofengfangxh.springmvc.demo.service;

public interface OrderService {


    /**
     * 创建订单的业务接口
     * @param orderId  订单号id
     * @param amount   订单金额
     * @return         返回订单信息
     */
    String createOrder(String orderId, Integer amount);

}
