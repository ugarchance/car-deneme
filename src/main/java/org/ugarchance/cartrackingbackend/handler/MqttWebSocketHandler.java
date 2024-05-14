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
        System.out.println("WebSocket connection established.");
        session.sendMessage(new TextMessage("Connected to WebSocket"));
        subscribeService.setWebSocketSession(session);  // Set the WebSocket session in the service
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JSONObject jsonMessage = new JSONObject(message.getPayload());
        String topic = jsonMessage.getString("topic");

        System.out.println("Subscribing to topic: " + topic);

        subscribeService.subscribe(topic).thenAccept(confirmationMessage -> {
            try {
                session.sendMessage(new TextMessage(confirmationMessage.toString())); // Başarı mesajını gönder
                System.out.println("Subscription successful.");

                // Mesajları dinlemek için publishEvents kullanın
                subscribeService.getMessageFuture().thenAccept(mqttMessage -> {
                    try {
                        if (session.isOpen()) {
                            session.sendMessage(new TextMessage(mqttMessage.toString()));
                            System.out.println("Received message: " + mqttMessage.toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    session.sendMessage(new TextMessage("Error while sending the confirmation: " + e.getMessage()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
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