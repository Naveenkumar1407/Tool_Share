package com.toolshare.toolservice.service;

import com.toolshare.toolservice.dto.BorrowRequestDTO;
import com.toolshare.toolservice.model.BorrowRequest;
import com.toolshare.toolservice.model.BorrowStatus;
import com.toolshare.toolservice.model.Tool;
import com.toolshare.toolservice.repository.BorrowRequestRepository;
import com.toolshare.toolservice.repository.ToolRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BorrowService {

    private final BorrowRequestRepository borrowRepo;
    private final ToolRepository toolRepo;

    public BorrowService(BorrowRequestRepository borrowRepo, ToolRepository toolRepo) {
        this.borrowRepo = borrowRepo;
        this.toolRepo = toolRepo;
    }

    public BorrowRequest createBorrowRequest(BorrowRequestDTO dto, Long borrowerId, String borrowerUsername) {
        Tool tool = toolRepo.findById(dto.getToolId())
                .orElseThrow(() -> new RuntimeException("Tool not found"));

        // can't borrow your own tool
        if (tool.getOwnerId().equals(borrowerId)) {
            throw new RuntimeException("You can't borrow your own tool");
        }

        // check date validity
        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new RuntimeException("Start date must be before end date");
        }

        // check for overlapping approved bookings
        List<BorrowRequest> overlapping = borrowRepo.findOverlapping(
                dto.getToolId(), dto.getStartDate(), dto.getEndDate(), BorrowStatus.APPROVED);

        if (!overlapping.isEmpty()) {
            throw new RuntimeException("Tool is already booked for those dates");
        }

        BorrowRequest request = new BorrowRequest();
        request.setTool(tool);
        request.setBorrowerId(borrowerId);
        request.setBorrowerUsername(borrowerUsername);
        request.setStartDate(dto.getStartDate());
        request.setEndDate(dto.getEndDate());
        request.setMessage(dto.getMessage());
        request.setStatus(BorrowStatus.PENDING);

        return borrowRepo.save(request);
    }

    public BorrowRequest approveRequest(Long requestId, Long ownerId) {
        BorrowRequest req = borrowRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Borrow request not found"));

        // only tool owner can approve
        if (!req.getTool().getOwnerId().equals(ownerId)) {
            throw new RuntimeException("Only the tool owner can approve requests");
        }

        if (req.getStatus() != BorrowStatus.PENDING) {
            throw new RuntimeException("Can only approve pending requests");
        }

        // double check no conflict with already approved requests
        List<BorrowRequest> conflicts = borrowRepo.findOverlapping(
                req.getTool().getId(), req.getStartDate(), req.getEndDate(), BorrowStatus.APPROVED);
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Dates conflict with another approved booking");
        }

        req.setStatus(BorrowStatus.APPROVED);
        req.getTool().setAvailable(false);
        toolRepo.save(req.getTool());

        return borrowRepo.save(req);
    }

    public BorrowRequest rejectRequest(Long requestId, Long ownerId) {
        BorrowRequest req = borrowRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Borrow request not found"));

        if (!req.getTool().getOwnerId().equals(ownerId)) {
            throw new RuntimeException("Only the tool owner can reject requests");
        }

        req.setStatus(BorrowStatus.REJECTED);
        return borrowRepo.save(req);
    }

    public BorrowRequest markReturned(Long requestId, Long userId) {
        BorrowRequest req = borrowRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Borrow request not found"));

        // either borrower or owner can mark as returned
        if (!req.getBorrowerId().equals(userId) && !req.getTool().getOwnerId().equals(userId)) {
            throw new RuntimeException("Not authorized");
        }

        if (req.getStatus() != BorrowStatus.APPROVED) {
            throw new RuntimeException("Can only return currently borrowed items");
        }

        req.setStatus(BorrowStatus.RETURNED);
        req.getTool().setAvailable(true);
        toolRepo.save(req.getTool());

        return borrowRepo.save(req);
    }

    public List<BorrowRequest> getMyBorrowRequests(Long borrowerId) {
        return borrowRepo.findByBorrowerId(borrowerId);
    }

    public List<BorrowRequest> getIncomingRequests(Long ownerId) {
        return borrowRepo.findByToolOwnerId(ownerId);
    }

    public List<BorrowRequest> getRequestsForTool(Long toolId) {
        return borrowRepo.findByToolId(toolId);
    }
}
