package com.youyuan.lock.zklock;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author zhangyu
 * @version 1.0
 * @description zookeeper实现分布式锁
 *
 * 实现思路：
 *  每一个客户端连接获取锁的时候创建一个临时顺序节点，临时顺序节点的数字最小就获取锁
 *
 * @date 2019/7/15 13:41
 */
public class ZookeeperLock implements Lock {

    //服务器地址
    private static String HOSTNAME="192.168.123.252:2181";

    //超时时间 单位毫秒
    private static Integer SESSION_TIMEOUT=3000;

    //根节点名称信息
    private String LOCK_NAME="/LOCK";

    //当前节点名
    private ThreadLocal<String> CURRENT_NODE=new ThreadLocal<String>();

    private ThreadLocal<ZooKeeper> zk=new ThreadLocal<ZooKeeper>();

    public void lock() {
        init();

        tryLock();
    }

    /**
     * 初始化zk
     */
    private void init() {
        try {
            zk.set(new ZooKeeper(HOSTNAME, SESSION_TIMEOUT, new Watcher() {
                public void process(WatchedEvent watchedEvent) {

                }
            }));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean tryLock() {
        //当前节点
        String nodeName=LOCK_NAME+"/zk_";
        try {
            //创建临时顺序节点 并将节点名称赋值变量  创建完成后返回的节点格式/LOCK/zk_1
            CURRENT_NODE.set(zk.get().create(nodeName,new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL));

            //查询锁根节点下的所有子节点  获取子节点的格式为  zk_1  zk_2  zk_3
            List<String> children = zk.get().getChildren(LOCK_NAME, false);
            //对临时顺序子节点升序排序
            Collections.sort(children);
            //获取子节点中顺序值最小的子节点
            String minNode=children.get(0);
            //判断当前创建的节点是否是最小的子节点，如果是就获取锁，否 创建监听
            if (CURRENT_NODE.get().equals(LOCK_NAME+"/"+minNode)){
                return true;
            }else {
                //等待，监听
                //因为是排序好了，索引实现思路是监听前一个节点的删除事件

                //获取当前节点在子节点列表中的索引值
                int index=children.indexOf(CURRENT_NODE.get().substring(CURRENT_NODE.get().lastIndexOf("/")+1));
                String beforeNode=LOCK_NAME+"/"+children.get(index-1);

                //设置一个闭锁用于阻塞等待锁的线程
                final CountDownLatch countDownLatch=new CountDownLatch(1);

                //创建前一个节点的监听事件
                zk.get().exists(beforeNode, new Watcher() {
                    public void process(WatchedEvent watchedEvent) {
                        //获取事件类型
                        Event.EventType type = watchedEvent.getType();
                        //监听删除事件
                        if (Event.EventType.NodeDeleted.equals(type)){
                            //解除阻塞
                            countDownLatch.countDown();
                        }
                    }
                });

                //阻塞等待
                countDownLatch.await();
            }

        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void unlock() {
        try {
            zk.get().delete(CURRENT_NODE.get(),-1);
            CURRENT_NODE.remove();
            zk.get().close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
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
