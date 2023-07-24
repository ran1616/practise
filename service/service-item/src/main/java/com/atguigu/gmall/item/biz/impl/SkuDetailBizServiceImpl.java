package com.atguigu.gmall.item.biz.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.cache.anno.GmallCache;
import com.atguigu.gmall.common.cache.service.RedisCacheService;
import com.atguigu.gmall.common.constant.GmallConstant;
import com.atguigu.gmall.common.feign.product.SkuFeignClient;
import com.atguigu.gmall.common.feign.seach.SearchFeignClient;
import com.atguigu.gmall.common.properties.ThreadPoolProperties;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.biz.SkuDetailBizService;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.atguigu.gmall.product.vo.AttrValueConcatVo;
import com.atguigu.gmall.product.vo.CategoryView;
import com.atguigu.gmall.product.vo.SkuDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SkuDetailBizServiceImpl implements SkuDetailBizService {

    @Autowired
    private SkuFeignClient skuFeignClient ;

    @Autowired
    private RedisTemplate<String , String> redisTemplate ;

    @Autowired
    private SearchFeignClient searchFeignClient ;

    private ReentrantLock reentrantLock = new ReentrantLock() ;

    // 声明一个布隆过滤器
    // private BloomFilter bloomFilter = BloomFilter.create(Funnels.longFunnel() , 100000  , 0.00001) ;

//    public SkuDetailBizServiceImpl() {      // 在执行构造方法的时候成员变量没有注入进来
//        init() ;
//    }

    private RBloomFilter<Long> rBloomFilter ;

    @PostConstruct          // 声明该方法就是一个初始化方法
    public void init() {     // 在服务器启动的时候调用该方法
//        Result<List<Long>> result = skuFeignClient.findAllSkuIds();
//        List<Long> list = result.getData();
//        list.stream().forEach(skuId -> {
//            bloomFilter.put(skuId) ;
//        });
//        bloomFilter.put(99L) ;
//        log.info("布隆过滤器初始化成功了...");

        rBloomFilter = redissonClient.getBloomFilter(GmallConstant.REDIS_SKUID_BLOOM_FILTER);

    }

    @Autowired
    private RedissonClient redissonClient ;

    @Autowired
    private RedisCacheService redisCacheService ;

    @Override
    @GmallCache(
            cacheKey = GmallConstant.REDSI_SKU_DETAIL_PREFIX + "#{#params[0]}" ,
            bloomFilterName = GmallConstant.REDIS_SKUID_BLOOM_FILTER ,
            bloomFilterValue = "#{#params[0]}" ,
            enableLock = true ,
            lockName = GmallConstant.REDIS_ITEM_LOCK_PREFIX + "#{#params[0]}" ,
            time = 7 ,
            timeUnit = TimeUnit.DAYS
    )
    public SkuDetailVo item(Long skuId) {
        Result<SkuDetailVo> detailVoResult = skuFeignClient.findSkuDetailVo(skuId);// 远程调用findSkuDetailVoFromRpc方法
        return detailVoResult.getData() ;
    }

    @Override
    public void updateHotScore(Long skuId) {
        Long increment = redisTemplate.opsForValue().increment(GmallConstant.REDIS_SKU_HOT_SCORE_PRE + skuId);
        if(increment % 5 == 0) {
            searchFeignClient.updateHotScore(skuId , increment) ;
        }
    }

    public SkuDetailVo itemRedissonClient(Long skuId) {

        // 通过布隆过滤器进行判断
//        if (!bloomFilter.mightContain(skuId)) {
//            log.info("布隆过滤器中不存在对应的数据,返回null...");
//            return null ;
//        }

        if (!rBloomFilter.contains(skuId)) {
            log.info("分布式的布隆过滤器中不存在对应的数据,返回null...");
            return null ;
        }

        // 查询redis
        SkuDetailVo detailVo = redisCacheService.getData(GmallConstant.REDSI_SKU_DETAIL_PREFIX + skuId, SkuDetailVo.class);
        if(detailVo != null) {
            log.info("从缓存中查询到了数据，返回...");
            return detailVo ;
        }

        RLock rLock = redissonClient.getLock(GmallConstant.REDIS_ITEM_LOCK_PREFIX + skuId);
        boolean tryLock = rLock.tryLock();
        if(tryLock) {
            log.info("线程:{}获取到了锁，从数据库中查询数据..." , Thread.currentThread().getId());
            SkuDetailVo skuDetailVo = null ;
            try {

                // 远程调用findSkuDetailVoFromRpc方法
                skuDetailVo = findSkuDetailVoFromRpc(skuId) ;
                if(skuDetailVo == null) {
                    log.info("线程:{} , 从数据库没有查询到数据，将Redis中存储是X" , Thread.currentThread().getId());
                    redisCacheService.saveData(GmallConstant.REDSI_SKU_DETAIL_PREFIX + skuId , GmallConstant.REDIS_NULL_VALUE);
                }else {
                    log.info("线程:{} , 从数据库查询到了数据，然后将数据存储到Redis中" , Thread.currentThread().getId());
                    redisCacheService.saveData(GmallConstant.REDSI_SKU_DETAIL_PREFIX + skuId , skuDetailVo);
                }

            }catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 进行锁释放
                rLock.unlock();
            }

            // 返回
            log.info("返回数据库中查询到的数据：{}" , JSON.toJSONString(skuDetailVo) );
            return skuDetailVo ;

        }else {     // 从Redis中查询数据

            log.info("线程:{}没有获取到了锁，从redis中进行查询数据..." , Thread.currentThread().getId());
            SkuDetailVo skuDetailVo = redisCacheService.getData(GmallConstant.REDSI_SKU_DETAIL_PREFIX + skuId, SkuDetailVo.class);
            if(skuDetailVo != null) {
                log.info("线程:{} 从缓存中查询到了数据，返回..." , Thread.currentThread().getId());
                return skuDetailVo ;
            }

            try {
                TimeUnit.MILLISECONDS.sleep(500);       // 线程休眠500ms
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            return redisCacheService.getData(GmallConstant.REDSI_SKU_DETAIL_PREFIX + skuId, SkuDetailVo.class);

        }
    }

    public SkuDetailVo itemRedisson(Long skuId) {

        // 通过布隆过滤器进行判断
//        if (!bloomFilter.mightContain(skuId)) {
//            log.info("布隆过滤器中不存在对应的数据,返回null...");
//            return null ;
//        }

        // 查询redis
        String skuDetailVoJSON = redisTemplate.opsForValue().get(GmallConstant.REDSI_SKU_DETAIL_PREFIX + skuId);
        if(!StringUtils.isEmpty(skuDetailVoJSON)) {
            if(GmallConstant.REDIS_NULL_VALUE.equalsIgnoreCase(skuDetailVoJSON)) {
                log.info("从缓存中查询到了x数据，返回null...");
                return null ;
            }else {
                log.info("从缓存中查询到了数据，返回...");
                SkuDetailVo skuDetailVo = JSON.parseObject(skuDetailVoJSON, SkuDetailVo.class);
                return skuDetailVo ;
            }
        }

        RLock rLock = redissonClient.getLock(GmallConstant.REDIS_ITEM_LOCK_PREFIX + skuId);
        boolean tryLock = rLock.tryLock();
        if(tryLock) {
            log.info("线程:{}获取到了锁，从数据库中查询数据..." , Thread.currentThread().getId());
            SkuDetailVo skuDetailVo = null ;
            try {

                // 远程调用findSkuDetailVoFromRpc方法
                skuDetailVo = findSkuDetailVoFromRpc(skuId) ;
                if(skuDetailVo == null) {
                    log.info("线程:{} , 从数据库没有查询到数据，将Redis中存储是X" , Thread.currentThread().getId());
                    redisTemplate.opsForValue().set(GmallConstant.REDSI_SKU_DETAIL_PREFIX + skuId , GmallConstant.REDIS_NULL_VALUE);
                }else {
                    log.info("线程:{} , 从数据库查询到了数据，然后将数据存储到Redis中" , Thread.currentThread().getId());
                    redisTemplate.opsForValue().set(GmallConstant.REDSI_SKU_DETAIL_PREFIX + skuId , JSON.toJSONString(skuDetailVo));
                }

            }catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 进行锁释放
                rLock.unlock();
            }

            // 返回
            log.info("返回数据库中查询到的数据：{}" , JSON.toJSONString(skuDetailVo) );
            return skuDetailVo ;

        }else {     // 从Redis中查询数据
            log.info("线程:{}没有获取到了锁，从redis中进行查询数据..." , Thread.currentThread().getId());
            skuDetailVoJSON = redisTemplate.opsForValue().get(GmallConstant.REDSI_SKU_DETAIL_PREFIX + skuId);
            if(!StringUtils.isEmpty(skuDetailVoJSON)) {
                if (GmallConstant.REDIS_NULL_VALUE.equalsIgnoreCase(skuDetailVoJSON)) {
                    log.info("线程:{}从缓存中查询到了x数据，返回null..." , Thread.currentThread().getId());
                    return null ;
                }else {
                    log.info("线程:{} 从缓存中查询到了数据，返回..." , Thread.currentThread().getId());
                    return JSON.parseObject(skuDetailVoJSON , SkuDetailVo.class) ;
                }
            }else {

                try {
                    TimeUnit.MILLISECONDS.sleep(500);       // 线程休眠500ms
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                skuDetailVoJSON = redisTemplate.opsForValue().get(GmallConstant.REDSI_SKU_DETAIL_PREFIX + skuId);
                if (GmallConstant.REDIS_NULL_VALUE.equalsIgnoreCase(skuDetailVoJSON)) {
                    log.info("线程:{} 从缓存中查询到了x数据，返回null..." , Thread.currentThread().getId());
                    return null ;
                }else {
                    log.info("线程:{}  从缓存中查询到了数据，返回...", Thread.currentThread().getId());
                    return JSON.parseObject(skuDetailVoJSON , SkuDetailVo.class) ;
                }
            }
        }
    }

    public SkuDetailVo itemRedisTemplate(Long skuId) {

        // 通过布隆过滤器进行判断
//        if (!bloomFilter.mightContain(skuId)) {
//            log.info("布隆过滤器中不存在对应的数据,返回null...");
//            return null ;
//        }

        // 查询redis
        String skuDetailVoJSON = redisTemplate.opsForValue().get(GmallConstant.REDSI_SKU_DETAIL_PREFIX + skuId);
        if(!StringUtils.isEmpty(skuDetailVoJSON)) {
            if(GmallConstant.REDIS_NULL_VALUE.equalsIgnoreCase(skuDetailVoJSON)) {
                log.info("从缓存中查询到了x数据，返回null...");
                return null ;
            }else {
                log.info("从缓存中查询到了数据，返回...");
                SkuDetailVo skuDetailVo = JSON.parseObject(skuDetailVoJSON, SkuDetailVo.class);
                return skuDetailVo ;
            }
        }

        String uuid = UUID.randomUUID().toString().replace("-", "");
        boolean tryLock = tryLock(uuid , skuId) ;
        if(tryLock) {
            log.info("线程:{}获取到了锁，从数据库中查询数据..." , Thread.currentThread().getId());
            SkuDetailVo skuDetailVo = null ;
            try {

                // 远程调用findSkuDetailVoFromRpc方法
                skuDetailVo = findSkuDetailVoFromRpc(skuId) ;
                if(skuDetailVo == null) {
                    log.info("线程:{} , 从数据库没有查询到数据，将Redis中存储是X" , Thread.currentThread().getId());
                    redisTemplate.opsForValue().set(GmallConstant.REDSI_SKU_DETAIL_PREFIX + skuId , GmallConstant.REDIS_NULL_VALUE);
                }else {
                    log.info("线程:{} , 从数据库查询到了数据，然后将数据存储到Redis中" , Thread.currentThread().getId());
                    redisTemplate.opsForValue().set(GmallConstant.REDSI_SKU_DETAIL_PREFIX + skuId , JSON.toJSONString(skuDetailVo));
                }

            }catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 进行锁释放
                unLock(uuid , skuId);
            }

            // 返回
            log.info("返回数据库中查询到的数据：{}" , JSON.toJSONString(skuDetailVo) );
            return skuDetailVo ;

        }else {     // 从Redis中查询数据
            log.info("线程:{}没有获取到了锁，从redis中进行查询数据..." , Thread.currentThread().getId());
            skuDetailVoJSON = redisTemplate.opsForValue().get(GmallConstant.REDSI_SKU_DETAIL_PREFIX + skuId);
            if(!StringUtils.isEmpty(skuDetailVoJSON)) {
                if (GmallConstant.REDIS_NULL_VALUE.equalsIgnoreCase(skuDetailVoJSON)) {
                    log.info("线程:{}从缓存中查询到了x数据，返回null..." , Thread.currentThread().getId());
                    return null ;
                }else {
                    log.info("线程:{} 从缓存中查询到了数据，返回..." , Thread.currentThread().getId());
                    return JSON.parseObject(skuDetailVoJSON , SkuDetailVo.class) ;
                }
            }else {

                try {
                    TimeUnit.MILLISECONDS.sleep(500);       // 线程休眠500ms
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                skuDetailVoJSON = redisTemplate.opsForValue().get(GmallConstant.REDSI_SKU_DETAIL_PREFIX + skuId);
                if (GmallConstant.REDIS_NULL_VALUE.equalsIgnoreCase(skuDetailVoJSON)) {
                    log.info("线程:{} 从缓存中查询到了x数据，返回null..." , Thread.currentThread().getId());
                    return null ;
                }else {
                    log.info("线程:{}  从缓存中查询到了数据，返回...", Thread.currentThread().getId());
                    return JSON.parseObject(skuDetailVoJSON , SkuDetailVo.class) ;
                }
            }
        }
    }

    // 解锁方法
    public void unLock(String uuid , Long skuId) {

        String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                "then\n" +
                "    return redis.call(\"del\",KEYS[1])\n" +
                "else\n" +
                "    return 0\n" +
                "end" ;

        Long result = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList("item-lock:" + skuId), uuid);
        if(result == 1) {
            System.out.println("锁释放成功了...");
        }else {
            System.out.println("锁是别人的，释放锁失败...");
        }
    }

    // 加锁的方法
    public boolean tryLock(String uuid , Long skuId) {
        Boolean absent = redisTemplate.opsForValue().setIfAbsent("item-lock:" + skuId, uuid, 30, TimeUnit.SECONDS);
        return absent ;
    }

    public SkuDetailVo itemLocalLock(Long skuId) {          // 使用本地锁解决缓存击穿的问题

        // 通过布隆过滤器进行判断
//        if (!bloomFilter.mightContain(skuId)) {
//            log.info("布隆过滤器中不存在对应的数据,返回null...");
//            return null ;
//        }

        // 查询redis
        String skuDetailVoJSON = redisTemplate.opsForValue().get(GmallConstant.REDSI_SKU_DETAIL_PREFIX + skuId);
        if(!StringUtils.isEmpty(skuDetailVoJSON)) {
            if(GmallConstant.REDIS_NULL_VALUE.equalsIgnoreCase(skuDetailVoJSON)) {
                log.info("从缓存中查询到了x数据，返回null...");
                return null ;
            }else {
                log.info("从缓存中查询到了数据，返回...");
                SkuDetailVo skuDetailVo = JSON.parseObject(skuDetailVoJSON, SkuDetailVo.class);
                return skuDetailVo ;
            }
        }

        boolean tryLock = reentrantLock.tryLock();
        if(tryLock) {
            log.info("线程:{}获取到了锁，从数据库中查询数据..." , Thread.currentThread().getId());

            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // 远程调用findSkuDetailVoFromRpc方法
            SkuDetailVo skuDetailVo = findSkuDetailVoFromRpc(skuId) ;
            if(skuDetailVo == null) {
                log.info("从数据库没有查询到数据，将Redis中存储是X");
                redisTemplate.opsForValue().set(GmallConstant.REDSI_SKU_DETAIL_PREFIX + skuId , GmallConstant.REDIS_NULL_VALUE);
            }else {
                log.info("从数据库查询到了数据，然后将数据存储到Redis中");
                redisTemplate.opsForValue().set(GmallConstant.REDSI_SKU_DETAIL_PREFIX + skuId , JSON.toJSONString(skuDetailVo));
            }

            // 进行锁释放
            reentrantLock.unlock();

            // 返回
            return skuDetailVo ;

        }else {     // 从Redis中查询数据
            log.info("线程:{}没有获取到了锁，从redis中进行查询数据..." , Thread.currentThread().getId());
            skuDetailVoJSON = redisTemplate.opsForValue().get(GmallConstant.REDSI_SKU_DETAIL_PREFIX + skuId);
            if(!StringUtils.isEmpty(skuDetailVoJSON)) {
                if (GmallConstant.REDIS_NULL_VALUE.equalsIgnoreCase(skuDetailVoJSON)) {
                    log.info("从缓存中查询到了x数据，返回null...");
                    return null ;
                }else {
                    log.info("从缓存中查询到了数据，返回...");
                    return JSON.parseObject(skuDetailVoJSON , SkuDetailVo.class) ;
                }
            }else {

                try {
                    TimeUnit.MILLISECONDS.sleep(500);       // 线程休眠500ms
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                skuDetailVoJSON = redisTemplate.opsForValue().get(GmallConstant.REDSI_SKU_DETAIL_PREFIX + skuId);
                if (GmallConstant.REDIS_NULL_VALUE.equalsIgnoreCase(skuDetailVoJSON)) {
                    log.info("从缓存中查询到了x数据，返回null...");
                    return null ;
                }else {
                    log.info("从缓存中查询到了数据，返回...");
                    return JSON.parseObject(skuDetailVoJSON , SkuDetailVo.class) ;
                }
            }
        }
    }

    public SkuDetailVo itemBloomFilter(Long skuId) {

        // 通过布隆过滤器进行判断
//        if (!bloomFilter.mightContain(skuId)) {
//            log.info("布隆过滤器中不存在对应的数据,返回null...");
//            return null ;
//        }

        // 查询redis
        String skuDetailVoJSON = redisTemplate.opsForValue().get(GmallConstant.REDSI_SKU_DETAIL_PREFIX + skuId);
        if(!StringUtils.isEmpty(skuDetailVoJSON)) {
            if(GmallConstant.REDIS_NULL_VALUE.equalsIgnoreCase(skuDetailVoJSON)) {
                log.info("从缓存中查询到了x数据，返回null...");
                return null ;
            }else {
                log.info("从缓存中查询到了数据，返回...");
                SkuDetailVo skuDetailVo = JSON.parseObject(skuDetailVoJSON, SkuDetailVo.class);
                return skuDetailVo ;
            }
        }

        // 远程调用findSkuDetailVoFromRpc方法
        SkuDetailVo skuDetailVo = findSkuDetailVoFromRpc(skuId) ;
        if(skuDetailVo == null) {
            log.info("从数据库没有查询到数据，将Redis中存储是X");
            redisTemplate.opsForValue().set(GmallConstant.REDSI_SKU_DETAIL_PREFIX + skuId , GmallConstant.REDIS_NULL_VALUE);
        }else {
            log.info("从数据库查询到了数据，然后将数据存储到Redis中");
            redisTemplate.opsForValue().set(GmallConstant.REDSI_SKU_DETAIL_PREFIX + skuId , JSON.toJSONString(skuDetailVo));
        }

        // 返回
        return skuDetailVo;
    }

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor ;

    @Autowired
    private ThreadPoolProperties threadPoolProperties ;     // TODO: 线程池配置的热更新?

    // 1、把串行调用改成并行调用
    // 2、可以多个rpc调用，改成批量的rpc调用
    public SkuDetailVo findSkuDetailVoFromRpc(Long skuId) {

        // 创建skuDetailVo对象，封装查询结果数据
        SkuDetailVo skuDetailVo = new SkuDetailVo() ;

        // 根据skuId查询sku的基本信息和图片信息
        Result<SkuInfo> skuInfoAndImageResult = skuFeignClient.findSkuInfoAndImageBySkuId(skuId);
        SkuInfo skuInfoAndImage = skuInfoAndImageResult.getData();
        if(skuInfoAndImage == null) {
            log.info("数据库不存在该数据, skuId: {}" , skuId);
            return null ;
        }
        skuDetailVo.setSkuInfo(skuInfoAndImage);

        // 根据skuId查询三级分类数据
        CompletableFuture<Void> cf1 = CompletableFuture.runAsync(() -> {
            log.info("根据skuId查询三级分类数据...");
            Result<CategoryView> viewResult = skuFeignClient.findCategoryViewBySkuId(skuId);
            CategoryView categoryView = viewResult.getData();
            skuDetailVo.setCategoryView(categoryView);
        } , threadPoolExecutor);

        // 根据skuId查询价格数据
        CompletableFuture<Void> cf2 = CompletableFuture.runAsync(() -> {
            log.info("根据skuId查询价格数据...");
            Result<SkuInfo> skuInfoResult = skuFeignClient.findSkuInfoBySkuId(skuId);
            BigDecimal price = skuInfoResult.getData().getPrice();
            skuDetailVo.setPrice(price);
        }, threadPoolExecutor);

        // 根据skuId查询其所对应的spu的销售属性和销售属性值
        CompletableFuture<Void> cf3 = CompletableFuture.runAsync(() -> {
            log.info("根据skuId查询其所对应的spu的销售属性和销售属性值");
            Result<List<SpuSaleAttr>> spuSalAttrResult = skuFeignClient.findSpuSalAttrBySkuId(skuId);
            List<SpuSaleAttr> spuSaleAttrList = spuSalAttrResult.getData();
            skuDetailVo.setSpuSaleAttrList(spuSaleAttrList);
        }, threadPoolExecutor);

        //  根据skuId查询出该sku所对应的所有的兄弟sku(包含自己)的销售属性值的组合
        CompletableFuture<Void> cf4 = CompletableFuture.runAsync(() -> {
            log.info("根据skuId查询出该sku所对应的所有的兄弟sku(包含自己)的销售属性值的组合");
            Result<List<AttrValueConcatVo>> listResult = skuFeignClient.findSkuAttrValueConcatBySkuId(skuId);
            List<AttrValueConcatVo> data = listResult.getData();
            Map<String, Long> map = data.stream().collect(Collectors.toMap(attrValueConcatVo -> attrValueConcatVo.getAttrValueConcat(), attrValueConcatVo -> attrValueConcatVo.getSkuId()));
            String toJSONString = JSON.toJSONString(map);
            skuDetailVo.setValuesSkuJson(toJSONString);
        }, threadPoolExecutor);

        // 任务的组合
        CompletableFuture.allOf(cf1 , cf2 , cf3 , cf4).join(); // 阻塞当前线程，等待其他的线程执行完毕以后在执行当前线程

        // 返回
        return skuDetailVo ;

    }

    /**
     * 创建一个线程方式：
     * 1、继承Thread类
     * 2、实现Runnable接口
     * 3、实现Callable接口
     * 4、使用线程池               -----> 创建方式一：Executors        创建方式二：ThreadPoolExecutor
     */
    public SkuDetailVo findSkuDetailVoFromRpcThreadPool(Long skuId) {

        Integer corePoolSize = threadPoolProperties.getCorePoolSize();
        System.out.println(corePoolSize);

        CountDownLatch countDownLatch = new CountDownLatch(4) ;

        // 创建skuDetailVo对象，封装查询结果数据
        SkuDetailVo skuDetailVo = new SkuDetailVo() ;

        // 根据skuId查询sku的基本信息和图片信息
        Result<SkuInfo> skuInfoAndImageResult = skuFeignClient.findSkuInfoAndImageBySkuId(skuId);
        SkuInfo skuInfoAndImage = skuInfoAndImageResult.getData();
        if(skuInfoAndImage == null) {
            log.info("数据库不存在该数据, skuId: {}" , skuId);
            return null ;
        }
        skuDetailVo.setSkuInfo(skuInfoAndImage);

        threadPoolExecutor.submit(() -> {
            log.info("根据skuId查询三级分类数据...");
            // 根据skuId查询三级分类数据
            Result<CategoryView> viewResult = skuFeignClient.findCategoryViewBySkuId(skuId);
            CategoryView categoryView = viewResult.getData();
            skuDetailVo.setCategoryView(categoryView);
            countDownLatch.countDown();
        }) ;

        threadPoolExecutor.submit(() -> {
            log.info("根据skuId查询价格数据...");
            // 根据skuId查询价格数据
            Result<SkuInfo> skuInfoResult = skuFeignClient.findSkuInfoBySkuId(skuId);
            BigDecimal price = skuInfoResult.getData().getPrice();
            skuDetailVo.setPrice(price);
            countDownLatch.countDown();
        });

        threadPoolExecutor.submit(() -> {
            log.info("根据skuId查询其所对应的spu的销售属性和销售属性值");
            // 根据skuId查询其所对应的spu的销售属性和销售属性值
            Result<List<SpuSaleAttr>> spuSalAttrResult = skuFeignClient.findSpuSalAttrBySkuId(skuId);
            List<SpuSaleAttr> spuSaleAttrList = spuSalAttrResult.getData();
            skuDetailVo.setSpuSaleAttrList(spuSaleAttrList);
            countDownLatch.countDown();
        }) ;

        threadPoolExecutor.submit(() -> {
            log.info("根据skuId查询出该sku所对应的所有的兄弟sku(包含自己)的销售属性值的组合");
            //  根据skuId查询出该sku所对应的所有的兄弟sku(包含自己)的销售属性值的组合
            Result<List<AttrValueConcatVo>> listResult = skuFeignClient.findSkuAttrValueConcatBySkuId(skuId);
            List<AttrValueConcatVo> data = listResult.getData();
            Map<String, Long> map = data.stream().collect(Collectors.toMap(attrValueConcatVo -> attrValueConcatVo.getAttrValueConcat(), attrValueConcatVo -> attrValueConcatVo.getSkuId()));
            String toJSONString = JSON.toJSONString(map);
            skuDetailVo.setValuesSkuJson(toJSONString);
            countDownLatch.countDown();
        }) ;

        try {
            countDownLatch.await();         // 阻塞当前线程
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return skuDetailVo ;

    }

}
