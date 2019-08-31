package queue;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * @author luch
 * @date 2019/8/31-16:00
 */
public class JmsConsumer_queue {
    public static final String ACTIVEMQ_URL="tcp://192.168.0.104:61616";
    public static final String QUEUE_NAME="queue01";

    public static void main(String[] args) throws Exception {

        System.out.println("我是2号队列消费者");

        //1、创建连接工厂，按照给定的url，采用默认用户名和密码
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);

        //2、通过连接工厂，获得连接对象connection并启动访问
        Connection connection = connectionFactory.createConnection();
        connection.start();

        //3、通过连接对象，获得会话对象session
        //构造器中两个参数。第一个叫事务，第二个叫签收
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        //4、通过会话对象，创建目的地对象destination（包括队列queue和主题topic两种）
        Queue queue = session.createQueue(QUEUE_NAME);

        //5、创建消费者
        MessageConsumer messageConsumer = session.createConsumer(queue);

        //以同步阻塞方式等待消息
//        while (true){
//            TextMessage textMessage = (TextMessage) messageConsumer.receive();
//            if(textMessage!=null){
//                System.out.println("****************消费者接收到消息："+textMessage.getText()+"****************");
//            }else{
//                break;
//            }
//        }

        //通过监听的方式来消费消息
        messageConsumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                if(message!=null&&message instanceof TextMessage){
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        System.out.println("****************消费者接收到消息："+textMessage.getText()+"****************");
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //使程序一直监听消息
        System.in.read();

        //6、关闭资源
        messageConsumer.close();
        session.close();
        connection.close();

        System.out.println("****************消息发布到MQ完成****************");


    }
}
