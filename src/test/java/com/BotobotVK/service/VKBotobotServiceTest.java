package com.BotobotVK.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VKBotobotServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private VKBotobotService vkBotobotService;

    @Test
    void testHandleMessage_NoMessage() {
        Map<String, Object> request = new HashMap<>();

        request.put("object", new HashMap<>());

        boolean result = vkBotobotService.handleMessage(request);

        assertFalse(result);
    }

    @Test
    void testHandleMessage_Success() {
        Map<String, Object> request = new HashMap<>();
        Map<String, Object> message = new HashMap<>();
        message.put("peer_id", 123L);
        message.put("text", "Hello world");
        Map<String, Object> object = new HashMap<>();
        object.put("message", message);
        request.put("object", object);

        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>("ok", HttpStatus.OK));

        boolean result = vkBotobotService.handleMessage(request);

        assertTrue(result);
    }

    @Test
    void testSendMessage_Failure() {
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenThrow(new RuntimeException("Network error"));

        boolean result = vkBotobotService.sendMessage(123L, "Hello world");

        assertFalse(result);
    }

    @Test
    void testSendMessage_Success() {
        String expectedResponse = "ok";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenReturn(responseEntity);

        boolean result = vkBotobotService.sendMessage(123L, "Hello world");

        assertTrue(result);
    }

}