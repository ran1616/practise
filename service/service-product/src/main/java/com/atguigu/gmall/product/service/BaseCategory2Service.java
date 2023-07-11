package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.entity.BaseCategory2;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_category2(二级分类表)】的数据库操作Service
* @createDate 2023-07-10 16:46:04
*/
public interface BaseCategory2Service extends IService<BaseCategory2> {

    public abstract List<BaseCategory2> findBaseCategoryByC1Id(Long c1Id);

}
