package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.entity.BaseCategory3;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_category3(三级分类表)】的数据库操作Service
* @createDate 2023-07-10 16:46:04
*/
public interface BaseCategory3Service extends IService<BaseCategory3> {

    public abstract List<BaseCategory3> findBaseCategory3ByC2Id(Long c2Id);
}
