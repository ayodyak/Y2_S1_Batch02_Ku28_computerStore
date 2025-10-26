package org.computerspareparts.csms.global.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "supplier_delivery_item")
public class SupplierDeliveryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "delivery_id", nullable = false)
    private SupplierDelivery delivery;

    @ManyToOne(optional = false)
    @JoinColumn(name = "part_id", nullable = false)
    private SupplierPart part;

    @Column(name = "quantity_received", nullable = false)
    private Integer quantityReceived;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    public SupplierDeliveryItem() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public SupplierDelivery getDelivery() { return delivery; }
    public void setDelivery(SupplierDelivery delivery) { this.delivery = delivery; }

    public SupplierPart getPart() { return part; }
    public void setPart(SupplierPart part) { this.part = part; }

    public Integer getQuantityReceived() { return quantityReceived; }
    public void setQuantityReceived(Integer quantityReceived) { this.quantityReceived = quantityReceived; }

    public java.math.BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(java.math.BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}

