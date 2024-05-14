package org.ugarchance.cartrackingbackend.handler;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.ugarchance.cartrackingbackend.service.AwsIotSubscribeService;

import java.util.concurrent.CompletableFuture;

@Component
public class MqttWebSocketHandler extends TextWebSocketHandler {

    private final AwsIotSubscribeService subscribeService;

    public MqttWebSocketHandler(AwsIotSubscribeService subscribeService) {
        this.subscribeService = subscribeService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Send initial message to client
        session.sendMessage(new TextMessage("Connected to WebSocket"));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Parse incoming message as JSON
        JSONObject jsonMessage = new JSONObject(message.getPayload());
        String topic = jsonMessage.getString("topic");

        // Subscribe to the topic and listen for MQTT messages
        subscribeService.subscribe(topic).thenAccept(combinedMessage -> {
            try {
                // Send confirmation message
                JSONObject confirmationMessage = combinedMessage.getJSONObject("confirmation");
                session.sendMessage(new TextMessage(confirmationMessage.toString()));

                // Listen for MQTT messages
                JSONObject mqttMessage = combinedMessage.getJSONObject("mqttMessage");
                session.sendMessage(new TextMessage(mqttMessage.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).exceptionally(throwable -> {
            try {
                session.sendMessage(new TextMessage("Error subscribing to topic: " + throwable.getMessage()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}