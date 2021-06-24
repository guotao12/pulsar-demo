package com.guo.key_Shared;

import org.apache.pulsar.client.api.*;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName Client
 * @Description ???
 * @Author 32688
 * @Date 2021年 06月 11日 18:10
 */
public class ConsumerKey_Shared {

    //Pulsar集群中broker的serviceurl
    private static final String brokerServiceurl = "pulsar://192.168.1.170:6650";
    //需要订阅的topic name
    private static final String topicName = "my-topic";
    //订阅名
    private static final String subscriptionName = "my-sub";

    public static void main(String[] args) throws PulsarClientException {
        //构造Pulsar client
        PulsarClient client = PulsarClient.builder()
                .serviceUrl(brokerServiceurl)
                .build();

        //创建consumer
        Consumer consumer = client.newConsumer()
                .topic(topicName)
                .subscriptionName(subscriptionName)
                .subscriptionType(SubscriptionType.Key_Shared)//指定消费模式，包含：Exclusive，Failover，Shared，Key_Shared。默认Exclusive模式
                .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)//指定从哪里开始消费还有Latest，valueof可选，默认Latest
                .negativeAckRedeliveryDelay(60, TimeUnit.SECONDS)//指定消费失败后延迟多久broker重新发送消息给consumer，默认60s
                .subscribe();//创建
        //消费消息
        while (true) {

//            该receive方法同步接收消息（消费者进程被阻塞，直到有消息可用）
            Message message = consumer.receive();

            try {
                System.out.printf("消费的消息是：", new String(message.getData()) );
                consumer.acknowledge(message);
            } catch (Exception e) {
                e.printStackTrace();
                consumer.negativeAcknowledge(message);
            }
        }
    }

}

