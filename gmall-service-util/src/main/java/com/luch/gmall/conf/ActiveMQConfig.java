package com.luch.gmall.conf;

import com.luch.gmall.util.ActiveMQUtil;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import sun.plugin2.os.windows.SECURITY_ATTRIBUTES;

import javax.jms.Session;

/**
 * @author luch
 * @date 2019/8/30-9:51
 */
@Configuration
public class ActiveMQConfig {

    @Value("${spring.activemq.broker-url:disabled}")
    String brokerUrl;

    @Value("${activemq.listener.enable:disabled}")
    String listenerEnable;

    @Bean
    public ActiveMQUtil getActiveMQUtil(){
        if(brokerUrl.equals("disabled")){
            return null;
        }
        ActiveMQUtil activeMQUtil = new ActiveMQUtil();
        activeMQUtil.init(brokerUrl);
        return activeMQUtil;
    }

    //定义一个消息监听器连接工厂，这里定义的是点对点模式的监听器连接工厂
    @Bean(name = "jmsQueueListener")
    public DefaultJmsListenerContainerFactory jmsQueueListenerContainerFactory(ActiveMQConnectionFactory connectionFactory){
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        if(!listenerEnable.equals("true")){
            return null;
        }

        factory.setConnectionFactory(connectionFactory);
        //设置并发数
        factory.setConcurrency("5");

        //设置重连间隔时间
        factory.setRecoveryInterval(5000L);
        factory.setSessionTransacted(false);
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);

        return factory;
    }


    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory(){
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        return activeMQConnectionFactory;
    }
}
