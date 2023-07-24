package com.atguigu.gmall.product.service.impl;
import com.atguigu.gmall.product.biz.SkuBizService;
import com.atguigu.gmall.product.entity.*;
import com.atguigu.gmall.product.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.product.service.*;
import com.atguigu.gmall.product.vo.CategoryView;
import com.atguigu.gmall.search.entity.SearchAttr;
import com.google.common.collect.Lists;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.atguigu.gmall.common.constant.GmallConstant;
import com.atguigu.gmall.common.feign.seach.SearchFeignClient;
import com.atguigu.gmall.product.dto.SkuInfoDto;
import com.atguigu.gmall.search.entity.Goods;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author Administrator
* @description 针对表【sku_info(库存单元表)】的数据库操作Service实现
* @createDate 2023-07-10 16:46:04
*/
@Service
@Slf4j
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo> implements SkuInfoService{

    @Autowired
    private SkuImageService skuImageService ;

    @Autowired
    private SkuAttrValueService skuAttrValueService ;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService ;

    @Autowired
    private RedisTemplate<String , String> redisTemplate ;

    @Autowired
    private SearchFeignClient searchFeignClient ;

    @Autowired
    private BaseTrademarkService baseTrademarkService ;

    @Autowired
    private SkuBizService skuBizService ;

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper ;

    @Override
    public Page findByPage(Integer pageNo, Integer pageSize) {
        Page page = new Page(pageNo , pageSize) ;
        this.page(page) ;
        return page;
    }

    @Override
    @Transactional
    public void saveSkuInfo(SkuInfoDto skuInfoDto) {

        // 保存sku的基本信息
        SkuInfo skuInfo = new SkuInfo() ;
        BeanUtils.copyProperties(skuInfoDto , skuInfo);
        skuInfo.setIsSale(0);
        this.save(skuInfo) ;

        // 保存sku的图片数据
        List<SkuImage> skuImageList = skuInfoDto.getSkuImageList();
        skuImageList = skuImageList.stream().map(skuImage -> {
            skuImage.setSkuId(skuInfo.getId());
            return skuImage ;
        }).collect(Collectors.toList()) ;
        skuImageService.saveBatch(skuImageList) ;

        // 保存sku的平台属性值数据
        List<SkuAttrValue> skuAttrValueList = skuInfoDto.getSkuAttrValueList();
        skuAttrValueList = skuAttrValueList.stream().map(skuAttrValue -> {
            skuAttrValue.setSkuId(skuInfo.getId());
            return skuAttrValue ;
        }).collect(Collectors.toList()) ;
        skuAttrValueService.saveBatch(skuAttrValueList) ;

        // 保存sku的销售属性值数据
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfoDto.getSkuSaleAttrValueList();
        skuSaleAttrValueList = skuSaleAttrValueList.stream().map(skuSaleAttrValue -> {
            skuSaleAttrValue.setSkuId(skuInfo.getId());
            skuSaleAttrValue.setSpuId(skuInfoDto.getSpuId());
            return skuSaleAttrValue ;
        }).collect(Collectors.toList()) ;
        skuSaleAttrValueService.saveBatch(skuSaleAttrValueList) ;

    }


    private ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);

    @Override
    public void onSale(Long skuId) {

        // 删除redis中的数据
        // redisTemplate.delete(GmallConstant.REDSI_SKU_DETAIL_PREFIX + skuId) ;

        // 对数据库中的数据进行更新
        SkuInfo skuInfo = this.getById(skuId);
        skuInfo.setIsSale(1);
        this.updateById(skuInfo) ;

        // 延迟删除
//        scheduledThreadPool.schedule(() -> {
//            redisTemplate.delete(GmallConstant.REDSI_SKU_DETAIL_PREFIX + skuId) ;
//            log.info("延迟删除任务执行完毕了...");
//        }  , 300 , TimeUnit.MILLISECONDS) ;

        // 远程调用service-search微服务的接口，把商品数据添加到es索引库中
        Goods goods = buildGoods(skuId) ;
        searchFeignClient.saveGoods(goods) ;

    }

    private Goods buildGoods(Long skuId) {

        // 创建Goods对象
        Goods goods = new Goods() ;
        goods.setId(skuId);

        // 设置sku的基本数据
        SkuInfo skuInfo = this.getById(skuId);
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setTitle(skuInfo.getSkuName());
        goods.setPrice(skuInfo.getPrice());
        goods.setCreateTime(new Date());

        CompletableFuture<Void> cf1 = CompletableFuture.runAsync(() -> {
            // 设置品牌数据
            BaseTrademark baseTrademark = baseTrademarkService.getById(skuInfo.getTmId());
            goods.setTmId(baseTrademark.getId());
            goods.setTmName(baseTrademark.getTmName());
            goods.setTmLogoUrl(baseTrademark.getLogoUrl());
        });

        CompletableFuture<Void> cf2 = CompletableFuture.runAsync(() -> {
            // 根据skuId查询三级分类数据
            CategoryView categoryView = skuBizService.findCategoryViewBySkuId(skuId);
            goods.setCategory1Id(categoryView.getCategory1Id());
            goods.setCategory1Name(categoryView.getCategory1Name());
            goods.setCategory2Id(categoryView.getCategory2Id());
            goods.setCategory2Name(categoryView.getCategory2Name());
            goods.setCategory3Id(categoryView.getCategory3Id());
            goods.setCategory3Name(categoryView.getCategory3Name());
        });

        CompletableFuture<Void> cf3 = CompletableFuture.runAsync(() -> {
            // 平台属性和平台属性值
            List<SearchAttr> searchAttrList = skuAttrValueMapper.findSearchAttrBySkuId(skuId);
            goods.setAttrs(searchAttrList);
        });

        // 设置热度分
        goods.setHotScore(0L);

        CompletableFuture.allOf(cf1 , cf2 , cf3).join() ;

        // 返回
        return goods ;
    }

    @Override
    public void cancelSale(Long skuId) {
        SkuInfo skuInfo = this.getById(skuId);
        skuInfo.setIsSale(0);
        this.updateById(skuInfo) ;

        // 远程调用service-search微服务的接口，从ES索引库中删除商品数据
        searchFeignClient.deleteById(skuId) ;

    }

}




