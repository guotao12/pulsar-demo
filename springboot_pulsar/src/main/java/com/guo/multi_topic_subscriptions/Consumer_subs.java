package com.guo.multi_topic_subscriptions;

import org.apache.pulsar.client.api.*;

import java.util.regex.Pattern;

/**
 * @ClassName Consumer_subs
 * @Description ???
 * @Author 32688
 * @Date 2021年 06月 19日 14:50
 */
public class Consumer_subs {
    //Pulsar集群中broker的serviceurl
    private static final String brokerServiceurl = "pulsar://192.168.10.25:6650";
    //需要订阅的topic name
    private static final String topicName = "my-topic";

    private static final String subscriptionName = "my-sub";
    public static void main(String[] args) throws PulsarClientException {

        //构造Pulsar client
        PulsarClient client = PulsarClient.builder()
                .serviceUrl(brokerServiceurl)
                .build();


        ConsumerBuilder consumerBuilder = client.newConsumer()
                .subscriptionName(subscriptionName)
                .subscriptionTopicsMode(RegexSubscriptionMode.AllTopics);

// Subscribe to all topics in a namespace
        Pattern allTopicsInNamespace = Pattern.compile("public/default/.*");
        Consumer allTopicsConsumer = consumerBuilder
                .topicsPattern(allTopicsInNamespace)
                .subscribe();

// Subscribe to a subsets of topics in a namespace, based on regex
//        Pattern someTopicsInNamespace = Pattern.compile("public/default/foo.*");
//        Consumer allTopicsConsumer = consumerBuilder
//                .topicsPattern(someTopicsInNamespace)
//                .subscribe();

    }


}
