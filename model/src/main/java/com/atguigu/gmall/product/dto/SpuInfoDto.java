package com.atguigu.gmall.product.dto;

import com.atguigu.gmall.product.entity.SpuImage;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import lombok.Data;

import java.util.List;

@Data
public class SpuInfoDto {

    private Long id ;
    private String spuName ;
    private String description ;
    private Long category3Id ;
    private Long tmId ;

    private List<SpuImage> spuImageList ;
    private List<SpuSaleAttr> spuSaleAttrList ;
}
