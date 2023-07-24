package com.atguigu.gmall.product.vo;

import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuDetailVo {

    private CategoryView categoryView ;
    private SkuInfo skuInfo ;
    private BigDecimal price ;
    private List<SpuSaleAttr>  spuSaleAttrList ;
    private String valuesSkuJson ;

}
