package com.Mixer.library.service.impl;


import com.Mixer.library.Exception.CategoryNameAlreadyExistsException;
import com.Mixer.library.dto.CategoryDto;
import com.Mixer.library.model.Category;
import com.Mixer.library.repository.CategoryRepository;
import com.Mixer.library.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.Name;
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



    @Override
    public Category save(CategoryDto categoryDto) {

        String  name = categoryDto.getName();

        Long id = categoryDto.getId();

        if(existByName(name,id))
        {
            throw new CategoryNameAlreadyExistsException("The category is already Exist!");
        }

         try
         {
             Category category=new Category();
             category.setName(categoryDto.getName());
             category.setDeleted(false);
             category.setActivated(true);
             category.setDescription(categoryDto.getDescription());
             return categoryRepository.save(category);
         }
         catch(Exception e)
         {
             e.printStackTrace();
             return null;
         }

    }




    @Override
    public List<Category> findAll()
    {
        return categoryRepository.findAll();
    }


    @Override
    public Category update(Category category)
    {

        String name =category.getName();

        long id =category.getId();

        if(existByName(name,id))
        {
            throw new CategoryNameAlreadyExistsException("Category name already exist!");
        }
        try
        {
            Category categoryUpdate = categoryRepository.getById(category.getId());
            categoryUpdate.setName(category.getName());
            categoryUpdate.setDescription(category.getDescription());
            return categoryRepository.save(categoryUpdate);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }



    }


    @Override
    public Optional<Category> findById(Long id)
    {
        return categoryRepository.findById(id);
    }


    @Override
    public void deleteById(Long id) {
        Category category = categoryRepository.getById(id);
        category.setDeleted(true);
        category.setActivated(false);
        categoryRepository.save(category);

    }



    @Override
    public void enableById(Long id) {
        Category category = categoryRepository.getById(id);
        category.setDeleted(false);
        category.setActivated(true);
        categoryRepository.save(category);
    }


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



    @Override
    public boolean existByName(String name,Long id)
    {
        return categoryRepository.existsByNameAndIdNot(name,id);
    }
}


