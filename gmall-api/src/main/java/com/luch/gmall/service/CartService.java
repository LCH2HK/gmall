package com.luch.gmall.service;

import com.luch.gmall.bean.OmsCartItem;

import java.util.List;

/**
 * @author luch
 * @date 2019/8/23-19:39
 */
public interface CartService {
    OmsCartItem getCartItem(String memberId, String skuId);

    void addCart(OmsCartItem omsCartItem);

    void updateCart(OmsCartItem omsCartItem);

    void updateCache(String memberId);

    List<OmsCartItem> getCartList(String memberId);

    void checkCart(OmsCartItem omsCartItem);
}
