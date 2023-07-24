package com.atguigu.gmall.product.biz.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.cache.anno.GmallCache;
import com.atguigu.gmall.common.constant.GmallConstant;
import com.atguigu.gmall.product.biz.BaseCategoryBizService;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import com.atguigu.gmall.product.vo.CategoryVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class BaseCategoryBizServiceImpl implements BaseCategoryBizService {

    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper ;

    private ConcurrentHashMap<String , List<CategoryVo>> cacheMap = new ConcurrentHashMap<>() ;

    @Autowired
    private RedisTemplate<String , String> redisTemplate ;

    @Override
    @GmallCache(cacheKey = "allCategory")
    public List<CategoryVo> findAllCategoryTree() {
        List<CategoryVo> categoryVos = baseCategory1Mapper.findAllCategoryTree();
        return categoryVos ;
    }

    public List<CategoryVo> findAllCategoryTreeRedisTemplate() {

        // 从Redis缓存中进行命中
        String allCategoryJSON = redisTemplate.opsForValue().get(GmallConstant.REDIS_CATEGORY_KEY);
        if(!StringUtils.isEmpty(allCategoryJSON)) {

            // 判断数据是否为x
            if(GmallConstant.REDIS_NULL_VALUE.equalsIgnoreCase(allCategoryJSON)) {
                log.info("从Redis缓存中查询到了数据...x");
                return null ;
            }else {
                log.info("从Redis缓存中查询到了数据...");
                List<CategoryVo> categoryVoList = JSON.parseArray(allCategoryJSON, CategoryVo.class);
                return categoryVoList ;
            }

        }

        log.info("从数据库中查询到了数据...");
        List<CategoryVo> categoryVos = baseCategory1Mapper.findAllCategoryTree();
        if(categoryVos != null && categoryVos.size() > 0) {
            redisTemplate.opsForValue().set(GmallConstant.REDIS_CATEGORY_KEY , JSON.toJSONString(categoryVos) );
        }else {
            redisTemplate.opsForValue().set(GmallConstant.REDIS_CATEGORY_KEY , GmallConstant.REDIS_NULL_VALUE);
        }

        return categoryVos ;
    }


//    @Override
//    public List<CategoryVo> findAllCategoryTree() {
//
//        // 从Redis缓存中进行命中
//        Object allCategory = redisTemplate.opsForValue().get("allCategory");
//        if(allCategory != null) {
//            log.info("从Redis缓存中查询到了数据...");
//            List<CategoryVo> categoryVoList = (List<CategoryVo>)allCategory ;
//            return categoryVoList ;
//        }
//
//        log.info("从数据库中查询到了数据...");
//        List<CategoryVo> categoryVos = baseCategory1Mapper.findAllCategoryTree();
//        redisTemplate.opsForValue().set("allCategory", categoryVos);
//
//        return categoryVos ;
//    }

//    @Override
//    public List<CategoryVo> findAllCategoryTree() {
//
//        // 从本地缓存中进行命中
//        List<CategoryVo> categoryVoList = cacheMap.get("allCategory");
//        if(categoryVoList != null && categoryVoList.size() > 0) {
//            log.info("从本地缓存中查询到了数据...");
//            return categoryVoList ;
//        }
//
//        log.info("从数据库中查询到了数据...");
//        List<CategoryVo> categoryVos = baseCategory1Mapper.findAllCategoryTree();
//        cacheMap.put("allCategory" , categoryVos) ;
//
//        return categoryVos ;
//    }
}
