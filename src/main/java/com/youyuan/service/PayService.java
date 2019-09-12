package com.youyuan.service;

/**
 * @author zhangyu
 * @version 1.0
 * @description 模拟支付请求
 * @date 2019/7/12 11:14
 */
public class PayService {

    /**
     * 模拟发起支付请求
     */
    public void pay(){
        System.out.println(Thread.currentThread().getName()+" 支付成功......");
    }

}
