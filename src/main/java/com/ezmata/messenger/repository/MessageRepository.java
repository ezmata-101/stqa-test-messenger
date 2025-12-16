package com.ezmata.messenger.repository;

import com.ezmata.messenger.model.Message;

import java.util.List;
import java.util.Optional;

public interface MessageRepository {
    public Optional<List<Message>> getMessagesForConversation(long conversationId);
    public Optional<List<Message>> sendMessage(long conversationId, long senderId, String content);

    void reset();
}
