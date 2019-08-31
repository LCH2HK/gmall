package com.luch.gmall.conf;

import com.luch.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author luch
 * @date 2019/8/20-15:32
 */
@Configuration
public class RedisConfig {
    //读取配置文件中的redis的ip地址
    @Value("${spring.redis.host:disabled}")
    private String host = "192.168.0.104";
    @Value("${spring.redis.port:0}")
    private int port = 6379;
    @Value("${spring.redis.database:0}")
    private int database=0;
    @Bean
    public RedisUtil getRedisUtil(){
        if(host.equals("disabled")){
            return null;
        }
        RedisUtil redisUtil=new RedisUtil();
        redisUtil.initPool(host,port,database);
        return redisUtil;
    }
}
