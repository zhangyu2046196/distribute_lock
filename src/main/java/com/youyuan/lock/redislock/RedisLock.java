package com.youyuan.lock.redislock;

import com.sun.org.apache.regexp.internal.RE;
import com.youyuan.util.JedisPoolUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author zhangyu
 * @version 1.0
 * @description 模拟redis锁
 * @date 2019/7/12 11:49
 */
public class RedisLock implements Lock {

    public Jedis jedis=null;

    private String REDIS_KEY="redis_key"; //redis 存储的 key

    private String REQUEST_ID= UUID.randomUUID().toString(); //redis的value存储值

    public RedisLock(){
        init();
    }

    /**
     * 保存指定的key，保存成功了就获取到了锁，否则线程阻塞，一直获取锁
     * @return
     */
    public boolean tryLock() {
        while(true){
            Long setnx = jedis.setnx(REDIS_KEY, REQUEST_ID);
            if (setnx==1){
                return true;
            }
        }
    }

    public void lock() {
        if (tryLock()){
            jedis.set(REDIS_KEY,REQUEST_ID,"NX","PX",3000);//设置过期时间 ，防止redis宕机引起的锁不能被释放
        }
    }

    /**
     * 初始化redis连接
     */
    private void init() {
        if (jedis==null){
            jedis=JedisPoolUtil.getInstance().getResource();
        }
    }

    /**
     * 连接用完后放回池子
     * @param jedisPool 连接池
     * @param jedis 连接
     */
    private void relace(JedisPool jedisPool, Jedis jedis){
        JedisPoolUtil.relace(jedisPool,jedis);
    }

    public void unlock() {
        //此处需要判断一下value在删除，因为存在第一个线程拿到锁之后再有效期结束后还没有执行完业务逻辑，这个时候第二个
        // 线程重新拿到了锁，如果这时候第一个线程删除key会影响第二个线程的并发操作
//        String redisValue=jedis.get(REDIS_KEY);
//        if (redisValue.equals(REQUEST_ID)){
//            jedis.del(REDIS_KEY);
//        }

        //通过lua脚本删除
        String script="if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        jedis.eval(script, Collections.singletonList(REDIS_KEY),Collections.singletonList(REQUEST_ID));

        //将redis连接放回连接池
        relace(JedisPoolUtil.getInstance(),jedis);

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
