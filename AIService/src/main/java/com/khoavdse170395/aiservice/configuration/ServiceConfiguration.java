package com.khoavdse170395.aiservice.configuration;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfiguration {

//    @Bean
//    ApplicationRunner applicationRunner(ChatClient chatClient){
//        return args -> {
//            String response = chatClient.prompt().user("What is the meaning of 'Hello' in Vietnamese").call().content();
//            System.out.println(response);
//        };
//    }


    @Bean("ChatClientWithoutMemory")
    ChatClient chatClientWithoutMemory(ChatClient.Builder chatClientBuilder){
        return chatClientBuilder
                .build();

    }
}
