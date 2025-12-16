package com.ezmata.messenger.repository.inmemory;

import com.ezmata.messenger.model.Message;
import com.ezmata.messenger.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryMessageRepository implements MessageRepository {
    private final Map<Long, List<Message>> messages = new HashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1001);
    @Override
    public Optional<List<Message>> getMessagesForConversation(long conversationId) {
        return Optional.ofNullable(messages.get(conversationId));
    }

    @Override
    public Optional<List<Message>> sendMessage(long conversationId, long senderId, String content) {
        List<Message> convoMessages = new ArrayList<>();

        if(messages.containsKey(conversationId)) {
            convoMessages = this.messages.get(conversationId);
        }

        convoMessages.add(
                new Message(
                        nextId.getAndIncrement(),
                        conversationId,
                        senderId,
                        content,
                        System.currentTimeMillis()
                )
        );
        messages.put(conversationId, convoMessages);
        return Optional.ofNullable(messages.get(conversationId));
    }

    @Override
    public void reset() {
        System.out.println("InMemoryMessageRepository contents before reset:");
        for (Map.Entry<Long, List<Message>> entry : messages.entrySet()) {
            System.out.println("Conversation ID: " + entry.getKey());
            for (Message message : entry.getValue()) {
                System.out.println(message);
            }
        }
        messages.clear();
        nextId.set(1001);
    }
}
