package com.atguigu.gmall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cart.entity.CartItem;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.constant.GmallConstant;
import com.atguigu.gmall.common.feign.interceptor.AuthUserInfo;
import com.atguigu.gmall.common.feign.util.AuthUserInfoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate<String , String> redisTemplate ;

    @Override
    public List<CartItem> findAllCartItem() {
        String redisKey = buildRedisKey();
        List<Object> objectList = redisTemplate.opsForHash().values(redisKey);
        List<CartItem> cartItemList = objectList.stream().map(obj -> {
            String cartItemJSON = obj.toString();
            CartItem cartItem = JSON.parseObject(cartItemJSON, CartItem.class);
            return cartItem;
        }).collect(Collectors.toList());
        return cartItemList;
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
