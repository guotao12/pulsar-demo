package com.guo.de_duplication;

import org.apache.pulsar.client.api.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName Producer
 * @Description ???
 * @Author 32688
 * @Date 2021年 06月 20日 11:19
 */
public class Producer_delay {

    public static void main(String[] args) throws PulsarClientException {
        PulsarClient pulsarClient = PulsarClient.builder()
                .serviceUrl("pulsar://192.168.137.16:6650,192.168.10.12:6650,192.168.10.28:6650")
                .build();

        Producer producer = pulsarClient.newProducer()
                .producerName("producer-1")
                .topic("persistent://public/default/topic-1")
                .create();


        for (int i = 0; i < 99; i++) {
            producer.newMessage().value("Hello Pulsar!".getBytes()).deliverAfter(15L, TimeUnit.SECONDS).sendAsync();

        }
        System.err.println("发送完毕");
        producer.close();
        pulsarClient.close();
    }
}
