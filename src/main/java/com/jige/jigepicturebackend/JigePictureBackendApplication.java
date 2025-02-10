package com.jige.jigepicturebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.jige.jigepicturebackend.mapper")
public class JigePictureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(JigePictureBackendApplication.class, args);
    }

}
