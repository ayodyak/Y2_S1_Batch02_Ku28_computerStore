package org.computerspareparts.csms.global.dto;

public class OrderItemDTO {
    private Integer partId;
    private Integer quantity;

    public OrderItemDTO() {}

    public Integer getPartId() { return partId; }
    public void setPartId(Integer partId) { this.partId = partId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}

