package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.sql.Time;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@RequestMapping(value = "/hello")
@Slf4j
public class HelloController {

    @Autowired
    private RedisTemplate<String , String> redisTemplate ;

    private ReentrantLock reentrantLock = new ReentrantLock() ;

    @Autowired
    private RedissonClient redissonClient ;

    private String value = null ;

    RSemaphore semaphore = null ;

//    @PostConstruct          // 自定义初始化方法
    public void init() {
        semaphore = redissonClient.getSemaphore("test-semaphore");
        semaphore.addPermits(2);		// 分配两个许可证
    }

    @GetMapping(value = "/semaphore")
    public Result semaphore() throws InterruptedException {
        semaphore.acquire();  // 申请许可证
        log.info(Thread.currentThread().getName() + "----> 申请到了一个许可证...");
        Thread.sleep(500);
        semaphore.release();
        log.info(Thread.currentThread().getName() + "----> 归还了一个许可证....");
        return Result.ok() ;
    }

    @GetMapping(value = "/read")
    public Result read() throws InterruptedException {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("read-write-lock");
        RLock readLock = readWriteLock.readLock();
        readLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + "读锁加锁成功，执行业务操作...");
            TimeUnit.SECONDS.sleep(10);
            return Result.ok(value) ;
        }finally {
            readLock.unlock();
            System.out.println(Thread.currentThread().getName() + "读锁已经释放了...");
        }
    }

    @GetMapping(value = "/write")
    public Result write() throws InterruptedException {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("read-write-lock");
        RLock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + "写锁锁加锁成功，执行业务操作...");
            TimeUnit.SECONDS.sleep(10);
            value = UUID.randomUUID().toString().replace("-" ,"") ;
            return Result.ok(value) ;
        }finally {
            writeLock.unlock();
            System.out.println(Thread.currentThread().getName() + "写锁已经释放了...");
        }
    }

    @GetMapping(value = "/testLock02")
    public Result testLock2() {
        RLock rLock = redissonClient.getLock("redisson-lock");
        rLock.lock();
        rLock.lock();

        System.out.println("执行业务操作");

        rLock.unlock();
        rLock.unlock();

        return Result.ok() ;

    }


    @GetMapping(value = "/testLock01")
    public Result testLock() throws InterruptedException {

        // 获取锁对象，通过锁对象获取锁
        RLock rLock = redissonClient.getLock("redisson-lock");
        rLock.lock();           // 会阻塞当前线程 , 默认的过期时间为30s
        // rLock.lock(30 , TimeUnit.SECONDS);          // 会阻塞当前线程，自定义锁的过期时间

        // boolean tryLock = rLock.tryLock();// 获取锁，如果获取成功了返回true，否则返回false , 默认的过期时间为30s

        // 等待锁的时间为4s ,4s之内如果没有获取到锁线程阻塞，超过4s没有获取到锁返回false，自定义锁的过期时间为60s
        // boolean tryLock = rLock.tryLock(4, 60, TimeUnit.SECONDS);

        /**
         * 面试题： 在进行项目开发的时候有没有遇到过一些技术问题(难点)，你是怎么进行解决的？
         */
        try {

            // 执行业务操作
            log.info("线程：{}获取到了锁，开始执行业务操作" , Thread.currentThread().getId());
            System.out.println("执行业务操作正在执行...");
            int a = 1 / 0 ;
            log.info("线程：{}获取到了锁，业务操作执行完毕了" , Thread.currentThread().getId());

        }catch (Exception e ) {
            e.printStackTrace();
        } finally {
            // 释放锁
            rLock.unlock();
        }

//        if(tryLock) {
//
//
//
//        }else {
//            log.info("线程：{}没有获取到了锁..." , Thread.currentThread().getId());
//        }

        // 返回数据
        return Result.ok() ;

    }




















    @GetMapping(value = "/count")
    public String count() {

        // 获取锁
        // reentrantLock.lock();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        lock(uuid) ;

        try {
            // 执行业务操作
            incr() ;
        }catch (Exception e) {
            e.printStackTrace();
        }finally {                      // 当服务器出现宕机的情况，那么锁没有被释放，出现了死锁！解决方案：就是让锁具有自动释放的功能
                                        // 给锁设置一个过期时间   30s
            // 释放锁
            // reentrantLock.unlock();
            unlock(uuid);
        }

        return "ok" ;
    }

    public void unlock(String uuid) {

        String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                "then\n" +
                "    return redis.call(\"del\",KEYS[1])\n" +
                "else\n" +
                "    return 0\n" +
                "end" ;


        // RedisScript: 封装lua脚本的对象
        // List: key的集合，每一个元素有一个索引，索引从1开始
        // Object: 传入lua脚本的参数，每一个元素有一个索引，索引从1开始
        Long result = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList("lock"), uuid);
        if(result == 1) {
            System.out.println("锁释放成功了...");
        }else {
            System.out.println("锁是别人的，释放锁失败...");
        }

        // 使用lua
//        String lockValue = redisTemplate.opsForValue().get("lock");
//        if(lockValue.equals(uuid)) {
//            redisTemplate.delete("lock") ;
//        }else {
//            System.out.println("锁是别人的，释放锁失败...");
//        }
    }

    public void lock(String uuid) {        // 加锁
        while(!redisTemplate.opsForValue().setIfAbsent("lock", uuid , 30 , TimeUnit.SECONDS)) {}
        // redisTemplate.expire("lock" , 30 , TimeUnit.SECONDS) ;          // 给锁设置一个过期时间   30s

        // 创建一个定时任务，给锁进行续期
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
        scheduledThreadPool.scheduleAtFixedRate(() -> {
            redisTemplate.expire("lock" , 30 , TimeUnit.SECONDS) ;
        } , 10 , 10 , TimeUnit.SECONDS) ;

    }

    public void incr() {

        try {
            TimeUnit.SECONDS.sleep(60);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        String count = redisTemplate.opsForValue().get("count");
        if(StringUtils.isEmpty(count)) {
            redisTemplate.opsForValue().set("count" , "1");
        }else {
            int parseInt = Integer.parseInt(count);
            parseInt++ ;
            redisTemplate.opsForValue().set("count" , String.valueOf(parseInt));
        }
    }

}