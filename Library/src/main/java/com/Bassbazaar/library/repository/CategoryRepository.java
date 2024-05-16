package com.Bassbazaar.library.repository;

import com.Bassbazaar.library.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>
{
    Category deleteById(long id);

    @Query(value = "select * from categories where is_activated = true", nativeQuery = true)
    List<Category> findAllByActivatedTrue();

    @Query(value = "select COUNT(*) FROM Category")
    Long countAllCategories();

    Category findById(long id);


    /* Get the category name */


    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category c WHERE c.name = :name AND c.id <> :id")
    boolean existsByNameAndIdNot(String name, Long id);


}



// -> native SQL queries interact directly with the database tables and columns.