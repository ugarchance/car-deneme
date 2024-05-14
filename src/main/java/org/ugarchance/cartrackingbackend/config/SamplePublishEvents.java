package org.ugarchance.cartrackingbackend.config;


import org.json.JSONObject;
import software.amazon.awssdk.crt.mqtt5.*;
import software.amazon.awssdk.crt.mqtt5.packets.PublishPacket;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;


public class SamplePublishEvents implements Mqtt5ClientOptions.PublishEvents {
    private CompletableFuture<JSONObject> messageFuture;

    public SamplePublishEvents() {
        this.messageFuture = new CompletableFuture<>();
    }

    @Override
    public void onMessageReceived(Mqtt5Client client, PublishReturn publishReturn) {
        PublishPacket publishPacket = publishReturn.getPublishPacket();
        String topic = publishPacket.getTopic();
        String payload = new String(publishPacket.getPayload(), StandardCharsets.UTF_8);

        System.out.println("Received message on topic: " + topic);
        System.out.println("Message payload: " + payload);

        JSONObject json = new JSONObject();
        json.put("topic", topic);
        json.put("message", payload);

        messageFuture.complete(json);
        messageFuture = new CompletableFuture<>();  // Reset the future for the next message
    }

    public CompletableFuture<JSONObject> getMessageFuture() {
        return messageFuture;
    }
}