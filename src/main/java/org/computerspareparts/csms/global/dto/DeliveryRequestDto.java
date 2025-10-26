package org.computerspareparts.csms.global.dto;

import java.util.List;

public class DeliveryRequestDto {
    public Integer supplierId; // optional: can be inferred from auth
    public Integer requestId;
    public String deliveryDate; // ISO datetime string, optional
    public String trackingInfo;
    public String status; // optional
    public List<DeliveryItemDto> items;
}

