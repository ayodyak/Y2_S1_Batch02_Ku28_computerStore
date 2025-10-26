package org.computerspareparts.csms.global.repository;

import org.computerspareparts.csms.global.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByIsDeletedFalseOrderByCreatedAtDesc();
//0
    // Find tickets created by a specific user (by email) and not deleted, newest first
    List<Ticket> findByCreatedByEmailAndIsDeletedFalseOrderByCreatedAtDesc(String email);
}
