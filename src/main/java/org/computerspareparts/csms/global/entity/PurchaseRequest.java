package org.computerspareparts.csms.global.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.computerspareparts.csms.global.entity.PurchaseRequestItem; // explicit import to resolve analyzer

@Entity
@Table(name = "purchase_request")
public class PurchaseRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Integer requestId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "manager_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pr_manager"))
    private Employee manager;

    @ManyToOne(optional = false)
    @JoinColumn(name = "supplier_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pr_supplier"))
    private Supplier supplier;

    @Column(name = "request_date", nullable = false)
    private LocalDateTime requestDate;

    @Column(nullable = false)
    private String status;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PurchaseRequestItem> items = new ArrayList<>();

    public PurchaseRequest() {}

    public Integer getRequestId() { return requestId; }
    public void setRequestId(Integer requestId) { this.requestId = requestId; }

    public Employee getManager() { return manager; }
    public void setManager(Employee manager) { this.manager = manager; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public LocalDateTime getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public List<PurchaseRequestItem> getItems() { return items; }
    public void setItems(List<PurchaseRequestItem> items) { this.items = items; }

    public void addItem(PurchaseRequestItem item) {
        items.add(item);
        item.setRequest(this);
    }
}
