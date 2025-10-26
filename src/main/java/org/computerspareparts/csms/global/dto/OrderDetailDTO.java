package org.computerspareparts.csms.global.dto;

public class OrderDetailDTO {
    private Long order_detail_id;
    private Integer part_id;
    private String part_name;
    private String part_brand;
    private Integer quantity;
    private Double price;

    public OrderDetailDTO() {}

    public Long getOrder_detail_id() { return order_detail_id; }
    public void setOrder_detail_id(Long order_detail_id) { this.order_detail_id = order_detail_id; }

    public Integer getPart_id() { return part_id; }
    public void setPart_id(Integer part_id) { this.part_id = part_id; }

    public String getPart_name() { return part_name; }
    public void setPart_name(String part_name) { this.part_name = part_name; }

    public String getPart_brand() { return part_brand; }
    public void setPart_brand(String part_brand) { this.part_brand = part_brand; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}
