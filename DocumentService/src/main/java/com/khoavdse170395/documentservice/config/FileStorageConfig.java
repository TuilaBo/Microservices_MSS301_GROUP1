package com.khoavdse170395.documentservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@Slf4j
public class FileStorageConfig {

    @Value("${app.upload.dir:uploads/documents/}")
    private String uploadDir;

    @Bean
    public ApplicationRunner createUploadDirectories() {
        return args -> {
            try {
                // Tạo thư mục uploads
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                    log.info("✅ Created upload directory: {}", uploadPath.toAbsolutePath());
                } else {
                    log.info("📁 Upload directory already exists: {}", uploadPath.toAbsolutePath());
                }

                // Tạo thư mục thumbnails
                Path thumbnailPath = Paths.get("uploads/thumbnails/");
                if (!Files.exists(thumbnailPath)) {
                    Files.createDirectories(thumbnailPath);
                    log.info("✅ Created thumbnail directory: {}", thumbnailPath.toAbsolutePath());
                } else {
                    log.info("📁 Thumbnail directory already exists: {}", thumbnailPath.toAbsolutePath());
                }

                // Tạo thư mục static resources
                Path staticPath = Paths.get("src/main/resources/static/thumbnails/");
                if (!Files.exists(staticPath)) {
                    Files.createDirectories(staticPath);
                    log.info("✅ Created static thumbnail directory: {}", staticPath.toAbsolutePath());
                } else {
                    log.info("📁 Static thumbnail directory already exists: {}", staticPath.toAbsolutePath());
                }

            } catch (IOException e) {
                log.error("❌ Failed to create directories: {}", e.getMessage());
            }
        };
    }
}
