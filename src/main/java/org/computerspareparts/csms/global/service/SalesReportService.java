package org.computerspareparts.csms.global.service;

import org.computerspareparts.csms.global.dto.SalesReportCreateRequest;
import org.computerspareparts.csms.global.dto.SalesReportDTO;
import org.computerspareparts.csms.global.entity.SalesReport;
import org.computerspareparts.csms.global.entity.User;
import org.computerspareparts.csms.global.entity.PaymentReceipt;
import org.computerspareparts.csms.global.repository.PaymentReceiptRepository;
import org.computerspareparts.csms.global.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SalesReportService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private PaymentReceiptRepository paymentReceiptRepository;

    @Autowired
    private UserRepository userRepository;

    // Create a sales report by aggregating payment receipts between start and end (inclusive)
    @Transactional
    public SalesReportDTO createReport(String creatorEmail, SalesReportCreateRequest req) {
        if (req.getStartDate() == null || req.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }
        if (req.getEndDate().isBefore(req.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        Optional<User> creatorOpt = userRepository.findByEmail(creatorEmail);
        User creator = creatorOpt.orElseThrow(() -> new IllegalArgumentException("Creator user not found"));

        LocalDate start = req.getStartDate();
        LocalDate end = req.getEndDate();

        LocalDateTime startDt = start.atStartOfDay();
        LocalDateTime endDt = end.atTime(LocalTime.MAX);

        List<PaymentReceipt> receipts = paymentReceiptRepository.findByPaidAtBetween(startDt, endDt);

        BigDecimal totalRevenue = receipts.stream()
                .map(PaymentReceipt::getAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalPayments = receipts.size();

        // Build and persist SalesReport entity using EntityManager
        SalesReport report = new SalesReport();
        report.setCreatedBy(creator);
        report.setStartDate(start);
        report.setEndDate(end);
        report.setSummaryText(req.getSummaryText());
        // filePath left null for now (could be set after exporting to CSV/PDF)

        em.persist(report);
        em.flush();

        SalesReportDTO dto = mapToDTO(report);
        dto.setTotalRevenue(totalRevenue);
        dto.setTotalPayments(totalPayments);
        return dto;
    }

    public List<SalesReportDTO> listReportsForUser(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return List.of();
        User u = userOpt.get();
        List<SalesReport> reports = em.createQuery("SELECT s FROM SalesReport s WHERE s.createdBy.userId = :userId ORDER BY s.createdAt DESC", SalesReport.class)
                .setParameter("userId", u.getUserId())
                .getResultList();
        return reports.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public Optional<SalesReportDTO> findById(Long id) {
        SalesReport r = em.find(SalesReport.class, id);
        if (r == null) return Optional.empty();
        SalesReportDTO dto = mapToDTO(r);
        LocalDateTime start = r.getStartDate().atStartOfDay();
        LocalDateTime end = r.getEndDate().atTime(LocalTime.MAX);
        List<PaymentReceipt> receipts = paymentReceiptRepository.findByPaidAtBetween(start, end);
        BigDecimal totalRevenue = receipts.stream().map(PaymentReceipt::getAmount).filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalRevenue(totalRevenue);
        dto.setTotalPayments(receipts.size());
        return Optional.of(dto);
    }

    @Transactional
    public void deleteReport(Long id) {
        SalesReport r = em.find(SalesReport.class, id);
        if (r != null) {
            em.remove(r);
        }
    }

    // List all reports with optional start/end date filter (by report start/end)
    public List<SalesReportDTO> listAllReports(LocalDate filterStart, LocalDate filterEnd) {
        StringBuilder q = new StringBuilder("SELECT s FROM SalesReport s WHERE 1=1");
        if (filterStart != null) q.append(" AND s.startDate >= :filterStart");
        if (filterEnd != null) q.append(" AND s.endDate <= :filterEnd");
        q.append(" ORDER BY s.createdAt DESC");

        var query = em.createQuery(q.toString(), SalesReport.class);
        if (filterStart != null) query.setParameter("filterStart", filterStart);
        if (filterEnd != null) query.setParameter("filterEnd", filterEnd);

        List<SalesReport> reports = query.getResultList();
        List<SalesReportDTO> dtos = new ArrayList<>();
        for (SalesReport r : reports) {
            SalesReportDTO dto = mapToDTO(r);
            // compute totals for each report period
            LocalDateTime start = r.getStartDate().atStartOfDay();
            LocalDateTime end = r.getEndDate().atTime(LocalTime.MAX);
            List<PaymentReceipt> receipts = paymentReceiptRepository.findByPaidAtBetween(start, end);
            BigDecimal totalRevenue = receipts.stream().map(PaymentReceipt::getAmount).filter(a -> a != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setTotalRevenue(totalRevenue);
            dto.setTotalPayments(receipts.size());
            dtos.add(dto);
        }
        return dtos;
    }

    private SalesReportDTO mapToDTO(SalesReport r) {
        SalesReportDTO dto = new SalesReportDTO();
        dto.setReportId(r.getReportId());
        dto.setCreatedById(r.getCreatedBy() != null ? r.getCreatedBy().getUserId() : null);
        dto.setCreatedByName(r.getCreatedBy() != null ? r.getCreatedBy().getName() : null);
        dto.setStartDate(r.getStartDate());
        dto.setEndDate(r.getEndDate());
        dto.setCreatedAt(r.getCreatedAt());
        dto.setSummaryText(r.getSummaryText());
        dto.setFilePath(r.getFilePath());
        return dto;
    }
}
