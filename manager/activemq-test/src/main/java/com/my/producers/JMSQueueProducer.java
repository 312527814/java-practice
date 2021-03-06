package com.my.producers;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 腾讯课堂搜索 咕泡学院
 * 加群获取视频：608583947
 * 风骚的Michael 老师
 */
public class JMSQueueProducer {

    public static void main(String[] args) {
        ConnectionFactory connectionFactory =
                new ActiveMQConnectionFactory
                        ("tcp://192.168.77.130:61616");
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession
                    (Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);

            //创建目的地
            Destination destination = session.createQueue("myQueue");
            //创建发送者
            MessageProducer producer = session.createProducer(destination);
            producer.setPriority(9);
            producer.setTimeToLive(10*1000);
            for (int i = 0; i < 5; i++) {
                //创建需要发送的消息
                TextMessage message = session.createTextMessage("Hello World:" + i);
//                message.setJMSPriority(10);
                message.setJMSRedelivered(true);
//                message.setJMSExpiration(1000 * 10);
                //Text   Map  Bytes  Stream  Object
                producer.send(message);
                System.out.println(message.getText());
            }

//            session.commit();
            session.close();
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
