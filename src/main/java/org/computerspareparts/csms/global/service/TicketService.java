package org.computerspareparts.csms.global.service;

import org.computerspareparts.csms.global.dto.TicketDto;
import org.computerspareparts.csms.global.dto.TicketDetailDto;
import org.computerspareparts.csms.global.dto.TicketMessageDto;
import org.computerspareparts.csms.global.entity.*;
import org.computerspareparts.csms.global.repository.TicketMessageRepository;
import org.computerspareparts.csms.global.repository.TicketRepository;
import org.computerspareparts.csms.global.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketService {

    @Autowired//
    private TicketMessageRepository ticketMessageRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    // Use ISO-like format with 'T' separator so JS Date can reliably parse createdAt strings
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public List<TicketMessageDto> getMessagesFromNonItStakeholders() {
        List<TicketMessage> msgs = ticketMessageRepository.findAllActiveWithSenderAndTicket();

        return msgs.stream()
                .filter(m -> m.getSender() != null)
                .filter(m -> m.getSender().getRole() != Role.IT_TECHNICIAN)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private TicketMessageDto toDto(TicketMessage m) {
        TicketMessageDto dto = new TicketMessageDto();
        dto.setId(m.getId());
        dto.setCreatedAt(m.getCreatedAt() != null ? m.getCreatedAt().format(fmt) : null);
        dto.setMessage(m.getMessage());
        if (m.getSender() != null) {
            dto.setSenderId(m.getSender().getUserId());
            dto.setSenderName(m.getSender().getName());
            dto.setSenderRole(m.getSender().getRole() != null ? m.getSender().getRole().name() : null);
        }
        if (m.getTicket() != null) {
            dto.setTicketId(m.getTicket().getId());
            dto.setTicketTitle(m.getTicket().getTitle());
        }
        return dto;
    }

    // New: list tickets for IT dashboard
    public List<TicketDto> listActiveTickets() {
        return ticketRepository.findByIsDeletedFalseOrderByCreatedAtDesc().stream()
                .map(this::ticketToDto)
                .collect(Collectors.toList());
    }

    public TicketDetailDto getTicketDetail(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        TicketDetailDto detail = new TicketDetailDto();
        detail.setId(ticket.getId());
        detail.setTitle(ticket.getTitle());
        detail.setDescription(ticket.getDescription());
        detail.setPriority(ticket.getPriority() != null ? ticket.getPriority().name() : null);
        detail.setStatus(ticket.getStatus() != null ? ticket.getStatus().name() : null);
        detail.setCreatedAt(ticket.getCreatedAt() != null ? ticket.getCreatedAt().format(fmt) : null);
        detail.setCreatedByName(ticket.getCreatedBy() != null ? ticket.getCreatedBy().getName() : null);

        List<TicketMessage> msgs = ticketMessageRepository.findByTicketIdAndIsDeletedFalseOrderByCreatedAtAsc(ticketId);
        detail.setMessages(msgs.stream().map(this::toDto).collect(Collectors.toList()));
        return detail;
    }

    // Add a reply message to a ticket (used by IT message controller)
    public TicketMessageDto addReply(Long ticketId, String message, String username) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        User sender = null;
        if (username != null) {
            sender = userRepository.findByEmail(username).orElse(null);
        }

        TicketMessage tm = new TicketMessage();
        tm.setMessage(message);
        tm.setTicket(ticket);
        if (sender != null) tm.setSender(sender);
        tm.setCreatedAt(LocalDateTime.now());
        tm.setDeleted(false);

        TicketMessage saved = ticketMessageRepository.save(tm);
        return toDto(saved);
    }

    private TicketDto ticketToDto(Ticket t) {
        TicketDto dto = new TicketDto();
        dto.setId(t.getId());
        dto.setTitle(t.getTitle());
        dto.setDescription(t.getDescription());
        dto.setPriority(t.getPriority() != null ? t.getPriority().name() : null);
        dto.setStatus(t.getStatus() != null ? t.getStatus().name() : null);
        dto.setCreatedAt(t.getCreatedAt() != null ? t.getCreatedAt().format(fmt) : null);
        dto.setCreatedByName(t.getCreatedBy() != null ? t.getCreatedBy().getName() : null);
        return dto;
    }

    public List<TicketDto> listTicketsForUser(String userEmail) {
        if (userEmail == null) return List.of();
        return ticketRepository.findByCreatedByEmailAndIsDeletedFalseOrderByCreatedAtDesc(userEmail).stream()
                .map(this::ticketToDto)
                .collect(Collectors.toList());
    }

    public TicketDto createTicket(String title, String description, String priorityName, String username) {
        if (username == null) throw new IllegalArgumentException("Unauthenticated");
        User user = userRepository.findByEmail(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

        Ticket ticket = new Ticket();
        ticket.setTitle(title != null ? title : "");
        ticket.setDescription(description != null ? description : "");
        try {
            ticket.setPriority(priorityName != null ? TicketPriority.valueOf(priorityName) : TicketPriority.MEDIUM);
        } catch (Exception ex) {
            ticket.setPriority(TicketPriority.MEDIUM);
        }
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreatedBy(user);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setDeleted(false);

        Ticket saved = ticketRepository.save(ticket);

        // Create initial message if description provided
        if (description != null && !description.trim().isEmpty()) {
            TicketMessage tm = new TicketMessage();
            tm.setMessage(description);
            tm.setTicket(saved);
            tm.setSender(user);
            tm.setCreatedAt(LocalDateTime.now());
            tm.setDeleted(false);
            ticketMessageRepository.save(tm);
        }

        return ticketToDto(saved);
    }
}

