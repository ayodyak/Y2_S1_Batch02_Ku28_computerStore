package org.computerspareparts.csms.global.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "customer_order_detail")
public class CustomerOrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    private Long orderDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private CustomerOrder order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;

    @Column(nullable = false)
    private Integer quantity;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    public CustomerOrderDetail() {}

    // getters and setters
    public Long getOrderDetailId() { return orderDetailId; }
    public void setOrderDetailId(Long orderDetailId) { this.orderDetailId = orderDetailId; }

    public CustomerOrder getOrder() { return order; }
    public void setOrder(CustomerOrder order) { this.order = order; }

    public Part getPart() { return part; }
    public void setPart(Part part) { this.part = part; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public java.math.BigDecimal getPrice() { return price; }
    public void setPrice(java.math.BigDecimal price) { this.price = price; }
}
