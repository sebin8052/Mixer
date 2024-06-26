package com.Mixer.library.service.impl;

import com.Mixer.library.Exception.InsufficientProductQuantityException;
import com.Mixer.library.dto.ProductDto;
import com.Mixer.library.model.CartItem;
import com.Mixer.library.model.Customer;
import com.Mixer.library.model.Product;
import com.Mixer.library.model.ShoppingCart;
import com.Mixer.library.repository.CartItemRepository;
import com.Mixer.library.repository.ShoppingCartRepository;
import com.Mixer.library.service.CustomerService;
import com.Mixer.library.service.ProductService;
import com.Mixer.library.service.ShoppingCartService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashSet;
import java.util.Set;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService
{
    private ShoppingCartRepository shoppingCartRepository;
    private CartItemRepository cartItemRepository;
    private CustomerService customerService;


    private ProductService productService;


    public ShoppingCartServiceImpl(ShoppingCartRepository shoppingCartRepository, CartItemRepository cartItemRepository, CustomerService customerService, ProductService productService) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.cartItemRepository = cartItemRepository;
        this.customerService = customerService;
        this.productService = productService;
    }


    @Override
    public ShoppingCart addItemToCart(ProductDto productDto, int quantity, String username, Long sizeId) {

        Customer customer = customerService.findByEmail(username);

        ShoppingCart shoppingCart = customer.getCart();

        if (shoppingCart == null)
        {
            shoppingCart = new ShoppingCart();
        }

        Set<CartItem> cartItemList = shoppingCart.getCartItems();

        CartItem cartItem = find(cartItemList, productDto.getId());

                 Product product = transfer(productDto);

        double unitPrice = 0;
        if(productDto.getSalePrice() == 0) {
            unitPrice = productDto.getCostPrice();
        }else{
            unitPrice = productDto.getSalePrice();
        }
        int itemQuantity = 0;

        if (cartItemList == null)
        {
            cartItemList = new HashSet<>();


            if (cartItem == null)
            {
                cartItem = new CartItem();
                cartItem.setProduct(product);
                cartItem.setCart(shoppingCart);
                cartItem.setQuantity(quantity);
                cartItem.setUnitPrice(unitPrice);
                cartItem.setCart(shoppingCart);
                cartItemList.add(cartItem);
                cartItemRepository.save(cartItem);
            }
            else
            {

                itemQuantity = cartItem.getQuantity() + quantity;
                cartItem.setQuantity(itemQuantity);
                cartItemRepository.save(cartItem);
            }
        } else
        {
            if (cartItem == null) {
                cartItem = new CartItem();
                cartItem.setProduct(product);
                cartItem.setCart(shoppingCart);
                cartItem.setQuantity(quantity);
                cartItem.setUnitPrice(unitPrice);
                cartItem.setCart(shoppingCart);
                cartItemList.add(cartItem);
                cartItemRepository.save(cartItem);
            }
            else {
                cartItem = new CartItem();
                cartItem.setProduct(product);
                cartItem.setCart(shoppingCart);
                cartItem.setQuantity(quantity);
                cartItem.setUnitPrice(unitPrice);
                cartItem.setCart(shoppingCart);
                cartItemList.add(cartItem);
                cartItemRepository.save(cartItem);
            }
        }

        shoppingCart.setCartItems(cartItemList);
        double totalPrice = totalPrice(shoppingCart.getCartItems());
        int totalItem = totalItem(shoppingCart.getCartItems());
        shoppingCart.setTotalPrice(totalPrice);
        shoppingCart.setTotalItems(totalItem);
        shoppingCart.setCustomer(customer);
        return shoppingCartRepository.save(shoppingCart);
    }

    private Product transfer(ProductDto productDto)
    {
        Product product = new Product();

        product.setId(productDto.getId());
        product.setName(productDto.getName());
        product.setCurrentQuantity(productDto.getCurrentQuantity());
        product.setCostPrice(productDto.getCostPrice());
        product.setSalePrice(productDto.getSalePrice());
        product.setShortDescription(productDto.getShortDescription());
        product.setLongDescription(productDto.getLongDescription());
        product.setImage(productDto.getImage());
        product.setBrand(productDto.getBrand());
        product.set_activated(productDto.isActivated());
        product.setCategory(productDto.getCategory());
        return product;
    }


    @Override
    public ShoppingCart updateCart(ProductDto productDto, int quantity, String username,Long cart_Item_Id,long size_id)
    {
        Customer customer = customerService.findByEmail(username);

        ShoppingCart shoppingCart = customer.getCart();

        Set<CartItem> cartItemList = shoppingCart.getCartItems();

        CartItem item = find(cartItemList, productDto.getId(),cart_Item_Id);
        int itemQuantity = quantity;
        item.setQuantity(itemQuantity);
        cartItemRepository.save(item);
        shoppingCart.setCartItems(cartItemList);
        int totalItem = totalItem(cartItemList);
        double totalPrice = totalPrice(cartItemList);
        shoppingCart.setTotalPrice(totalPrice);
        shoppingCart.setTotalItems(totalItem);
        return shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCart removeItemFromCart(ProductDto productDto, String username)
    {
        Customer customer = customerService.findByEmail(username);
        ShoppingCart shoppingCart = customer.getCart();
        Set<CartItem> cartItemList = shoppingCart.getCartItems();
        CartItem item = find(cartItemList, productDto.getId());
        cartItemList.remove(item);
        cartItemRepository.deleteById(item.getId());


        double totalPrice = totalPrice(cartItemList);

        int totalItem = totalItem(cartItemList);

        shoppingCart.setCartItems(cartItemList);
        shoppingCart.setTotalPrice(totalPrice);
        shoppingCart.setTotalItems(totalItem);
        if(cartItemList.isEmpty()){
            shoppingCart.setCustomer(null);
            shoppingCart.getCartItems().clear();
            shoppingCart.setTotalPrice(0);
            shoppingCart.setTotalItems(0);
        }
        return shoppingCartRepository.save(shoppingCart);
    }

    private double totalPrice(Set<CartItem> cartItemList)
    {
        double totalPrice = 0.0;
        for (CartItem item : cartItemList) {
            totalPrice += item.getUnitPrice() * item.getQuantity();
        }
        return totalPrice;
    }

    private int totalItem(Set<CartItem> cartItemList)
    {
        int totalItem = 0;
        for (CartItem item : cartItemList) {
            totalItem += item.getQuantity();
        }
        return totalItem;
    }

    @Override
    public ShoppingCart updateTotalPrice(Double newTotalPrice,String username)
    {
        Customer customer = customerService.findByEmail(username);

        ShoppingCart shoppingCart = customer.getCart();

        shoppingCart.setTotalPrice(newTotalPrice);
        shoppingCartRepository.save(shoppingCart);

        return shoppingCart;
    }

    @Transactional
    @Override
    public void deleteCartById(long id) {
        ShoppingCart shoppingCart = shoppingCartRepository.getById(id);

        for (CartItem cartItem : shoppingCart.getCartItems())
        {
            cartItem.setCart(null);
            cartItemRepository.deleteById(cartItem.getId());
        }
        shoppingCart.setCustomer(null);
        shoppingCart.getCartItems().clear();
        shoppingCart.setTotalPrice(0);
        shoppingCart.setTotalItems(0);
        shoppingCartRepository.save(shoppingCart);
    }









    private CartItem find(Set<CartItem> cartItems, long productId) {
        if (cartItems == null) {
            return null;
        }
        CartItem cartItem = null;
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == productId) {
                cartItem = item;
            }
        }
        return cartItem;
    }


    private CartItem find(Set<CartItem> cartItems, long productId,long cart_Item_Id) {
        if (cartItems == null) {
            return null;
        }
        CartItem cartItem = null;
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == productId && item.getId()==cart_Item_Id) {
                cartItem = item;
            }
        }
        return cartItem;
    }



    @Override
    public void increment(Long cartId, Long cartItemId)
    {

        ShoppingCart shoppingCart = shoppingCartRepository.getReferenceById(cartId);
        CartItem cartItem = findCartItemById(shoppingCart.getCartItems(), cartItemId);

        if (cartItem != null) {
            Product product = cartItem.getProduct();
            if (product.getCurrentQuantity() > cartItem.getQuantity()) {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                cartItemRepository.save(cartItem);


                updateShoppingCartTotals(shoppingCart);
                shoppingCartRepository.save(shoppingCart);
            }

            else
            {
                throw new InsufficientProductQuantityException("Insufficient product quantity for product ID:" + product.getId());
            }
        }


    }

    private void updateShoppingCartTotals(ShoppingCart shoppingCart) {
        double totalPrice = totalPrice(shoppingCart.getCartItems());
        int totalItem = totalItem(shoppingCart.getCartItems());
        shoppingCart.setTotalPrice(totalPrice);
        shoppingCart.setTotalItems(totalItem);
    }

    private CartItem findCartItemById(Set<CartItem> cartItems, Long cartItemId) {
        for (CartItem item : cartItems) {
            if (item.getId().equals(cartItemId)) {
                return item;
            }
        }
        return null;
    }


    @Override
    public void decrement(Long cartId, Long cartItemId) {
        ShoppingCart shoppingCart = shoppingCartRepository.getReferenceById(cartId);
        CartItem cartItem = findCartItemById(shoppingCart.getCartItems(), cartItemId);

        if (cartItem != null) {
            if (cartItem.getQuantity() > 1) {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
                cartItemRepository.save(cartItem);
            } else {

                shoppingCart.getCartItems().remove(cartItem);
                cartItemRepository.delete(cartItem);
            }


            updateShoppingCartTotals(shoppingCart);
            shoppingCartRepository.save(shoppingCart);
        }
    }

}
