package com.ey.pft.category.service;

import com.ey.pft.category.*;
import com.ey.pft.category.dto.CategoryResponse;
import com.ey.pft.category.dto.CreateCategoryRequest;
import com.ey.pft.common.exception.BadRequestException;
import com.ey.pft.common.exception.ResourceNotFoundException;
import com.ey.pft.common.util.SecurityUtils;
import com.ey.pft.user.User;
import com.ey.pft.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> listByType(CategoryType type) {
        UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
        List<Category> system = categoryRepository.findByUserIsNullAndTypeOrderByNameAsc(type);
        List<Category> userCats = categoryRepository.findByUser_IdAndTypeOrderByNameAsc(userId, type);

        List<Category> merged = new ArrayList<>(system.size() + userCats.size());
        merged.addAll(system);
        merged.addAll(userCats);

        return merged.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public CategoryResponse create(CreateCategoryRequest req) {
        UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

        // Check uniqueness for this user/type/name
        if (categoryRepository.findUserCategoryByTypeAndName(userId, req.getType(), req.getName().trim()).isPresent()) {
            throw new BadRequestException("Category with same name and type already exists");
        }

        Category parent = null;
        if (req.getParentId() != null) {
            // Parent must be either a system category or a category owned by this user, and same type ideally
            Optional<Category> candidate = categoryRepository.findById(req.getParentId());
            if (candidate.isEmpty()) {
                throw new BadRequestException("Parent category not found");
            }
            Category parentCat = candidate.get();
            // Enforce tenancy: if parent has a user, it must be current user
            if (parentCat.getUser() != null && !parentCat.getUser().getId().equals(userId)) {
                throw new BadRequestException("Invalid parent category");
            }
            // Optional consistency by type
            if (parentCat.getType() != req.getType()) {
                throw new BadRequestException("Parent type must match child type");
            }
            parent = parentCat;
        }

        Category c = new Category();
        c.setUser(user); // user-defined
        c.setName(req.getName().trim());
        c.setType(req.getType());
        c.setParent(parent);

        Category saved = categoryRepository.save(c);
        return toResponse(saved);
    }

    private CategoryResponse toResponse(Category c) {
        UUID parentId = c.getParent() != null ? c.getParent().getId() : null;
        boolean system = (c.getUser() == null);
        return new CategoryResponse(
                c.getId(),
                c.getName(),
                c.getType(),
                parentId,
                system,
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }
}