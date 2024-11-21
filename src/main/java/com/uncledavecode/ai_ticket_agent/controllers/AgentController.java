package com.uncledavecode.ai_ticket_agent.controllers;

import com.uncledavecode.ai_ticket_agent.model.AIResponse;
import com.uncledavecode.ai_ticket_agent.model.ChatRequest;
import com.uncledavecode.ai_ticket_agent.model.ChatResponse;
import com.uncledavecode.ai_ticket_agent.services.AIMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentController {

    private final AIMessageService aiMessageService;

    @PostMapping("/chat")
    private ResponseEntity<ChatResponse> chat(
            @RequestBody ChatRequest chatRequest) {

        String conversationId = (chatRequest.userId() == null)
                ? UUID.randomUUID().toString() : chatRequest.userId();

        AIResponse response = aiMessageService.getAIResponse(conversationId, chatRequest.message());

        return ResponseEntity.ok(new ChatResponse(conversationId, response.answer()));

    }
}
