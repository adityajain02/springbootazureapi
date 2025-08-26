package com.example.hellodemo;

import com.azure.storage.blob.BlobClientBuilder;
import com.example.hellodemo.model.EventGridEvent;
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
        public ResponseEntity<String> handleEventGridEvent(@RequestBody EventGridEvent[] events) {
            for (EventGridEvent event : events) {
                // Create success file
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                String fileName = "success_" + timestamp + ".txt";
                String content = "Event processed: " + event.getId();

                // Upload to blob storage
                new BlobClientBuilder()
                    .connectionString(connectionString)
                    .containerName(containerName)
                    .blobName(fileName)
                    .buildClient()
                    .upload(new ByteArrayInputStream(content.getBytes()), content.length());
            }

            return ResponseEntity.ok("Events processed successfully");
        }
    }
}