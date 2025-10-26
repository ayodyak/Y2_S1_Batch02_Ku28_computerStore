package org.computerspareparts.csms.global.service;

import org.computerspareparts.csms.global.dto.DeliveryItemDto;
import org.computerspareparts.csms.global.dto.DeliveryRequestDto;
import org.computerspareparts.csms.global.entity.*;
import org.computerspareparts.csms.global.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

@Service
public class SupplierDeliveryService {

    private final SupplierDeliveryRepository deliveryRepository;
    private final SupplierDeliveryItemRepository deliveryItemRepository;
    private final SupplierPartRepository supplierPartRepository;
    private final PurchaseRequestRepository purchaseRequestRepository;
    private final SupplierRepository supplierRepository;

    public SupplierDeliveryService(SupplierDeliveryRepository deliveryRepository,
                                   SupplierDeliveryItemRepository deliveryItemRepository,
                                   SupplierPartRepository supplierPartRepository,
                                   PurchaseRequestRepository purchaseRequestRepository,
                                   SupplierRepository supplierRepository) {
        this.deliveryRepository = deliveryRepository;
        this.deliveryItemRepository = deliveryItemRepository;
        this.supplierPartRepository = supplierPartRepository;
        this.purchaseRequestRepository = purchaseRequestRepository;
        this.supplierRepository = supplierRepository;
    }

    @Transactional
    public SupplierDelivery createDelivery(DeliveryRequestDto dto) {
        // If a requestId is supplied, validate the purchase request and require it to be APPROVED.
        PurchaseRequest pr = null;
        if (dto.requestId != null) {
            pr = purchaseRequestRepository.findById(dto.requestId)
                    .orElseThrow(() -> new IllegalArgumentException("Purchase request not found"));

            // Ensure request is APPROVED before creating a delivery
            if (pr.getStatus() != null && !pr.getStatus().equalsIgnoreCase("APPROVED")) {
                throw new IllegalArgumentException("Purchase request must be APPROVED to create a delivery");
            }
        }

        // Resolve supplier: prefer dto.supplierId, otherwise derive from purchase request if present
        Supplier supplier;
        if (dto.supplierId != null) {
            supplier = supplierRepository.findById(dto.supplierId.longValue())
                    .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
        } else if (pr != null) {
            supplier = pr.getSupplier();
            if (supplier == null) throw new IllegalArgumentException("Supplier not provided and not found on purchase request");
        } else {
            // Standalone deliveries must include supplierId
            throw new IllegalArgumentException("SupplierId must be provided for deliveries not linked to a purchase request");
        }

        SupplierDelivery delivery = new SupplierDelivery();
        if (pr != null) delivery.setRequest(pr);
        delivery.setSupplier(supplier);
        delivery.setStatus(dto.status != null ? dto.status : "IN_TRANSIT");
        delivery.setTrackingInfo(dto.trackingInfo);

        if (dto.deliveryDate != null) {
            try {
                // Try parsing as a LocalDateTime first
                delivery.setDeliveryDate(LocalDateTime.parse(dto.deliveryDate));
            } catch (DateTimeParseException e1) {
                try {
                    // Fallback: parse as Instant (ISO with timezone) and convert to system default LocalDateTime
                    Instant inst = Instant.parse(dto.deliveryDate);
                    delivery.setDeliveryDate(LocalDateTime.ofInstant(inst, ZoneId.systemDefault()));
                } catch (DateTimeParseException e2) {
                    delivery.setDeliveryDate(LocalDateTime.now());
                }
            }
        } else {
            delivery.setDeliveryDate(LocalDateTime.now());
        }

        // Persist delivery first to get id
        SupplierDelivery saved = deliveryRepository.save(delivery);

        // For each item, ensure supplier has stock in supplier_part and deduct
        if (dto.items != null) {
            for (DeliveryItemDto it : dto.items) {
                SupplierPart sp = supplierPartRepository.findById(it.partId)
                        .orElseThrow(() -> new IllegalArgumentException("Supplier part not found: " + it.partId));

                if (sp.getStockLevel() == null || sp.getStockLevel() < it.quantity) {
                    throw new IllegalArgumentException("Insufficient stock for part: " + sp.getName());
                }

                // deduct stock
                sp.setStockLevel(sp.getStockLevel() - it.quantity);
                supplierPartRepository.save(sp);

                // create delivery item
                SupplierDeliveryItem item = new SupplierDeliveryItem();
                item.setDelivery(saved);
                item.setPart(sp);
                item.setQuantityReceived(it.quantity);
                BigDecimal price = it.unitPrice != null ? BigDecimal.valueOf(it.unitPrice) : sp.getPrice();
                item.setUnitPrice(price);
                deliveryItemRepository.save(item);

                saved.addItem(item);
            }
        }

        // Update purchase request status
        if (pr != null) {
            pr.setStatus(delivery.getStatus().equals("DELIVERED") ? "DELIVERED" : "IN_TRANSIT");
            purchaseRequestRepository.save(pr);
        }

        return saved;
    }

    // New: list deliveries for a supplier (or all deliveries if supplierId null)
    public java.util.List<java.util.Map<String,Object>> listDeliveries(Integer supplierId) {
        java.util.List<SupplierDelivery> list;
        if (supplierId != null) {
            list = deliveryRepository.findBySupplierSupplierId(supplierId);
        } else {
            list = deliveryRepository.findAll();
        }

        java.util.List<java.util.Map<String,Object>> out = new java.util.ArrayList<>();
        for (SupplierDelivery d : list) {
            java.util.Map<String,Object> m = new java.util.HashMap<>();
            m.put("deliveryId", d.getDeliveryId());
            m.put("supplierId", d.getSupplier() != null ? d.getSupplier().getSupplierId() : null);
            m.put("requestId", d.getRequest() != null ? d.getRequest().getRequestId() : null);
            m.put("deliveryDate", d.getDeliveryDate() != null ? d.getDeliveryDate().toString() : null);
            m.put("receivedBy", d.getReceivedBy() != null ? d.getReceivedBy().getUserId() : null);
            m.put("status", d.getStatus());
            m.put("trackingInfo", d.getTrackingInfo());

            java.util.List<java.util.Map<String,Object>> items = new java.util.ArrayList<>();
            if (d.getItems() != null) {
                for (SupplierDeliveryItem it : d.getItems()) {
                    java.util.Map<String,Object> im = new java.util.HashMap<>();
                    im.put("id", it.getId());
                    im.put("deliveryId", d.getDeliveryId());
                    im.put("partId", it.getPart() != null ? it.getPart().getPartId() : null);
                    im.put("partName", it.getPart() != null ? it.getPart().getName() : null);
                    im.put("quantity", it.getQuantityReceived());
                    im.put("unitPrice", it.getUnitPrice() != null ? it.getUnitPrice().doubleValue() : null);
                    items.add(im);
                }
            }
            m.put("items", items);
            out.add(m);
        }
        return out;
    }

}
