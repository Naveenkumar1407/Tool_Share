package com.toolshare.toolservice.repository;

import com.toolshare.toolservice.model.BorrowRequest;
import com.toolshare.toolservice.model.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BorrowRequestRepository extends JpaRepository<BorrowRequest, Long> {

    // requests I made (as borrower)
    List<BorrowRequest> findByBorrowerId(Long borrowerId);

    // requests for tools I own
    @Query("SELECT br FROM BorrowRequest br WHERE br.tool.ownerId = :ownerId")
    List<BorrowRequest> findByToolOwnerId(@Param("ownerId") Long ownerId);

    // check for date conflicts on same tool
    @Query("SELECT br FROM BorrowRequest br WHERE br.tool.id = :toolId " +
           "AND br.status = :status " +
           "AND br.startDate <= :endDate AND br.endDate >= :startDate")
    List<BorrowRequest> findOverlapping(@Param("toolId") Long toolId,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate,
                                        @Param("status") BorrowStatus status);

    List<BorrowRequest> findByToolId(Long toolId);
}
