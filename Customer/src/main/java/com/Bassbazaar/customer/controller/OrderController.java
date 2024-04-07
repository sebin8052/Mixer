package com.Bassbazaar.customer.controller;

import com.Bassbazaar.library.dto.AddressDto;
import com.Bassbazaar.library.model.*;
import com.Bassbazaar.library.service.AddressService;
import com.Bassbazaar.library.service.CustomerService;
import com.Bassbazaar.library.service.OrderService;
import com.Bassbazaar.library.service.ShoppingCartService;
import jakarta.servlet.http.HttpSession;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.*;

@Controller
public class OrderController {
    private CustomerService customerService;
    private OrderService orderService;
    private ShoppingCartService shoppingCartService;
    private AddressService addressService;

    public OrderController(CustomerService customerService, OrderService orderService, ShoppingCartService shoppingCartService, AddressService addressService) {
        this.customerService = customerService;
        this.orderService = orderService;
        this.shoppingCartService = shoppingCartService;
        this.addressService = addressService;
    }

                /* Check out the cart one by one */
    @GetMapping("/check-out/{itemId}")
    public String checkOutItem(@PathVariable Long itemId, Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        } else {
            Customer customer = customerService.findByEmail(principal.getName());

            ShoppingCart cart = customer.getCart();
            Set<CartItem> cartItems = cart.getCartItems();


            CartItem checkoutItem = cartItems.stream()
                    .filter(item -> item.getId().equals(itemId))
                    .findFirst()
                    .orElse(null);

            if (checkoutItem == null) {
                return "redirect:/checkout-error";
            }
            double totalPrice = checkoutItem.getQuantity() * checkoutItem.getUnitPrice();
            List<Address> addressList = customer.getAddress();

                                                                                                        /*
                                                                                                                    Wallet wallet=walletService.findByCustomer(customer);
                                                                                                                    List<CouponDto> couponDto=couponService.getAllCoupons();
                                                                                                                    model.addAttribute("wallet",wallet);
                                                                                                                    model.addAttribute("coupons",couponDto);
                                                                                                        */

            model.addAttribute("addressDto", new AddressDto());

            model.addAttribute("customer", customer);
            model.addAttribute("addressList", addressList);
            model.addAttribute("size", addressList.size());
            model.addAttribute("cartItems", Collections.singleton(checkoutItem));
            model.addAttribute("page", "Check-Out");
            model.addAttribute("shoppingCart", cart);
            model.addAttribute("grandTotal", totalPrice);
            return "checkout";
        }
    }

               /* Checkout all in the cart*/
    @GetMapping("/check-out/all")
    public String checkOutAll(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        } else {
            Customer customer = customerService.findByEmail(principal.getName());
            ShoppingCart cart = customer.getCart();
            Set<CartItem> cartItems = cart.getCartItems();
            List<Address> addressList = customer.getAddress();

                                                                                                            /*            Wallet wallet=walletService.findByCustomer(customer);
                                                                                                                        List<CouponDto> couponDto=couponService.getAllCoupons();
                                                                                                                        model.addAttribute("wallet",wallet);
                                                                                                                        model.addAttribute("coupons",couponDto);*/


            model.addAttribute("addressDto", new AddressDto());
            model.addAttribute("customer", customer);
            model.addAttribute("addressList", addressList);
            model.addAttribute("size", addressList.size());
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("page", "Check-Out");
            model.addAttribute("shoppingCart", cart);
            model.addAttribute("grandTotal", cart.getTotalItems());
            return "checkout";
        }
    }

    /* Check this Method . this is for the different payment method */

    @RequestMapping(value = "/add-order", method = RequestMethod.POST)
    @ResponseBody
    public String createOrder(@RequestBody Map<String, Object> data, Principal principal, HttpSession session, Model model) {

        String paymentMethod = data.get("payment_Method").toString();

        Long address_id = Long.parseLong(data.get("addressId").toString());
        Double oldTotalPrice = (Double) session.getAttribute("totalPrice");

        Customer customer = customerService.findByEmail(principal.getName());
        ShoppingCart cart = customer.getCart();

        if (paymentMethod.equals("COD"))
        {
            Order order = orderService.save(cart, address_id, paymentMethod, oldTotalPrice);

            session.removeAttribute("totalItems");
            session.removeAttribute("totalPrice");
            session.setAttribute("orderId", order.getId());
            model.addAttribute("order", order);
            model.addAttribute("page", "Order Detail");
            model.addAttribute("success", "Order Added Successfully");
            JSONObject options = new JSONObject();
            options.put("status", "Cash");
            return options.toString();
        }
        else      // check this  else statement
        {



            JSONObject error = new JSONObject();
            error.put("error", "Invalid payment method");
            return error.toString();
        }

    }





          /* Order Confiramation */
    @GetMapping("/order-confirmation")
    public String getOrderConfirmation(Model model,HttpSession session){
        Long order_id=(Long)session.getAttribute("orderId");
        Order order=orderService.findOrderById(order_id);
        String paymentMethod = order.getPaymentMethod();
        if (paymentMethod.equals("COD")){
            model.addAttribute("payment","Pending");
        }
        else{
            model.addAttribute("payment","Successful");
        }
        model.addAttribute("order", order);
        model.addAttribute("success", "Order Added Successfully");
        session.removeAttribute("orderId");
        return "order-detail";
    }


             /* Order Details [Dashboard]*/
    @GetMapping("/orders")
    public String getOrder(Principal principal,Model model){
        if (principal == null) {
            return "redirect:/login";
        }
        else {
            Customer customer = customerService.findByEmail(principal.getName());
            List<Order> orderList = orderService.findAllOrdersByCustomer(customer.getId());
            model.addAttribute("orders", orderList);
            model.addAttribute("title", "Order");
            model.addAttribute("page", "Order");
            return "orders";
        }
    }

    /*         Cancel the Order[My Account]    */
    @GetMapping("/cancel-order/{id}")
    public String cancelOrder(@PathVariable("id")long order_id, RedirectAttributes attributes){
        orderService.cancelOrder(order_id);
        attributes.addFlashAttribute("success", "Cancel order successfully!");
        return "redirect:/dashboard?tab=orders";
    }



    /* Order view based on Id [My Account ]*/
    @GetMapping("/order-view/{id}")
    public String orderView(@PathVariable("id")long order_id,Model model,HttpSession session){
        Order order=orderService.findOrderById(order_id);
        String paymentMethod = order.getPaymentMethod();
        if (paymentMethod.equals("COD")){
            model.addAttribute("payment","Pending");
        }
        else {
            model.addAttribute("payment", "Paid");
        }
        Customer customer=customerService.findById(order.getCustomer().getId());
        Address address = addressService.findDefaultAddress(customer.getId());
        model.addAttribute("order",order);
        model.addAttribute("address",address);
        return "order-view";
    }














                     /* Order Track */

    @GetMapping("/order-tracking/{id}")
    public String orderTrack(@PathVariable("id")long order_id,Model model,HttpSession session){
        Order order=orderService.findOrderById(order_id);
        String paymentMethod = order.getPaymentMethod();
        if (paymentMethod.equals("COD")){
            model.addAttribute("payment","Pending");
        }
        else {
            model.addAttribute("payment", "Paid");
        }
        Date currentTime = new Date();
        Customer customer=customerService.findById(order.getCustomer().getId());
        Address address = addressService.findDefaultAddress(customer.getId());
        if(order.getOrderStatus().equals("Pending")){
            int pending=1;
            model.addAttribute("pending",pending);
        }else if(order.getOrderStatus().equals("Confirmed")){
            int pending=1;
            int confirmed=2;
            model.addAttribute("pending",pending);
            model.addAttribute("confirmed",confirmed);
        }else if(order.getOrderStatus().equals("Shipped")){
            int pending=1;
            int confirmed=2;
            int shipped=3;
            model.addAttribute("pending",pending);
            model.addAttribute("confirmed",confirmed);
            model.addAttribute("shipped",shipped);
        }else if(order.getOrderStatus().equals("Delivered")){
            int pending=1;
            int confirmed=2;
            int shipped=3;
            int delivered=4;
            model.addAttribute("pending",pending);
            model.addAttribute("confirmed",confirmed);
            model.addAttribute("shipped",shipped);
            model.addAttribute("delivered",delivered);
        }
        model.addAttribute("order",order);
        model.addAttribute("time",currentTime);
        model.addAttribute("address",address);
        return "order-track";
    }

    /* Return the order*/

    @GetMapping("/return-order/{id}")
    public String returnOrder(@PathVariable("id")long order_id, RedirectAttributes attributes,
                              Principal principal){
        Customer customer=customerService.findByEmail(principal.getName());
        orderService.returnOrder(order_id,customer);
        attributes.addFlashAttribute("success", "Order Returned successfully!");
        return "redirect:/dashboard?tab=orders";
    }

    /*  Replace the Order*/
    @GetMapping("/replace-order/{id}")
    public String replaceOrder(@PathVariable("id")long order_id, RedirectAttributes attributes,
                               Principal principal){
        Customer customer=customerService.findByEmail(principal.getName());
        orderService.replaceOrderCustomer(order_id,customer);
        attributes.addFlashAttribute("success", "Order Replaced successfully!");
        return "redirect:/dashboard?tab=orders";
    }

}



