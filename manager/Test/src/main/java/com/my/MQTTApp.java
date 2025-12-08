package com.my;

import org.eclipse.paho.client.mqttv3.*;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class MQTTApp {

    @Test
    public void MqttPublisher() {
        String topic = "test/topic";
        String content = "Hello MQTT";
        int qos = 0;
        String broker = "tcp://192.168.16.129:1883";
        String clientId = "JavaSamplePublisher";

        try {
            // 创建客户端
            MqttClient sampleClient = new MqttClient(broker, clientId);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            // 连接到代理
            System.out.println("Connecting to broker: " + broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");

            // 创建消息并设置QoS
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);

            // 发布消息
            sampleClient.publish(topic, message);
            System.out.println("Message published");

            // 断开连接
            sampleClient.disconnect();
            System.out.println("Disconnected");
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }

    @Test
    public void MqttSubscriber(){
        try {
            MqttSubscriber mqttSubscriber = new MqttSubscriber();

            new CountDownLatch(1).await();
        } catch (MqttException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public class MqttSubscriber implements MqttCallback {

        MqttClient sampleClient;

        public MqttSubscriber() throws MqttException {
            String broker = "tcp://192.168.16.129:1883";
            String clientId = "JavaSampleSubscriber2";
            sampleClient = new MqttClient(broker, clientId);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(false);

            sampleClient.setCallback(this);
            sampleClient.connect(connOpts);
            System.out.println("Connected");

            // 订阅主题
            sampleClient.subscribe("test/topic");
        }

        @Override
        public void connectionLost(Throwable cause) {
            System.out.println("Connection lost because: " + cause.getMessage());
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            System.out.println("Message arrived. Topic: " + topic + " Message: " + new String(message.getPayload()));
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            // 不需要实现，因为我们是Subscriber
            System.out.println(token);
        }
    }
}
