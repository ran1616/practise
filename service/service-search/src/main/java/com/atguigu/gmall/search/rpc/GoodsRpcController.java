package com.atguigu.gmall.search.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.search.biz.GoodsBizService;
import com.atguigu.gmall.search.dto.SearchParamDTO;
import com.atguigu.gmall.search.entity.Goods;
import com.atguigu.gmall.search.vo.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/inner/search")
public class GoodsRpcController {

    @Autowired
    private GoodsBizService goodsBizService ;

    @PostMapping(value = "/saveGoods")
    public Result saveGoods(@RequestBody Goods goods) {
        goodsBizService.saveGoods(goods) ;
        return Result.ok() ;
    }

    @DeleteMapping(value = "/deleteById/{id}")
    public Result deleteById(@PathVariable(value = "id") Long skuId) {
        goodsBizService.deleteById(skuId) ;
        return Result.ok() ;
    }

    @PostMapping(value = "/search")
    public Result<SearchResponseVo> search(@RequestBody SearchParamDTO searchParamDTO) {
        SearchResponseVo searchResponseVo = goodsBizService.search(searchParamDTO) ;
        return Result.build(searchResponseVo , ResultCodeEnum.SUCCESS)  ;
    }

    @PutMapping(value = "/updateHotScore/{skuId}/{hotScore}")
    public Result updateHotScore(@PathVariable(value = "skuId") Long skuId , @PathVariable(value = "hotScore")Long hotScore) {
        goodsBizService.updateHotScore(skuId , hotScore) ;
        return Result.ok() ;
    }

}
