package com.my;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ProducerApp {
    @Test
    public void Producer() throws Exception {
        // 创建生产者
        DefaultMQProducer producer = new DefaultMQProducer("aa");
        producer.setNamesrvAddr("192.168.16.129:9876");
        producer.setSendMsgTimeout(1000*60);
        // 启动
        producer.start();
        // 创建TAG
        for (int i = 0; i < 1; i++) {
            // 生成订单ID
            int orderId = i % 10;
            // 创建消息
            Message msg =
                    new Message("my-test2", "tag",
                            ("Hello RocketMQ " + orderId).getBytes());
            SendResult sendResult = producer.send(msg); // 这里传入了订单ID
            System.out.printf("%s%n", sendResult);
        }
        producer.shutdown();
    }

    @Test
    public void ProducerInOrder() throws Exception {
        // 创建生产者
        DefaultMQProducer producer = new DefaultMQProducer("aa");
        producer.setNamesrvAddr("192.168.16.129:9876");
        // 启动
        producer.start();
        // 创建TAG
        for (int i = 0; i < 100; i++) {
            // 生成订单ID
            int orderId = i % 10;
            // 创建消息
            Message msg =
                    new Message("my-test2", "tag",
                            ("Hello RocketMQ " + orderId).getBytes());
            SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    // 获取订单ID
                    Integer id = (Integer) arg;
                    // 对消息队列个数取余
                    int index = id % mqs.size();
                    // 根据取余结果选择消息要发送给哪个消息队列
                    return mqs.get(index);
                }
            }, orderId); // 这里传入了订单ID
            System.out.printf("%s%n", sendResult);
        }
        producer.shutdown();
    }


    @Test
    public void ConsumerInOrder() throws Exception{
        // 创建消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("aa10");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.setNamesrvAddr("192.168.16.129:9876");
        // 订阅主题
        consumer.subscribe("my-test2", "tag || TagC || TagD");
//        consumer.setPullInterval();
//        consumer.setPullBatchSize();
        // 注册消息监听器，使用的是MessageListenerOrderly
        consumer.registerMessageListener(new MessageListenerOrderly() {
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
//                context.setAutoCommit(true);
                for (MessageExt msg : msgs) {
                    // 打印消息
//                    System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), new String(msg.getBody()) );

                    System.out.printf("%s queueId: %s %n", Thread.currentThread().getName(), msg.getQueueId() );

                }
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });
        consumer.start();


        new CountDownLatch(1).await();
        System.out.printf("Consumer Started.%n");
    }
}
