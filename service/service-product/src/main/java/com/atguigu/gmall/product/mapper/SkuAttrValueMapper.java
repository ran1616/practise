package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.product.entity.SkuAttrValue;
import com.atguigu.gmall.search.entity.SearchAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Administrator
* @description 针对表【sku_attr_value(sku平台属性值关联表)】的数据库操作Mapper
* @createDate 2023-07-10 16:46:04
* @Entity com.atguigu.gmall.product.entity.SkuAttrValue
*/
public interface SkuAttrValueMapper extends BaseMapper<SkuAttrValue> {

    public abstract List<SearchAttr> findSearchAttrBySkuId(Long skuId) ;

}




