package com.uncledavecode.ai_ticket_agent.model;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomChatMemory implements ChatMemory {
    Map<String, List<Message>> conversationHistory = new ConcurrentHashMap();

    public void add(String conversationId, List<Message> messages) {
        // Insert the message into a Database
        this.conversationHistory.putIfAbsent(conversationId, new ArrayList());
        ((List)this.conversationHistory.get(conversationId)).addAll(messages);
    }

    public List<Message> get(String conversationId, int lastN) {
        //Get the messages from the Database
        List<Message> all = (List)this.conversationHistory.get(conversationId);
        return all != null ? all.stream().skip((long)Math.max(0, all.size() - lastN)).toList() : List.of();
    }

    public void clear(String conversationId) {
        // Delete an entire conversation from the Database
        this.conversationHistory.remove(conversationId);
    }
}
