package com.Mixer.customer.controller;

import com.Mixer.library.dto.ProductDto;
import com.Mixer.library.model.Category;
import com.Mixer.library.model.Customer;
import com.Mixer.library.model.Product;
import com.Mixer.library.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class ProductController
{
    private final CategoryService categoryService;

    private final ProductService productService;

    private final CustomerService customerService;

    private  final WishlistService wishlistService;

    private final ShoppingCartService shoppingCartService;


    public ProductController(CategoryService categoryService, ProductService productService,
                             CustomerService customerService, WishlistService wishlistService, ShoppingCartService shoppingCartService) {
        this.customerService=customerService;
        this.categoryService = categoryService;
        this.productService = productService;
        this.wishlistService = wishlistService;
        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping("/")
    public String getHomePage(Model model, Principal principal, HttpSession session)                          //principal :hold the info about current authenticated user
     {
        if (principal != null)
        {
            Customer customer = customerService.findByEmail(principal.getName());
            session.setAttribute("userLoggedIn",true);
            session.setAttribute("username", customer.getFirstName() + " " + customer.getLastName());
            int wishlistCount = wishlistService.getWishlistCountByCustomer(customer);
            model.addAttribute("wishlistCount", wishlistCount);

        }
        List<Category> categories = categoryService.findAllByActivatedTrue();
        List<ProductDto> products=productService.findAllProducts();



        model.addAttribute("categories",categories);
        model.addAttribute("products",products);


        return "index";
    }




    @GetMapping("/products-list")
    public String getShopPage(@RequestParam(name = "id", required = false, defaultValue = "0") long categoryId,
                              @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
                              @RequestParam(name = "pageNo", required = false, defaultValue = "0") int pageNo,
                              @RequestParam(name = "sort", required = false, defaultValue = "") String sort,
                              Model model) {

        List<Category> categories = categoryService.findAllByActivatedTrue();

        Page<ProductDto> products;
        if (keyword.isEmpty() && categoryId == 0) {
            products = productService.findAllByActivated(pageNo, sort);
        } else {
            products = productService.searchProducts(pageNo, keyword, categoryId == 0 ? null : categoryId);
        }

        long totalProducts = products.getTotalElements();

        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("products", products);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("size", products.getSize());
        model.addAttribute("categories", categories);
        model.addAttribute("sort", sort);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);

        return "shop";
    }










    @GetMapping("/search-products/{pageNo}")
    public String searchProduct(@PathVariable("pageNo") int pageNo,
                                @RequestParam(name="keyword") String keyword,
                                @RequestParam(name="categoryId",required = false)Long categoryId
                                ,Model model)
    {
        Page<ProductDto> products = productService.searchProducts(pageNo,keyword,categoryId);
        long totalProducts = products.getTotalElements();
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("products", products);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", products.getTotalPages());
        return "shop";
    }




    @GetMapping("/product-full/{id}")
    public String getProductFull(@PathVariable("id")long id,Model model)
    {
        List<Category> categories = categoryService.findAllByActivatedTrue();
        ProductDto productDto=productService.findById(id);
        List<Product> products=productService.findProductsByCategory(productDto.getCategory().getId());

        model.addAttribute("categories",categories);
        model.addAttribute("productDto",productDto);
        model.addAttribute("products",products);
        return "product-full";
    }
}
