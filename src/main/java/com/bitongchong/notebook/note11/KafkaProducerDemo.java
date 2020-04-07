package com.bitongchong.notebook.note11;

import lombok.SneakyThrows;
import org.apache.kafka.clients.producer.*;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * @author liuyuehe
 * @date 2020/4/7 20:12
 */
public class KafkaProducerDemo extends Thread {
    final private KafkaProducer<Integer, String> producer;
    final private String topic;
    final private boolean isAsync;

    public KafkaProducerDemo(String topic, boolean isAsync) {
        this.isAsync = isAsync;
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "39.105.83.97:9092");
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaProducerDemo");
        properties.put(ProducerConfig.ACKS_CONFIG, "-1");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG
                , "org.apache.kafka.common.serialization.IntegerSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG
                , "org.apache.kafka.common.serialization.StringSerializer");
        this.producer = new KafkaProducer<>(properties);
        this.topic = topic;
    }

    @SneakyThrows
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            String msg = "this is NO." + i + " message";
            if (isAsync) {
                // 同步
                RecordMetadata recordMetadata = producer.send(new ProducerRecord<>(topic, msg)).get();
                System.out.println("message send, the content is : " + msg);
                System.out.println("the offset : " + recordMetadata.offset());
            } else {
                // 异步
                producer.send(new ProducerRecord<>(topic, msg), (metadata, exception) -> {
                    System.out.println("massage is " + msg + "the offset : " + metadata.offset());
                });
                System.out.println("message send, the content is : " + msg);
            }
            // Thread.sleep(1000);
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        new KafkaProducerDemo("demo", true).start();
        new CountDownLatch(1).await();
    }
}