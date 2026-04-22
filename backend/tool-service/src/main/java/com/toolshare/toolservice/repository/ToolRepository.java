package com.toolshare.toolservice.repository;

import com.toolshare.toolservice.model.Tool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ToolRepository extends JpaRepository<Tool, Long> {
    List<Tool> findByOwnerId(Long ownerId);
    List<Tool> findByAvailableTrue();
    List<Tool> findByCategoryIgnoreCase(String category);

    @Query("SELECT t FROM Tool t WHERE " +
           "LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.category) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Tool> searchTools(@Param("keyword") String keyword);
}
