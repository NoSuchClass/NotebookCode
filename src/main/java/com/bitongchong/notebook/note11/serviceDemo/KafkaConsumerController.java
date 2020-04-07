package com.bitongchong.notebook.note11.serviceDemo;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuyuehe
 * @date 2020/4/8 0:17
 */
@RestController
public class KafkaConsumerController {
    @KafkaListener(topics = "bitKafka")
    public void listenMsg(String msg) {
        System.out.println("received msg is :" + msg);
    }
}
