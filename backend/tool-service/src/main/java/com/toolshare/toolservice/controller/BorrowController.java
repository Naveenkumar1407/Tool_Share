package com.toolshare.toolservice.controller;

import com.toolshare.toolservice.dto.BorrowRequestDTO;
import com.toolshare.toolservice.model.BorrowRequest;
import com.toolshare.toolservice.service.BorrowService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrows")
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    // create a borrow request
    @PostMapping
    public ResponseEntity<?> createRequest(@Valid @RequestBody BorrowRequestDTO dto, Authentication auth) {
        try {
            Long userId = (Long) auth.getCredentials();
            String username = auth.getName();
            BorrowRequest request = borrowService.createBorrowRequest(dto, userId, username);
            return ResponseEntity.ok(request);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // my borrow requests (things I want to borrow)
    @GetMapping("/my-requests")
    public ResponseEntity<List<BorrowRequest>> getMyRequests(Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        return ResponseEntity.ok(borrowService.getMyBorrowRequests(userId));
    }

    // incoming requests (people wanting to borrow my tools)
    @GetMapping("/incoming")
    public ResponseEntity<List<BorrowRequest>> getIncomingRequests(Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        return ResponseEntity.ok(borrowService.getIncomingRequests(userId));
    }

    // approve a request
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id, Authentication auth) {
        try {
            Long userId = (Long) auth.getCredentials();
            return ResponseEntity.ok(borrowService.approveRequest(id, userId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // reject a request
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id, Authentication auth) {
        try {
            Long userId = (Long) auth.getCredentials();
            return ResponseEntity.ok(borrowService.rejectRequest(id, userId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // mark tool as returned
    @PutMapping("/{id}/return")
    public ResponseEntity<?> markReturned(@PathVariable Long id, Authentication auth) {
        try {
            Long userId = (Long) auth.getCredentials();
            return ResponseEntity.ok(borrowService.markReturned(id, userId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
