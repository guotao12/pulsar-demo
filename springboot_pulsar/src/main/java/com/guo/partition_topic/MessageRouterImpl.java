package com.guo.partition_topic;

import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageRouter;
import org.apache.pulsar.client.api.TopicMetadata;

/**
 * @ClassName MessageRouterImpl
 * @Description 自定义的一个路由策略
 * @Author 32688
 * @Date 2021年 06月 19日 16:12
 */
public class MessageRouterImpl implements MessageRouter {



    /**
     * 根据 msg 和主题元数据选择一个分区。
     *
     * @param msg 路由消息
     * @param metadata 主题元数据
     * @return 用于路由消息的分区。
     * @since 1.22.0
     */

    @Override
    public int choosePartition(Message<?> msg, TopicMetadata metadata) {

        return 0;
    }
}
