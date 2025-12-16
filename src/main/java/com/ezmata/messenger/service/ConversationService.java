package com.ezmata.messenger.service;

import com.ezmata.messenger.records.response.MemberModificationResponse;
import com.ezmata.messenger.model.Conversation;
import com.ezmata.messenger.model.User;
import com.ezmata.messenger.repository.ConversationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConversationService {

    private final UserService userService;
    private ConversationRepository conversationRepository;

    public ConversationService(ConversationRepository conversationRepository, UserService userService) {
        this.userService = userService;
        this.conversationRepository = conversationRepository;
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

    public Conversation createGroupConversation(String username, String groupName, long[] memberIds) {
        System.out.print("Creating group conversation with members: ");
        for (Long id : memberIds) {
            System.out.print(id + " ");
        }
        System.out.println();
        Optional<User> userOpt = userService.getByUsername(username);
        System.out.println("User found: " + userOpt.isPresent());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("Creating group conversation for user ID: " + user.getUserId());

            System.out.println("trying to add user ID to member list: " + user.getUserId());
            System.out.println("Current member IDs before adding: " + memberIds);
            List<Long> memberIdList = new ArrayList<>();
            for (long id : memberIds) {
                memberIdList.add(id);
            }
            if (!memberIdList.contains(user.getUserId())) {
                memberIdList.add(user.getUserId());
            }

            Optional<Conversation> conversationOpt = conversationRepository.addGroupConversation(groupName, memberIdList);
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

        if (membersOpt.isEmpty()) {
            throw new IllegalArgumentException("Failed to retrieve conversation members");
        }
        List<Long> members = membersOpt.get();
        if (!members.contains(user.getUserId())) {
            throw new IllegalArgumentException("User is not a member of this conversation");
        }
    }

    public long getOtherParticipantId(long conversationId, long userId) {
        Conversation conversation = conversationRepository.get(conversationId).
                orElseThrow(() -> new IllegalArgumentException("Conversation not found"));
        if (conversation.getType() != com.ezmata.messenger.model.ConversationType.DIRECT) {
            throw new IllegalArgumentException("Not a direct conversation");
        }
        List<Long> members = conversationRepository.getMembers(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Failed to retrieve conversation members"));
        for (long memberId : members) {
            if (memberId != userId) {
                return memberId;
            }
        }
        throw new IllegalArgumentException("Other participant not found");
    }
}
