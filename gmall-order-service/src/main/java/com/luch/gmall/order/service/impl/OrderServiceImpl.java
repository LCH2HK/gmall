package com.luch.gmall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.luch.gmall.bean.OmsOrder;
import com.luch.gmall.bean.OmsOrderItem;
import com.luch.gmall.order.mapper.OmsOrderItemMapper;
import com.luch.gmall.order.mapper.OmsOrderMapper;
import com.luch.gmall.service.OrderService;
import com.luch.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author luch
 * @date 2019/8/28-20:39
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    OmsOrderMapper omsOrderMapper;

    @Autowired
    OmsOrderItemMapper omsOrderItemMapper;

    @Override
    public String generateTradeCode(String memberId) {
        Jedis jedis = redisUtil.getJedis();
        String tradeKey="user:"+memberId+":tradeCode";
        String tradeCode= UUID.randomUUID().toString();
        jedis.setex(tradeKey,60*15,tradeCode);
        jedis.close();
        return tradeCode;
    }

    @Override
    public String checkTradeCode(String memberId, String tradeCode) {

        Jedis jedis=null;

        try{
            jedis = redisUtil.getJedis();
            String tradeKey="user:"+memberId+":tradeCode";
            String tradeCode0 = jedis.get(tradeKey);
            if(StringUtils.isNotBlank(tradeCode0)&&tradeCode0.equals(tradeCode)){
                jedis.del(tradeKey);
                return "success";
            }else{
                return "fail";
            }
        }finally {
            jedis.close();
        }

        //String tradeCodeFromCache = jedis.get(tradeKey);// 使用lua脚本在发现key的同时将key删除，防止并发订单攻击
        //对比防重删令牌
//        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
//        Long eval = (Long) jedis.eval(script, Collections.singletonList(tradeKey), Collections.singletonList(tradeCode));
//
//        if (eval!=null&&eval!=0) {
//            jedis.del(tradeKey);
//            return "success";
//        } else {
//            return "fail";
//        }

    }

    @Override
    public void saveOrder(OmsOrder omsOrder) {
        //保存订单表
        omsOrderMapper.insertSelective(omsOrder);
        String orderId = omsOrder.getId();
        //保存订单详情表
        List<OmsOrderItem> omsOrderItems = omsOrder.getOmsOrderItems();
        for (OmsOrderItem omsOrderItem : omsOrderItems) {
            omsOrderItem.setOrderId(orderId);
            omsOrderItemMapper.insertSelective(omsOrderItem);
        }
    }

    @Override
    public OmsOrder getOrderByOutTradeNo(String outTradeNo) {
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setOrderSn(outTradeNo);
        OmsOrder omsOrder1 = omsOrderMapper.selectOne(omsOrder);
        return omsOrder1;
    }

    @Override
    public void updateOrder(OmsOrder omsOrder) {
        Example example = new Example(OmsOrder.class);
        example.createCriteria().andEqualTo("orderSn",omsOrder.getOrderSn());
        OmsOrder omsOrder1 = new OmsOrder();
        omsOrder1.setStatus("1");
        omsOrderMapper.updateByExampleSelective(omsOrder1,example);
    }
}
