package com.luch.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.luch.gmall.bean.OmsCartItem;
import com.luch.gmall.cart.mapper.OmsCartItemMapper;
import com.luch.gmall.service.CartService;
import com.luch.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author luch
 * @date 2019/8/23-19:40
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    OmsCartItemMapper omsCartItemMapper;

    @Autowired
    RedisUtil redisUtil;



    @Override
    public OmsCartItem getCartItem(String memberId, String skuId) {

        OmsCartItem cartItem = new OmsCartItem();
        cartItem.setProductSkuId(skuId);
        cartItem.setMemberId(memberId);

        OmsCartItem omsCartItem = omsCartItemMapper.selectOne(cartItem);

        return omsCartItem;
    }

    @Override
    public void addCart(OmsCartItem omsCartItem) {

        omsCartItemMapper.insertSelective(omsCartItem);
    }

    @Override
    public void updateCart(OmsCartItem omsCartItem) {
        Example e=new Example(OmsCartItem.class);
        e.createCriteria().andEqualTo("id",omsCartItem.getId());

        omsCartItemMapper.updateByExampleSelective(omsCartItem,e);
    }

    @Override
    public void updateCache(String memberId) {
        //缓存为hash结构
        //mapKey为"user:" + userId + ":cart"，
        //mapValue为键为skuId，值为cartItem的Map
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        List<OmsCartItem> omsCartItems = omsCartItemMapper.select(omsCartItem);
        Map<String,String> cartInfo=new HashMap<>();
        for (OmsCartItem cartItem : omsCartItems) {
            String skuId=cartItem.getProductSkuId();
            cartItem.setTotalPrice(cartItem.getPrice().multiply(cartItem.getQuantity()));
            String cart = JSON.toJSONString(cartItem);
            cartInfo.put(skuId,cart);
        }

        Jedis jedis = redisUtil.getJedis();

        String key="user:"+memberId+":cart";

        jedis.del(key);
        jedis.hmset(key,cartInfo);

        jedis.close();
    }

    @Override
    public List<OmsCartItem> getCartList(String memberId) {

        String key="user:"+memberId+":cart";

        Jedis jedis = redisUtil.getJedis();

        List<String> items = jedis.hvals(key);

        List<OmsCartItem> omsCartItemList=new ArrayList<>();

        for (String item : items) {
            OmsCartItem omsCartItem = JSON.parseObject(item, OmsCartItem.class);
            omsCartItemList.add(omsCartItem);
        }

        return omsCartItemList;
    }

    @Override
    public void checkCart(OmsCartItem omsCartItem) {
        Example example=new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("memberId",omsCartItem.getMemberId()).andEqualTo("productSkuId",omsCartItem.getProductSkuId());
        omsCartItemMapper.updateByExampleSelective(omsCartItem,example);

        updateCache(omsCartItem.getMemberId());
    }
}
