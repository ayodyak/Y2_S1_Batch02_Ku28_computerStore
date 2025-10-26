package org.computerspareparts.csms.global.dto;

import java.util.List;

public class TicketDetailDto {
    private Long id;
    private String title;
    private String description;
    private String priority;
    private String status;
    private String createdAt;
    private String createdByName;
    private List<TicketMessageDto> messages;

    public TicketDetailDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public List<TicketMessageDto> getMessages() { return messages; }
    public void setMessages(List<TicketMessageDto> messages) { this.messages = messages; }
}

