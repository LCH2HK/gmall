package topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * @author luch
 * @date 2019/8/31-16:28
 */
public class JmsProducer_topic {
    public static final String ACTIVEMQ_URL="tcp://192.168.0.104:61616";
    public static final String TOPIC_NAME="topic01";

    public static void main(String[] args) throws JMSException {
        //1、创建连接工厂，按照给定的url，采用默认用户名和密码
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);

        //2、通过连接工厂，获得连接对象connection并启动访问
        Connection connection = connectionFactory.createConnection();
        connection.start();

        //3、通过连接对象，获得会话对象session
        //构造器中两个参数。第一个叫事务，第二个叫签收
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        //4、通过会话对象，创建目的地对象destination（包括队列queue和主题topic两种）
        Topic topic = session.createTopic(TOPIC_NAME);

        //5、创建消息的生产者
        MessageProducer messageProducer = session.createProducer(topic);

        //6、通过使用messageProducer生产4条消息发送到MQ的队列里面
        for (int i = 1; i <= 4; i++) {
            //7、创建消息
            TextMessage textMessage = session.createTextMessage("****************topic message: " + i+"****************");

            //8、发送消息到mq
            messageProducer.send(textMessage);
        }

        //9、关闭资源
        messageProducer.close();
        session.close();
        connection.close();

        System.out.println("****************主题消息发布到MQ完成****************");


    }
}
