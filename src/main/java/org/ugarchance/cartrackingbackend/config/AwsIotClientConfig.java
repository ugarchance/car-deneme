package org.ugarchance.cartrackingbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.crt.mqtt.MqttClient;
import software.amazon.awssdk.crt.mqtt5.*;
import software.amazon.awssdk.crt.mqtt5.packets.ConnectPacket;
import software.amazon.awssdk.iot.AwsIotMqtt5ClientBuilder;

import java.util.concurrent.CompletableFuture;

@Configuration
public class AwsIotClientConfig {
    @Bean
    public Mqtt5Client mqtt5Client(SampleLifeCycleEvents lifeCycleEvents,SamplePublishEvents publishEvents) {
        String endpoint = "an726pjx0w8v9-ats.iot.eu-north-1.amazonaws.com";
        String clientId = "ugar";
        String certPath = "C:\\Users\\Ugar\\Downloads\\DeviceCertificate.crt";
        String privateKeyPath = "C:\\Users\\Ugar\\Downloads\\PrivateKey.key";

        AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithMtlsFromPath(endpoint, certPath, privateKeyPath);
        ConnectPacket.ConnectPacketBuilder connectProperties = new ConnectPacket.ConnectPacketBuilder();
        connectProperties.withClientId(clientId);

        return builder
                .withConnectProperties(connectProperties).
                withLifeCycleEvents(lifeCycleEvents)
                .withPublishEvents(publishEvents)
                .build();

    }
    @Bean
    public SampleLifeCycleEvents sampleLifeCycleEvents(){
        return  new SampleLifeCycleEvents();
    }
    @Bean
    public SamplePublishEvents samplePublishEvents() {
        return new SamplePublishEvents();
    }


}
