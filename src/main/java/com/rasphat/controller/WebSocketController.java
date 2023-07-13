package com.rasphat.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public String handleMessage(String message) {
        // Process the received message and return a response
        return "Received: " + message;
    }
}

