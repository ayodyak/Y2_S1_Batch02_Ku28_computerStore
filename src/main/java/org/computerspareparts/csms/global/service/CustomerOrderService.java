package org.computerspareparts.csms.global.service;

import org.computerspareparts.csms.global.entity.CustomerOrder;
import org.computerspareparts.csms.global.entity.CustomerOrderDetail;
import org.computerspareparts.csms.global.entity.Part;
import org.computerspareparts.csms.global.entity.User;
import org.computerspareparts.csms.global.entity.PaymentReceipt;
import org.computerspareparts.csms.global.repository.CustomerOrderRepository;
import org.computerspareparts.csms.global.repository.PartRepository;
import org.computerspareparts.csms.global.repository.UserRepository;
import org.computerspareparts.csms.global.repository.PaymentReceiptRepository;
import org.computerspareparts.csms.global.dto.OrderDTO;
import org.computerspareparts.csms.global.dto.OrderDetailDTO;
import org.computerspareparts.csms.global.dto.PaymentRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CustomerOrderService {

    private static final Logger log = LoggerFactory.getLogger(CustomerOrderService.class);

    private final CustomerOrderRepository orderRepository;
    private final PartRepository partRepository;
    private final UserRepository userRepository;
    private final PaymentReceiptRepository paymentRepository;

    @Autowired
    public CustomerOrderService(CustomerOrderRepository orderRepository,
                                PartRepository partRepository,
                                UserRepository userRepository,
                                PaymentReceiptRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.partRepository = partRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public CustomerOrder createOrder(String userEmail, Map<String, Integer> cart) {
        log.info("Creating order for userEmail={} with {} items", userEmail, (cart == null ? 0 : cart.size()));
        if (cart == null || cart.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("User not found"));

        CustomerOrder order = new CustomerOrder();
        order.setCustomer(user);

        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<String, Integer> e : cart.entrySet()) {
            Integer partId;
            try {
                partId = Integer.valueOf(e.getKey());
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Invalid part id: " + e.getKey());
            }
            Integer qty = e.getValue();
            if (qty == null || qty <= 0) {
                throw new IllegalArgumentException("Invalid quantity for part " + partId);
            }

            Part part = partRepository.findById(partId).orElseThrow(() -> new IllegalArgumentException("Part not found: " + partId));

            if (part.getStockLevel() == null || part.getStockLevel() < qty) {
                throw new IllegalArgumentException("Insufficient stock for part " + part.getName());
            }

            // Decrement stock
            part.setStockLevel(part.getStockLevel() - qty);
            partRepository.save(part);

            BigDecimal unitPrice = part.getPrice();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(qty));
            total = total.add(lineTotal);

            CustomerOrderDetail detail = new CustomerOrderDetail();
            detail.setPart(part);
            detail.setQuantity(qty);
            detail.setPrice(unitPrice);

            order.addItem(detail);
        }

        order.setTotal(total);

        CustomerOrder saved = orderRepository.save(order);
        log.info("Saved order id={} for userEmail={}", saved.getOrderId(), userEmail);
        return saved;
    }

    // Fetch orders for a user by email
    public List<CustomerOrder> findOrdersByUserEmail(String email) {
        log.info("Loading orders for userEmail={}", email);
        List<CustomerOrder> orders = orderRepository.findByCustomerEmailFetchItems(email);
        log.info("Found {} orders for userEmail={}", orders == null ? 0 : orders.size(), email);
        return orders;
    }

    // Fetch orders for a user by numeric user id (guest/dev fallback)
    public List<CustomerOrder> findOrdersByUserId(Long userId) {
        return orderRepository.findByCustomerUserId(userId);
    }

    // Helper: map entity -> DTO
    private OrderDTO mapToDto(CustomerOrder o) {
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        OrderDTO dto = new OrderDTO();
        dto.setOrder_id(o.getOrderId());
        if (o.getCustomer() != null) {
            dto.setCustomer_id(o.getCustomer().getUserId());
            dto.setCustomer_name(o.getCustomer().getName());
            dto.setCustomer_email(o.getCustomer().getEmail());
        }
        dto.setOrder_date(o.getOrderDate() != null ? o.getOrderDate().format(fmt) : null);
        dto.setStatus(o.getStatus());
        dto.setPayment_status(o.getPaymentStatus());
        dto.setTotal(o.getTotal() != null ? o.getTotal().doubleValue() : 0.0);

        List<OrderDetailDTO> details = o.getItems().stream().map(d -> {
            OrderDetailDTO dd = new OrderDetailDTO();
            dd.setOrder_detail_id(d.getOrderDetailId());
            if (d.getPart() != null) {
                dd.setPart_id(d.getPart().getPartId());
                dd.setPart_name(d.getPart().getName());
                dd.setPart_brand(d.getPart().getBrand());
            }
            dd.setQuantity(d.getQuantity());
            dd.setPrice(d.getPrice() != null ? d.getPrice().doubleValue() : 0.0);
            return dd;
        }).collect(Collectors.toList());

        dto.setDetails(details);
        return dto;
    }

    // Update order status (for sales staff)
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, String newStatus) {
        CustomerOrder order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.setStatus(newStatus);
        CustomerOrder saved = orderRepository.save(order);
        return mapToDto(saved);
    }

    // Record a payment for an order. staffEmail can be null; paidById can be taken from request or order.customer.
    @Transactional
    public OrderDTO recordPayment(Long orderId, PaymentRequestDTO req, String staffEmail) {
        if (req == null || req.getAmount() == null || req.getAmount() <= 0) {
            throw new IllegalArgumentException("Invalid payment amount");
        }

        CustomerOrder order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // Determine payer (customer)
        User payer = null;
        if (req.getPaid_by() != null) {
            payer = userRepository.findById(req.getPaid_by()).orElse(null);
        }
        if (payer == null && order.getCustomer() != null) {
            payer = order.getCustomer();
        }
        if (payer == null) throw new IllegalArgumentException("Payer not found");

        // Determine receiver (staff)
        User receiver = null;
        if (req.getReceived_by() != null) {
            receiver = userRepository.findById(req.getReceived_by()).orElse(null);
        } else if (staffEmail != null) {
            receiver = userRepository.findByEmail(staffEmail).orElse(null);
        }

        // Sum previous payments
        BigDecimal existing = paymentRepository.sumAmountByOrderId(orderId);
        BigDecimal paymentAmount = BigDecimal.valueOf(req.getAmount());
        BigDecimal newTotalPaid = existing.add(paymentAmount);

        // Create receipt
        PaymentReceipt receipt = new PaymentReceipt();
        receipt.setOrder(order);
        receipt.setPaidBy(payer);
        receipt.setAmount(paymentAmount);
        receipt.setMethod(req.getMethod() != null ? req.getMethod() : "CASH");
        receipt.setReceivedBy(receiver);
        receipt.setPaidAt(LocalDateTime.now());

        paymentRepository.save(receipt);

        // Update order payment status
        BigDecimal orderTotal = order.getTotal() != null ? order.getTotal() : BigDecimal.ZERO;
        String newPaymentStatus;
        if (newTotalPaid.compareTo(orderTotal) >= 0) {
            newPaymentStatus = "PAID";
        } else if (newTotalPaid.compareTo(BigDecimal.ZERO) > 0) {
            newPaymentStatus = "PARTIAL";
        } else {
            newPaymentStatus = "UNPAID";
        }
        order.setPaymentStatus(newPaymentStatus);
        orderRepository.save(order);

        return mapToDto(order);
    }

    // --- New: fetch all orders (for sales dashboard) and map to DTOs ---
    public List<OrderDTO> findAllOrdersForSales() {
        List<CustomerOrder> orders = orderRepository.findAllFetchItemsAndCustomer();
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        return orders.stream().map(o -> {
            OrderDTO dto = new OrderDTO();
            dto.setOrder_id(o.getOrderId());
            if (o.getCustomer() != null) {
                dto.setCustomer_id(o.getCustomer().getUserId());
                dto.setCustomer_name(o.getCustomer().getName());
                dto.setCustomer_email(o.getCustomer().getEmail());
            }
            dto.setOrder_date(o.getOrderDate() != null ? o.getOrderDate().format(fmt) : null);
            dto.setStatus(o.getStatus());
            dto.setPayment_status(o.getPaymentStatus());
            dto.setTotal(o.getTotal() != null ? o.getTotal().doubleValue() : 0.0);

            List<OrderDetailDTO> details = o.getItems().stream().map(d -> {
                OrderDetailDTO dd = new OrderDetailDTO();
                dd.setOrder_detail_id(d.getOrderDetailId());
                if (d.getPart() != null) {
                    dd.setPart_id(d.getPart().getPartId());
                    dd.setPart_name(d.getPart().getName());
                    dd.setPart_brand(d.getPart().getBrand());
                }
                dd.setQuantity(d.getQuantity());
                dd.setPrice(d.getPrice() != null ? d.getPrice().doubleValue() : 0.0);
                return dd;
            }).collect(Collectors.toList());

            dto.setDetails(details);
            return dto;
        }).collect(Collectors.toList());
    }

}
