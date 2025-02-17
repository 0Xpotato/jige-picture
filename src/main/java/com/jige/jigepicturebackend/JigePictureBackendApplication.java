package com.jige.jigepicturebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.jige.jigepicturebackend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)     //对象自动代理，对原始对象进行功能增强，例如代理后的对象支持transactional事务
public class JigePictureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(JigePictureBackendApplication.class, args);
    }

}
