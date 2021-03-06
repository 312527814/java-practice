package com.my.consumer;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 腾讯课堂搜索 咕泡学院
 * 加群获取视频：608583947
 * 风骚的Michael 老师
 */
public class JMSQueueConsumer2 {

    public static void main(String[] args) {
        ConnectionFactory connectionFactory =
                new ActiveMQConnectionFactory
                        ("tcp://192.168.77.130:61616");
        Connection connection = null;
        try {

            connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession
                    (Boolean.FALSE, Session.CLIENT_ACKNOWLEDGE); //延迟确认

            //创建目的地
            Destination destination = session.createQueue("myQueue");

            //创建发送者
            MessageConsumer consumer = session.createConsumer(destination);
            for (int i = 0; i < 5; i++) {
                TextMessage textMessage = (TextMessage) consumer.receive();
                System.out.println(textMessage.getText());
                String string = textMessage.toString();

//                if(i==8) {
//                textMessage.acknowledge();
//                }
            }
            session.recover();
//            session.commit();//表示消息被自动确认

//            session.close();
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
