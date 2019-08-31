package com.luch.gmall.user.service.Impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.luch.gmall.bean.UmsMember;
import com.luch.gmall.service.UserService;
import com.luch.gmall.user.mapper.UserMapper;
import com.luch.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;


import java.util.List;

/**
 * @author luch
 * @date 2019/8/14-21:16
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public List<UmsMember> getAllUser() {
        return userMapper.selectAll();
    }

    @Override
    public UmsMember checkUser(UmsMember umsMember) {
        UmsMember umsMember1 = userMapper.selectOne(umsMember);
        return umsMember1;
    }

    @Override
    public void addUserTokenToCache(String token, String memberId) {
        Jedis jedis = redisUtil.getJedis();
        jedis.setex("user:"+memberId+":token",60*60*2, token);
        jedis.close();
    }
}
