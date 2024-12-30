package com.project.bee_rushtech.services;

import com.project.bee_rushtech.dtos.CategoryDTO;
import com.project.bee_rushtech.models.Category;

import java.util.List;

public interface ICategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(Long id) throws Exception;
    Category createCategory(CategoryDTO categoryDTO);
    Category updateCategory(Long id, CategoryDTO categoryDTO);
    void deleteCategory(Long id);
}
