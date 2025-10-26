package org.computerspareparts.csms.global.service;

import org.computerspareparts.csms.global.dto.PurchaseRequestDto;
import org.computerspareparts.csms.global.dto.PurchaseRequestItemDto;
import org.computerspareparts.csms.global.entity.*;
import org.computerspareparts.csms.global.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PurchaseRequestService {

    private final PurchaseRequestRepository prRepository;
    private final EmployeeRepository employeeRepository;
    private final SupplierRepository supplierRepository;
    private final PartRepository partRepository;

    public PurchaseRequestService(PurchaseRequestRepository prRepository,
                                  EmployeeRepository employeeRepository,
                                  SupplierRepository supplierRepository,
                                  PartRepository partRepository) {
        this.prRepository = prRepository;
        this.employeeRepository = employeeRepository;
        this.supplierRepository = supplierRepository;
        this.partRepository = partRepository;
    }

    public List<PurchaseRequest> findAll() {
        return prRepository.findAll();
    }

    public Optional<PurchaseRequest> findById(Integer id) {
        return prRepository.findById(id);
    }

    @Transactional
    public PurchaseRequest createFromDto(PurchaseRequestDto dto) {
        PurchaseRequest pr = new PurchaseRequest();

        // manager
        Employee manager = employeeRepository.findById(dto.managerId.longValue()).orElseThrow(() -> new IllegalArgumentException("Manager not found"));
        pr.setManager(manager);

        // supplier (convert incoming Integer to Long)
        Supplier supplier = supplierRepository.findById(dto.supplierId.longValue()).orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
        pr.setSupplier(supplier);

        // request date
        LocalDateTime date = LocalDateTime.now();
        if (dto.requestDate != null) {
            try {
                date = LocalDateTime.parse(dto.requestDate);
            } catch (DateTimeParseException ex) {
                date = LocalDateTime.now();
            }
        }
        pr.setRequestDate(date);

        // Normalize status to uppercase and default to 'PENDING'
        pr.setStatus(dto.status != null ? dto.status.toUpperCase() : "PENDING");

        BigDecimal total = dto.totalAmount != null ? dto.totalAmount : BigDecimal.ZERO;
        pr.setTotalAmount(total);

        // items
        if (dto.items != null) {
            for (PurchaseRequestItemDto it : dto.items) {
                PurchaseRequestItem item = new PurchaseRequestItem();
                Part part = partRepository.findById(it.partId).orElseThrow(() -> new IllegalArgumentException("Part not found: " + it.partId));
                item.setPart(part);
                item.setQuantity(it.quantity);
                item.setUnitPrice(it.unitPrice != null ? it.unitPrice : part.getPrice());
                // Normalize item status as well
                item.setStatus(it.status != null ? it.status.toUpperCase() : "PENDING");
                pr.addItem(item);
            }
        }

        // Calculate total from items if not provided
        if (total.compareTo(BigDecimal.ZERO) == 0 && pr.getItems() != null && !pr.getItems().isEmpty()) {
            BigDecimal calc = pr.getItems().stream()
                    .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            pr.setTotalAmount(calc);
        }

        return prRepository.save(pr);
    }

    @Transactional
    public boolean delete(Integer id) {
        if (!prRepository.existsById(id)) return false;
        prRepository.deleteById(id);
        return true;
    }

    @Transactional
    public PurchaseRequest updateStatus(Integer id, String newStatus) {
        PurchaseRequest pr = prRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Purchase request not found"));
        // Normalize incoming status to uppercase to keep values consistent
        pr.setStatus(newStatus != null ? newStatus.toUpperCase() : null);
        return prRepository.save(pr);
    }
}
