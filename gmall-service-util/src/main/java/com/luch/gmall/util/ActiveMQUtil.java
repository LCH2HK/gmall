package com.luch.gmall.util;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;

import javax.jms.ConnectionFactory;

/**
 * @author luch
 * @date 2019/8/30-9:42
 */
public class ActiveMQUtil {

    PooledConnectionFactory pooledConnectionFactory;

    public ConnectionFactory init(String brokerUrl){
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        //加入连接池
        pooledConnectionFactory = new PooledConnectionFactory(factory);
        //出现异常时重新连接
        pooledConnectionFactory.setReconnectOnException(true);

        pooledConnectionFactory.setMaxConnections(5);
        pooledConnectionFactory.setExpiryTimeout(10000);
        return pooledConnectionFactory;
    }

    public ConnectionFactory getConnectionFactory(){
        return pooledConnectionFactory;
    }


}
