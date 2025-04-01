package com.southwind;

import java.io.File;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan("com.southwind.mapper")
public class ParkApplication {

    public static void main(String[] args) {
        // 指定配置文件路径
        SpringApplication.run(ParkApplication.class, 
            "--spring.config.location=G:\\毕业设计\\park-lot\\park_demo\\src\\main\\resources\\application.yml");
    }

    @Bean
    CommandLineRunner init() {
        return args -> {
            // 创建上传目录
            createDirectoryIfNotExists("D:/park/upload/number");
            createDirectoryIfNotExists("D:/park/upload/excel");
        };
    }

    private void createDirectoryIfNotExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                System.out.println("目录创建成功: " + directoryPath);
            } else {
                System.out.println("目录创建失败: " + directoryPath);
            }
        }
    }
}
