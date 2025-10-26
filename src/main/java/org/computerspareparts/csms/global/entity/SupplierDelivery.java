package org.computerspareparts.csms.global.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "supplier_delivery")
public class SupplierDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Integer deliveryId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne(optional = false)
    @JoinColumn(name = "request_id", nullable = false)
    private PurchaseRequest request;

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;

    @ManyToOne
    @JoinColumn(name = "received_by")
    private User receivedBy;

    @Column(length = 20)
    private String status = "PENDING";

    @Column(name = "tracking_info", length = 255)
    private String trackingInfo;

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupplierDeliveryItem> items = new ArrayList<>();

    public SupplierDelivery() {}

    public Integer getDeliveryId() { return deliveryId; }
    public void setDeliveryId(Integer deliveryId) { this.deliveryId = deliveryId; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public PurchaseRequest getRequest() { return request; }
    public void setRequest(PurchaseRequest request) { this.request = request; }

    public LocalDateTime getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDateTime deliveryDate) { this.deliveryDate = deliveryDate; }

    public User getReceivedBy() { return receivedBy; }
    public void setReceivedBy(User receivedBy) { this.receivedBy = receivedBy; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTrackingInfo() { return trackingInfo; }
    public void setTrackingInfo(String trackingInfo) { this.trackingInfo = trackingInfo; }

    public List<SupplierDeliveryItem> getItems() { return items; }
    public void setItems(List<SupplierDeliveryItem> items) { this.items = items; }

    public void addItem(SupplierDeliveryItem item) {
        items.add(item);
        item.setDelivery(this);
    }
}

