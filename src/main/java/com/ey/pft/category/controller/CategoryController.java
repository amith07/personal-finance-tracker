package com.ey.pft.category.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ey.pft.category.CategoryType;
import com.ey.pft.category.dto.CategoryResponse;
import com.ey.pft.category.dto.CreateCategoryRequest;
import com.ey.pft.category.service.CategoryService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

	private final CategoryService categoryService;

	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@GetMapping
	public ResponseEntity<List<CategoryResponse>> list(@RequestParam("type") @NotNull CategoryType type) {
		return ResponseEntity.ok(categoryService.listByType(type));
	}

	@PostMapping
	public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CreateCategoryRequest req) {
		return ResponseEntity.ok(categoryService.create(req));
	}
}