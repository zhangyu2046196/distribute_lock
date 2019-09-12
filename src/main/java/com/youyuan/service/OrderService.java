package com.youyuan.service;

/**
 * @author zhangyu
 * @version 1.0
 * @description 模拟下单请求
 * @date 2019/7/12 11:00
 */
public class OrderService {

    /**
     * 模拟下单方法
     */
    public void createOrder(){
        System.out.println(Thread.currentThread().getName()+" 下单成功......");
    }

}
