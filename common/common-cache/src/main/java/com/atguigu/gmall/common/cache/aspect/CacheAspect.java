package com.atguigu.gmall.common.cache.aspect;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.cache.anno.GmallCache;
import com.atguigu.gmall.common.cache.service.RedisCacheService;
import com.atguigu.gmall.common.constant.GmallConstant;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

//@Component
@Aspect
@Slf4j
public class CacheAspect {

    @Autowired
    private RedissonClient redissonClient ;

    private RBloomFilter<Long> rBloomFilter ;

    @Autowired
    private RedisCacheService redisCacheService ;

    // @PostConstruct
    public void init() {     // 在服务器启动的时候调用该方法
        rBloomFilter = redissonClient.getBloomFilter(GmallConstant.REDIS_SKUID_BLOOM_FILTER);
    }

    @Around(value = "@annotation(gmallCache)")
    public Object around(ProceedingJoinPoint  proceedingJoinPoint , GmallCache gmallCache) {

        String bloomFilterNameExpression = gmallCache.bloomFilterName();        // 获取布隆过滤器名称的表达式
        if(!StringUtils.isEmpty(bloomFilterNameExpression)) {                    // 需要使用布隆过滤器
            String bloomFilterValueExpression = gmallCache.bloomFilterValue();
            if(!StringUtils.isEmpty(bloomFilterValueExpression)) {              // 进行布隆过滤器的判断
                String bloomFilterName = paraseExpression(proceedingJoinPoint, bloomFilterNameExpression, String.class);
                Object bloomFilterValue = paraseExpression(proceedingJoinPoint, bloomFilterValueExpression, Object.class);
                RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(bloomFilterName);
                if (!bloomFilter.contains(bloomFilterValue)) {            // 使用布隆过滤器进行数据的判断
                    log.info("分布式的布隆过滤器中不存在对应的数据,返回null...");
                    return null ;
                }
            }

        }

        // 获取redis的数据key
        String cacheKeyExpression = gmallCache.cacheKey();  // sku-detail:#{#params[0]}
        String cacheKey = paraseExpression(proceedingJoinPoint, cacheKeyExpression, String.class);

        // 获取目标方法的返回值类型
        Type returnType = getMethodReturnType(proceedingJoinPoint) ;

        // 查询Redis, Redis中如果没有数据继续向下执行
        Object result = redisCacheService.getData(cacheKey, returnType);
        if(result != null) {
            log.info("从缓存中查询到了数据，返回...");
            return result ;
        }

        boolean enableLock = gmallCache.enableLock();
        long time = gmallCache.time();      // 缓存时间
        TimeUnit timeUnit = gmallCache.timeUnit();      // 缓存时间单位

        if(enableLock) {

            String lockNameExpression = gmallCache.lockName();
            if(StringUtils.isEmpty(lockNameExpression)) {           // 不使用分布式锁
                return executTargetMethod(proceedingJoinPoint , cacheKey , time , timeUnit) ;
            }else {     // 使用分布式锁
                String lockName = paraseExpression(proceedingJoinPoint, lockNameExpression, String.class);
                RLock rLock = redissonClient.getLock(lockName);
                boolean tryLock = rLock.tryLock();
                if(tryLock) {
                    try {
                        log.info("线程:{}获取到了锁，从数据库中查询数据..." , Thread.currentThread().getId());
                        Object method = executTargetMethod(proceedingJoinPoint, cacheKey, time , timeUnit);
                        return method ;
                    }catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(e) ;
                    }finally {
                        rLock.unlock();
                    }
                }else {

                    log.info("线程:{}没有获取到了锁，从redis中进行查询数据..." , Thread.currentThread().getId());
                    result = redisCacheService.getData(cacheKey, returnType);
                    if(result != null) {
                        log.info("线程:{} 从缓存中查询到了数据，返回..." , Thread.currentThread().getId());
                        return result ;
                    }

                    try {
                        TimeUnit.MILLISECONDS.sleep(500);       // 线程休眠500ms
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    return redisCacheService.getData(cacheKey, returnType);
                }
            }

        }else {     // 不需要使用分布式锁
            return executTargetMethod(proceedingJoinPoint , cacheKey, time , timeUnit) ;
        }

    }

    public Object executTargetMethod(ProceedingJoinPoint proceedingJoinPoint , String cacheKey , Long time , TimeUnit timeUnit) {
        Object result = null ;
        try {
            result = proceedingJoinPoint.proceed();     // 执行目标方法
            if(result == null) {
                log.info("线程:{} , 从数据库没有查询到数据，将Redis中存储是X" , Thread.currentThread().getId());
                redisCacheService.saveData(cacheKey , GmallConstant.REDIS_NULL_VALUE , time , timeUnit);
            }else {
                log.info("线程:{} , 从数据库查询到了数据，然后将数据存储到Redis中" , Thread.currentThread().getId());
                redisCacheService.saveData(cacheKey , result , time , timeUnit);
            }

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        // 返回
        log.info("返回数据库中查询到的数据：{}" , JSON.toJSONString(result) );
        return result ;
    }

    private SpelExpressionParser spelExpressionParser = new SpelExpressionParser() ;

    public Type getMethodReturnType(ProceedingJoinPoint proceedingJoinPoint) {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature() ;
        Method method = methodSignature.getMethod();
        Type returnType = method.getGenericReturnType();
        return returnType ;
    }

    // 对表达式进行解析获取结果
    public <T> T paraseExpression(ProceedingJoinPoint proceedingJoinPoint , String  expressionStr , Class<T> clazz) {
        Expression expression = spelExpressionParser.parseExpression(expressionStr, ParserContext.TEMPLATE_EXPRESSION);
        EvaluationContext evaluationContext = new StandardEvaluationContext() ;
        evaluationContext.setVariable("params" , proceedingJoinPoint.getArgs());
        T value = expression.getValue(evaluationContext, clazz);
        return value ;
    }

}
