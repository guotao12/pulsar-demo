package com.guo.failover;

/**
 * @ClassName d
 * @Description ???
 * @Author 32688
 * @Date 2021年 06月 11日 18:37
 */
import com.google.common.collect.Lists;
import org.apache.pulsar.client.api.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


public class ProducerFailover {
    //Pulsar集群中broker的serviceurl，多个的话就用，号隔开
    private static final String brokerServiceurl = "pulsar://192.168.10.25:6650";
    //指定topic name
    private static final String topicName = "my-topic";

    public static void main(String[] args) throws PulsarClientException {
        //实例化PulsarClient对象使用的实际对象是PulsarClientImpl
        PulsarClient client = PulsarClient.builder()
                .serviceUrl(brokerServiceurl)
                .build();

        //当有多个broker，你可以想这样启动一个PulsarClient  详细配置看
        // http://pulsar.apache.org/docs/en/client-libraries-javbatchingMaxPublishDelaya/
        /*PulsarClient client = PulsarClient.builder()
                .serviceUrl("pulsar://localhost:6650,localhost:6651,localhost:6652")
                .build();*/

        //创建producer
        Producer<byte[]> producer = client.newProducer()
                .topic(topicName) //根据写入主题，创建一个生产者
                .enableBatching(true)//是否开启批量处理消息，默认true,需要注意的是enableBatching只在异步发送sendAsync生效，同步发送send失效。因此建议生产环境若想使用批处理，则需使用异步发送，或者多线程同步发送
                .compressionType(CompressionType.LZ4)//消息压缩（四种压缩方式：LZ4，ZLIB，ZSTD，SNAPPY），consumer端不用做改动就能消费，开启后大约可以降低3/4带宽消耗和存储（官方测试）
                .batchingMaxPublishDelay(10, TimeUnit.MILLISECONDS) //设置将对发送的消息进行批处理的时间段,10ms；可以理解为若该时间段内批处理成功，则一个batch中的消息数量不会被该参数所影响。
                .sendTimeout(0, TimeUnit.SECONDS)//设置发送超时0s；如果在sendTimeout过期之前服务器没有确认消息，则会发生错误。默认30s，设置为0代表无限制，建议配置为0
                .batchingMaxMessages(1000)//批处理中允许的最大消息数。默认1000
                .maxPendingMessages(1000)//设置等待接受来自broker确认消息的队列的最大大小，默认1000
                .blockIfQueueFull(true)//设置当消息队列中等待的消息已满时，Producer.send 和 Producer.sendAsync 是否应该block阻塞。默认为false，达到maxPendingMessages后send操作会报错，设置为true后，send操作阻塞但是不报错。建议设置为true
                .roundRobinRouterBatchingPartitionSwitchFrequency(10)//向不同partition分发消息的切换频率，默认10ms，可根据batch情况灵活调整
                .batcherBuilder(BatcherBuilder.DEFAULT)//key_Shared模式要用KEY_BASED,才能保证同一个key的message在一个batch里
                .create();

        ProducerFailover pro = new ProducerFailover();
        //异步发送100条消息
        pro.AsyncSend(client, producer);
        //同步发送100条消息
//        pro.SyncSend(client, producer);
    }


    public void AsyncSend(PulsarClient client, Producer producer) throws PulsarClientException {
        /*
         * 异步发送
         * @param client
         * @param producer
         * @return void
         * @exception
         * @author guojiangtao
         * @date 2020/11/6 13:36
         */
        List<CompletableFuture<MessageId>> futures = Lists.newArrayList();

        for (int i = 0; i < 100; i++) {
            final String content = "my-AsyncSend-message-" + i;
            CompletableFuture<MessageId> future = producer.sendAsync(content.getBytes());//异步发送

            future.handle((v, ex) -> {

                System.out.println("v是============="+v+"ex========"+ex);
                if (ex == null) {
                    System.out.println("Message persisted: " + content);
                } else {
                    System.out.println("Error persisting message: " + content + ex);
                }
                return null;
            });

            futures.add(future);
        }

        System.out.println("Waiting for async ops to complete");
        for (CompletableFuture<MessageId> future : futures) {
            future.join();
        }

        System.out.println("All operations completed");

        producer.close();//关闭producer
        client.close();//关闭client
    }


    public void SyncSend(PulsarClient client, Producer producer) throws PulsarClientException {
        /*
         * 同步发送
         * @param client
         * @param producer
         * @return void
         * @exception
         * @author guojiangtao
         * @date 2020/11/6 13:39
         */

        for (int i = 0; i < 100; i++) {
            final String content = "my-SyncSend-message-" + i;
            producer.send((content).getBytes());//同步发送
            System.out.println("Send message: " + content);
        }

        producer.close();//关闭producer
        client.close();//关闭client
    }
}
