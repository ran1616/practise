package com.atguigu.gmall.common.feign.product;

import com.atguigu.gmall.common.feign.product.fallback.BaseCategoryFeignClientFallback;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.vo.CategoryVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(value = "service-product" , fallback = BaseCategoryFeignClientFallback.class)
public interface BaseCategoryFeignClient {

    @GetMapping(value = "/api/inner/product/findAllCategoryTree")
    public abstract Result<List<CategoryVo>> findAllCategoryTree() ;

}
