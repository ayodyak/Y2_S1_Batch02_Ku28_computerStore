// SalesReportRepository
package org.computerspareparts.csms.global.repository;

import org.computerspareparts.csms.global.entity.SalesReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesReportRepository extends JpaRepository<SalesReport, Long> {
    // Derived query might be fragile; provide explicit JPQL to fetch reports for a specific creator user id, newest first
    @Query("SELECT s FROM SalesReport s WHERE s.createdBy.userId = :userId ORDER BY s.createdAt DESC")
    List<SalesReport> findByCreatedByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
}
