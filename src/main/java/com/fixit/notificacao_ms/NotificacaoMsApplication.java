package com.fixit.notificacao_ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class NotificacaoMsApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificacaoMsApplication.class, args);
    }
}
