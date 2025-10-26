package org.computerspareparts.csms.global.controller;

import org.computerspareparts.csms.global.dto.TicketDto;
import org.computerspareparts.csms.global.dto.TicketDetailDto;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/employee/it/api/tickets")
public class ITTicketController {

    private static final Logger log = LoggerFactory.getLogger(ITTicketController.class);

    @Autowired
    private TicketService ticketService;

    private boolean isCallerIt(Authentication auth) {
        return auth != null && auth.getAuthorities() != null && auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(a -> a.endsWith("IT_TECHNICIAN"));
    }

    @GetMapping
    public ResponseEntity<List<TicketDto>> listTickets(Authentication auth) {
        String user = auth != null ? auth.getName() : "anonymous";
        log.info("{} requested ticket list", user);
        if (!isCallerIt(auth)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        List<TicketDto> list = ticketService.listActiveTickets();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDetailDto> getDetail(@PathVariable Long id, Authentication auth) {
        String user = auth != null ? auth.getName() : "anonymous";
        log.info("{} requested ticket detail id={}", user, id);
        if (!isCallerIt(auth)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        try {
            TicketDetailDto detail = ticketService.getTicketDetail(id);
            return ResponseEntity.ok(detail);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Accept messages for a ticket at POST /employee/it/api/tickets/{id}/messages
    public static class MessageRequest {
        public String message;
        public MessageRequest() {}
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<TicketMessageDto> postMessage(@PathVariable Long id, @RequestBody MessageRequest req, Authentication auth) {
        String username = auth != null ? auth.getName() : null;
        log.info("User {} posting message to ticket {}", username, id);

        if (!isCallerIt(auth)) {
            log.warn("Unauthorized message attempt by {}", username);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (req == null || req.message == null || req.message.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            TicketMessageDto created = ticketService.addReply(id, req.message.trim(), username);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
