package com.atguigu.gmall.common.feign.item.fallback;

import com.atguigu.gmall.common.feign.item.SkuDetailFeignClient;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.product.vo.SkuDetailVo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SkuDetailFeignClientFallback implements SkuDetailFeignClient {

    @Override
    public Result<SkuDetailVo> item(Long skuId) {
        log.info("SkuDetailFeignClientFallback...item方法执行了...");
        return Result.build(null , ResultCodeEnum.SUCCESS) ;
    }
}
