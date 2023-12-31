package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.product.entity.BaseCategory1;
import com.atguigu.gmall.product.vo.CategoryView;
import com.atguigu.gmall.product.vo.CategoryVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_category1(一级分类表)】的数据库操作Mapper
* @createDate 2023-07-10 16:46:04
* @Entity com.atguigu.gmall.product.entity.BaseCategory1
*/
public interface BaseCategory1Mapper extends BaseMapper<BaseCategory1> {

    public abstract List<CategoryVo> findAllCategoryTree();

    public abstract  CategoryView findCategoryViewBySkuId(Long skuId);

}




