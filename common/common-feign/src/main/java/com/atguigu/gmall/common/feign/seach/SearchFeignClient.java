package com.atguigu.gmall.common.feign.seach;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.search.dto.SearchParamDTO;
import com.atguigu.gmall.search.entity.Goods;
import com.atguigu.gmall.search.vo.SearchResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "service-search")
public interface SearchFeignClient {

    @PostMapping(value = "/api/inner/search/saveGoods")
    public Result saveGoods(@RequestBody Goods goods) ;

    @DeleteMapping(value = "/api/inner/search/deleteById/{id}")
    public Result deleteById(@PathVariable(value = "id") Long skuId) ;

    @PostMapping(value = "/api/inner/search/search")
    public Result<SearchResponseVo> search(@RequestBody SearchParamDTO searchParamDTO) ;

    @PutMapping(value = "/api/inner/search/updateHotScore/{skuId}/{hotScore}")
    public Result updateHotScore(@PathVariable(value = "skuId") Long skuId , @PathVariable(value = "hotScore")Long hotScore) ;

}
