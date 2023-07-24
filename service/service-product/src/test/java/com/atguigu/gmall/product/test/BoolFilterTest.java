package com.atguigu.gmall.product.test;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

public class BoolFilterTest {

    public static void main(String[] args) {

        // 创建一个布隆过滤器对象
        // Funnel<? super T> funnel, int expectedInsertions, double fpp
        // Funnel: 用来指定布隆过滤器中存储元素的数据类型
        // expectedInsertions:  期望布隆过滤器中存储数据的最大数据量
        // fpp: 误判率
        BloomFilter<Long> bloomFilter = BloomFilter.create(Funnels.longFunnel(), 1000000, 0.000001);

        // 添加数据
        bloomFilter.put(49L) ;
        bloomFilter.put(50L) ;
        bloomFilter.put(51L) ;
        bloomFilter.put(52L) ;

        // 判断数据是否存在
        System.out.println(bloomFilter.mightContain(49L));
        System.out.println(bloomFilter.mightContain(100L));

    }

}
