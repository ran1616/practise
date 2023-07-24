package com.atguigu.gmall.common.feign.product.fallback;

import com.atguigu.gmall.common.feign.product.BaseCategoryFeignClient;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.product.vo.CategoryVo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

//@Component
@Slf4j
public class BaseCategoryFeignClientFallback implements BaseCategoryFeignClient {

    @Override
    public Result<List<CategoryVo>> findAllCategoryTree() {
        log.info("BaseCategoryFeignClientFallback....findAllCategoryTree执行了....");
        return Result.build(null  , ResultCodeEnum.SUCCESS) ;
    }

}
