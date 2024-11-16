package com.BotobotVK.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class VKBotobotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${vk.confirmationCode}")
    private String confirmationCode;


    @Test
    void testHandleCallback_ConfirmationType() throws Exception {
        String requestJson = "{\"type\":\"confirmation\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/vk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(confirmationCode));
    }

    @Test
    void testHandleCallback_MessageType() throws Exception {
        String requestJson = "{\"type\":\"message\",\"object\":{\"message\":{\"peer_id\":123456,\"text\":\"Hello\"}}}";

        mockMvc.perform(MockMvcRequestBuilders.post("/vk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testHandleCallback_InvalidType() throws Exception {
        String requestJson = "{\"type\":\"unknown\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/vk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Invalid type"));
    }

    @Test
    void testHandleCallback_MissingType() throws Exception {
        String requestJson = "{}";
        mockMvc.perform(MockMvcRequestBuilders.post("/vk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Invalid request: missing type"));
    }

}