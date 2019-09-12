package com.youyuan.lock.locallock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhangyu
 * @version 1.0
 * @description 本地锁
 * @date 2019/7/12 11:44
 */
public class LocalLock implements Lock{

    Lock lock=new ReentrantLock();
    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    public boolean tryLock() {
        return false;
    }

    public void lockInterruptibly() throws InterruptedException {

    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    public Condition newCondition() {
        return null;
    }
}