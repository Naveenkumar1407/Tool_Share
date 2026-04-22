package com.toolshare.toolservice.controller;

import com.toolshare.toolservice.dto.ToolRequest;
import com.toolshare.toolservice.model.Tool;
import com.toolshare.toolservice.service.ToolService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tools")
public class ToolController {

    private final ToolService toolService;

    public ToolController(ToolService toolService) {
        this.toolService = toolService;
    }

    // browse all tools - public
    @GetMapping
    public ResponseEntity<List<Tool>> getAllTools(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category) {

        List<Tool> tools;
        if (search != null && !search.isEmpty()) {
            tools = toolService.searchTools(search);
        } else if (category != null && !category.isEmpty()) {
            tools = toolService.getToolsByCategory(category);
        } else {
            tools = toolService.getAllTools();
        }
        return ResponseEntity.ok(tools);
    }

    // get single tool
    @GetMapping("/{id}")
    public ResponseEntity<?> getToolById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(toolService.getToolById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // get available tools only
    @GetMapping("/available")
    public ResponseEntity<List<Tool>> getAvailableTools() {
        return ResponseEntity.ok(toolService.getAvailableTools());
    }

    // list a new tool (logged-in users)
    @PostMapping
    public ResponseEntity<?> addTool(@Valid @RequestBody ToolRequest request, Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        String username = auth.getName();
        Tool tool = toolService.addTool(request, userId, username);
        return ResponseEntity.ok(tool);
    }

    // update tool
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTool(@PathVariable Long id,
                                        @Valid @RequestBody ToolRequest request,
                                        Authentication auth) {
        try {
            Long userId = (Long) auth.getCredentials();
            Tool updated = toolService.updateTool(id, request, userId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // delete tool
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTool(@PathVariable Long id, Authentication auth) {
        try {
            Long userId = (Long) auth.getCredentials();
            String role = auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
            toolService.deleteTool(id, userId, role);
            return ResponseEntity.ok("Tool deleted");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // get my listed tools
    @GetMapping("/my")
    public ResponseEntity<List<Tool>> getMyTools(Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        return ResponseEntity.ok(toolService.getMyTools(userId));
    }
}
