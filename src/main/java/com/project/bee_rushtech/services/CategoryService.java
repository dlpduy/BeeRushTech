package com.project.bee_rushtech.services;

import com.project.bee_rushtech.dtos.CategoryDTO;
import com.project.bee_rushtech.models.Category;
import com.project.bee_rushtech.repositories.CategoryRepository;
import com.project.bee_rushtech.utils.errors.DataNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            throw new DataNotFoundException("No categories found.");
        }
        return categories;
    }

    @Override
    public Category getCategoryById(Long id) throws DataNotFoundException {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Category not found"));
    }

    public Category createCategory(CategoryDTO category) {
        return categoryRepository.save(Category.builder()
                .name(category.getName())
                .build());
    }

    @Override
    public Category updateCategory(Long id, CategoryDTO categoryDTO) {
        Category existingCategory = getCategoryById(id);
        existingCategory.setName(categoryDTO.getName());
        return categoryRepository.save(existingCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }
}
