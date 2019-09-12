package com.youyuan;

import com.youyuan.lock.locallock.LocalLock;
import com.youyuan.lock.redislock.RedisLock;
import com.youyuan.lock.zklock.ZookeeperLock;
import com.youyuan.service.OrderService;
import com.youyuan.service.PayService;
import com.youyuan.service.StockService;

import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;

/**
 * @author zhangyu
 * @version 1.0
 * @description
 * @date 2019/7/12 11:17
 */
public class App {

    //创建订单接口
    private static OrderService orderService=new OrderService();

    //减库存接口
    private static StockService stockService=new StockService();

    //支付接口
    private static PayService payService=new PayService();

    public static void main(String[] args) {
        //核心线程数
        int corePoolSize=2;
        //最大线程数
        int maximumPoolSize=5;
        //空闲时间
        long keepAliveTime= 60L;
        //空闲时间 单位  秒
        TimeUnit timeUnit=TimeUnit.SECONDS;
        //创建有界队列 3
        BlockingQueue<Runnable> blockingQueue=new ArrayBlockingQueue<Runnable>(30);
        //创建线程池  模拟并发场景
        ExecutorService executorService= new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime,timeUnit,blockingQueue);

        executorService.submit(new LockTask(1,"线程1"));
        executorService.submit(new LockTask(2,"线程2"));
        executorService.submit(new LockTask(3,"线程3"));
        executorService.submit(new LockTask(4,"线程4"));
        executorService.submit(new LockTask(5,"线程5"));

        executorService.shutdown();
    }


    static class LockTask implements Runnable{

        private int taskId;

        private String threadName;

        public LockTask(int taskId, String threadName) {
            this.taskId = taskId;
            this.threadName = threadName;
        }

        public int getTaskId() {
            return taskId;
        }

        public void setTaskId(int taskId) {
            this.taskId = taskId;
        }

        public String getThreadName() {
            return threadName;
        }

        public void setThreadName(String threadName) {
            this.threadName = threadName;
        }

        public void run() {
            Lock lock=null;
            try {
                //调用创建订单接口
                orderService.createOrder();
                //加锁操作
                //lock=new LocalLock();
                //lock=new RedisLock();
                lock=new ZookeeperLock();
                lock.lock();
                //调用减库存接口
                boolean stockResult = stockService.reduceStock();
                if (stockResult){
                    //调用支付接口
                    payService.pay();
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }
    }

}
