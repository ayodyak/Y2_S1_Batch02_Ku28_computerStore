package org.computerspareparts.csms.global.repository;

import org.computerspareparts.csms.global.entity.CustomerOrder;
import org.computerspareparts.csms.global.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
    List<CustomerOrder> findByCustomer(User customer);
    List<CustomerOrder> findByCustomerUserId(Long userId);
    List<CustomerOrder> findByCustomerEmail(String email);

    @Query("SELECT DISTINCT o FROM CustomerOrder o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.part p WHERE o.customer.email = :email")
    List<CustomerOrder> findByCustomerEmailFetchItems(@Param("email") String email);

    @Query("SELECT DISTINCT o FROM CustomerOrder o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.part p LEFT JOIN FETCH o.customer c")
    List<CustomerOrder> findAllFetchItemsAndCustomer();
}
