package org.ugarchance.cartrackingbackend.service;

import lombok.Setter;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.ugarchance.cartrackingbackend.config.SamplePublishEvents;
import software.amazon.awssdk.crt.mqtt5.Mqtt5Client;
import software.amazon.awssdk.crt.mqtt5.PublishReturn;
import software.amazon.awssdk.crt.mqtt5.QOS;
import software.amazon.awssdk.crt.mqtt5.packets.PublishPacket;
import software.amazon.awssdk.crt.mqtt5.packets.SubscribePacket.Subscription;
import software.amazon.awssdk.crt.mqtt5.packets.SubscribePacket;


import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static software.amazon.awssdk.crt.mqtt5.QOS.AT_LEAST_ONCE;

@Service
public class AwsIotSubscribeService {

    private final Mqtt5Client client;
    private final SamplePublishEvents publishEvents;
    private WebSocketSession webSocketSession;

    @Autowired
    public AwsIotSubscribeService(Mqtt5Client client, SamplePublishEvents publishEvents) {
        this.client = client;
        this.publishEvents = publishEvents;
        client.start();
    }

    public void setWebSocketSession(WebSocketSession session) {
        this.webSocketSession = session;
    }

    public CompletableFuture<JSONObject> subscribe(String topic) throws Exception {
        CompletableFuture<JSONObject> subscriptionFuture = new CompletableFuture<>();

        SubscribePacket subscribePacket = new SubscribePacket.SubscribePacketBuilder()
                .withSubscription(topic, AT_LEAST_ONCE, false, false, SubscribePacket.RetainHandlingType.DONT_SEND)
                .build();
        System.out.println("Attempting to subscribe to topic: " + topic);

        client.subscribe(subscribePacket).get(60, TimeUnit.SECONDS);

        System.out.println("Successfully subscribed to topic: " + topic);

        JSONObject confirmationMessage = new JSONObject();
        confirmationMessage.put("message", "Subscribed successfully to topic: " + topic);
        subscriptionFuture.complete(confirmationMessage);

        publishEvents.getMessageFuture().thenAccept(mqttMessage -> {
            try {
                if (webSocketSession != null && webSocketSession.isOpen()) {
                    webSocketSession.sendMessage(new TextMessage(mqttMessage.toString()));
                    System.out.println("Received message: " + mqttMessage.toString());

                    // Send a confirmation message that the message was received
                    JSONObject receivedConfirmation = new JSONObject();
                    receivedConfirmation.put("message", "Message received from MQTT broker");
                    webSocketSession.sendMessage(new TextMessage(receivedConfirmation.toString()));
                    System.out.println("Confirmation message sent to WebSocket client.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return subscriptionFuture;
    }

    public CompletableFuture<JSONObject> getMessageFuture() {
        return publishEvents.getMessageFuture();
    }
}