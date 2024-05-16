package com.Bassbazaar.library.service;

import com.Bassbazaar.library.dto.ProductDto;
import com.Bassbazaar.library.model.Product;
import com.Bassbazaar.library.model.ShoppingCart;

public interface ShoppingCartService
{
    ShoppingCart addItemToCart(ProductDto productDto, int quantity, String username, Long size);  // add item to cart
    ShoppingCart updateCart(ProductDto productDto, int quantity, String username,Long cart_Item_Id,long size_id);   // update cart
    ShoppingCart removeItemFromCart(ProductDto productDto, String username); // delete item from the cart

    ShoppingCart updateTotalPrice(Double newTotalPrice,String username);  // reduce the price when coupon is applied

    void deleteCartById(long id);  // delete when order the product from the cart


   void increment(Long cartId,Long cartItemId);


  void decrement(Long cartId,Long cartItemId);
}
