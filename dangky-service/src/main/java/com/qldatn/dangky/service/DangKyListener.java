package com.qldatn.dangky.service;

import com.qldatn.dangky.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class DangKyListener {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void listenDangKyEvent(String message) {
        System.out.println("============================================");
        System.out.println("[RABBITMQ ASYNC EVENT] Đã nhận tin nhắn xử lý ngầm: " + message);
        System.out.println("============================================");
    }
}