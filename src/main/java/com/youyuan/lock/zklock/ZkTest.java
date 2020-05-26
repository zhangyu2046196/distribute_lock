package com.youyuan.lock.zklock;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;

/**
 * @author zhangy
 * @version 1.0
 * @description
 * @date 2020/5/26 10:20
 */
public class ZkTest {

    private static List<String> list = new ArrayList<String>();

    static {
        list.add("17979898");
        list.add("11111111");
        list.add("22222222");
        list.add("33333333");
        list.add("55555555");
        list.add("66666666");
    }

    //100张票
    private static Integer n = 20;

    public static void printInfo() {
        System.out.println(Thread.currentThread().getName() + "正在运行,剩余余票:" + --n);
    }

    public class TicketThread implements Runnable {
        public void run() {
            Lock lock = new DistributedLock("127.0.0.1:2181", list.get(new Random().nextInt(list.size())), "zk");
            lock.lock();
            try {
                if (n > 0) {
                    printInfo();
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public void ticketStart() {
        TicketThread thread = new TicketThread();
        for (int i = 0; i < 50; i++) {
            Thread t = new Thread(thread, "mem" + i);
            t.start();
        }
    }

    public static void main(String[] args) {
        new ZkTest().ticketStart();

//        //核心线程数
//        int corePoolSize=2;
//        //最大线程数
//        int maximumPoolSize=5;
//        //空闲时间
//        long keepAliveTime= 60L;
//        //空闲时间 单位  秒
//        TimeUnit timeUnit=TimeUnit.SECONDS;
//        //创建有界队列 3
//        BlockingQueue<Runnable> blockingQueue=new ArrayBlockingQueue<Runnable>(30);
//        //创建线程池  模拟并发场景
//        ExecutorService executorService= new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime,timeUnit,blockingQueue);
//
//        TicketThread ticketThread = new TicketThread();
//        for (int i=1;i<=30;i++){
//            executorService.submit(ticketThread);
//        }


    }
}
