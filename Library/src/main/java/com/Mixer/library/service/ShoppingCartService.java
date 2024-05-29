package com.Mixer.library.service;

import com.Mixer.library.dto.ProductDto;
import com.Mixer.library.model.Product;
import com.Mixer.library.model.ShoppingCart;

public interface ShoppingCartService
{
    ShoppingCart addItemToCart(ProductDto productDto, int quantity, String username, Long size);
    ShoppingCart updateCart(ProductDto productDto, int quantity, String username,Long cart_Item_Id,long size_id);
    ShoppingCart removeItemFromCart(ProductDto productDto, String username);

    ShoppingCart updateTotalPrice(Double newTotalPrice,String username);

    void deleteCartById(long id);


   void increment(Long cartId,Long cartItemId);


  void decrement(Long cartId,Long cartItemId);
}
