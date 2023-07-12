package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Service
* @createDate 2023-07-10 16:46:04
*/
public interface SpuSaleAttrService extends IService<SpuSaleAttr> {

    public abstract List<SpuSaleAttr> findBySpuId(Long spuId);
}
