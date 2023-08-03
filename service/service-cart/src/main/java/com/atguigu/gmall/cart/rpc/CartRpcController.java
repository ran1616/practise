package com.atguigu.gmall.cart.rpc;

import com.atguigu.gmall.cart.biz.CartBizService;
import com.atguigu.gmall.cart.vo.AddCartSuccessVo;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/inner/cart")
@Slf4j
public class CartRpcController {

    @Autowired
    private CartBizService cartBizService ;

    @GetMapping(value = "/addCart")
    public Result<AddCartSuccessVo> addCart(@RequestParam(value = "skuId") Long skuId ,
                                            @RequestParam(value = "skuNum")Integer skuNum ) {
        log.info("skuId: {} , skuNum: {} " , skuId , skuNum);
        AddCartSuccessVo addCartSuccessVo = cartBizService.addCart(skuId , skuNum) ;
        return Result.build(addCartSuccessVo , ResultCodeEnum.SUCCESS) ;
    }

}
