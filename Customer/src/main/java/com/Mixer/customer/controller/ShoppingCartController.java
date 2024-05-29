package com.Mixer.customer.controller;

import com.Mixer.library.Exception.InsufficientProductQuantityException;
import com.Mixer.library.dto.ProductDto;
import com.Mixer.library.model.Customer;
import com.Mixer.library.model.ShoppingCart;
import com.Mixer.library.service.CustomerService;
import com.Mixer.library.service.ProductService;
import com.Mixer.library.service.ShoppingCartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;


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



    @GetMapping("/cart")
    public String getCart(Model model, Principal principal)
    {
        if (principal == null) {
            return "redirect:/login";
        }
        else
        {
            Customer customer = customerService.findByEmail(principal.getName());

            ShoppingCart cart = customer.getCart();
            if (cart==null) {
                model.addAttribute("check","You don't have any items in your cart");
            }
            if (cart != null)
            {
                model.addAttribute("grandTotal", cart.getTotalPrice());
            }

            model.addAttribute("shoppingCart", cart);
            model.addAttribute("title", "Cart");
            return "cart";
        }
    }


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
        if (principal == null)
        {
            return "redirect:/login";
        }
        else
        {
            String username = principal.getName();
            ShoppingCart shoppingCart = shoppingCartService.addItemToCart(productDto, quantity, username, sizeId);
            session.setAttribute("totalItems", shoppingCart.getTotalItems());
            model.addAttribute("shoppingCart", shoppingCart);
            if (buyNow)
            {
                return "redirect:/cart";
            }
        }
        return "redirect:" + request.getHeader("Referer");
    }



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



    @RequestMapping(value = "/update-cart", method = RequestMethod.POST, params = "action=update")
    public String updateCart(@RequestParam("id") Long id,
                             @RequestParam("cart_item_id")Long cart_item_id,
                             @RequestParam("quantity") int quantity,
                             @RequestParam(value = "size",required = false,defaultValue = "0") long sizeId,
                             Model model,
                             Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        else
        {
            System.out.println(sizeId);
            ProductDto productDto = productService.findById(id);

            String username = principal.getName();

            ShoppingCart shoppingCart = shoppingCartService.updateCart(productDto, quantity, username,cart_item_id,sizeId);
            model.addAttribute("shoppingCart", shoppingCart);
            return "redirect:/cart";
        }
    }





    @GetMapping("/incrementQuantity")
    public String showQuantityIncrement(@RequestParam("id") Long cartId, @RequestParam("cart_item_id") Long cartItemId, RedirectAttributes redirectAttributes) {
       try
       {
           shoppingCartService.increment(cartId, cartItemId);
       }
       catch(InsufficientProductQuantityException e)
       {
           e.printStackTrace();
           redirectAttributes.addFlashAttribute("error","Insufficient product quantity ");
       }

        return "redirect:/cart";
    }


    @GetMapping("/decrementQuantity")
    public String showQuantityDecrement(@RequestParam("id") Long cartId ,@RequestParam("cart_item_id")Long cartItemId)
    {
        shoppingCartService.decrement(cartId,cartItemId);
        return "redirect:/cart";
    }

}
