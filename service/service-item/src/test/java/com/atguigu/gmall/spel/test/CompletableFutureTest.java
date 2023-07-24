package com.atguigu.gmall.spel.test;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//@SpringBootTest(classes = ItemApplication.class)
public class CompletableFutureTest {

//    @Autowired
    private ThreadPoolExecutor threadPoolExecutor ;

    // 组合多任务
//    CompletableFuture<Void> allOf(CompletableFuture<?>... cfs)   当所有的任务执行完毕以后，线程再向下进行执行
//    CompletableFuture<Object> anyOf(CompletableFuture<?>... cfs) 当任意一个任务执行完毕以后,线程再向下进行执行
//    CompletableFuture<Void> runAfterBoth(other,action) 当两个任务执行完毕以后在执行一个新的任务

    @Test
    public void test08() throws Exception {
        CompletableFuture<Void> cf1 = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "执行了一个任务");
        });

        CompletableFuture<Void> cf2 = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "执行了一个任务");
        });

        // CompletableFuture.anyOf(cf1 , cf2).join() ;   // 让当前线程阻塞
        cf1.runAfterBoth(cf2 , () -> {
            System.out.println(Thread.currentThread().getName() + "执行了一个任务");
        }) ;

        TimeUnit.SECONDS.sleep(30);

        // System.out.println(Thread.currentThread().getName() + "程序执行结束了");
    }

    // 需求：在控制台按照顺序输出"haha"、"hehe"、"heihei"
    @Test
    public void test07() throws Exception {
        CompletableFuture.runAsync(() -> {
            System.out.println("haha");
        }).thenRunAsync(() -> {
            System.out.println("hehe");
        }).thenRunAsync(() -> {
            System.out.println("heihei");
        }) ;
    }

    @Test
    public void test06() throws Exception {
        Integer r = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "执行了一个任务");
            int a = 1 / 0 ;
            return 10;
        }).thenApplyAsync(result -> {
            System.out.println(Thread.currentThread().getName() + "----" + result);
            return 20;
        }).get();
        System.out.println(r);
    }

    @Test
    public void test05() throws Exception {
        CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "执行了一个任务");
            // int a = 1 / 0 ;
            return 10;
        }).thenAcceptAsync(result -> System.out.println(Thread.currentThread().getName() + "----" + result)) ;
    }

    @Test
    public void test04() throws Exception  {
        CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "执行了一个任务");
            // int a = 1 / 0 ;
            return 10;
        }).thenRunAsync(() -> System.out.println(Thread.currentThread().getName() + "任务执行完毕了")) ;
    }

    @Test
    public void test03() throws Exception {
        Integer r = CompletableFuture.supplyAsync(() -> {
                    System.out.println(Thread.currentThread().getName() + "执行了一个任务");
                    int a = 1 / 0;
                    return 10;
                }).whenCompleteAsync((result, e) -> System.out.println(Thread.currentThread().getName() + "----" + result + "---" + e))
                .exceptionally(e -> {      // 当最后一次任务执行的时候产生了异常，那么此时通过该方法返回一个默认的值
                    System.out.println(e);
                    return 20;
                }).get();
        System.out.println(r);
    }

    @Test
    public void test02() throws Exception {
        Integer result = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "执行了一个任务");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 10;
        }).get();           // get方法会阻塞当前线程

        System.out.println("test02....");

        System.out.println(result);
    }

    @Test
    public void test01() {
       // CompletableFuture.runAsync(() -> System.out.println(Thread.currentThread().getName() + "执行了一个任务")) ;
       // CompletableFuture.runAsync(() -> System.out.println(Thread.currentThread().getName() + "执行了一个任务") , threadPoolExecutor) ;
    }


}
