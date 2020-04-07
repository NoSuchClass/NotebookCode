package com.bitongchong.notebook.note11.serviceDemo;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuyuehe
 * @date 2020/4/8 0:12
 */
@RestController
public class KafkaProductController {
    final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProductController(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @RequestMapping("/send")
    public String sendMsg(@RequestParam("msg") String msg) {
        kafkaTemplate.send("bitKafka", msg);
        return "msg: " + msg +" send success";
    }
}
