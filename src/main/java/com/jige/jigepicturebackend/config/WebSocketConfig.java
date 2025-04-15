package com.jige.jigepicturebackend.config;

import com.jige.jigepicturebackend.aop.WsHandshakeInterceptor;
import com.jige.jigepicturebackend.manager.websocket.PictureEditHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.annotation.Resource;

/**
 * 类似于编写 Spring MVC 的 Controller 接口，可以为指定的路径配置处理器和拦截器：
 * 前端可以通过 WebSocket 连接项目启动端口的 /ws/picture/edit 路径了。
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Resource
    private PictureEditHandler pictureEditHandler;

    @Resource
    private WsHandshakeInterceptor wsHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        //  websocket
        registry.addHandler(pictureEditHandler, "/ws/picture/edit")
                .addInterceptors(wsHandshakeInterceptor)
                .setAllowedOrigins("*");
    }
}
