package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.cart.entity.CartItem;

import java.util.List;

public interface CartService {

    List<CartItem> findAllCartItem();
}
