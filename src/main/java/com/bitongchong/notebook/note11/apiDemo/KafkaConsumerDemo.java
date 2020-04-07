package com.bitongchong.notebook.note11.apiDemo;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * @author liuyuehe
 * @date 2020/4/7 20:11
 */
public class KafkaConsumerDemo extends Thread {

    final private KafkaConsumer<Integer, String> consumer;

    public KafkaConsumerDemo(String topic) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "39.105.83.97:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaConsumerDemo");
        // 消费者消费消息以后自动提交，只有当消息提交以后，该消息才不会被再次接收到
        // 还可以配合auto.commit.interval.ms控制自动提交的频率
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG
                , "org.apache.kafka.common.serialization.IntegerDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG
                , "org.apache.kafka.common.serialization.StringDeserializer");
        // 此处是新的消费者会从该 topic 最早的消息开始消费，而latest是指新的消费者将会从其他消费者最后消费的offset
        // 处开始消费 Topic 下的消息
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singletonList(topic));
    }


    @Override
    public void run() {
        while(true) {
            ConsumerRecords<Integer, String> consumerRecords = consumer.poll(Duration.ofSeconds(1));
            for (ConsumerRecord<Integer, String> consumerRecord : consumerRecords) {
                System.out.println("the message received: " + consumerRecord.value());
            }
        }
    }
    
    public static void main(String[] args){
        new KafkaConsumerDemo("demo").start();
    }
}
