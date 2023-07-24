package com.atguigu.gmall.product.biz.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.constant.GmallConstant;
import com.atguigu.gmall.product.biz.SkuBizService;
import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.atguigu.gmall.product.mapper.SkuSaleAttrValueMapper;
import com.atguigu.gmall.product.mapper.SpuSaleAttrMapper;
import com.atguigu.gmall.product.vo.AttrValueConcatVo;
import com.atguigu.gmall.product.vo.CategoryView;
import com.atguigu.gmall.product.vo.SkuDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SkuBizServiceImpl implements SkuBizService {

    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper ;

    @Autowired
    private SkuInfoMapper skuInfoMapper ;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper ;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper ;

    @Override
    public CategoryView findCategoryViewBySkuId(Long skuId) {
        return baseCategory1Mapper.findCategoryViewBySkuId(skuId);
    }

    @Override
    public SkuInfo findSkuInfoAndImageBySkuId(Long skuId) {
        return skuInfoMapper.findSkuInfoAndImageBySkuId(skuId);
    }

    @Override
    public SkuInfo findSkuInfoBySkuId(Long skuId) {
        return skuInfoMapper.selectById(skuId);
    }

    @Override
    public List<SpuSaleAttr> findSpuSalAttrBySkuId(Long skuId) {
        return spuSaleAttrMapper.findSpuSalAttrBySkuId(skuId);
    }

    @Override
    public List<AttrValueConcatVo> findSkuAttrValueConcatBySkuId(Long skuId) {
        return skuSaleAttrValueMapper.findSkuAttrValueConcatBySkuId(skuId);
    }

    @Override
    public SkuDetailVo findSkuDetailVo(Long skuId) {

        SkuDetailVo skuDetailVo = new SkuDetailVo() ;
        SkuInfo skuInfoAndImage = findSkuInfoAndImageBySkuId(skuId);
        if(skuInfoAndImage == null) {
            return null ;
        }
        skuDetailVo.setSkuInfo(skuInfoAndImage);

        CompletableFuture<Void>  cf1 = CompletableFuture.runAsync(() -> {
            CategoryView categoryView = this.findCategoryViewBySkuId(skuId);
            skuDetailVo.setCategoryView(categoryView);
        });

        CompletableFuture<Void>  cf2 = CompletableFuture.runAsync(() -> {
            SkuInfo skuInfo = findSkuInfoBySkuId(skuId);
            skuDetailVo.setPrice(skuInfo.getPrice());
        });

        CompletableFuture<Void>  cf3 = CompletableFuture.runAsync(() -> {
            List<SpuSaleAttr> spuSaleAttrList = findSpuSalAttrBySkuId(skuId) ;
            skuDetailVo.setSpuSaleAttrList(spuSaleAttrList);
        });

        CompletableFuture<Void>  cf4 = CompletableFuture.runAsync(() -> {
            List<AttrValueConcatVo> attrValueConcatVos= findSkuAttrValueConcatBySkuId(skuId) ;
            Map<String, Long> map = attrValueConcatVos.stream().collect(Collectors.toMap(attrValueConcatVo -> attrValueConcatVo.getAttrValueConcat(), attrValueConcatVo -> attrValueConcatVo.getSkuId()));
            String toJSONString = JSON.toJSONString(map);
            skuDetailVo.setValuesSkuJson(toJSONString);
        });

        CompletableFuture.allOf(cf1 , cf2 , cf3 , cf4).join() ;

        return skuDetailVo;
    }

    @Override
    public List<Long> findAllSkuIds() {
        List<SkuInfo> skuInfoList = skuInfoMapper.selectList(null);
        List<Long> list = skuInfoList.stream().map(skuInfo -> skuInfo.getId()).collect(Collectors.toList());
        return list;
    }

    @Autowired
    private RedissonClient redissonClient ;

    @PostConstruct
    public void initBloomFilter(){          // 初始化布隆过滤器
        RBloomFilter<Long> rBloomFilter = redissonClient.getBloomFilter(GmallConstant.REDIS_SKUID_BLOOM_FILTER);
        rBloomFilter.tryInit(1000000 , 0.000001) ;
        List<Long> allSkuIds = findAllSkuIds();
        allSkuIds.forEach(skuId -> rBloomFilter.add(skuId) );
        log.info("分布式的布隆过滤器初始化成功了...");
    }

}
