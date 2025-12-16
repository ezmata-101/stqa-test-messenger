package com.ezmata.messenger.repository.inmemory;

import com.ezmata.messenger.model.Conversation;
import com.ezmata.messenger.model.ConversationType;
import com.ezmata.messenger.repository.ConversationRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryConversationRepository implements ConversationRepository {
    private final Map<Long, Conversation> conversations = new ConcurrentHashMap<>();
    private final Map<Long, Set<Long>> members = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(201);


    @Override
    public List<Conversation> findByUserId(long userId) {
        List<Conversation> result = new ArrayList<>();
        for (Map.Entry<Long, Set<Long>> entry : members.entrySet()) {
            if (entry.getValue().contains(userId)) {
                Conversation conversation = conversations.get(entry.getKey());
                if (conversation != null) {
                    result.add(conversation);
                }
            }
        }
        return result;
    }

    private Conversation addNewConversation(ConversationType type, List<Long> memberSet, String name) {
        long id = idGenerator.getAndIncrement();
        Conversation conversation = new Conversation(id, type, name, new Date().toInstant(), memberSet.get(0));
        conversations.put(id, conversation);
        members.put(id, new HashSet<>(memberSet));
        return conversation;
    }

    @Override
    public Optional<Conversation> addDirectConversation(long userId1, long userId2) {
        return Optional.of(
                addNewConversation(ConversationType.DIRECT, Arrays.asList(userId1, userId2), null)
        );
    }

    @Override
    public Optional<Conversation> addGroupConversation(String name, List<Long> memberIds) {
        return Optional.of(
                addNewConversation(ConversationType.GROUP, memberIds, name)
        );
    }
    @Override
    public Optional<Conversation> get(long id) {
        return Optional.ofNullable(conversations.get(id));
    }
    @Override
    public Optional<List<Long>> getMembers(long conversationId) {
        Set<Long> memberSet = members.get(conversationId);
        if (memberSet == null) {
            return Optional.empty();
        }
        return Optional.of(new ArrayList<>(memberSet));
    }
    @Override
    public Optional<Conversation> getDirectConversationBetweenUsers(long userId1, long userId2) {
        for (Map.Entry<Long, Set<Long>> entry : members.entrySet()) {
            Set<Long> memberSet = entry.getValue();
            if (memberSet.size() == 2 && memberSet.contains(userId1) && memberSet.contains(userId2)) {
                Conversation conversation = conversations.get(entry.getKey());
                if (conversation != null && conversation.getType() == ConversationType.DIRECT) {
                    return Optional.of(conversation);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ConversationType> getConversationType(long conversationId) {
        Conversation conversation = conversations.get(conversationId);
        if (conversation != null) {
            return Optional.of(conversation.getType());
        }
        return Optional.empty();
    }

    @Override
    public boolean updateGroupConversationName(long conversationId, String name) {
        Conversation conversation = conversations.get(conversationId);
        if (conversation != null && conversation.getType() == ConversationType.GROUP) {
            conversation.setName(name);
            return true;
        }
        return false;
    }
    @Override
    public boolean addMembers(long conversationId, long[] userId) {
        Set<Long> memberSet = members.get(conversationId);
        if (memberSet != null) {
            for (long id : userId) {
                memberSet.add(id);
            }
            return true;
        }
        return false;
    }
    @Override
    public boolean removeMember(long conversationId, long userId) {
        Set<Long> memberSet = members.get(conversationId);
        if (memberSet != null) {
            return memberSet.remove(userId);
        }
        return false;
    }

    @Override
    public void reset() {
        System.out.println("Resetting InMemoryConversationRepository...");
        for (Map.Entry<Long, Conversation> entry : conversations.entrySet()) {
            System.out.println("Conversation ID: " + entry.getKey() + ", Conversation: " + entry.getValue());
        }
        for(Map.Entry<Long, Set<Long>> entry : members.entrySet()) {
            System.out.print("Conversation ID: " + entry.getKey() + ", Members: " );
            for(Long memberId : entry.getValue()) {
                System.out.print(memberId + " ");
            }
            System.out.println();
        }
        conversations.clear();
        members.clear();
        idGenerator.set(201);
    }
}
