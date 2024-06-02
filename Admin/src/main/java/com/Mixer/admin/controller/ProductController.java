package com.Mixer.admin.controller;

import com.Mixer.library.Exception.ProductNameAlreadyExistsException;
import com.Mixer.library.dto.CategoryDto;
import com.Mixer.library.dto.ProductDto;
import com.Mixer.library.model.Category;
import com.Mixer.library.model.Image;
import com.Mixer.library.model.Product;
import com.Mixer.library.service.CategoryService;
import com.Mixer.library.service.ImageService;
import com.Mixer.library.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProductController
{
    private ProductService productService;
    private CategoryService categoryService;
    private ImageService imageService;





    @Autowired
    public ProductController(ProductService productService, CategoryService categoryService,
                             ImageService imageService) {
        this.imageService=imageService;
        this.productService = productService;
        this.categoryService = categoryService;
    }


    @ExceptionHandler(ProductNameAlreadyExistsException.class)
     public ModelAndView handleProductNameAlreadyExistsException(ProductNameAlreadyExistsException ex)
     {
         ModelAndView modelAndView = new ModelAndView("add-product");
         modelAndView.addObject("error",ex.getMessage());
         return modelAndView;
     }

    @GetMapping("/products")
    public String productList(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        List<Category> categories = categoryService.findAllByActivatedTrue();

        List<ProductDto> products = productService.findAllByOrderDesc();
        model.addAttribute("products", products);
        model.addAttribute("categories",categories);

        return "products";

    }

    @GetMapping("/add-product")
    public String addProductPage(Model model) {

        model.addAttribute("title", "Add Product");
        List<Category> categories = categoryService.findAllByActivatedTrue();

        model.addAttribute("categories", categories);

        model.addAttribute("productDto", new ProductDto());
        model.addAttribute("categoryNew", new CategoryDto());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        return "add-product";

    }

    /* without cropping */
/*
    @PostMapping("/save-product")
    public String saveProduct(@ModelAttribute("productDto") ProductDto product,
                              @RequestParam("imageProduct") List<MultipartFile> imageProduct,

                              RedirectAttributes redirectAttributes) {
        try {
            productService.save(imageProduct,product);
            redirectAttributes.addFlashAttribute("success", "Added new product successfully!");
        } catch (ProductNameAlreadyExistsException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Product with same name is already exist!");
        }
        catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to add new product!");
        }
        return "redirect:/products";
    }
*/

    @PostMapping("/save-product")
    public String saveProduct(@ModelAttribute("productDto") ProductDto product,
                              @RequestParam("imageProduct") List<MultipartFile> imageProduct,
                              @RequestParam("cropX") int x,
                              @RequestParam("cropY") int y,
                              @RequestParam("cropWidth") int width,
                              @RequestParam("cropHeight") int height,
                              RedirectAttributes redirectAttributes)
    {
        try {
            Product savedProduct = productService.save(imageProduct, product, x, y, width, height);
            if (savedProduct != null && savedProduct.getImage() != null) {
                List<String> imageUrls = savedProduct.getImage().stream()
                        .map(Image::getName) // Modify to include full URL if necessary
                        .collect(Collectors.toList());
                redirectAttributes.addFlashAttribute("imageUrls", imageUrls);
            }
            redirectAttributes.addFlashAttribute("success", "Added new product successfully!");
        } catch (ProductNameAlreadyExistsException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Product with the same name already exists!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to add new product!");
        }
        return "redirect:/products";
    }


    @GetMapping("/update-product/{id}")
    public String updateProductForm(@PathVariable("id") long id, Model model)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
        {
            return "redirect:/login";
        }

        List<Category> categories = categoryService.findAllByActivatedTrue();

        ProductDto productDto = productService.findById(id);
        List<Image> images =imageService.findProductImages(id);
        model.addAttribute("title", "Update Product");
        model.addAttribute("categories", categories);
        model.addAttribute("images", images);


        model.addAttribute("productDto", productDto);
        return "update-product";
    }


    /*without cropping*/
/*
    @PostMapping("/update-product/{id}")
    public String updateProduct(@ModelAttribute("productDto") ProductDto productDto,
                                @RequestParam("imageProduct") List<MultipartFile> imageProduct,
                                RedirectAttributes redirectAttributes)
    {
        try {

            productService.update(imageProduct, productDto);
            redirectAttributes.addFlashAttribute("success", "Update successfully!");
        }catch (ProductNameAlreadyExistsException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Product with same name is already exist!");
        }
        catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error server, please try again!");
        }
        return "redirect:/products";
    }
*/


    @PostMapping("/update-product/{id}")
    public String updateProduct(@ModelAttribute("productDto") ProductDto productDto,
                                @RequestParam("imageProduct") List<MultipartFile> imageProduct,
                                @RequestParam(value = "cropX", required = false, defaultValue = "0") int x,
                                @RequestParam(value = "cropY", required = false, defaultValue = "0") int y,
                                @RequestParam(value = "cropWidth", required = false, defaultValue = "0") int width,
                                @RequestParam(value = "cropHeight", required = false, defaultValue = "0") int height,
                                RedirectAttributes redirectAttributes) {
        try {
            productService.update(imageProduct, productDto, x, y, width, height);
            redirectAttributes.addFlashAttribute("success", "Updated successfully!");
        } catch (ProductNameAlreadyExistsException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Product with the same name already exists!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Server error, please try again!");
        }
        return "redirect:/products";
    }


    @GetMapping("/disable-product/{id}")
    public String disable(@PathVariable("id")long id,RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("success","Product Disabled");

        productService.disable(id);
        return "redirect:/products";
    }

    @GetMapping("/enable-product/{id}")
    public String enable(@PathVariable("id")long id, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("success","Product Enabled");

        productService.enable(id);
        return "redirect:/products";
    }

    @GetMapping("/delete-product/{id}")
    public String delete(@PathVariable("id")long id,RedirectAttributes redirectAttributes){

        productService.deleteProduct(id);

        redirectAttributes.addFlashAttribute("success","Product deleted");

        return "redirect:/products";
    }

}
