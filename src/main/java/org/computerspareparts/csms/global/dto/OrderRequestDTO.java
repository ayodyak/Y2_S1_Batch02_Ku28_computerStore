package org.computerspareparts.csms.global.dto;

import java.util.List;

public class OrderRequestDTO {
    private Long customerId;
    private List<OrderItemDTO> items;

    public OrderRequestDTO() {}

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public List<OrderItemDTO> getItems() { return items; }
    public void setItems(List<OrderItemDTO> items) { this.items = items; }
}

