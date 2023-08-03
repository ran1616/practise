package com.atguigu.gmall.cart.biz;

import com.atguigu.gmall.cart.vo.AddCartSuccessVo;

public interface CartBizService {


    AddCartSuccessVo addCart(Long skuId, Integer skuNum);

}
