package org.computerspareparts.csms.global.controller;

import org.computerspareparts.csms.global.dto.TicketDto;
import org.computerspareparts.csms.global.dto.TicketDetailDto;
import org.computerspareparts.csms.global.dto.TicketMessageDto;
import org.computerspareparts.csms.global.service.TicketService;
import org.computerspareparts.csms.global.repository.TicketRepository;
import org.computerspareparts.csms.global.entity.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/customer/tickets")
public class CustomerTicketController {

    private static final Logger log = LoggerFactory.getLogger(CustomerTicketController.class);

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @GetMapping
    public ResponseEntity<List<TicketDto>> listMyTickets(Authentication auth) {
        if (auth == null || auth.getName() == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String user = auth.getName();
        log.info("{} requested their tickets", user);
        List<TicketDto> list = ticketService.listTicketsForUser(user);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDetailDto> getTicketDetail(@PathVariable Long id, Authentication auth) {
        if (auth == null || auth.getName() == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String user = auth.getName();
        Optional<Ticket> t = ticketRepository.findById(id);
        if (t.isEmpty() || t.get().isDeleted()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        if (t.get().getCreatedBy() == null || !user.equals(t.get().getCreatedBy().getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            TicketDetailDto detail = ticketService.getTicketDetail(id);
            return ResponseEntity.ok(detail);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    public static class CreateTicketRequest {
        public String title;
        public String description;
        public String priority;
        // category may be UI-only and is ignored by backend
    }

    @PostMapping
    public ResponseEntity<?> createTicket(@RequestBody CreateTicketRequest req, Authentication auth) {
        if (auth == null || auth.getName() == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthenticated"));
        String user = auth.getName();
        if (req == null || req.title == null || req.title.trim().isEmpty()) return ResponseEntity.badRequest().body(Map.of("error", "Title required"));
        try {
            TicketDto created = ticketService.createTicket(req.title.trim(), req.description != null ? req.description.trim() : "", req.priority, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            log.error("Create ticket failed", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Could not create ticket"));
        }
    }

    public static class MessageRequest { public String message; }

    @PostMapping("/{id}/messages")
    public ResponseEntity<?> postMessage(@PathVariable Long id, @RequestBody MessageRequest req, Authentication auth) {
        if (auth == null || auth.getName() == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String user = auth.getName();
        Optional<Ticket> t = ticketRepository.findById(id);
        if (t.isEmpty() || t.get().isDeleted()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        if (t.get().getCreatedBy() == null || !user.equals(t.get().getCreatedBy().getEmail())) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        if (req == null || req.message == null || req.message.trim().isEmpty()) return ResponseEntity.badRequest().body(Map.of("error","Message required"));
        try {
            TicketMessageDto created = ticketService.addReply(id, req.message.trim(), user);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<?> closeTicket(@PathVariable Long id, Authentication auth) {
        if (auth == null || auth.getName() == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String user = auth.getName();
        Optional<Ticket> tOpt = ticketRepository.findById(id);
        if (tOpt.isEmpty() || tOpt.get().isDeleted()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        Ticket t = tOpt.get();
        if (t.getCreatedBy() == null || !user.equals(t.getCreatedBy().getEmail())) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        t.setStatus(org.computerspareparts.csms.global.entity.TicketStatus.CLOSED);
        ticketRepository.save(t);
        return ResponseEntity.ok(Map.of("status","CLOSED"));
    }
}

