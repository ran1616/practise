package com.atguigu.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.entity.BaseCategory2;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import com.atguigu.gmall.product.mapper.BaseCategory2Mapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_category2(二级分类表)】的数据库操作Service实现
* @createDate 2023-07-10 16:46:04
*/
@Service
public class BaseCategory2ServiceImpl extends ServiceImpl<BaseCategory2Mapper, BaseCategory2> implements BaseCategory2Service{

    @Override
    public List<BaseCategory2> findBaseCategoryByC1Id(Long c1Id) {
//        QueryWrapper<BaseCategory2> queryWrapper = new QueryWrapper<>() ;
//        queryWrapper.eq("category1_id" , c1Id) ;

        LambdaQueryWrapper<BaseCategory2> lambdaQueryWrapper = new LambdaQueryWrapper<>() ;
        lambdaQueryWrapper.eq(BaseCategory2::getCategory1Id ,c1Id ) ;
        return this.list(lambdaQueryWrapper) ;
    }
}




