package com.atguigu.gmall.cart.biz.impl;
import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cart.biz.CartBizService;
import com.atguigu.gmall.cart.entity.CartItem;
import com.atguigu.gmall.cart.vo.AddCartSuccessVo;
import com.atguigu.gmall.common.constant.GmallConstant;
import com.atguigu.gmall.common.feign.interceptor.AuthUserInfo;
import com.atguigu.gmall.common.feign.product.SkuFeignClient;
import com.atguigu.gmall.common.feign.util.AuthUserInfoUtils;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.entity.SkuInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class CartBizServiceImpl implements CartBizService {

    @Autowired
    private RedisTemplate<String , String> redisTemplate ;

    @Autowired
    private SkuFeignClient skuFeignClient ;

    @Override
    public AddCartSuccessVo addCart(Long skuId, Integer skuNum) {
        log.info("CartBizServiceImpl...addCart方法执行了...");

        // 构建Redis数据key
        String cartRedisKey = buildRedisKey() ;

        // 把商品添加购物车中
        // 判断当前要添加的商品在购物车是否存在，如果不存在直接创建一个购物项数据对象，然后把购物项添加到购物车中
        Boolean result = redisTemplate.opsForHash().hasKey(cartRedisKey, String.valueOf(skuId));

        // 远程调用product微服务的接口，根据skuId获取skuInfo数据
        Result<SkuInfo> skuInfoResult = skuFeignClient.findSkuInfoBySkuId(skuId);
        SkuInfo skuInfo = skuInfoResult.getData();

        if(result) {        // 如果已经存在，购物项的数量进行加操作

            String cartItemJSON = redisTemplate.opsForHash().get(cartRedisKey, String.valueOf(skuId)).toString();
            CartItem cartItem = JSON.parseObject(cartItemJSON, CartItem.class);
            cartItem.setSkuNum(cartItem.getSkuNum() + skuNum);
            redisTemplate.opsForHash().put(cartRedisKey , String.valueOf(skuId) , JSON.toJSONString(cartItem));

        }else {  // 如果不存在直接创建一个购物项数据对象，然后把购物项添加到购物车中

            CartItem cartItem = new CartItem() ;
            cartItem.setId(skuId);
            cartItem.setSkuId(skuId);
            cartItem.setCartPrice(skuInfo.getPrice());
            cartItem.setSkuPrice(skuInfo.getPrice());
            cartItem.setSkuNum(skuNum);
            cartItem.setImgUrl(skuInfo.getSkuDefaultImg());
            cartItem.setSkuName(skuInfo.getSkuName());
            cartItem.setIsChecked(1);
            cartItem.setCreateTime(new Date());
            cartItem.setUpdateTime(new Date());

            // 存储购物项数据到Redis中
            redisTemplate.opsForHash().put(cartRedisKey , String.valueOf(skuId) , JSON.toJSONString(cartItem));

        }

        // 构建响应结果
        AddCartSuccessVo addCartSuccessVo = new AddCartSuccessVo() ;
        addCartSuccessVo.setSkuInfo(skuInfo);
        addCartSuccessVo.setSkuNum(skuNum);

        // 返回
        return addCartSuccessVo;
    }

    private String buildRedisKey() {

        AuthUserInfo authUserInfo = AuthUserInfoUtils.getAuthUserInfo();
        if(authUserInfo != null) {
            String userId = authUserInfo.getUserId();
            String userTempId = authUserInfo.getUserTempId();
            if(!StringUtils.isEmpty(userId)) {
                return GmallConstant.REDIS_CART_PRE + userId ;
            }else {
                return GmallConstant.REDIS_CART_PRE + userTempId ;
            }
        }else {
            return null ;
        }

    }

}
