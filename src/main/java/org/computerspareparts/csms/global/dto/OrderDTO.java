package org.computerspareparts.csms.global.dto;

import java.util.List;

public class OrderDTO {
    private Long order_id;
    private Long customer_id;
    private String customer_name;
    private String customer_email;
    private String order_date;
    private String status;
    private String payment_status;
    private Double total;
    private List<OrderDetailDTO> details;

    public OrderDTO() {}

    public Long getOrder_id() { return order_id; }
    public void setOrder_id(Long order_id) { this.order_id = order_id; }

    public Long getCustomer_id() { return customer_id; }
    public void setCustomer_id(Long customer_id) { this.customer_id = customer_id; }

    public String getCustomer_name() { return customer_name; }
    public void setCustomer_name(String customer_name) { this.customer_name = customer_name; }

    public String getCustomer_email() { return customer_email; }
    public void setCustomer_email(String customer_email) { this.customer_email = customer_email; }

    public String getOrder_date() { return order_date; }
    public void setOrder_date(String order_date) { this.order_date = order_date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPayment_status() { return payment_status; }
    public void setPayment_status(String payment_status) { this.payment_status = payment_status; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public List<OrderDetailDTO> getDetails() { return details; }
    public void setDetails(List<OrderDetailDTO> details) { this.details = details; }
}
