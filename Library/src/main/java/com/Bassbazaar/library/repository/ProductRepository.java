package com.Bassbazaar.library.repository;

import com.Bassbazaar.library.model.Category;
import com.Bassbazaar.library.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>
{
    Product findById(long id);

    @Query(value = "select * from products where is_activated = true and category_id = :id", nativeQuery = true)
    List<Product> findAllByActivatedTrue(@Param("id") long id);

    @Query(value = "select * from products where is_activated = true", nativeQuery = true)
    List<Product> findAllByActivatedTrue();

    @Query(value = "select * from products ORDER BY product_id DESC", nativeQuery = true)
    List<Product> findAllByOrderById();

    @Query(value = "select count(*) from Product")
    Long countAllProducts();

    List<Product> findAllByCategoryId(long id);

    @Query(value = "SELECT * FROM products WHERE is_activated = true ORDER BY CASE WHEN :sort = 'lowToHigh' THEN cost_price END ASC, CASE WHEN :sort = 'highToLow' THEN cost_price END DESC", nativeQuery = true)
    List<Product> findAllByActivatedTrueAndSortBy(@Param("sort") String sort);

    List<Product> findAllByNameContainingIgnoreCase(String keyword);

    List<Product> findProductsByCategory(Category category);
    boolean existsByName(String name);



}
