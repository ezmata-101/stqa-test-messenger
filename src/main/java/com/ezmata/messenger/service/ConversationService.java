package com.ezmata.messenger.service;

import com.ezmata.messenger.api.response.ConversationMessagesResponse;
import com.ezmata.messenger.api.response.MemberModificationResponse;
import com.ezmata.messenger.model.Conversation;
import com.ezmata.messenger.model.User;
import com.ezmata.messenger.repository.ConversationRepository;
import com.ezmata.messenger.security.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConversationService {

    private UserService userService;
    private ConversationRepository conversationRepository;
    private JwtUtil jwtUtil;

    public ConversationService(ConversationRepository conversationRepository, JwtUtil jwtUtil) {
        this.conversationRepository = conversationRepository;
        this.jwtUtil = jwtUtil;
    }

    public List<Conversation> getUserConversations(String username) {
        Optional<User> userOpt = userService.getByUsername(username);
        if (userOpt.isPresent()) {
            var user = userOpt.get();
            return conversationRepository.findByUserId(user.getUserId());
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    public Conversation getConversationById(String username, long conversationId) {
        isAConversationMember(username, conversationId);
        Optional<Conversation> conversationOpt = conversationRepository.get(conversationId);
        if (conversationOpt.isPresent()) {
            return conversationOpt.get();
        } else {
            throw new IllegalArgumentException("Conversation not found");
        }
    }

    public Conversation createDirectConversation(String username, long otherUserId) {
        Optional<User> userOpt = userService.getByUsername(username);
        if (userOpt.isPresent()) {
            var user = userOpt.get();
            Optional<Conversation> conversationOpt = conversationRepository.addDirectConversation(user.getUserId(), otherUserId);
            if (conversationOpt.isPresent()) {
                return conversationOpt.get();
            } else {
                throw new IllegalArgumentException("Failed to create direct conversation");
            }
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    public Conversation createGroupConversation(String username, String groupName, List<Long> memberIds) {
        Optional<User> userOpt = userService.getByUsername(username);
        if (userOpt.isPresent()) {
            var user = userOpt.get();
            memberIds.add(user.getUserId());
            Optional<Conversation> conversationOpt = conversationRepository.addGroupConversation(groupName, memberIds);
            if (conversationOpt.isPresent()) {
                return conversationOpt.get();
            } else {
                throw new IllegalArgumentException("Failed to create group conversation");
            }
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    public Conversation updateGroupConversationName(String username, long conversationId, String newName) {
        isAConversationMember(username, conversationId);

        boolean updated = conversationRepository.updateGroupConversationName(conversationId, newName);
        if (updated) {
            return conversationRepository.get(conversationId).get();
        } else {
            throw new IllegalArgumentException("Failed to update conversation name");
        }
    }

    public MemberModificationResponse addMembersToConversation(String username, long conversationId, long[] newMemberId) {
        isAConversationMember(username, conversationId);

        boolean added = conversationRepository.addMembers(conversationId, newMemberId);
        if (added) {
            Optional<List<Long>> membersOpt = conversationRepository.getMembers(conversationId);
            if (membersOpt.isPresent()) {
                long[] membersArray = membersOpt.get().stream().mapToLong(Long::longValue).toArray();
                return new MemberModificationResponse("Members added successfully", conversationId, membersArray);
            } else {
                throw new IllegalArgumentException("Failed to retrieve updated member list");
            }
        } else {
            throw new IllegalArgumentException("Failed to add member to conversation");
        }
    }

    public MemberModificationResponse removeMemberFromConversation(String username, long conversationId, long newMemberId) {
        isAConversationMember(username, conversationId);

        boolean removed = conversationRepository.removeMember(conversationId, newMemberId);
        if (removed) {
            long[] membersArray = conversationRepository.getMembers(conversationId)
                    .orElse(List.of())
                    .stream()
                    .mapToLong(Long::longValue)
                    .toArray();
            return new MemberModificationResponse("Member removed successfully", conversationId, membersArray);
        } else {
            throw new IllegalArgumentException("Failed to remove member from conversation");
        }
    }

    private void isAConversationMember(String username, long conversationId) {
        Optional<User> userOpt = userService.getByUsername(username);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOpt.get();
        Optional<Conversation> conversationOpt = conversationRepository.get(conversationId);
        if (conversationOpt.isEmpty()) {
            throw new IllegalArgumentException("Conversation not found");
        }

        Conversation conversation = conversationOpt.get();
        Optional<List<Long>> membersOpt = conversationRepository.getMembers(conversationId);
        if (membersOpt.isEmpty() || !membersOpt.get().contains(user.getUserId())) {
            throw new SecurityException("User is not a member of this conversation");
        }
    }

    public ConversationMessagesResponse getConversationMessages(String username, long conversationId) {
        isAConversationMember(username, conversationId);
        // Implementation to retrieve messages would go here
        return null; // Placeholder return
    }
}
