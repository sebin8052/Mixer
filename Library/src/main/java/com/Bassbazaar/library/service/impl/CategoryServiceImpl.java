package com.Bassbazaar.library.service.impl;


import com.Bassbazaar.library.dto.CategoryDto;
import com.Bassbazaar.library.model.Category;
import com.Bassbazaar.library.repository.CategoryRepository;
import com.Bassbazaar.library.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService
{

    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {

        this.categoryRepository = categoryRepository;
    }


    //used to save the category
    @Override
    public Category save(CategoryDto categoryDto) {
        Category category=new Category();
        category.setName(categoryDto.getName());
        category.setDeleted(false);
        category.setActivated(true);
        category.setDescription(categoryDto.getDescription());
        return categoryRepository.save(category);
    }


    // retrive all entity of the category entity

    @Override
    public List<Category> findAll()
    {
        return categoryRepository.findAll();
    }

    //Update the category
    @Override
    public Category update(Category category) {
        Category categoryUpdate = categoryRepository.getById(category.getId());
        categoryUpdate.setName(category.getName());
        categoryUpdate.setDescription(category.getDescription());
        return categoryRepository.save(categoryUpdate);
    }

    // find category by Id [find / not find ]
    @Override
    public Optional<Category> findById(Long id)
    {
        return categoryRepository.findById(id);
    }

   // disable the category
    @Override
    public void deleteById(Long id) {
        Category category = categoryRepository.getById(id);
        category.setDeleted(true);
        category.setActivated(false);
        categoryRepository.save(category);

    }

    //enable the category

    @Override
    public void enableById(Long id) {
        Category category = categoryRepository.getById(id);
        category.setDeleted(false);
        category.setActivated(true);
        categoryRepository.save(category);
    }

    // find the category based on activated
    @Override
    public List<Category> findAllByActivatedTrue() {
        return categoryRepository.findAllByActivatedTrue();
    }


    @Override
    public Long countAllCategories() {
        return categoryRepository.countAllCategories();
    }


    @Override
    public Category findById(long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public void disableCategoryAndProductsById(Long id) {
        Category category = categoryRepository.getById(id);
        category.disableCategoryAndProducts();
        categoryRepository.save(category);
    }
}


