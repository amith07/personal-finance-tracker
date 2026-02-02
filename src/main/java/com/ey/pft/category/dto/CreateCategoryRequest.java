package com.ey.pft.category.dto;

import com.ey.pft.category.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class CreateCategoryRequest {

    @NotBlank
    @Size(min = 2, max = 120)
    private String name;

    @NotNull
    private CategoryType type;

    // Optional: create as child of an existing category
    private UUID parentId;

    public CreateCategoryRequest() {
    }

    public CreateCategoryRequest(String name, CategoryType type, UUID parentId) {
        this.name = name;
        this.type = type;
        this.parentId = parentId;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public CategoryType getType() { return type; }
    public void setType(CategoryType type) { this.type = type; }

    public UUID getParentId() { return parentId; }
    public void setParentId(UUID parentId) { this.parentId = parentId; }
}