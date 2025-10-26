package org.computerspareparts.csms.global.repository;

import org.computerspareparts.csms.global.entity.TicketMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface TicketMessageRepository extends JpaRepository<TicketMessage, Long> {

    @Query("select m from TicketMessage m join fetch m.sender s join fetch m.ticket t where m.isDeleted = false")
    List<TicketMessage> findAllActiveWithSenderAndTicket();
//0
    List<TicketMessage> findByTicketIdAndIsDeletedFalseOrderByCreatedAtAsc(Long ticketId);
}
