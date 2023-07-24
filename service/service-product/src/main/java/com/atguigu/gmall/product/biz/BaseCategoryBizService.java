package com.atguigu.gmall.product.biz;

import com.atguigu.gmall.product.vo.CategoryVo;

import java.util.List;

public interface BaseCategoryBizService {

    public abstract List<CategoryVo> findAllCategoryTree();
}
