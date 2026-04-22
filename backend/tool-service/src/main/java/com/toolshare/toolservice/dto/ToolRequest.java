package com.toolshare.toolservice.dto;

import jakarta.validation.constraints.NotBlank;

public class ToolRequest {

    @NotBlank(message = "Tool name is required")
    private String name;

    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    private String toolCondition;  // NEW, GOOD, FAIR, WORN

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getToolCondition() { return toolCondition; }
    public void setToolCondition(String toolCondition) { this.toolCondition = toolCondition; }
}
