package com.atguigu.gmall.product.test;

import com.atguigu.gmall.product.ProductApplicaiton;
import com.atguigu.gmall.product.entity.SkuImage;
import com.atguigu.gmall.product.mapper.SkuImageMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ProductApplicaiton.class)
public class ShardingSphereTest {

    @Autowired
    private SkuImageMapper skuImageMapper ;

    @Test
    public void saveSkuImage() {
        SkuImage skuImage = new SkuImage() ;
        skuImage.setSkuId(3L);
        skuImage.setImgName("sssss");
        skuImage.setImgUrl("aaaa");
        skuImageMapper.insert(skuImage) ;
    }

    @Test
    public void findSkuImageById() {
        for(int x = 0 ; x < 10 ; x++) {
            SkuImage skuImage = skuImageMapper.selectById(269L);
            System.out.println(skuImage);
        }
    }


}
