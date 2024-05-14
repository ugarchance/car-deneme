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
    public Mqtt5Client mqtt5Client() {
        String endpoint = "an726pjx0w8v9-ats.iot.eu-north-1.amazonaws.com";
        String clientId = "esp322";
        String certPath = "C:\\Users\\Ugar\\Downloads\\DeviceCertificate.crt";
        String privateKeyPath = "C:\\Users\\Ugar\\Downloads\\PrivateKey.key";

        AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithMtlsFromPath(endpoint, certPath, privateKeyPath);
        ConnectPacket.ConnectPacketBuilder connectProperties = new ConnectPacket.ConnectPacketBuilder();
        connectProperties.withClientId(clientId);

        SampleLifeCycleEvents lifeCycleEvents = new SampleLifeCycleEvents();
        return builder.withConnectProperties(connectProperties).
                withLifeCycleEvents(lifeCycleEvents)
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
