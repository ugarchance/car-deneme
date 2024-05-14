package org.ugarchance.cartrackingbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.ugarchance.cartrackingbackend.handler.MqttWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private  final MqttWebSocketHandler mqttWebSocketHandler;

    public WebSocketConfig(MqttWebSocketHandler mqttWebSocketHandler) {
        this.mqttWebSocketHandler = mqttWebSocketHandler;
    }


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(mqttWebSocketHandler, "/ws/mqtt").setAllowedOrigins("*");
    }
}
