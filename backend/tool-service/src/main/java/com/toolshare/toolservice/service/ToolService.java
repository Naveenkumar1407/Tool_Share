package com.toolshare.toolservice.service;

import com.toolshare.toolservice.dto.ToolRequest;
import com.toolshare.toolservice.model.Tool;
import com.toolshare.toolservice.model.ToolCondition;
import com.toolshare.toolservice.repository.ToolRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToolService {

    private final ToolRepository toolRepo;

    public ToolService(ToolRepository toolRepo) {
        this.toolRepo = toolRepo;
    }

    public Tool addTool(ToolRequest req, Long ownerId, String ownerUsername) {
        Tool tool = new Tool();
        tool.setName(req.getName());
        tool.setDescription(req.getDescription());
        tool.setCategory(req.getCategory());
        tool.setOwnerId(ownerId);
        tool.setOwnerUsername(ownerUsername);
        tool.setAvailable(true);

        if (req.getToolCondition() != null) {
            tool.setToolCondition(ToolCondition.valueOf(req.getToolCondition().toUpperCase()));
        }

        return toolRepo.save(tool);
    }

    public Tool updateTool(Long toolId, ToolRequest req, Long userId) {
        Tool tool = toolRepo.findById(toolId)
                .orElseThrow(() -> new RuntimeException("Tool not found"));

        // only the owner can update their tool
        if (!tool.getOwnerId().equals(userId)) {
            throw new RuntimeException("You can only edit your own tools");
        }

        tool.setName(req.getName());
        tool.setDescription(req.getDescription());
        tool.setCategory(req.getCategory());
        if (req.getToolCondition() != null) {
            tool.setToolCondition(ToolCondition.valueOf(req.getToolCondition().toUpperCase()));
        }

        return toolRepo.save(tool);
    }

    public void deleteTool(Long toolId, Long userId, String role) {
        Tool tool = toolRepo.findById(toolId)
                .orElseThrow(() -> new RuntimeException("Tool not found"));

        // owner or admin can delete
        if (!tool.getOwnerId().equals(userId) && !"ADMIN".equals(role)) {
            throw new RuntimeException("Not authorized to delete this tool");
        }

        toolRepo.delete(tool);
    }

    public Tool getToolById(Long id) {
        return toolRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Tool not found"));
    }

    public List<Tool> getAllTools() {
        return toolRepo.findAll();
    }

    public List<Tool> searchTools(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return toolRepo.findAll();
        }
        return toolRepo.searchTools(keyword.trim());
    }

    public List<Tool> getToolsByCategory(String category) {
        return toolRepo.findByCategoryIgnoreCase(category);
    }

    public List<Tool> getMyTools(Long ownerId) {
        return toolRepo.findByOwnerId(ownerId);
    }

    public List<Tool> getAvailableTools() {
        return toolRepo.findByAvailableTrue();
    }
}
