package com.atguigu.gmall.product.biz;

import com.atguigu.gmall.product.entity.SkuInfo;
import com.atguigu.gmall.product.entity.SpuSaleAttr;
import com.atguigu.gmall.product.vo.AttrValueConcatVo;
import com.atguigu.gmall.product.vo.CategoryView;
import com.atguigu.gmall.product.vo.SkuDetailVo;

import java.util.List;

public interface SkuBizService {

    public abstract CategoryView findCategoryViewBySkuId(Long skuId);

    public abstract  SkuInfo findSkuInfoAndImageBySkuId(Long skuId);

    public abstract SkuInfo findSkuInfoBySkuId(Long skuId);

    public abstract  List<SpuSaleAttr> findSpuSalAttrBySkuId(Long skuId);

    public abstract List<AttrValueConcatVo> findSkuAttrValueConcatBySkuId(Long skuId);

    public abstract SkuDetailVo findSkuDetailVo(Long skuId);

    public abstract List<Long> findAllSkuIds();
}
