package org.computerspareparts.csms.global.dto;

public class TicketReplyRequest {
    private Long ticketId;
    private String message;

    public TicketReplyRequest() {}

    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

