package com.Mixer.library.service;

import com.Mixer.library.dto.ProductDto;
import com.Mixer.library.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

public interface ProductService
{
    List<Product> findAll();

    List<ProductDto> allProducts();

/*    Product save(List<MultipartFile> imageProduct, ProductDto product);*/

    Product save(List<MultipartFile> imageProduct, ProductDto product, int x, int y, int width, int height);
    ProductDto findById(long id);


  /*  Product update(List<MultipartFile> imageProduct,ProductDto productDto);*/
    Product update(List<MultipartFile> imageProduct,ProductDto productDto, int x, int y, int width, int height);
    void disable(long id);
    void enable(long id);
    Page<ProductDto> findAllByActivated(long id, int pageNo);
    Page<ProductDto> findAllByActivated(int pageNo,String sort);
    List<ProductDto> findAllProducts();
    List<ProductDto> findAllByOrderDesc();
    void deleteProduct(long id);

    Long countAllProducts();


    List<Object[]> getProductStats();
    List<Object[]> getProductsStatsBetweenDates(Date startDate, Date endDate);

    Product findBYId(long id);




    List<Product> findProductsByCategory(long id);




    Page<ProductDto> searchProducts(int pageNo,String Keyword,Long categoryId);



    void disableCategoryAndProductsById(Long id);
    boolean existsByName(String name);

    boolean existsByNameandId(String name,Long id);







}
