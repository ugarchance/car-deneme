package org.ugarchance.cartrackingbackend.controller;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.ugarchance.cartrackingbackend.service.AwsIotPublishService;
import org.ugarchance.cartrackingbackend.service.AwsIotSubscribeService;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/iot")
public class AwsIotController {

    private final AwsIotPublishService publishService;
    private final AwsIotSubscribeService subscriberService;

    public AwsIotController(AwsIotPublishService publishService, AwsIotSubscribeService subscriberServiceİ, AwsIotSubscribeService subscriberService) {
        this.publishService = publishService;

        this.subscriberService = subscriberService;
    }

    @PostMapping("/publish")
    public String publishLocation(@RequestParam double latitude, @RequestParam double longitude) {
        try {
            publishService.publish(latitude, longitude);
            return "Publish succesfully!";
        } catch (Exception e) {
            return "Failed to publish!";
        }
    }
    @GetMapping("/subscribe")
    public String subscribeToTopic(@RequestParam String topic) {
        try {
            CompletableFuture<JSONObject> messageFuture = subscriberService.subscribe(topic);
            JSONObject message = messageFuture.get(); // Blocking call, wait for the message
            return message.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to subscribe!";
        }
    }
}
