package org.computerspareparts.csms.global.dto;

import java.math.BigDecimal;
import java.util.List;

public class PurchaseRequestDto {
    public Integer managerId;
    public Integer supplierId;
    public String requestDate;
    public String status;
    public BigDecimal totalAmount;
    public List<PurchaseRequestItemDto> items;
}

