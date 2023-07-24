package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.GmallConstant;
import com.atguigu.gmall.product.biz.SkuBizService;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.atguigu.gmall.product.service.BloomFilterService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class BloomFilterServiceImpl implements BloomFilterService {

    @Autowired
    private RedissonClient redissonClient ;

    @Autowired
    private SkuBizService skuBizService;

    @Autowired
    private RedisTemplate<String , String> redisTemplate ;

    @Override
    public void resetBloomFilter() {

        // 创建一个新的bloomFilter
        RBloomFilter<Long> newBloomFilter = redissonClient.getBloomFilter(GmallConstant.REDIS_SKUID_BLOOM_FILTER_NEW);
        newBloomFilter.tryInit(1000000 , 0.000001) ;
        List<Long> allSkuIds = skuBizService.findAllSkuIds();
        allSkuIds.forEach(skuId -> newBloomFilter.add(skuId));
        newBloomFilter.add(100L) ;          // 为了进行测试
        log.info("新的布隆过滤器初始化好了...");

        // 删除之前的布隆过滤器，对新布隆过滤器进行重命名
        String script = "redis.call('del' , KEYS[1])\n" +
                "redis.call('del' , \"{\"..KEYS[1]..\"}:config\")\n" +
                "redis.call('rename' , KEYS[2] , KEYS[1])\n" +
                "redis.call('rename' , \"{\"..KEYS[2]..\"}:config\" , \"{\"..KEYS[1]..\"}:config\")\n" +
                "return 1" ;

        // 执行lua脚本
        Long reuslt = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList(GmallConstant.REDIS_SKUID_BLOOM_FILTER, GmallConstant.REDIS_SKUID_BLOOM_FILTER_NEW));
        if(reuslt == 1) {
            log.info("布隆过滤器重置成功了...");
        }

    }

}
