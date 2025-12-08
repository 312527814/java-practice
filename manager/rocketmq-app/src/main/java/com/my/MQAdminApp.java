package com.my;

import org.apache.rocketmq.client.QueryResult;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.impl.MQAdminImpl;
import org.apache.rocketmq.client.impl.factory.MQClientInstance;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

public class MQAdminApp {
    public static void main(String[] args) throws MQClientException, InterruptedException {
//        MQClientInstance mqClientInstance = new MQClientInstance();
//        MQAdminImpl mqAdmin = new MQAdminImpl();
//        MessageExt messageExt = mqAdmin.queryMessageByUniqKey("topic-1", "key-1");
//        QueryResult queryResult = mqAdmin.queryMessage("topc-1", "key-1", 10, 0, 10000);
//        List<MessageExt> messageList = queryResult.getMessageList();
    }
}
