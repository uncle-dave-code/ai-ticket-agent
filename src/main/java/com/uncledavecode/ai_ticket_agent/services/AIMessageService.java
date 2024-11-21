package com.uncledavecode.ai_ticket_agent.services;

import com.uncledavecode.ai_ticket_agent.model.AIResponse;
import com.uncledavecode.ai_ticket_agent.model.CustomChatMemory;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;

@Service
@Slf4j
public class AIMessageService {

    @Value("classpath:schema.sql")
    private Resource ddlResource;

    @Value("classpath:prompts/pt-ticket-agent.st")
    private Resource ptResource;

    private final ChatClient chatClient;
    private final QueryExecutorService queryExecutorService;
    private String schema;
    private String promptFile;

    public AIMessageService(ChatClient.Builder chatClientBuilder, QueryExecutorService queryExecutorService) {
        this.chatClient = chatClientBuilder
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        new PromptChatMemoryAdvisor(new CustomChatMemory())
                )
                .build();
        this.queryExecutorService = queryExecutorService;
    }

    @PostConstruct
    public void init() throws IOException {
        this.schema = ddlResource.getContentAsString(Charset.defaultCharset());
        this.promptFile = ptResource.getContentAsString(Charset.defaultCharset());
    }


    public AIResponse getAIResponse(String conversationId, String message) {
        var firstStep = chatClient
                .prompt()
                .user(userSpec -> userSpec
                        .text(promptFile)
                        .param("message", message)
                        .param("ddl", schema)
                        .param("sql","")
                        .param(PromptChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, conversationId)
                        .param(PromptChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 20)
                )
                .call().entity(AIResponse.class);

        if(firstStep==null || firstStep.sql()==null){
            return firstStep;
        }

        var result = this.queryExecutorService.executeQuery(firstStep.sql());

        var secondStep = chatClient
                .prompt()
                .user(userSpec -> userSpec
                        .text(promptFile)
                        .param("message", message)
                        .param("ddl", schema)
                        .param("sql",result)
                        .param(PromptChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, conversationId)
                        .param(PromptChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 20)
                )
                .call().entity(AIResponse.class);

        return secondStep;
    }
}
