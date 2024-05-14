package org.ugarchance.cartrackingbackend.service;

import com.google.gson.JsonObject;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.crt.mqtt5.Mqtt5Client;
import software.amazon.awssdk.crt.mqtt5.QOS;
import software.amazon.awssdk.crt.mqtt5.packets.PublishPacket;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class AwsIotPublishService {

    private final Mqtt5Client client;


    public AwsIotPublishService(Mqtt5Client client) {
        this.client = client;
        client.start();
    }

    public void publish(double latitude,double longitude) throws  Exception{
        String topic ="esp32/data";
        String timestamp = Instant.now().toString();

        JSONObject json = new JSONObject();

        json.put("timestamp",timestamp);
        json.put("latitude",latitude);
        json.put("longitude",longitude);

        byte[] payload = json.toString().getBytes();

        PublishPacket.PublishPacketBuilder publishBuilder = new PublishPacket.PublishPacketBuilder();
        publishBuilder.withTopic(topic).withQOS(QOS.AT_LEAST_ONCE);
        publishBuilder.withPayload(payload);

        client.publish(publishBuilder.build()).get(60, TimeUnit.SECONDS);

    }
}
