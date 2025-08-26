package com.example.hellodemo;

import com.azure.storage.blob.BlobClientBuilder;
import com.example.hellodemo.model.EventGridEvent;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class HelloDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloDemoApplication.class, args);
    }

    @RestController
    public class HelloController {
        @Value("${azure.storage.connection-string}")
        private String connectionString;

        @Value("${azure.storage.container-name}")
        private String containerName;

        @GetMapping("/testapi")
        public String sayHello() {
            return "Hello, World!!!";
        }

        @PostMapping("/eventgrid")
        public ResponseEntity<String> handleEventGridEvent(@RequestBody JsonNode request) {
        try {
            // Event Grid always sends an array
            if (request.isArray()) {
                for (JsonNode event : request) {
                    String eventType = event.get("eventType").asText();

                    // 1. Handle handshake (validation event)
                    if ("Microsoft.EventGrid.SubscriptionValidationEvent".equals(eventType)) {
                        String validationCode = event.get("data").get("validationCode").asText();
                        // Respond with validationResponse
                        return ResponseEntity.ok("{\"validationResponse\":\"" + validationCode + "\"}");
                    }

                    // 2. Handle real events (e.g., BlobCreated)
                    if ("Microsoft.Storage.BlobCreated".equals(eventType)) {
                        String blobUrl = event.get("data").get("url").asText();
                        String id = event.get("id").asText();

                        // Example: create a success file to confirm processing
                        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                        String fileName = "success_" + timestamp + ".txt";
                        String content = "Processed blob: " + blobUrl + " | EventId: " + id;

                        new BlobClientBuilder()
                                .connectionString(connectionString)
                                .containerName(containerName)
                                .blobName(fileName)
                                .buildClient()
                                .upload(new ByteArrayInputStream(content.getBytes()), content.length());
                    }
                }
            }

            return ResponseEntity.ok("Events processed successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error processing event: " + e.getMessage());
        }
    }
}
}