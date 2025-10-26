package org.computerspareparts.csms.global.service;

import org.computerspareparts.csms.global.dto.OrderItemDTO;
import org.computerspareparts.csms.global.dto.OrderRequestDTO;
import org.computerspareparts.csms.global.entity.CustomerOrder;
import org.computerspareparts.csms.global.entity.CustomerOrderDetail;
import org.computerspareparts.csms.global.entity.Part;
import org.computerspareparts.csms.global.entity.User;
import org.computerspareparts.csms.global.repository.CustomerOrderDetailRepository;
import org.computerspareparts.csms.global.repository.CustomerOrderRepository;
import org.computerspareparts.csms.global.repository.PartRepository;
import org.computerspareparts.csms.global.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class OrderService {

    @Autowired private CustomerOrderRepository customerOrderRepository;
    @Autowired private CustomerOrderDetailRepository customerOrderDetailRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PartRepository partRepository;

    public CustomerOrder createOrder(OrderRequestDTO request) {
        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        CustomerOrder order = new CustomerOrder();
        order.setCustomer(customer);

        BigDecimal total = BigDecimal.ZERO;

        if (request.getItems() != null) {
            for (OrderItemDTO item : request.getItems()) {
                Part part = partRepository.findById(item.getPartId())
                        .orElseThrow(() -> new IllegalArgumentException("Part not found: " + item.getPartId()));

                CustomerOrderDetail detail = new CustomerOrderDetail();
                detail.setPart(part);
                detail.setQuantity(item.getQuantity());
                detail.setPrice(part.getPrice());

                order.addItem(detail);

                BigDecimal line = part.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                total = total.add(line);

                // reduce stock level (best-effort)
                if (part.getStockLevel() != null) {
                    int newStock = part.getStockLevel() - item.getQuantity();
                    part.setStockLevel(Math.max(newStock, 0));
                    partRepository.save(part);
                }
            }
        }

        order.setTotal(total);

        CustomerOrder saved = customerOrderRepository.save(order);
        return saved;
    }
}

