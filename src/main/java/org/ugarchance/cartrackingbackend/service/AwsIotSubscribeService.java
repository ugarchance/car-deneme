package org.ugarchance.cartrackingbackend.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.ugarchance.cartrackingbackend.config.SamplePublishEvents;
import software.amazon.awssdk.crt.mqtt5.Mqtt5Client;
import software.amazon.awssdk.crt.mqtt5.PublishReturn;
import software.amazon.awssdk.crt.mqtt5.QOS;
import software.amazon.awssdk.crt.mqtt5.packets.SubscribePacket.Subscription;
import software.amazon.awssdk.crt.mqtt5.packets.SubscribePacket;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static software.amazon.awssdk.crt.mqtt5.QOS.AT_LEAST_ONCE;

@Service
public class AwsIotSubscribeService {

    private final Mqtt5Client client;
    private final SamplePublishEvents publishEvents;

    private CompletableFuture<JSONObject> messageFuture;

    @Autowired
    public AwsIotSubscribeService(Mqtt5Client client, SamplePublishEvents publishEvents) {
        this.client = client;
        this.publishEvents = publishEvents;
        client.start();
    }


    public CompletableFuture<JSONObject> subscribe(String topic) throws Exception {
        CompletableFuture<JSONObject> subscriptionFuture = new CompletableFuture<>();

        SubscribePacket.SubscribePacketBuilder subscribePacketBuilder = new SubscribePacket.SubscribePacketBuilder();
        SubscribePacket subscribePacket = subscribePacketBuilder.withSubscription(
                topic, QOS.AT_LEAST_ONCE, false, false, SubscribePacket.RetainHandlingType.DONT_SEND
        ).build();

        client.subscribe(subscribePacket).get(60, TimeUnit.SECONDS);

        JSONObject confirmationMessage = new JSONObject();
        confirmationMessage.put("message", "Subscribed to topic: " + topic);
        subscriptionFuture.complete(confirmationMessage);

        return subscriptionFuture.thenCombine(publishEvents.getMessageFuture(), (confirmation, message) -> {
            JSONObject combined = new JSONObject();
            combined.put("confirmation", confirmation);
            combined.put("mqttMessage", message);
            return combined;
        });
    }

}
