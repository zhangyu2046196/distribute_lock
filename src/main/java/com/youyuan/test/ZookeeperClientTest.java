package com.youyuan.test;

import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * @author zhangyu
 * @version 1.0
 * @description zk客户端测试工具
 * @date 2019/7/12 16:16
 */
public class ZookeeperClientTest {

    //服务器地址
    private static String HOSTNAME="192.168.123.252:2181";

    //超时时间 单位毫秒
    private static Integer SESSION_TIMEOUT=3000;

    public static void main(String[] args) throws Exception {
        //通过原生方式创建连接
        ZooKeeper zk=new ZooKeeper(HOSTNAME, SESSION_TIMEOUT, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                //....
            }
        });

        //创建持久节点
        //zk.create("/work","工作".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        //创建临时节点
        //zk.create("/tmp","tmp_data".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);

        //创建持久顺序节点
        //zk.create("/youyuan","youyuan".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT_SEQUENTIAL);
        //zk.create("/youyuan","youyuan".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT_SEQUENTIAL);

        //创建临时顺序节点
        //zk.create("/youyuan_tmp","youyuan_tmp".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
        //zk.create("/youyuan_tmp","youyuan_tmp".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);

        //监听事件  (创建监听之前 监听的节点必须先创建，否则不能监听)
        zk.exists("/youyuan_watch", new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                //事件类型
                Event.EventType type = watchedEvent.getType();
                //删除事件
                if (Event.EventType.NodeDeleted.equals(type)){
                    System.out.println("节点被删除......");
                }
            }
        });


        //控制台输入任何字符，关闭zookeeper
        System.in.read();

        zk.close();
    }

}
