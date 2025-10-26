package org.computerspareparts.csms.global.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "purchase_request_item")
public class PurchaseRequestItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "request_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pri_request"))
    @JsonBackReference
    private PurchaseRequest request;

    @ManyToOne(optional = false)
    @JoinColumn(name = "part_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pri_part"))
    private Part part;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private String status;

    public PurchaseRequestItem() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public PurchaseRequest getRequest() { return request; }
    public void setRequest(PurchaseRequest request) { this.request = request; }

    public Part getPart() { return part; }
    public void setPart(Part part) { this.part = part; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

