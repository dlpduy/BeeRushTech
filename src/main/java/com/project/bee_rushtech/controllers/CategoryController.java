package com.project.bee_rushtech.controllers;

import com.project.bee_rushtech.dtos.CategoryDTO;
import com.project.bee_rushtech.models.Category;
import com.project.bee_rushtech.services.CategoryService;
import com.project.bee_rushtech.utils.SecurityUtil;
import com.project.bee_rushtech.utils.annotation.ApiMessage;
import com.project.bee_rushtech.utils.errors.InvalidException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final SecurityUtil securityUtil;

    public CategoryController(CategoryService categoryService, SecurityUtil securityUtil) {
        this.categoryService = categoryService;
        this.securityUtil = securityUtil;
    }

    @PostMapping("")
    @ApiMessage("Category created successfully")
    public ResponseEntity<?> createCategory(@RequestBody CategoryDTO categoryDTO,
            HttpServletRequest request) throws InvalidException {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String role = this.securityUtil.getUserFromToken(token).getRole();
        if (!role.equals("ADMIN")) {
            throw new InvalidException("You are not authorized");
        }
        if (this.categoryService.existsByName(categoryDTO.getName()) == true) {
            throw new InvalidException("Category already exists");

        }
        Category newCategory = categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok(newCategory);
    }

    @GetMapping("")
    @ApiMessage("All categories fetched successfully")
    public ResponseEntity<List<Category>> getAllCategories()
            throws InvalidException {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    @ApiMessage("Category fetched successfully")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id)
            throws InvalidException {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);

    }

    @PutMapping("/{id}")
    @ApiMessage("Category updated successfully")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO categoryDTO,
            HttpServletRequest request)
            throws InvalidException {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String role = this.securityUtil.getUserFromToken(token).getRole();
        if (!role.equals("ADMIN")) {
            throw new InvalidException("You are not authorized");
        }
        Category updatedCategory = categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(updatedCategory);

    }

    @DeleteMapping("/{id}")
    @ApiMessage("Category deleted successfully")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id,
            HttpServletRequest request) throws InvalidException {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String role = this.securityUtil.getUserFromToken(token).getRole();
        if (!role.equals("ADMIN")) {
            throw new InvalidException("You are not authorized");
        }
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(null);
    }
}
