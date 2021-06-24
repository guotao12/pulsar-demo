package com.guo.de_duplication;

import org.apache.pulsar.client.api.*;


/**
 * @ClassName Consumer_duplication
 * @Description ???
 * @Author 32688
 * @Date 2021年 06月 20日 11:38
 */
public class Consumer_delay {


    public static void main(String[] args) throws Exception {
        //构造Pulsar client
        PulsarClient client = PulsarClient.builder()
                .serviceUrl("pulsar://192.168.137.16:6650,192.168.10.12:6650,192.168.10.28:6650")
                .build();

        //创建consumer
        Consumer consumer = client.newConsumer()
                .topic("persistent://public/default/topic-1")
                .subscriptionName("youxiu")
                .subscriptionType(SubscriptionType.Key_Shared)
                .subscribe();//创建


        //消费消息
        while (true) {
//            该receive方法同步接收消息（消费者进程被阻塞，直到有消息可用）
            Message message = consumer.receive();
            try {
                System.err.println(new String(message.getData()));
                consumer.acknowledge(message);
            } catch (Exception e) {
                e.printStackTrace();
                consumer.negativeAcknowledge(message);
            }
        }
    }
}
