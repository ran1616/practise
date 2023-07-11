package com.atguigu.gmall.product.service;

import com.atguigu.gmall.product.entity.BaseCategory1;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_category1(一级分类表)】的数据库操作Service
* @createDate 2023-07-10 16:46:04
*/
public interface BaseCategory1Service extends IService<BaseCategory1> {

    public abstract List<BaseCategory1> findAllBaseCategory1();
}
