package com.ezmata.messenger.service;

import com.ezmata.messenger.model.Conversation;
import com.ezmata.messenger.model.ConversationType;
import com.ezmata.messenger.model.Message;
import com.ezmata.messenger.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final ConversationService conversationService;
    private final UserService userService;

    public MessageService(MessageRepository messageRepository, ConversationService conversationService, UserService userService) {
        this.messageRepository = messageRepository;
        this.conversationService = conversationService;
        this.userService = userService;
    }

    public List<Message> getMessagesForConversation(String username, long conversationId) {
        try{
            Conversation conversation = conversationService.getConversationById(username, conversationId);
            try{
                return messageRepository.getMessagesForConversation(conversationId).orElse(new ArrayList<Message>());
            }catch (Exception e){
                throw new IllegalArgumentException("Failed to retrieve messages for this conversation: " + e.getMessage());
            }
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Cannot access messages for this conversation: " + e.getMessage());
        }
    }

    public List<Message> sendMessageToConversation(String username, long conversationId, String content) {
        Conversation conversation = conversationService.getConversationById(username, conversationId);
        long senderId = userService.getByUsername(username).get().getUserId();
        if(conversation.getType() == ConversationType.DIRECT){
            long receiverId = conversationService.getOtherParticipantId(conversationId, senderId);
            if(userService.isUserBlockedBy(receiverId, senderId)){
                throw new IllegalArgumentException("You must unblock the user to send messages");
            }
            if(!userService.isUserBlockedBy(senderId, receiverId)){
                try{
                    return messageRepository.sendMessage(conversationId, senderId, content).orElse(new ArrayList<Message>());
                }catch (Exception e){
                    throw new IllegalArgumentException("Failed to send message to this conversation: "+ e.getMessage());
                }
            }else{
                throw new IllegalArgumentException("You cannot send message to this conversation");
            }
        }else{
            try{
                return messageRepository.sendMessage(conversationId, senderId, content).orElse(new ArrayList<Message>());
            }catch (Exception e){
                throw new IllegalArgumentException("Failed to send message to this conversation:" + e.getMessage());
            }
        }
    }
}
