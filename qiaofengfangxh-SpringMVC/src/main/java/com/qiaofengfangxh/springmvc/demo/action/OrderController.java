package com.qiaofengfangxh.springmvc.demo.action;


import com.qiaofengfangxh.springmvc.annotation.QFAutowired;
import com.qiaofengfangxh.springmvc.annotation.QFController;
import com.qiaofengfangxh.springmvc.annotation.QFRequestMapping;
import com.qiaofengfangxh.springmvc.annotation.QFRequestParam;
import com.qiaofengfangxh.springmvc.demo.service.OrderService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 * 这里模拟实现SpringMVC的接口请求
 */
@QFController
@QFRequestMapping("/order")
public class OrderController {

    @QFAutowired
    private OrderService orderService;

    /**
     * 创建订单的业务接口
     * @param orderId
     * @param amount
     */
    @QFRequestMapping("/addOrder")
    public void createOrder(HttpServletRequest request, HttpServletResponse response,
            @QFRequestParam String orderId, @QFRequestParam Integer amount) {

        try {
            String order = orderService.createOrder(orderId, amount);
            response.getWriter().print(order);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
