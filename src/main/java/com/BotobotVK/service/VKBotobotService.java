package com.BotobotVK.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class VKBotobotService {

    @Value("${vk.accessToken}")
    private String accessToken;

    @Value("${vk.api.version}")
    private String vkApiVersion;

    private final RestTemplate restTemplate;

    @Autowired
    public VKBotobotService (RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean handleMessage(Map<String, Object> request) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.valueToTree(request);
        JsonNode messageNode = rootNode.path("object").path("message");

        if (messageNode != null) {
            Long peerId = messageNode.path("peer_id").asLong();
            String message = messageNode.path("text").asText();
            return sendMessage(peerId, "Вы сказали: " + message);
        }
        return false;
    }

    public boolean sendMessage(Long peerId, String message) {
        String url = "https://api.vk.com/method/messages.send";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("access_token", accessToken);
        params.add("v", vkApiVersion);
        params.add("peer_id", peerId.toString());
        params.add("message", message);
        params.add("random_id", String.valueOf(System.currentTimeMillis()));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
        }

        return false;
    }

}
