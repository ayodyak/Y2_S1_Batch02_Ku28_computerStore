package org.computerspareparts.csms.global.repository;

import org.computerspareparts.csms.global.entity.CustomerOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerOrderDetailRepository extends JpaRepository<CustomerOrderDetail, Long> {
}

