package com.uncledavecode.ai_ticket_agent.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ChatRequest(
        @JsonProperty("user_id")
        String userId,

        @JsonProperty("message")
        String message
) {
}
