package org.computerspareparts.csms.global.dto;

public class TicketMessageDto {
    private Long id;
    // use String to avoid potential Jackson LocalDateTime serialization issues
    private String createdAt;
    private String message;
    private Long senderId;
    private String senderName;
    private String senderRole;
    private Long ticketId;
    private String ticketTitle;

    public TicketMessageDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderRole() { return senderRole; }
    public void setSenderRole(String senderRole) { this.senderRole = senderRole; }

    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }

    public String getTicketTitle() { return ticketTitle; }
    public void setTicketTitle(String ticketTitle) { this.ticketTitle = ticketTitle; }
}
