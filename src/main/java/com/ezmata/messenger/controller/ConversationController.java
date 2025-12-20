package com.ezmata.messenger.controller;

import com.ezmata.messenger.records.request.MemberModificationRequest;
import com.ezmata.messenger.records.request.CreateConversationRequest;
import com.ezmata.messenger.records.response.MemberModificationResponse;
import com.ezmata.messenger.model.Conversation;
import com.ezmata.messenger.service.ConversationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conversations")
public class ConversationController {

    private final ConversationService conversationService;
    ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping("/get")
    public ResponseEntity<?> getConversation(Authentication authentication) {
        String username = authentication.getName();
        try{
            List<Conversation> conversations = conversationService.getUserConversations(username);
            return ResponseEntity.ok(conversations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/{conversationId}")
    public ResponseEntity<?> getConversationById(Authentication authentication, @PathVariable long conversationId) {
        String username = authentication.getName();
        try{
            Conversation conversation = conversationService.getConversationById(username, conversationId);
            return ResponseEntity.ok(conversation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createConversation(Authentication authentication, @RequestBody CreateConversationRequest request) {
        String username = authentication.getName();
        try{
            Conversation conversation;
            if(request.isGroup()){
                conversation = conversationService.createGroupConversation(username, request.name(), request.getMembersList());
            }else{
                conversation = conversationService.createDirectConversation(username, request.memberIds()[0]);
            }
            return ResponseEntity.ok(conversation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/addMember")
    public ResponseEntity<?> addMember(Authentication authentication, @RequestBody MemberModificationRequest request) {
        String username = authentication.getName();
        try{
            MemberModificationResponse response = conversationService.addMembersToConversation(username, request.conversationId(), request.members());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/removeMember")
    public ResponseEntity<?> removeMember(Authentication authentication, @RequestBody MemberModificationRequest request) {
        String username = authentication.getName();
        try{
            MemberModificationResponse response = conversationService.removeMemberFromConversation(username, request.conversationId(), request.members());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
