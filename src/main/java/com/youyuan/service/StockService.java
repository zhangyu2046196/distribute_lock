package com.youyuan.service;

/**
 * @author zhangyu
 * @version 1.0
 * @description 模拟库存请求
 * @date 2019/7/12 11:01
 */
public class StockService {

    //库存数量
    private static Integer COUNT=1;

    /**
     * 模拟减库存方法
     */
    public boolean reduceStock(){
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (COUNT>0){
            COUNT--;
            System.out.println(Thread.currentThread().getName()+" 减库存成功......");
            return true;
        }else {
            System.out.println(Thread.currentThread().getName()+" 减库存失败......");
            return false;
        }
    }

}
