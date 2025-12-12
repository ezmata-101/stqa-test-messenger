package com.ezmata.messenger.repository;

import com.ezmata.messenger.model.Conversation;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository {
    public List<Conversation> findByUserId(long userId);
    public Optional<Conversation> addDirectConversation(long userId1, long userId2);
    public Optional<Conversation> addGroupConversation(String name, List<Long> memberIds);
    public Optional<Conversation> get(long id);
    public Optional<List<Long>> getMembers(long conversationId);
    public Optional<Conversation> getDirectConversationBetweenUsers(long userId1, long userId2);
    public boolean updateGroupConversationName(long conversationId, String name);
    public boolean addMembers(long conversationId, long[] userId);
    public boolean removeMember(long conversationId, long userId);
}
