package com.Bassbazaar.customer.controller;

import com.Bassbazaar.library.dto.ProductDto;
import com.Bassbazaar.library.model.Customer;
import com.Bassbazaar.library.model.ShoppingCart;
import com.Bassbazaar.library.service.CustomerService;
import com.Bassbazaar.library.service.ProductService;
import com.Bassbazaar.library.service.ShoppingCartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

                             /* cart.html    */
@Controller
public class ShoppingCartController
{
    private ShoppingCartService shoppingCartService;
    private ProductService productService;
    private CustomerService customerService;


    public ShoppingCartController(ShoppingCartService shoppingCartService, ProductService productService,
                                  CustomerService customerService) {
        this.shoppingCartService = shoppingCartService;
        this.productService = productService;
        this.customerService = customerService;
    }


    /* Cart page*/
    @GetMapping("/cart")
    public String getCart(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        } else {
            Customer customer = customerService.findByEmail(principal.getName());
            ShoppingCart cart = customer.getCart();
            if (cart==null) {
                model.addAttribute("check","You don't have any items in your cart");
            }
            if (cart != null) {
                model.addAttribute("grandTotal", cart.getTotalPrice());
            }
            model.addAttribute("shoppingCart", cart);
            model.addAttribute("title", "Cart");
            return "cart";
        }
    }


    /* Add to cart button in product full details       */
    @PostMapping("/add-to-cart")
    public String addItemToCart(@RequestParam("productId") Long id,
                                @RequestParam(name = "quantity", defaultValue = "1") int quantity,
                                @RequestParam(value = "selectedSizeId", defaultValue = "2") long sizeId,
                                @RequestParam(value = "buyNow", defaultValue = "false") boolean buyNow,
                                HttpServletRequest request,
                                Model model,
                                Principal principal,
                                HttpSession session) {
        ProductDto productDto = productService.findById(id);
        if (principal == null) {
            return "redirect:/login";
        } else {
            String username = principal.getName();
            ShoppingCart shoppingCart = shoppingCartService.addItemToCart(productDto, quantity, username, sizeId);
            session.setAttribute("totalItems", shoppingCart.getTotalItems());
            model.addAttribute("shoppingCart", shoppingCart);
            if (buyNow) {
                return "redirect:/cart";
            }
        }
        return "redirect:" + request.getHeader("Referer");
    }



    /* Delete item in  the cart */
    @RequestMapping(value = "/update-cart", method = RequestMethod.POST, params = "action=delete")
    public String deleteItem(@RequestParam("id") Long id,
                             Model model,
                             Principal principal
    ) {
        if (principal == null) {
            return "redirect:/login";
        } else {
            ProductDto productDto = productService.findById(id);
            String username = principal.getName();
            ShoppingCart shoppingCart = shoppingCartService.removeItemFromCart(productDto, username);
            model.addAttribute("shoppingCart", shoppingCart);
            return "redirect:/cart";
        }
    }













/*Update the cart [not needed ]*/
    @RequestMapping(value = "/update-cart", method = RequestMethod.POST, params = "action=update")
    public String updateCart(@RequestParam("id") Long id,
                             @RequestParam("cart_item_id")Long cart_item_id,
                             @RequestParam("quantity") int quantity,
                             @RequestParam(value = "size",required = false,defaultValue = "0") long sizeId,
                             Model model,
                             Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        } else {
            System.out.println(sizeId);
            ProductDto productDto = productService.findById(id);
            String username = principal.getName();
            ShoppingCart shoppingCart = shoppingCartService.updateCart(productDto, quantity, username,cart_item_id,sizeId);
            model.addAttribute("shoppingCart", shoppingCart);
            return "redirect:/cart";
        }
    }
}
