package com.atguigu.gmall.product.dto;

import com.atguigu.gmall.product.entity.SkuAttrValue;
import com.atguigu.gmall.product.entity.SkuImage;
import com.atguigu.gmall.product.entity.SkuSaleAttrValue;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuInfoDto {

    private Long id ;
    private Long spuId ;
    private BigDecimal price ;
    private  String skuName ;
    private BigDecimal weight ;
    private String skuDesc ;
    private Long category3Id ;
    private String skuDefaultImg ;
    private Long tmId ;

    private List<SkuAttrValue> skuAttrValueList ;       // 平台属性值的集合

    private List<SkuSaleAttrValue> skuSaleAttrValueList ;

    private List<SkuImage> skuImageList ;

}
