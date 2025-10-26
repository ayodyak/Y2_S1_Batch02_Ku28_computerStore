package org.computerspareparts.csms.global.dto;

public class PaymentRequestDTO {
    private Double amount;
    private String method;
    private Long paid_by; // optional, customer id
    private Long received_by; // optional, staff id

    public PaymentRequestDTO() {}

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public Long getPaid_by() { return paid_by; }
    public void setPaid_by(Long paid_by) { this.paid_by = paid_by; }

    public Long getReceived_by() { return received_by; }
    public void setReceived_by(Long received_by) { this.received_by = received_by; }
}

