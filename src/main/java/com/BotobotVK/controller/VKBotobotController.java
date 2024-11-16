package com.BotobotVK.controller;

import com.BotobotVK.service.VKBotobotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/vk")
public class VKBotobotController {

    @Value("${vk.confirmationCode}")
    private String confirmationCode;

    private final VKBotobotService vkBotobotService;

    @Autowired
    public VKBotobotController(VKBotobotService vkBotService) {
        this.vkBotobotService = vkBotService;
    }

    @PostMapping
    public ResponseEntity<String> handleCallback(@RequestBody Map<String, Object> request) {
        String type = (String) request.get("type");

        if (type == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: missing type");
        }

        if ("confirmation".equals(type)) {
            return ResponseEntity.ok(confirmationCode);
        }

        if ("message_new".equals(type)) {
            try {
                boolean isProcessed = vkBotobotService.handleMessage(request);
                if (!isProcessed) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing message");
                }
            } catch (Exception e) {
                System.err.println("Error handling message: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error while processing message");
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid type");
    }


}
