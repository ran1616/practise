package com.atguigu.gmall.common.constant;

public interface GmallConstant {

    /**
     * 缓存三级分类的数据key
     */
    public static final String REDIS_CATEGORY_KEY = "allCategory" ;

    /**
     * 缓存x的值
     */
    public static final String REDIS_NULL_VALUE = "X" ;

    /**
     * 商品详情的redis的key的前缀
     */
    public static final String REDSI_SKU_DETAIL_PREFIX = "sku-detail:" ;

    /**
     * 分布式锁的前缀
     */
    public static final String REDIS_ITEM_LOCK_PREFIX = "item-lock:" ;

    /**
     * 分布式布隆过滤器的名称
     */
    public static final String REDIS_SKUID_BLOOM_FILTER = "skuId-bloom-filter" ;

    /**
     * 分布式布隆过滤器新的名称
     */
    public static final String REDIS_SKUID_BLOOM_FILTER_NEW = "skuId-bloom-filter-new" ;

}
