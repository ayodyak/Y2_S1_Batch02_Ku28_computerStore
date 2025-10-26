package org.computerspareparts.csms.global.controller;

import org.computerspareparts.csms.global.dto.TicketMessageDto;
import org.computerspareparts.csms.global.service.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/employee/it/api/messages")
public class ITMessageController {

    private static final Logger log = LoggerFactory.getLogger(ITMessageController.class);

    @Autowired
    private TicketService ticketService;

    @GetMapping("/others")
    public ResponseEntity<List<TicketMessageDto>> getMessagesFromOtherStakeholders(Authentication auth) {
        String username = auth != null ? auth.getName() : "anonymous";
        log.info("User {} requested messages from non-IT stakeholders", username);

        // Defense-in-depth: ensure caller has IT_TECHNICIAN role
        if (auth == null || auth.getAuthorities() == null || auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).noneMatch(a -> a.endsWith("IT_TECHNICIAN"))) {
            log.warn("Unauthorized access attempt to IT messages by {}", username);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<TicketMessageDto> msgs = ticketService.getMessagesFromNonItStakeholders();
        return ResponseEntity.ok(msgs);
    }

    // Accept replies from IT technicians for a ticket
    public static class ReplyRequest {
        public Long ticketId;
        public String message;
        public ReplyRequest() {}
    }

    @PostMapping
    public ResponseEntity<TicketMessageDto> postReply(@RequestBody ReplyRequest req, Authentication auth) {
        String username = auth != null ? auth.getName() : "anonymous";
        log.info("User {} posting reply to ticket {}", username, req == null ? null : req.ticketId);

        // Authorization: only IT technicians
        if (auth == null || auth.getAuthorities() == null || auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).noneMatch(a -> a.endsWith("IT_TECHNICIAN"))) {
            log.warn("Unauthorized reply attempt by {}", username);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (req == null || req.ticketId == null || req.message == null || req.message.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            TicketMessageDto created = ticketService.addReply(req.ticketId, req.message.trim(), username);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
