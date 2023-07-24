package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Administrator
* @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Mapper
* @createDate 2023-07-10 16:46:04
* @Entity com.atguigu.gmall.product.entity.SpuSaleAttr
*/
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {

    public abstract List<SpuSaleAttr> findBySpuId(Long spuId);

    public abstract List<SpuSaleAttr> findSpuSalAttrBySkuId(Long skuId);
}




