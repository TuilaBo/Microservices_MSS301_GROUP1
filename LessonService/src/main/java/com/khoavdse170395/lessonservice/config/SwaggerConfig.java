package com.khoavdse170395.lessonservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI lessonServiceOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8083");
        devServer.setDescription("Server URL in Development environment");

        Contact contact = new Contact();
        contact.setEmail("khoavdse170395@example.com");
        contact.setName("Lesson Service Team");

        Info info = new Info()
                .title("Lesson Service API")
                .version("1.0")
                .contact(contact)
                .description("API cho quản lý giáo án môn Ngữ văn - Vietnamese Literature Lesson Management System");

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}
